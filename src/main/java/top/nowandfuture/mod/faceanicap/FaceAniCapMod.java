package top.nowandfuture.mod.faceanicap;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import top.nowandfuture.jmediapipe.Loader;
import top.nowandfuture.mod.faceanicap.core.CaptureManager;
import top.nowandfuture.mod.faceanicap.core.GraphManager;
import top.nowandfuture.mod.faceanicap.forge.KeyHandler;
import top.nowandfuture.mod.faceanicap.forge.RenderHandler;

import java.io.File;

@Mod("faceanicap")
public class FaceAniCapMod
{
    public static final Logger LOGGER = LogManager.getLogger();
    public static final String MOD_ID = "faceanicap";

    public FaceAniCapMod() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::doClientStuff);
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void setup(final FMLCommonSetupEvent event)
    {

    }

    private void doClientStuff(final FMLClientSetupEvent event) {
        File gameDirectory = Minecraft.getInstance().gameDirectory;

        try{
            Loader.loadLibs();
            Loader.copySourcesTo(gameDirectory.getAbsolutePath());
            CaptureManager.getInstance().init(gameDirectory.getAbsolutePath());
            GraphManager.INSTANCE.init(gameDirectory.getAbsolutePath());

        }catch (Exception e){
            e.printStackTrace();
        }
        MinecraftForge.EVENT_BUS.register(new KeyHandler());
        MinecraftForge.EVENT_BUS.register(new RenderHandler());
    }
}
