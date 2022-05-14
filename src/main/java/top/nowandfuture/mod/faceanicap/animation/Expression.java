package top.nowandfuture.mod.faceanicap.animation;

import com.google.common.collect.Lists;
import net.minecraft.util.ResourceLocation;
import top.nowandfuture.jmediapipe.HeadPoseEstimator;
import top.nowandfuture.mod.faceanicap.FaceAniCapMod;

import java.util.List;

public class Expression {

    public float mouthOpen;
    public int mouthTextureNum;

    public float leftEyeOpen;
    public float rightEyeOpen;
    public int eyeTextureNum;

    public boolean autoBlink;

    public Expression(){
        mouthTextureNum = 4;
        eyeTextureNum = 4;
        autoBlink = false;
    }

    public List<ResourceLocation> getMouthTextures(){
        List<ResourceLocation> ret = Lists.newArrayList();
        String texturePath = "mouth_default/mouth_open_";
        for(int i = 0; i < mouthTextureNum; i++) {
            ResourceLocation resourceLocation = new ResourceLocation(FaceAniCapMod.MOD_ID, texturePath + i + ".png");
            ret.add(resourceLocation);
        }

        return ret;
    }

    public List<ResourceLocation> getEyeTextures(HeadPoseEstimator.Eyes side){
        List<ResourceLocation> ret = Lists.newArrayList();
        String texturePath = "eyes_default/" +
                (side == HeadPoseEstimator.Eyes.LEFT ? "left_eye/left_eye_": "right_eye/right_eye_");

        ret.add(new ResourceLocation(FaceAniCapMod.MOD_ID, texturePath + "mask.png"));
        for(int i = 1; i < eyeTextureNum; i++) {
            ResourceLocation resourceLocation = new ResourceLocation(FaceAniCapMod.MOD_ID, texturePath + "open_" + i + ".png");
            ret.add(resourceLocation);
        }

        return ret;
    }

    public int getIndexForMouth(){
        int idx = 0;
        if(mouthOpen > 0.10 && mouthOpen < 0.20){
            idx = 1;
        }else if(mouthOpen >= 0.20 && mouthOpen <0.4){
            idx = 2;
        }else if(mouthOpen >= 0.4){
            idx = 3;
        }

        return idx;
    }

    public int getIndexForEye(HeadPoseEstimator.Eyes side){
        float eyeOpen = side == HeadPoseEstimator.Eyes.LEFT ? leftEyeOpen: rightEyeOpen;
        int idx;
        if(eyeOpen < 0.22){
            idx = 1;
        }else if(eyeOpen >= 0.22 && eyeOpen <0.30){
            idx = 2;
        }else{
            idx = 3;
        }

        return idx;
    }
}
