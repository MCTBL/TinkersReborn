package mctbl.tinkersreborn.smeltery.itemblocks;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mctbl.tinkersreborn.library.itemblocks.TinkersRebornItemBlock;

public class SmelteryItemBlock extends TinkersRebornItemBlock {

    public SmelteryItemBlock(Block b) {
        super(b, "tinkersreborn.Smeltery", new String[] { "Cobblestone", "Brick", "Controller", "Furnace", "Drain" });
        setMaxDamage(0);
        setHasSubtypes(true);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> list, boolean advanced) {
        // TODO
        // switch (stack.getItemDamage()) {
        // case 0:
        // list.add(StatCollector.translateToLocal("smeltery.controller.tooltip"));
        // break;
        // case 1:
        // list.add(StatCollector.translateToLocal("smeltery.drain.tooltip1"));
        // list.add(StatCollector.translateToLocal("smeltery.drain.tooltip2"));
        // break;
        // case 3:
        // list.add(StatCollector.translateToLocal("smeltery.furnace.tooltip"));
        // break;
        // default:
        // list.add(StatCollector.translateToLocal("smeltery.brick.tooltip1"));
        // list.add(StatCollector.translateToLocal("smeltery.brick.tooltip2"));
        // break;
        // }
    }

    @Override
    public void onCreated(ItemStack item, World world, EntityPlayer player) {
        // TAchievements.triggerAchievement(player, "tconstruct.smelteryMaker");
    }

}
