package mctbl.tinkersreborn.library.utils;

import net.minecraft.block.Block;

public class BlockReplacement {

    public final Block target;

    public final int metadata;

    /**
     * @param target
     * @param metadata
     */
    public BlockReplacement(Block target, int metadata) {
        this.target = target;
        this.metadata = metadata;
    }

}
