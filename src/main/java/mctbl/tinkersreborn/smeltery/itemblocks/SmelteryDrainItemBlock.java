package mctbl.tinkersreborn.smeltery.itemblocks;

import net.minecraft.block.Block;

import mctbl.tinkersreborn.library.itemblocks.TinkersRebornItemBlock;

public class SmelteryDrainItemBlock extends TinkersRebornItemBlock {

    public SmelteryDrainItemBlock(Block b) {
        super(b, "tinkersreborn.Drain", new String[] { "" });
        setMaxDamage(0);
        setHasSubtypes(true);
    }
}
