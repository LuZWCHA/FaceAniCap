package top.nowandfuture.mod.faceanicap.utils;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MainWindow;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.vector.Matrix4f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.system.MemoryUtil;
import top.nowandfuture.jmediapipe.math.Vec3d;
import top.nowandfuture.mod.faceanicap.animation.ExtraPlayerRenderer;
import top.nowandfuture.mod.faceanicap.FaceAniCapMod;

import javax.annotation.Nullable;
import java.nio.ByteBuffer;

import static org.lwjgl.opengl.EXTBGRA.GL_BGR_EXT;
import static org.lwjgl.opengl.GL11.*;

public class DrawHelper {

    public static void drawVideoAndLandmarks(MatrixStack stack, @Nullable byte[] videoBuf, @Nullable Vec3d[] landmarks, int x, int y, int w, int h, int wo, int ho, int pointW, int pointH, boolean doMirror){
        if(videoBuf != null) DrawHelper.drawTexture(videoBuf, stack, x, y, wo, ho,w, h);
        if(landmarks != null) DrawHelper.drawPoints(stack, landmarks, x,y, pointW,pointH, w / (float)wo, h / (float)ho, wo, ho,0);
    }

    public static void drawTexture(byte[] buf, MatrixStack stack, int x, int y, int width, int height, int dx, int dy){
        //use fast 4-byte alignment (default anyway) if possible
        //set length of one complete row in destination data (doesn't need to equal img.cols)
        ByteBuffer buffer = MemoryUtil.memAlloc(buf.length);
        buffer.put(buf).flip();

        GL11.glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        GL11.glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);

        int alignment = glGetInteger(GL_UNPACK_ALIGNMENT);

        GlStateManager._pixelStore(GL_UNPACK_SKIP_PIXELS, 0);
        GlStateManager._pixelStore(GL_UNPACK_SKIP_ROWS, 0);

        GlStateManager._pixelStore(GL_UNPACK_ALIGNMENT, 1);
        GlStateManager._pixelStore(GL_UNPACK_ROW_LENGTH,  width);
        GL11.glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, width, height, 0, GL_BGR_EXT, GL_UNSIGNED_BYTE, buffer);
        GlStateManager._pixelStore(GL_UNPACK_ROW_LENGTH, 0);
        GlStateManager._pixelStore(GL_UNPACK_ALIGNMENT, alignment);
        MemoryUtil.memFree(buffer);

        int a = GL11.glGetError();
        if(a != GL_NO_ERROR){
            FaceAniCapMod.LOGGER.debug("GL error.");
        }

        stack.pushPose();
        drawTexturedModalRectMirror(stack, x , y, 0, 0, width, height, dx, dy, 0);

        stack.popPose();
    }

    public static void drawTexturedModalRectMirror(MatrixStack matrixStack, int x, int y, int u, int v, int width, int height, int scaleW, int scaleH, float zLevel)
    {
        final float uScale = 1f / width;
        final float vScale = 1f / height;

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder wr = tessellator.getBuilder();
        wr.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);

        Matrix4f matrix = matrixStack.last().pose();
        wr.vertex(matrix, x        , y + scaleH, zLevel).uv( (u + width) * uScale, ((v + height) * vScale)).endVertex();
        wr.vertex(matrix, x + scaleW, y + scaleH, zLevel).uv(u          * uScale, ((v + height) * vScale)).endVertex();
        wr.vertex(matrix, x + scaleW, y         , zLevel).uv(u          * uScale, ( v           * vScale)).endVertex();
        wr.vertex(matrix, x        , y         , zLevel).uv( (u + width) * uScale, ( v           * vScale)).endVertex();
        tessellator.end();
    }

    public static void drawPoints(MatrixStack stack, Vec3d[] points, float offsetX, float offsetY, float w, float h, float xScale, float yScale, float frameWidth, float frameHeight, float zLevel){

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder wr = tessellator.getBuilder();
        wr.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
        Matrix4f matrix = stack.last().pose();
        for(Vec3d point: points) {
            float x = (float) (frameWidth - point.x) * xScale + offsetX;
            float y = (float) point.y * yScale + offsetY;
            if(point.x < frameWidth && point.x > 0 && point.y > 0 && point.y < frameHeight) {
                wr.vertex(matrix, x, y + h, zLevel).color(255, 255, 255, 128).endVertex();
                wr.vertex(matrix, x + w, y + h, zLevel).color(255, 255, 255, 128).endVertex();
                wr.vertex(matrix, x + w, y, zLevel).color(255, 255, 255, 128).endVertex();
                wr.vertex(matrix, x, y, zLevel).color(255, 255, 255, 128).endVertex();
            }
        }

        tessellator.end();
    }

    public static void drawPlayerModel(AbstractClientPlayerEntity playerEntity, int viewWidth, int viewHeight, float scale, float offsetX, float offsetY, float yRot, boolean mirror){

        RenderSystem.runAsFancy(new Runnable() {
            @Override
            public void run() {
                RenderSystem.matrixMode(GL11.GL_PROJECTION);
                RenderSystem.loadIdentity();
                RenderSystem.multMatrix(Matrix4f.perspective(60, 1f, 0.05F, 256));
                //---------------------------------------------draw----------------------------------------------------
                RenderSystem.matrixMode(GL11.GL_MODELVIEW);
                RenderSystem.loadIdentity();
                RenderSystem.viewport((int)offsetX, (int)offsetY, viewWidth, viewHeight);

                MatrixStack stack = new MatrixStack();
                ExtraPlayerRenderer.renderStatic(stack,offsetX, offsetY, scale, 0, yRot, playerEntity, mirror);
                //restore init viewport
                RenderSystem.viewport(0, 0, Minecraft.getInstance().getWindow().getWidth(), Minecraft.getInstance().getWindow().getHeight());
                RenderSystem.matrixMode(GL11.GL_PROJECTION);
                RenderSystem.loadIdentity();
                RenderSystem.multMatrix(Matrix4f.perspective(60, 1f, 0.05F, 256));
                RenderSystem.matrixMode(GL11.GL_MODELVIEW);

                //setup overlay
                MainWindow mainwindow = Minecraft.getInstance().getWindow();
                RenderSystem.clear(256, Minecraft.ON_OSX);
                RenderSystem.matrixMode(GL11.GL_PROJECTION);
                RenderSystem.loadIdentity();
                RenderSystem.ortho(0.0D, (double)mainwindow.getWidth() / mainwindow.getGuiScale(), (double)mainwindow.getHeight() / mainwindow.getGuiScale(), 0.0D, 1000.0D, 3000.0D);
                RenderSystem.matrixMode(GL11.GL_MODELVIEW);
                RenderSystem.loadIdentity();
                RenderSystem.translatef(0.0F, 0.0F, -2000.0F);

            }
        });

    }
}
