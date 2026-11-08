package mctbl.tinkersreborn.plugins.TConstruct;

import net.minecraftforge.common.MinecraftForge;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import mctbl.tinkersreborn.library.ITinkersRebornModule;
import tconstruct.util.config.PHConstruct;

public class TConstructHandler implements ITinkersRebornModule {

    @Override
    public void preInit(FMLPreInitializationEvent e) {
        TConstructHandlerConfig.preInit();

        PHConstruct.naturalSlimeSpawn = 0;

        PHConstruct.generateCopper = false;
        PHConstruct.generateTin = false;
        PHConstruct.generateAluminum = false;
        PHConstruct.generateArdite = false;
        PHConstruct.generateCobalt = false;

        PHConstruct.generateIronBush = false;
        PHConstruct.generateGoldBush = false;
        PHConstruct.generateCopperBush = false;
        PHConstruct.generateTinBush = false;
        PHConstruct.generateAluminumBush = false;
        PHConstruct.generateEssenceBush = false;

        PHConstruct.generateIronSurface = false;
        PHConstruct.generateGoldSurface = false;
        PHConstruct.generateCopperSurface = false;
        PHConstruct.generateTinSurface = false;
        PHConstruct.generateAluminumSurface = false;
        PHConstruct.generateCobaltSurface = false;

        PHConstruct.addToVillages = false; // this needs change more early
        PHConstruct.allowVillagerTrading = false; // this needs change more early
        PHConstruct.slimeIslUseWhiteList = true;
        PHConstruct.cfgDimWhiteList = new int[] {};
        PHConstruct.cfgOreBushWhiteList = new int[] {};
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
