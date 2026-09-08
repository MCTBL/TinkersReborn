package mctbl.tinkersreborn.smeltery.itemblocks;

import net.minecraft.block.Block;
import net.minecraft.item.ItemDye;

import mctbl.tinkersreborn.library.itemblocks.TinkersRebornItemBlock;

public class ColoredGlassItemBlock extends TinkersRebornItemBlock {

    public ColoredGlassItemBlock(Block b) {
        super(b, "tinkersreborn.glass", ItemDye.field_150921_b);
        setMaxDamage(0);
        setHasSubtypes(true);
    }

}
