package top.nowandfuture.mod.faceanicap.animation;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.PlayerRenderer;
import net.minecraft.client.renderer.entity.layers.HeldItemLayer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Pose;
import net.minecraft.entity.player.PlayerModelPart;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.UseAction;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.HandSide;
import net.minecraft.util.math.MathHelper;
import top.nowandfuture.jmediapipe.math.Utils;

public class ExtraPlayerRenderer extends PlayerRenderer{


    public ExtraPlayerRenderer(EntityRendererManager manager) {
        super(manager);
        this.layers.add(new ExpressionLayerRenderer(this));
    }

    private boolean mirror = false;
    private float baseRotY = 0;

    public void setMirror(boolean mirror) {
        this.mirror = mirror;
    }

    public void setBaseRotY(float baseRotY) {
        this.baseRotY = baseRotY;
    }

    @Override
    public void render(AbstractClientPlayerEntity playerEntity, float scale, float pt, MatrixStack stack, IRenderTypeBuffer buffer, int light) {
        this.setModelProperties(playerEntity);

        stack.pushPose();

        stack.last().pose().setIdentity();
        stack.last().pose().setTranslation(0, -1.5f * scale, -3);
        stack.scale(mirror ? -scale: scale, scale, scale);
        stack.last().normal().setIdentity();
        stack.last().normal().set(-1,-1,-1);
        this.model.attackTime = this.getAttackAnim(playerEntity, pt);

        boolean shouldSit = playerEntity.isPassenger() && (playerEntity.getVehicle() != null && playerEntity.getVehicle().shouldRiderSit());

        if(playerEntity instanceof FakeClientPlayer){
            shouldSit |= ((FakeClientPlayer) playerEntity).isForceSit();
        }

        this.model.riding = shouldSit;
        this.model.young = playerEntity.isBaby();

        if(playerEntity instanceof FakeClientPlayer) {
            float rotLerp = MathHelper.rotLerp(pt, ((FakeClientPlayer) playerEntity).getRotZ0(), ((FakeClientPlayer) playerEntity).getRotZ());
            this.model.head.zRot = rotLerp / Utils.RAD2ANG;
        }

        float bodyRotLerp = MathHelper.rotLerp(pt, playerEntity.yBodyRotO, playerEntity.yBodyRot);
        float headRotLerp = MathHelper.rotLerp(pt, playerEntity.yHeadRotO, playerEntity.yHeadRot);

        headRotLerp =  headRotLerp - bodyRotLerp + baseRotY;
        bodyRotLerp = baseRotY;

        float relativeRotY = headRotLerp - bodyRotLerp;
        if (shouldSit && playerEntity.getVehicle() instanceof LivingEntity) {
            LivingEntity livingentity = (LivingEntity)playerEntity.getVehicle();
            bodyRotLerp = MathHelper.rotLerp(pt, livingentity.yBodyRotO, livingentity.yBodyRot);
            relativeRotY = headRotLerp - bodyRotLerp + baseRotY;
            bodyRotLerp = baseRotY;

            float f3 = MathHelper.wrapDegrees(relativeRotY);
            if (f3 < -85.0F) {
                f3 = -85.0F;
            }

            if (f3 >= 85.0F) {
                f3 = 85.0F;
            }

            bodyRotLerp = headRotLerp - f3;
            if (f3 * f3 > 2500.0F) {
                bodyRotLerp += f3 * 0.2F;
            }

            relativeRotY = headRotLerp - bodyRotLerp;
        } else if(shouldSit){
            bodyRotLerp = 0;
            relativeRotY = headRotLerp - bodyRotLerp + baseRotY;
            bodyRotLerp = baseRotY;

            float f3 = MathHelper.wrapDegrees(relativeRotY);
            if (f3 < -85.0F) {
                f3 = -85.0F;
            }

            if (f3 >= 85.0F) {
                f3 = 85.0F;
            }

            bodyRotLerp = headRotLerp - f3;
            if (f3 * f3 > 2500.0F) {
                bodyRotLerp += f3 * 0.2F;
            }

            relativeRotY = headRotLerp - bodyRotLerp;
        }

        float lerpRx = MathHelper.lerp(pt, playerEntity.xRotO, playerEntity.xRot);
        if (playerEntity.getPose() == Pose.SLEEPING) {
            Direction direction = playerEntity.getBedOrientation();
            if (direction != null) {
                float eyeHeight = playerEntity.getEyeHeight(Pose.STANDING) - 0.1F;
                stack.translate((float)(-direction.getStepX()) * eyeHeight, 0.0D, (float)(-direction.getStepZ()) * eyeHeight);
            }
        }

        float f7 = this.getBob(playerEntity, pt);
        this.setupRotations(playerEntity, stack, f7, bodyRotLerp, pt);
        stack.scale(-1.0F, -1.0F, 1.0F);
        this.scale(playerEntity, stack, pt);
        stack.translate(0.0D, (double)-1.501F, 0.0D);
        float aniPosOld = 0.0F;
        float aniPos = 0.0F;
        if (!shouldSit && playerEntity.isAlive()) {
            aniPosOld = MathHelper.lerp(pt, playerEntity.animationSpeedOld, playerEntity.animationSpeed);
            aniPos = playerEntity.animationPosition - playerEntity.animationSpeed * (1.0F - pt);
            if (playerEntity.isBaby()) {
                aniPos *= 3.0F;
            }

            if (aniPosOld > 1.0F) {
                aniPosOld = 1.0F;
            }
        }

        this.model.prepareMobModel(Minecraft.getInstance().player, aniPos, aniPosOld, pt);
        this.model.setupAnim(Minecraft.getInstance().player, aniPos, aniPosOld, f7, relativeRotY, lerpRx);

        Minecraft minecraft = Minecraft.getInstance();
        boolean bodyVisible = this.isBodyVisible(playerEntity);
        boolean notVisible = !bodyVisible && !playerEntity.isInvisibleTo(minecraft.player);
        boolean shouldEntityAppearGlowing = minecraft.shouldEntityAppearGlowing(playerEntity);
        RenderType rendertype = this.getRenderType(playerEntity, bodyVisible, notVisible, shouldEntityAppearGlowing);
        if (rendertype != null) {
            IVertexBuilder ivertexbuilder = buffer.getBuffer(rendertype);
            int i = getOverlayCoords(playerEntity, this.getWhiteOverlayProgress(playerEntity, pt));
            this.model.renderToBuffer(stack, ivertexbuilder, light, i, 1.0F, 1.0F, 1.0F, notVisible ? 0.15F : 1.0F);
        }

        if (!playerEntity.isSpectator()) {

            for (LayerRenderer layerrenderer : this.layers) {
                stack.pushPose();
                if(mirror && layerrenderer instanceof HeldItemLayer){
                    stack.scale(-1,1,1);
                    //exchange the left and right hands
                    playerEntity.setMainArm(HandSide.LEFT);
                }
                layerrenderer.render(stack, buffer, light, playerEntity, aniPos, aniPosOld, pt, f7, relativeRotY, lerpRx);

                stack.popPose();
            }
        }

        stack.popPose();
    }

