package mctbl.tinkersreborn.plugins.waila;

import mcp.mobius.waila.api.IWailaRegistrar;
import mctbl.tinkersreborn.common.entity.DryingRackLogic;
import mctbl.tinkersreborn.smeltery.blocks.LavaTankBlock;
import mctbl.tinkersreborn.smeltery.entity.CastingBasinLogic;
import mctbl.tinkersreborn.smeltery.entity.CastingChannelLogic;
import mctbl.tinkersreborn.smeltery.entity.CastingTableLogic;
import mctbl.tinkersreborn.smeltery.entity.SmelteryLogic;

public class WailaRegistrar {

    public static void wailaCallback(IWailaRegistrar registrar) {
        String modName = "Tinkers Reborn";

        // Configs
        registrar.addConfig(modName, SearedTankDataProvider.CONFIG_KEY);
        registrar.addConfig(modName, CastingChannelDataProvider.CONFIG_KEY);
        registrar.addConfig(modName, BasinDataProvider.CONFIG_KEY);
        registrar.addConfig(modName, TableDataProvider.CONFIG_KEY);
        registrar.addConfig(modName, SmelteryDataProvider.CONFIG_KEY);
        registrar.addConfig(modName, DryingRackDataProvider.CONFIG_KEY);

        registrar.registerBodyProvider(new SearedTankDataProvider(), LavaTankBlock.class);
        registrar.registerBodyProvider(new CastingChannelDataProvider(), CastingChannelLogic.class);
        registrar.registerBodyProvider(new BasinDataProvider(), CastingBasinLogic.class);
        registrar.registerBodyProvider(new TableDataProvider(), CastingTableLogic.class);
        registrar.registerBodyProvider(new SmelteryDataProvider(), SmelteryLogic.class);

        DryingRackDataProvider dryingRackDataProvider = new DryingRackDataProvider();
        registrar.registerBodyProvider(dryingRackDataProvider, DryingRackLogic.class);
        registrar.registerNBTProvider(dryingRackDataProvider, DryingRackLogic.class);
    }

}
