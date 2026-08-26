package mctbl.tinkersreborn.plugins.waila;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLInterModComms;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import mctbl.tinkersreborn.library.ITinkersRebornModule;

public class TinkersRebornWaila implements ITinkersRebornModule {

    @Override
    public void preInit(FMLPreInitializationEvent e) {
        // nothing to do
    }

    @Override
    public void init(FMLInitializationEvent e) {
        FMLInterModComms
            .sendMessage("Waila", "register", "mctbl.tinkersreborn.plugins.waila.WailaRegistrar.wailaCallback");
    }

    @Override
    public void postInit(FMLPostInitializationEvent e) {
        // nothing to do
    }

}
