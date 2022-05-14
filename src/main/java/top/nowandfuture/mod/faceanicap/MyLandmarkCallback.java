package top.nowandfuture.mod.faceanicap;

import net.minecraft.client.Minecraft;
import top.nowandfuture.jmediapipe.HeadPoseEstimator;
import top.nowandfuture.jmediapipe.Vec3dPool;
import top.nowandfuture.jmediapipe.callback.LandmarkCallback;
import top.nowandfuture.jmediapipe.math.Utils;
import top.nowandfuture.jmediapipe.math.Vec2d;
import top.nowandfuture.jmediapipe.math.Vec3d;
import top.nowandfuture.mod.faceanicap.forge.RenderHandler;

import java.util.LinkedList;
import java.util.PriorityQueue;

public class MyLandmarkCallback implements LandmarkCallback {
    private final Vec3dPool p;
    private int w;
    private int h;

    public MyLandmarkCallback(int w, int h) {
        this.p = Vec3dPool.POOL;
        this.w = w;
        this.h = h;
    }

    public void landmark(int idx, float[] landmarks, int count) {
        if(Minecraft.getInstance().player == null || Minecraft.getInstance().level == null) return;

        Vec3d[] fmlmk = new Vec3d[468];
        Vec3d[] irislmk = new Vec3d[10];
        Vec2d[] keyPoint2D = new Vec2d[36];
        HeadPoseEstimator estimator = new HeadPoseEstimator(this.w, this.h);

        for (int i = 0; i < 1434; i += 3) {
            int l_idx = i / 3;
            Vec3d vec3d = this.p.get((float) estimator.getWidth() - landmarks[i], landmarks[i + 1], 0.0D);
            int keyPointIdx = HeadPoseEstimator.getDefaultKeyPointIndex(l_idx);
            if (keyPointIdx != -1) {
                keyPoint2D[keyPointIdx] = new Vec2d(vec3d);
            }

            if (l_idx < 468) {
                fmlmk[l_idx] = vec3d;
            } else {
                irislmk[l_idx - 468] = vec3d;
            }
        }

        Vec3d[] ret = estimator.getHeadPose(keyPoint2D);
        Vec3d eular = Utils.rotationVector2Eular(ret[0]).scale(Utils.RAD2ANG);
        eular.z = eular.z > 0.0D ? 180.0D - eular.z : -eular.z - 180.0D;

        double leftAsR = HeadPoseEstimator.FacialFeatures.eyeAspectRatio(fmlmk, HeadPoseEstimator.Eyes.LEFT);
        double rightAsR = HeadPoseEstimator.FacialFeatures.eyeAspectRatio(fmlmk, HeadPoseEstimator.Eyes.RIGHT);
        double mouthAsR = HeadPoseEstimator.FacialFeatures.mouthAspectRatio(fmlmk);
        double[] irisLXY = HeadPoseEstimator.FacialFeatures.detectIris(fmlmk, irislmk, HeadPoseEstimator.Eyes.LEFT);
        double[] irisRXY = HeadPoseEstimator.FacialFeatures.detectIris(fmlmk, irislmk, HeadPoseEstimator.Eyes.RIGHT);

        this.parseLandmarks(eular, leftAsR, rightAsR, mouthAsR, irisLXY[0], irisLXY[1], irisRXY[0], irisRXY[1]);

        RenderHandler.setKeyPointList(fmlmk);

        Vec3d[] var18 = fmlmk;
        int var19 = fmlmk.length;

        int var20;
        Vec3d lmk;
        for (var20 = 0; var20 < var19; ++var20) {
            lmk = var18[var20];
            this.p.recycle(lmk);
        }

        var18 = irislmk;
        var19 = irislmk.length;

        for (var20 = 0; var20 < var19; ++var20) {
            lmk = var18[var20];
            this.p.recycle(lmk);
        }

    }

    LinkedList<Vec3d> poseHistory = new LinkedList<>();
    LinkedList<Float> mouthHistory = new LinkedList<>();
    LinkedList<Float> leftEyeHistory = new LinkedList<>();
    LinkedList<Float> rightEyeHistory = new LinkedList<>();

    float lastRotY = Integer.MAX_VALUE;
    float lastRotZ = lastRotY;
    float lastRotX = lastRotY;
    float lastMouseOpen = -1, lastLeftEye = -1, lastRightEye = -1;

    //均值滤波
    private Vec3d smooth(LinkedList<Vec3d> history, Vec3d item, int maxSize){
        if (history.size() > maxSize) {
            history.removeLast();
        }

        history.addFirst(item.copy());

        Vec3d smooth = new Vec3d();
        for (Vec3d h : history) {
            smooth = smooth.add(h);
        }

        smooth = smooth.scale(1D / history.size());

        return smooth;
    }

    //中值滤波
    private float mid(LinkedList<Float> history, float item, int maxSize){
        if (history.size() > maxSize) {
            history.removeLast();
        }

        history.addFirst(item);

        PriorityQueue<Float> sorted = new PriorityQueue<>();
        sorted.addAll(history);

        int i = 0;
        while(i++ < history.size() / 2){
            sorted.remove();
        }

        return sorted.peek();
    }

    public void parseLandmarks(Vec3d pose, double leftEye, double rightEye, double mouthOpen, double v3, double v4, double v5, double v6) {

        Vec3d smoothPose = this.smooth(poseHistory, pose, 5);

        float smoothMouthOpen = this.mid(mouthHistory, (float) mouthOpen, 5);

//        float smoothLeftEye = this.mid(leftEyeHistory, (float) v, 3);
//        float smoothRightEye = this.mid(rightEyeHistory, (float) v1, 3);

        if(lastLeftEye == -1){
            lastLeftEye = (float) leftEye;
        }
        if(lastRightEye == -1){
            lastRightEye = (float) rightEye;
        }
        if(lastMouseOpen == -1){
            lastMouseOpen = smoothMouthOpen;
        }

        smoothMouthOpen = (float) (lastMouseOpen * 0.1 + smoothMouthOpen * (1 - 0.1));
        float smoothLeftEye = (float) (lastLeftEye * 0.2 + leftEye * (1 - 0.2));
        float smoothRightEye = (float) (lastRightEye * 0.2 + rightEye * (1 - 0.2));

        lastMouseOpen = smoothMouthOpen;
        lastLeftEye = smoothLeftEye;
        lastRightEye = smoothRightEye;

        float yRot = (float) smoothPose.y;
        float xRot = (float) smoothPose.x;
        float zRot = (float) smoothPose.z;

        if (lastRotY == Integer.MAX_VALUE) {
            lastRotY = yRot;
            lastRotX = xRot;
            lastRotZ = zRot;
        }

        double dx = 0, dy = 0, dz = 0;
        if (Math.abs(yRot - lastRotY) > 2) {
            dy = -(yRot - lastRotY);
            lastRotY = yRot;
        }
        if (Math.abs(xRot - lastRotX) > 2) {
            dx = (xRot - lastRotX);
            lastRotX = xRot;
        }
        if (Math.abs(zRot - lastRotZ) > 2) {
            dz = (zRot - lastRotZ);
            lastRotZ = zRot;
        }

        RenderHandler.pushPose(
                new Vec3d(dx, dy, dz),
                new Vec3d(xRot, yRot, zRot)
        );

        RenderHandler.pushExpression(smoothMouthOpen, smoothLeftEye, smoothRightEye);

        //modify the animation of player.


    }
}
