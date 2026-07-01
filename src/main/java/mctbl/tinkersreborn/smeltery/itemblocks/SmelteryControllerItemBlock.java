package mctbl.tinkersreborn.smeltery.itemblocks;

import net.minecraft.block.Block;

import mctbl.tinkersreborn.library.itemblocks.TinkersRebornItemBlock;

public class SmelteryControllerItemBlock extends TinkersRebornItemBlock {

    public SmelteryControllerItemBlock(Block b) {
        super(b, "tinkersreborn.SmelteryController", new String[] { "" });
        setMaxDamage(0);
        setHasSubtypes(true);
    }

}
