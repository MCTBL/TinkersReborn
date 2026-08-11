package mctbl.tinkersreborn.tools.events;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.world.BlockEvent;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import mctbl.tinkersreborn.TinkersRebornConfig;
import mctbl.tinkersreborn.library.entity.TinkersEntityProperties;
import mctbl.tinkersreborn.library.event.TinkerToolEvent;
import mctbl.tinkersreborn.library.tools.ToolCore;
import mctbl.tinkersreborn.library.tools.leveling.ToolLevelingHelper;
import mctbl.tinkersreborn.tools.traits.TraitSpiky;
import mctbl.tinkersreborn.util.ToolTagsHelper;

public class TinkersRebornToolsEventsHandler {

    @SubscribeEvent
    public void tinkersToolTooltipEvent(ItemTooltipEvent e) {
        if (e.itemStack.getItem() instanceof ToolCore && e.itemStack.getItemDamage() != 0) {
            // use this to prevent vailnila durability display

            for (int idx = e.toolTip.size() - 1; idx >= 0; idx--) {
                if (e.toolTip.get(idx)
                    .startsWith("Durability: ")) {
                    e.toolTip.remove(e.toolTip.get(idx));
                    break;
                }
            }

        }
    }

    @SubscribeEvent
    public void onLivingUpdate(LivingEvent.LivingUpdateEvent event) {
        if (event.entityLiving.worldObj.isRemote) return;

        TinkersEntityProperties props = TinkersEntityProperties.getProps(event.entityLiving);
        if (props != null) {
            props.tick();
        }
    }

    @SubscribeEvent
    public void onEntityConstructing(EntityEvent.EntityConstructing event) {
        if (event.entity instanceof EntityLivingBase) {
            event.entity.registerExtendedProperties(TinkersEntityProperties.IDENTIFIER, new TinkersEntityProperties());
        }
    }

    @SubscribeEvent
    public void mineSpeed(PlayerEvent.BreakSpeed event) {
        if (event.entityPlayer == null) return;

        if (event.block == null || event.block == Blocks.air) return;

        // The tool does NOT require a tool, but has a harvest level for a tool set
        // we now manually check if this requiremnet is fulfilled
        String toolStr = event.block.getHarvestTool(event.metadata);
        ItemStack tool = event.entityPlayer.getCurrentEquippedItem();

        Block block = event.block;
        int hlvl = event.block.getHarvestLevel(event.metadata);

        // does the block require a tool?
        // tool requires a harvest level, but does the material require a tool?
        // if tool harvestlevel less then level needed
        if (hlvl > 0 && block.getMaterial()
            .isToolNotRequired()
            && tool != null
            && tool.getItem() != null
            && tool.getItem()
                .getHarvestLevel(tool, toolStr) < hlvl) {
            event.setCanceled(true);
            return;
        }

        if (isTool(tool) && !ToolTagsHelper.isBroken(tool)) {
            ToolTagsHelper.getTraitsOrdered(tool)
                .forEach(trait -> trait.miningSpeed(tool, event));
        }
    }

    @SubscribeEvent
    public void blockBreak(BlockEvent.BreakEvent event) {
        ItemStack tool = event.getPlayer().inventory.getCurrentItem();

        if (isTool(tool) && !ToolTagsHelper.isBroken(tool)) {
            ToolTagsHelper.getTraitsOrdered(tool)
                .forEach(trait -> trait.beforeBlockBreak(tool, event));
        }
    }

    @SubscribeEvent
    public void blockDropEvent(BlockEvent.HarvestDropsEvent event) {
        if (event.harvester == null) {
            return;
        }
        ItemStack tool = event.harvester.getHeldItem();

        if (isTool(tool) && !ToolTagsHelper.isBroken(tool)) {
            ToolTagsHelper.getTraitsOrdered(tool)
                .forEach(trait -> trait.blockHarvestDrops(tool, event));
        }
    }

    /**
     * Handles the onBlock or the onPlayerHurt trait callback. Note that only one of the two is called!
     * 
     * And will calc tool leveling logic
     */
    @SubscribeEvent
    public void playerBlockOrHurtEvent(LivingHurtEvent event) {
        if (TinkersRebornConfig.toolLevelingEnable) {
            ToolLevelingHelper.onHurt(event);
        }

        boolean isPlayerGettingDamaged = event.entityLiving instanceof EntityPlayer;
        boolean isClient = event.entityLiving.worldObj.isRemote;
        boolean isReflectedDamage = TraitSpiky.isThornsDamage(event.source);

        if (!isPlayerGettingDamaged || isClient || isReflectedDamage) {
            return;
        }
        final EntityPlayer player = (EntityPlayer) event.entityLiving;
        Entity attacker = event.source.getSourceOfDamage();

        ItemStack tool = player.isUsingItem() ? player.getCurrentEquippedItem() : null;
        if (isTool(tool) && !ToolTagsHelper.isBroken(tool)) {
            if (player.isBlocking() && !event.isCanceled()) {
                ToolTagsHelper.getTraitsOrdered(tool)
                    .forEach(trait -> trait.onBlock(tool, player, event));
            } else if (attacker instanceof EntityLivingBase attackerLiving && !attacker.isDead && !event.isCanceled()) {
                // else handle living hurt
                ToolTagsHelper.getTraitsOrdered(tool)
                    .forEach(trait -> trait.onPlayerHurt(tool, player, attackerLiving, event));
            }
        }
    }

    @SubscribeEvent
    public void onRepair(TinkerToolEvent.OnRepair event) {
        ItemStack tool = event.itemStack;

        ToolTagsHelper.getTraitsOrdered(tool)
            .forEach(trait -> trait.onRepair(tool, event.amount));
    }

    private boolean isTool(ItemStack stack) {
        return stack != null && stack.getItem() instanceof ToolCore;
    }

}
