package mctbl.tinkersreborn.plugins.TConstruct;

import net.minecraftforge.common.MinecraftForge;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import mctbl.tinkersreborn.library.ITinkersRebornModule;

public class TConstructHandler implements ITinkersRebornModule {

    @Override
    public void preInit(FMLPreInitializationEvent e) {
        TConstructHandlerConfig.preInit();
    }

    @Override
    public void init(FMLInitializationEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void postInit(FMLPostInitializationEvent e) {
        final TConstructEventHandler target = new TConstructEventHandler();
        MinecraftForge.EVENT_BUS.register(target);
        FMLCommonHandler.instance()
            .bus()
            .register(target);
    }

}
