package mctbl.tinkersreborn.tools.traits;

import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.common.MinecraftForge;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import mctbl.tinkersreborn.library.event.TinkersRebornEvent;
import mctbl.tinkersreborn.library.tools.traits.AbstractTrait;
import mctbl.tinkersreborn.util.ToolTagsHelper;

public class TraitCheapskate extends AbstractTrait {

    public TraitCheapskate() {
        super("cheapskate", EnumChatFormatting.GRAY);
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onToolBuilding(TinkersRebornEvent.OnItemBuilding event) {
        if (ToolTagsHelper.hasModifier(event.tag, this.getIdentifier())) {
            int oldDurability = ToolTagsHelper.getDurabilityStat(event.tag);
            // reduce durability by 20%
            int newDurability = Math.max(1, (oldDurability * 80) / 100);
            ToolTagsHelper.setDurabilityStat(event.tag, newDurability);

            // TinkersReborn.LOG.info("Before TraitCheapskate is {}, after is {}", oldDurability, newDurability);
        }
    }
}
