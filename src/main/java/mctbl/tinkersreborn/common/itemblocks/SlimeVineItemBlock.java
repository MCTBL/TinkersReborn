package mctbl.tinkersreborn.common.itemblocks;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;

import mctbl.tinkersreborn.TinkersRebornConfig;
import mctbl.tinkersreborn.library.itemblocks.TinkersRebornItemBlock;

public class SlimeVineItemBlock extends TinkersRebornItemBlock {

    private int color;

    public SlimeVineItemBlock(Block b, Integer type) {
        super(b, "tinkersreborn.slime.vine", new String[] { TinkersRebornConfig.slimeTypes[type] });
        setMaxDamage(0);
        setHasSubtypes(true);
        this.color = TinkersRebornConfig.slimeColors[type];
    }

    @Override
    public int getColorFromItemStack(ItemStack stack, int what) {
        return this.color;
    }
}
