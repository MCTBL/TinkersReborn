package mctbl.tinkersreborn.tools.traits;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.BlockEvent;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import mctbl.tinkersreborn.library.tools.traits.AbstractTrait;
import mctbl.tinkersreborn.util.ToolTagsHelper;

public class TraitEstablished extends AbstractTrait {

    public TraitEstablished() {
        super("established", 0xffffff);

        MinecraftForge.EVENT_BUS.register(this);
    }

    // @SubscribeEvent
    // public void onXpDrop(LivingDropsEvent event) {
    // for (EntityItem item : event.drops) {
    // LOG.info(
    // "name {} * {}",
    // item.getEntityItem()
    // .getDisplayName(),
    // item.getEntityItem().stackSize);
    // }
    // }

    @SubscribeEvent
    public void onBlockBreak(BlockEvent.BreakEvent event) {
        EntityPlayer player = event.getPlayer();
        if (player != null) {
            if (ToolTagsHelper.hasModifier(player.getHeldItem(), identifier)) {
                float r = random.nextFloat();
                int expToDrop = event.getExpToDrop();
                // 30% chance for 1 bonus xp
                if (r < 0.33f || (expToDrop == 0 && r < 0.03f)) {
                    // LOG.info("Nice !TraitEstablished gives you extra EXP!");
                    event.setExpToDrop(expToDrop + 1);
                }
            }
        }
    }
}
