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
        super(b, "tinkersreborn.MultiBlock", new String[] { "Cobblestone", "Brick" });
        setMaxDamage(0);
        setHasSubtypes(true);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> list, boolean advanced) {
        // TODO
    }

    @Override
    public void onCreated(ItemStack item, World world, EntityPlayer player) {
        // TAchievements.triggerAchievement(player, "tconstruct.smelteryMaker");
    }

}
