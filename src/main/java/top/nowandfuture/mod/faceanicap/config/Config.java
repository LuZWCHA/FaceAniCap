package top.nowandfuture.mod.faceanicap.config;

public enum Config {
    INSTANCE;

    CameraConfig cameraConfig;
    Config(){
        cameraConfig = new CameraConfig();
    }

    public float getPoseSmoothThreshold(){
        return cameraConfig.smoothThreshold;
    }
}
