package top.nowandfuture.mod.faceanicap.animation;

import net.minecraft.util.math.vector.Vector3f;

public class PositionTextureVertex {
    Vector3f pos;
    float u, v;

    private PositionTextureVertex(){
        pos = new Vector3f();
    }

    public float u(){
        return u;
    }

    public float v(){
        return v;
    }

    public float x(){
        return pos.x();
    }

    public float y(){
        return pos.y();
    }

    public float z(){
        return pos.z();
    }

    public static PositionTextureVertex create(float x, float y, float z, float u, float v){
        PositionTextureVertex vertex = new PositionTextureVertex();
        vertex.pos = new Vector3f(x, y, z);
        vertex.u = u;
        vertex.v = v;
        return vertex;
    }
}
