package mctbl.tinkersreborn.tools;

import net.minecraftforge.event.entity.player.ItemTooltipEvent;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import mctbl.tinkersreborn.library.tools.ToolCore;

public class TinkersRebornEvents {

    @SubscribeEvent
    public void tinkersToolTooltipEvent(ItemTooltipEvent e) {
        // use this to prevent vailnila durability display
        if (e.itemStack.getItemDamage() != 0 && e.itemStack.getItem() instanceof ToolCore) {
            for (int idx = e.toolTip.size() - 1; idx >= 0; idx--) {
                if (e.toolTip.get(idx)
                    .startsWith("Durability: ")) {
                    e.toolTip.remove(e.toolTip.get(idx));
                    break;
                }
            }
        }
    }

}