    public static void renderStatic(MatrixStack stack, float x, float y, float scale, float rx, float ry, LivingEntity livingEntity, boolean mirror) {

        IRenderTypeBuffer.Impl impl = Minecraft.getInstance().renderBuffers().bufferSource();
        ExtraPlayerRenderer renderer = new ExtraPlayerRenderer(Minecraft.getInstance().getEntityRenderDispatcher());
        if(mirror){
            renderer.setMirror(true);
        }

        renderer.setBaseRotY(ry);
//        stack.translate(x, y, 0);
        renderer.render((AbstractClientPlayerEntity) livingEntity, scale,0, stack, impl, 240);
        impl.endBatch();
    }

    private void setModelProperties(AbstractClientPlayerEntity p_177137_1_) {
        PlayerModel<AbstractClientPlayerEntity> playermodel = this.getModel();
        if (p_177137_1_.isSpectator()) {
            playermodel.setAllVisible(false);
            playermodel.head.visible = true;
            playermodel.hat.visible = true;
        } else {
            playermodel.setAllVisible(true);
            playermodel.hat.visible = p_177137_1_.isModelPartShown(PlayerModelPart.HAT);
            playermodel.jacket.visible = p_177137_1_.isModelPartShown(PlayerModelPart.JACKET);
            playermodel.leftPants.visible = p_177137_1_.isModelPartShown(PlayerModelPart.LEFT_PANTS_LEG);
            playermodel.rightPants.visible = p_177137_1_.isModelPartShown(PlayerModelPart.RIGHT_PANTS_LEG);
            playermodel.leftSleeve.visible = p_177137_1_.isModelPartShown(PlayerModelPart.LEFT_SLEEVE);
            playermodel.rightSleeve.visible = p_177137_1_.isModelPartShown(PlayerModelPart.RIGHT_SLEEVE);
            playermodel.crouching = p_177137_1_.isCrouching();
            BipedModel.ArmPose main = getArmPose(p_177137_1_, Hand.MAIN_HAND);
            BipedModel.ArmPose off = getArmPose(p_177137_1_, Hand.OFF_HAND);
            if (main.isTwoHanded()) {
                off = p_177137_1_.getOffhandItem().isEmpty() ? BipedModel.ArmPose.EMPTY : BipedModel.ArmPose.ITEM;
            }

            if (p_177137_1_.getMainArm() == HandSide.RIGHT) {
                playermodel.rightArmPose = main;
                playermodel.leftArmPose = off;
            } else {
                playermodel.rightArmPose = off;
                playermodel.leftArmPose = main;
            }
        }

    }

    private static BipedModel.ArmPose getArmPose(AbstractClientPlayerEntity playerEntity, Hand hand) {
        ItemStack itemstack = playerEntity.getItemInHand(hand);
        if (itemstack.isEmpty()) {
            return BipedModel.ArmPose.EMPTY;
        } else {
            if (playerEntity.getUsedItemHand() == hand && playerEntity.getUseItemRemainingTicks() > 0) {
                UseAction useaction = itemstack.getUseAnimation();
                if (useaction == UseAction.BLOCK) {
                    return BipedModel.ArmPose.BLOCK;
                }

                if (useaction == UseAction.BOW) {
                    return BipedModel.ArmPose.BOW_AND_ARROW;
                }

                if (useaction == UseAction.SPEAR) {
                    return BipedModel.ArmPose.THROW_SPEAR;
                }

                if (useaction == UseAction.CROSSBOW && hand == playerEntity.getUsedItemHand()) {
                    return BipedModel.ArmPose.CROSSBOW_CHARGE;
                }
            } else if (!playerEntity.swinging && itemstack.getItem() == Items.CROSSBOW && CrossbowItem.isCharged(itemstack)) {
                return BipedModel.ArmPose.CROSSBOW_HOLD;
            }

            return BipedModel.ArmPose.ITEM;
        }
    }
    
}
