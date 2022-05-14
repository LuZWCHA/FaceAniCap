package top.nowandfuture.mod.faceanicap.guis;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.recipebook.RecipeBookGui;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ToggleWidget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fml.client.gui.widget.ExtendedButton;
import top.nowandfuture.mod.faceanicap.core.CaptureManager;

import java.util.Objects;

public class SettingGui extends Screen {

    private int cameraId = 0;
    private Button cameraBtn;
    private Button toggleBtn;

    public SettingGui(ITextComponent p_i51108_1_) {
        super(p_i51108_1_);
    }

    @Override
    protected void init() {
        super.init();

    }

    @Override
    public void init(Minecraft mc, int w, int h) {
        super.init(mc, w, h);

//        boolean f = CaptureManager.getInstance().isRunning();
//
//        cameraBtn = new ExtendedButton(w / 2, 0, 20, 10, ITextComponent.nullToEmpty(f ? "off": "on"), new Button.IPressable() {
//            @Override
//            public void onPress(Button btn) {
//                Runnable runnable = CaptureManager.getInstance().start(cameraId);
//                if(runnable != null){
//                    cameraBtn.setMessage(ITextComponent.nullToEmpty("off"));
//                }else{
//                    cameraBtn.setMessage(ITextComponent.nullToEmpty("on"));
//                }
//            }
//        });
//
//        toggleBtn = new ExtendedButton(w / 2, 0, 20, 10, ITextComponent.nullToEmpty(f ? "off" : "on"), new Button.IPressable() {
//            @Override
//            public void onPress(Button p_onPress_1_) {
//
//            }
//        });
//
//        this.buttons.add(cameraBtn);
//        this.buttons.add(toggleBtn);

    }

    @Override
    public void render(MatrixStack stack, int p_230430_2_, int p_230430_3_, float p_230430_4_) {
        this.renderBackground(stack);
        super.render(stack, p_230430_2_, p_230430_3_, p_230430_4_);
    }

    @Override
    public boolean keyPressed(int p_231046_1_, int p_231046_2_, int p_231046_3_) {
        return super.keyPressed(p_231046_1_, p_231046_2_, p_231046_3_);
    }

    @Override
    public boolean mouseClicked(double p_231044_1_, double p_231044_3_, int p_231044_5_) {
        return super.mouseClicked(p_231044_1_, p_231044_3_, p_231044_5_);
    }


}
