package top.nowandfuture.mod.faceanicap.config;

public class CameraConfig {

    public static String DEFAULT_CAMERA_ID = "camera_id";
    public static String SHOW_CAMERA = "camera_show";
    public static String DRAW_LANDMARK = "draw_landmark";
    public static String POSE_CONTROL = "pose_control";

    public static String FACIAL_EXPRESSION = "facial_expression";
    public static String AUTO_BLINK = "auto_blink";

    public static String EXTRA_PLAYER_RENDER = "extra_player_render";
    public static String RENDER_POSITION = "render_position";

    public static String ROT_X_FIXED = "rotation_x_fixed";
    public static String ROT_Y_FIXED = "rotation_y_fixed";
    public static String ROT_Z_FIXED = "rotation_z_fixed";
    public static String SMOOTH_THRESHOLD = "smooth_threshold";


    public int cameraId;
    public boolean showCamera, drawLandmark, poseControl, facialExpression, extraPlayerRender, auto_blink;
    public long renderPosition;

    public float rotationXFixed, rotationYFixed, rotationZFixed;
    public float smoothThreshold = 1f;
}
