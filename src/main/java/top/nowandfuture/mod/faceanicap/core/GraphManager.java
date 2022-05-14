package top.nowandfuture.mod.faceanicap.core;

import top.nowandfuture.jmediapipe.CVFrame;
import top.nowandfuture.jmediapipe.Graph;
import top.nowandfuture.jmediapipe.ModelFiles;
import top.nowandfuture.jmediapipe.callback.LandmarkCallback;

import java.nio.file.Paths;

public enum GraphManager {
    INSTANCE;

    private final Graph graph;
    private LandmarkCallback landmarkCallback;
    GraphManager(){
        graph = new Graph();
        Runtime.getRuntime().addShutdownHook(new Thread(){
            @Override
            public void run() {
                super.run();
                try {
                    release();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void release(){
        graph.release();
    }

    public int init(String root){
        return graph.init(Paths.get(root, ModelFiles.PD_TXT).toString(), root);
    }

    public int setCallback(LandmarkCallback landmarkCallback){
        return graph.setCallback(landmarkCallback);
    }

    public int detect(long i, CVFrame frame){
        return graph.landmarks(i, frame);
    }
}
