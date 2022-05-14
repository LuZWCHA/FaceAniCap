package top.nowandfuture.mod.faceanicap.forge;

import com.mojang.authlib.GameProfile;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.lwjgl.opengl.GL11;
import top.nowandfuture.jmediapipe.CVFrame;
import top.nowandfuture.jmediapipe.math.Vec3d;
import top.nowandfuture.mod.faceanicap.animation.ExtraPlayerRenderer;
import top.nowandfuture.mod.faceanicap.animation.FakeClientPlayer;
import top.nowandfuture.mod.faceanicap.config.Config;
import top.nowandfuture.mod.faceanicap.core.CaptureManager;
import top.nowandfuture.mod.faceanicap.utils.DrawHelper;

import java.util.LinkedList;
import java.util.UUID;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;

public class RenderHandler {

//    No multi thread resource competition
    private static LinkedList<Vec3d> poses = new LinkedList<>();
    private static Vec3d[] keyPointList;

    private static CVFrame frame = null;

    Vec3d poseDelta = Vec3d.ZERO();
    static Vec3d pose = Vec3d.ZERO();
    private static final Lock lock = new ReentrantLock();

    int textureId = -1;

    public static FakeClientPlayer fakeClientPlayer;

    public RenderHandler(){
    }

    public static void setFrame(CVFrame frame) {
        lock.lock();
        RenderHandler.frame = frame;
        lock.unlock();
    }

    public static void setKeyPointList(Vec3d[] keyPointList) {
        lock.lock();
        RenderHandler.keyPointList = keyPointList;
        lock.unlock();
    }

    public static void pushPose(Vec3d dpose, Vec3d absPose) {
        lock.lock();
        if(dpose.x * dpose.x + dpose.y * dpose.y + dpose.z * dpose.z < 180 * 180 * 3) {
            RenderHandler.poses.addLast(dpose);
            pose = pose.sub(absPose).length() < Config.INSTANCE.getPoseSmoothThreshold()
                    ? pose.add(absPose).scale(0.5): absPose;
        }

        lock.unlock();
    }

    public static void pushExpression(float mouth, float leftEye, float rightEye) {
        lock.lock();
        fakeClientPlayer.getExpression().mouthOpen = mouth;
        //mirror
        fakeClientPlayer.getExpression().leftEyeOpen = leftEye;
        fakeClientPlayer.getExpression().rightEyeOpen = rightEye;
        lock.unlock();
    }

    public static CVFrame getFrame() {
        return frame;
    }

    @SubscribeEvent
    public void renderPlayer(RenderWorldLastEvent event){
        MatrixStack stack = event.getMatrixStack();
        float pt = event.getPartialTicks();

        EntityRendererManager manager = Minecraft.getInstance().getEntityRenderDispatcher();
        ExtraPlayerRenderer renderer = new ExtraPlayerRenderer(manager);

//        assert Minecraft.getInstance().player != null;
        if (Minecraft.getInstance().player != null) {

        }

    }

    @SubscribeEvent
    public void renderHud(RenderGameOverlayEvent event){
        //Render landmarks
        //Render video
        if(event.getType() == ElementType.HOTBAR) {
            if(fakeClientPlayer == null){
                fakeClientPlayer = new FakeClientPlayer(Minecraft.getInstance().level, new GameProfile(UUID.randomUUID(), "fake_player"), Minecraft.getInstance().player);
            }

            MatrixStack stack = event.getMatrixStack();

            lock.lock();
            if (frame != null) {
                if (textureId <= 0) textureId = GlStateManager._genTexture();
                RenderSystem.enableTexture();
                RenderSystem.bindTexture(textureId);
                DrawHelper.drawVideoAndLandmarks(stack, frame.getData(), keyPointList, 0,0, 160, 120, frame.getWidth(), frame.getHeight(), 2, 2, true);
                RenderSystem.bindTexture(0);
            }

            Vec3d temp = pose;

            fakeClientPlayer.yHeadRotO = fakeClientPlayer.yHeadRot;
            fakeClientPlayer.yHeadRot = (float) MathHelper.clamp(temp.y, -90, 90);

            fakeClientPlayer.xRotO = fakeClientPlayer.xRot;
            fakeClientPlayer.xRot = (float) MathHelper.clamp(temp.x, -90, 90);
            //Add the z rotation
            fakeClientPlayer.setRotZ((float) -temp.z);

            DrawHelper.drawPlayerModel(fakeClientPlayer,180, 200, 2.5f, 0, 0, 0, false);

            lock.unlock();
        }
    }

    @SubscribeEvent
    public void clientTick(TickEvent.ClientTickEvent tickEvent){
        ClientPlayerEntity p = Minecraft.getInstance().player;

        if(tickEvent.phase == TickEvent.Phase.START && p != null){

            float decFac = 0.8f;

            lock.lock();
            if(poses.isEmpty()){
                //Fixed empty...
                decFac = 0.9f;
            }

            Vec3d s = Vec3d.ZERO();
            while(!poses.isEmpty()){
                 s = s.add(poses.removeLast());
            }
            lock.unlock();

            poseDelta = poseDelta.add(s).scale(0.5).add(poseDelta.scale(0.5));

            p.turn(poseDelta.y * 6, poseDelta.x * 6);
//            fakeClientPlayer.resetPos();
//            fakeClientPlayer.turn(poseDelta.y, poseDelta.x);

//            System.out.println(target.y);

            poseDelta = poseDelta.scale(decFac);
        }

    }

    @SubscribeEvent
    public void onWorldUnload(WorldEvent.Unload unload){
        RenderSystem.recordRenderCall(() -> {
            if(textureId > 0) GL11.glDeleteTextures(textureId);
            textureId = 0;
        });

        CaptureManager.getInstance().stop();
    }

}
