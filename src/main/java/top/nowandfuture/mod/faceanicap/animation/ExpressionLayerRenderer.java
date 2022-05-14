package top.nowandfuture.mod.faceanicap.animation;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.*;
import top.nowandfuture.jmediapipe.HeadPoseEstimator;

import java.util.List;

public class ExpressionLayerRenderer extends LayerRenderer<AbstractClientPlayerEntity, PlayerModel<AbstractClientPlayerEntity>> {

    private static float OFFSET = 0.03f;

    private static PositionTextureVertex[] ptvs = new PositionTextureVertex[]{
            PositionTextureVertex.create(-4,-8,-4f + OFFSET,0,0),
            PositionTextureVertex.create(4,-8,-4f + OFFSET, 1, 0),
            PositionTextureVertex.create(4,0,-4f + OFFSET, 1, 1),
            PositionTextureVertex.create(-4,0,-4f + OFFSET, 0, 1)
    };

    public ExpressionLayerRenderer(IEntityRenderer<AbstractClientPlayerEntity, PlayerModel<AbstractClientPlayerEntity>> p_i50926_1_) {
        super(p_i50926_1_);
    }

    @Override
    public void render(MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn, AbstractClientPlayerEntity entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        matrixStackIn.pushPose();
        ModelRenderer renderer = getParentModel().head;
        seRotationAndTranslation(matrixStackIn, renderer.x, renderer.y, renderer.z, renderer.xRot, renderer.yRot, renderer.zRot);
        renderMouth(matrixStackIn, bufferIn, packedLightIn, entitylivingbaseIn);
        renderEyes(matrixStackIn, bufferIn, packedLightIn, entitylivingbaseIn, HeadPoseEstimator.Eyes.LEFT);
        renderEyes(matrixStackIn, bufferIn, packedLightIn, entitylivingbaseIn, HeadPoseEstimator.Eyes.RIGHT);
        matrixStackIn.popPose();
    }

    public void renderMouth(MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn, AbstractClientPlayerEntity entitylivingbaseIn){

        if(entitylivingbaseIn instanceof FakeClientPlayer) {
            FakeClientPlayer fakeClientPlayer = ((FakeClientPlayer) entitylivingbaseIn);
            Expression expression = fakeClientPlayer.getExpression();
            List<ResourceLocation> locations = expression.getMouthTextures();

            int idx = expression.getIndexForMouth();
            if(locations.isEmpty() || idx <= 0 || idx >= locations.size()) return;

            RenderType type = RenderType.entityCutoutNoCull(locations.get(idx));
            IVertexBuilder vertexBuilder = bufferIn.getBuffer(type);
            Matrix3f normalMatrix = matrixStackIn.last().normal().copy();
            Matrix4f poseMatrix = matrixStackIn.last().pose().copy();
            Vector3f normal = new Vector3f(0, 0, -1);
            normal.transform(normalMatrix);

            float red = 1, green = 1, blue = 1, alpha = 1;
            int packetOverlayIn = OverlayTexture.NO_OVERLAY;
            for(PositionTextureVertex vertex: ptvs) {
                float u = vertex.u(), v = vertex.v();

                Vector4f pose = new Vector4f(vertex.x() / 16, vertex.y() / 16, vertex.z() / 16, 1);
                pose.transform(poseMatrix);

                vertexBuilder.vertex(pose.x(), pose.y(), pose.z(), red, green, blue, alpha, u, v, packetOverlayIn, packedLightIn, normal.x(), normal.y(), normal.z());
            }
        }
    }

    public void renderEyes(MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn, AbstractClientPlayerEntity entitylivingbaseIn, HeadPoseEstimator.Eyes side){
        if(entitylivingbaseIn instanceof FakeClientPlayer) {
            FakeClientPlayer fakeClientPlayer = ((FakeClientPlayer) entitylivingbaseIn);
            Expression expression = fakeClientPlayer.getExpression();
            List<ResourceLocation> locations = expression.getEyeTextures(side);
            int idx = expression.getIndexForEye(side);

            if(locations.isEmpty() || idx >= locations.size()) return;

            //render mask
            RenderType type = RenderType.entityCutoutNoCull(locations.get(0));
            IVertexBuilder vertexBuilder = bufferIn.getBuffer(type);
            Matrix3f normalMatrix = matrixStackIn.last().normal().copy();
            Matrix4f poseMatrix = matrixStackIn.last().pose().copy();
            Vector3f normal = new Vector3f(0, 0, -1);
            normal.transform(normalMatrix);

            float red = 1, green = 1, blue = 1, alpha = 1;
            int packetOverlayIn = OverlayTexture.NO_OVERLAY;
            for(PositionTextureVertex vertex: ptvs) {
                float u = vertex.u(), v = vertex.v();

                Vector4f pose = new Vector4f(vertex.x() / 16, vertex.y() / 16, vertex.z() / 16, 1);
                pose.transform(poseMatrix);

                vertexBuilder.vertex(pose.x(), pose.y(), pose.z(), red, green, blue, alpha, u, v, packetOverlayIn, packedLightIn, normal.x(), normal.y(), normal.z());
            }

            //render eye
            type = RenderType.entityCutoutNoCull(locations.get(idx));
            vertexBuilder = bufferIn.getBuffer(type);

            for(PositionTextureVertex vertex: ptvs) {
                float u = vertex.u(), v = vertex.v();

                Vector4f pose = new Vector4f(vertex.x() / 16, vertex.y() / 16, vertex.z() / 16, 1);
                pose.transform(poseMatrix);

                vertexBuilder.vertex(pose.x(), pose.y(), pose.z() + OFFSET, red, green, blue, alpha, u, v, packetOverlayIn, packedLightIn, normal.x(), normal.y(), normal.z());
            }
        }
    }

    public void seRotationAndTranslation(MatrixStack stack, float x, float y, float z, float xRot, float yRot, float zRot){
        stack.translate(x / 16.0F, y / 16.0F, z / 16.0F);
        if (zRot != 0.0F) {
            stack.mulPose(Vector3f.ZP.rotation(zRot));
        }

        if (yRot != 0.0F) {
            stack.mulPose(Vector3f.YP.rotation(yRot));
        }

        if (xRot != 0.0F) {
            stack.mulPose(Vector3f.XP.rotation(xRot));
        }
    }
}
