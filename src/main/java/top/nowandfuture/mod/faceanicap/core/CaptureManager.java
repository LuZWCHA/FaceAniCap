package top.nowandfuture.mod.faceanicap.core;

import top.nowandfuture.jmediapipe.FrameGrabber;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CaptureManager {
    private volatile static CaptureManager instance;
    private String rootPath;
    private Runnable runnable;
    private final Map<Integer, FrameGrabber> cameraMap;

    private CaptureManager(){
        if(instance != null){
            throw new RuntimeException();
        }

        cameraMap = new HashMap<>();

        Runtime.getRuntime().addShutdownHook(new Thread(){
            @Override
            public void run() {
                for(FrameGrabber grabber: cameraMap.values()){
                    try {
                        grabber.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    public static List<String> listCameras(){
        return FrameGrabber.listCameras();
    }

    public synchronized FrameGrabber acquiredCamera(int idx){
        FrameGrabber grabber = cameraMap.get(idx);
        if(grabber != null){
            try {
                grabber.close();
                grabber = new FrameGrabber();
                cameraMap.put(idx, grabber);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else{
            grabber = new FrameGrabber();
            cameraMap.put(idx, grabber);
        }

        return cameraMap.get(idx);
    }

    public synchronized FrameGrabber closeCamera(int idx){
        FrameGrabber grabber = cameraMap.get(idx);
        if(grabber != null){
            try {
                grabber.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return cameraMap.remove(idx);
    }

    public static CaptureManager getInstance() {
        if(instance == null){
            synchronized (CaptureManager.class){
                if(instance == null){
                    instance = new CaptureManager();
                }
            }
        }
        return instance;
    }

    public void init(String rootPath){
        this.rootPath = rootPath;
    }

    public synchronized boolean isRunning(){
        return runnable != null && ((CaptureThread)runnable).isRunning();
    }

    public synchronized Runnable start(int cameraIdx){
        if(this.runnable != null){
            if(!stop()){
                return null;
            }
        }

        Runnable runnable = new CaptureThread(cameraIdx);

        Thread thread = new Thread(runnable, "capThread");
        thread.start();
        this.runnable = runnable;
        return runnable;
    }

    public synchronized boolean stop(){
        if(runnable != null){
            ((CaptureThread)runnable).stop();
            runnable = null;
            return true;
        }
        return false;
    }
}
