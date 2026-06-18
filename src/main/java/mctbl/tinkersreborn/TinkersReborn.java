package mctbl.tinkersreborn;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import mctbl.tinkersreborn.common.TinkersRebornGeneral;
import mctbl.tinkersreborn.common.network.AbstractPacketThreadsafe;
import mctbl.tinkersreborn.library.ITinkersRebornModule;
import mctbl.tinkersreborn.library.TinkersRebornRegistry;
import mctbl.tinkersreborn.smeltery.TinkersRebornSmeltery;
import mctbl.tinkersreborn.tools.TinkersRebornTools;

@Mod(
    modid = TinkersReborn.MODID,
    version = Tags.VERSION,
    name = "TinkersReborn",
    acceptedMinecraftVersions = "[1.7.10]",
    dependencies = "required-after:Forge@[10.13.3.1384,11.14);" + "after:NotEnoughItems;" + "after:Waila;")
public class TinkersReborn {

    public static final String MODID = "tinkersreborn";
    public static final String VERSION = "1.0";
    public static final Logger LOG = LogManager.getLogger(MODID);
    public static final Random random = new Random();

    @Instance(MODID)
    public static TinkersReborn instance;

    @SidedProxy(clientSide = "mctbl.tinkersreborn.ClientProxy", serverSide = "mctbl.tinkersreborn.CommonProxy")
    public static CommonProxy proxy;

    public static final List<ITinkersRebornModule> l = new ArrayList<>();

    public TinkersReborn() {
        l.add(new TinkersRebornGeneral());
        l.add(new TinkersRebornTools());
        l.add(new TinkersRebornSmeltery());
    }

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        TinkersRebornConfig.setupConfig(event.getModConfigurationDirectory());
        TinkersRebornRegistry.instance.preInit();
        AbstractPacketThreadsafe.init();

        NetworkRegistry.INSTANCE.registerGuiHandler(instance, proxy);

        l.forEach(m -> m.preInit(event));
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {

        l.forEach(m -> m.init(event));
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {

        l.forEach(m -> m.postInit(event));
    }
}
