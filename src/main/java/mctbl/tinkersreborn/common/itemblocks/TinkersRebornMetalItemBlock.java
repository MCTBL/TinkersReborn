package mctbl.tinkersreborn.common.itemblocks;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;

import mctbl.tinkersreborn.TinkersRebornConfig;
import mctbl.tinkersreborn.library.itemblocks.TinkersRebornItemBlock;

public class TinkersRebornMetalItemBlock extends TinkersRebornItemBlock {

    public TinkersRebornMetalItemBlock(Block b) {
        super(b, "tinkersreborn.StorageMetals", TinkersRebornConfig.metalTypes);
        setMaxDamage(0);
        setHasSubtypes(true);
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> list, boolean advanced) {
        list.add(StatCollector.translateToLocal("tinkersreborn.metalblock.tooltip"));
    }

}
