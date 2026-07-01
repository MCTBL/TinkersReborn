package mctbl.tinkersreborn.smeltery.itemblocks;

import net.minecraft.block.Block;

import mctbl.tinkersreborn.library.itemblocks.TinkersRebornItemBlock;

public class FurnaceControllerItemBlock extends TinkersRebornItemBlock {

    public FurnaceControllerItemBlock(Block b) {
        super(b, "tinkersreborn.FurnaceController", new String[] { "" });
        setMaxDamage(0);
        setHasSubtypes(true);
    }

}
