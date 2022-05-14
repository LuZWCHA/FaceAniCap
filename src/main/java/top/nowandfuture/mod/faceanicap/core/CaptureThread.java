package top.nowandfuture.mod.faceanicap.core;

import net.minecraft.client.Minecraft;
import top.nowandfuture.jmediapipe.CVFrame;
import top.nowandfuture.jmediapipe.FrameGrabber;
import top.nowandfuture.mod.faceanicap.MyLandmarkCallback;
import top.nowandfuture.mod.faceanicap.forge.RenderHandler;

public class CaptureThread implements Runnable{

    private boolean isRun;
    private int idx;

    public CaptureThread(int cameraIdx){
        this.idx = cameraIdx;
        this.isRun = true;
    }

    @Override
    public void run() {
        FrameGrabber grabber = CaptureManager.getInstance().acquiredCamera(idx);

        try{
            grabber.open(idx);
            grabber.set(5, 60);
            GraphManager.INSTANCE.setCallback(new MyLandmarkCallback((int)grabber.get(FrameGrabber.CV_PARAM.CAP_WIDTH.KEY()), (int)grabber.get(FrameGrabber.CV_PARAM.CAP_HEIGHT.KEY())));

            long i = 0;
            while(Minecraft.getInstance().level != null && isRun && grabber.isOpened()){
                final CVFrame frame = grabber.readFrame();
                RenderHandler.setFrame(frame);
                GraphManager.INSTANCE.detect(i++, frame);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            try {
                CaptureManager.getInstance().closeCamera(idx);
                RenderHandler.setFrame(null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public synchronized void stop(){
        isRun = false;
    }

    public synchronized boolean isRunning(){
        return isRun;
    }
}
