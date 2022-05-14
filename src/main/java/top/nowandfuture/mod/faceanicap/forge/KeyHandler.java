package top.nowandfuture.mod.faceanicap.forge;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import org.lwjgl.glfw.GLFW;
import top.nowandfuture.mod.faceanicap.core.CaptureManager;
import top.nowandfuture.mod.faceanicap.guis.SettingGui;

public class KeyHandler {

    private final KeyBinding keyBinding = new KeyBinding("toggle", GLFW.GLFW_KEY_I, "");

    public KeyHandler(){
        ClientRegistry.registerKeyBinding(keyBinding);
    }

    @SubscribeEvent
    public void clientTick(TickEvent.ClientTickEvent tickEvent){
        if(tickEvent.phase == TickEvent.Phase.START){
            if(keyBinding.consumeClick()){
//                if(!CaptureManager.getInstance().isRunning()) {
//                    CaptureManager.getInstance().start(0);
//                } else{
//                    CaptureManager.getInstance().stop();
//                }

                Minecraft.getInstance().setScreen(new SettingGui(ITextComponent.nullToEmpty("Camera Setting")));
            }
        }
    }
}
