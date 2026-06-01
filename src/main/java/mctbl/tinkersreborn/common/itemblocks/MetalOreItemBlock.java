package mctbl.tinkersreborn.common.itemblocks;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import mctbl.tinkersreborn.TinkersRebornConfig;
import mctbl.tinkersreborn.library.itemblocks.TinkersRebornItemBlock;

public class MetalOreItemBlock extends TinkersRebornItemBlock {

    public MetalOreItemBlock(Block b) {
        super(b, "tinkersreborn.MetalOre", TinkersRebornConfig.oreTypes);
        setMaxDamage(0);
        setHasSubtypes(true);
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> list, boolean advanced) {
        if (stack.hasTagCompound()) {
            NBTTagCompound liquidTag = stack.getTagCompound()
                .getCompoundTag("Liquid");
            if (liquidTag != null) {
                list.add("Contains " + liquidTag.getString("LiquidName"));
                list.add(liquidTag.getInteger("Amount") + " mB");
            }
        }
    }
}
