package top.nowandfuture.mod.faceanicap.animation;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import top.nowandfuture.mod.faceanicap.animation.Expression;

import javax.annotation.Nullable;

public class FakeClientPlayer extends AbstractClientPlayerEntity {

    AbstractClientPlayerEntity clientPlayerEntity;
    private Expression expression;
    private float zRot = 0;
    private float zRot0 = 0;

    public FakeClientPlayer(ClientWorld p_i50991_1_, GameProfile p_i50991_2_, AbstractClientPlayerEntity real) {
        super(p_i50991_1_, p_i50991_2_);
        clientPlayerEntity = real;
        expression = new Expression();
    }

    public Expression getExpression() {
        return expression;
    }

    public void setExpression(Expression expression) {
        this.expression = expression;
    }

    @Override
    public ResourceLocation getSkinTextureLocation() {
        return clientPlayerEntity.getSkinTextureLocation();
    }

    boolean forceSit = false;

    public void setForceSit(boolean forceSit) {
        this.forceSit = forceSit;
    }

    public boolean isForceSit() {
        return forceSit;
    }

    @Override
    public boolean isSkinLoaded() {
        return clientPlayerEntity.isSkinLoaded();
    }

    @Override
    public boolean hasPassenger(Class<? extends Entity> p_205708_1_) {
        return clientPlayerEntity.hasPassenger(p_205708_1_);
    }

    @Override
    public boolean isPassenger() {
        return clientPlayerEntity.isPassenger();
    }

    @Nullable
    @Override
    public Entity getVehicle() {
        return clientPlayerEntity.getVehicle();
    }

    @Override
    public ItemStack getOffhandItem() {
        return clientPlayerEntity.getOffhandItem();
    }

    @Override
    public ItemStack getItemBySlot(EquipmentSlotType equipmentSlotType) {
        return clientPlayerEntity.getItemBySlot(equipmentSlotType);
    }

    @Override
    public ItemStack getMainHandItem() {
        return clientPlayerEntity.getMainHandItem();
    }

    public void clonePoseFrom(ClientPlayerEntity p){
        this.yBodyRotO = p.yBodyRotO;
        this.yBodyRot = p.yBodyRot;
        this.yHeadRot = p.yHeadRot;
        this.yHeadRotO = p.yHeadRotO;
        this.xRot = p.xRot;
        this.yRot = p.yRot;
        this.xRotO = p.xRotO;
        this.yRotO = p.yRotO;
    }

    public void resetPos(){
        this.yBodyRotO = 0;
        this.yBodyRot = 0;
        this.yHeadRot = 0;
        this.yHeadRotO = 0;
        this.xRot = 0;
        this.yRot = 0;
        this.xRotO = 0;
        this.yRotO = 0;
    }

    public void setRotZ(float zRot) {
        setRotZ0(this.zRot);
        this.zRot = Math.min(90, Math.max(zRot, -90));
    }

    public float getRotZ() {
        return zRot;
    }

    public void setRotZ0(float zRot) {
        this.zRot0 = Math.min(90, Math.max(zRot, -90));
    }

    public float getRotZ0() {
        return zRot;
    }
}
