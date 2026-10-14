package mctbl.tinkersreborn.smeltery.itemblocks;

import net.minecraft.block.Block;

import mctbl.tinkersreborn.library.itemblocks.TinkersRebornItemBlock;

public class TinkerTankControllerItemBlock extends TinkersRebornItemBlock {

    public TinkerTankControllerItemBlock(Block b) {
        super(b, "tinkersreborn.TinkerTankController", new String[] { "" });
        setMaxDamage(0);
        setHasSubtypes(true);
    }

}
