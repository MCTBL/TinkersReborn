package mctbl.tinkersreborn.tools.itemblocks;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;

import mctbl.tinkersreborn.library.itemblocks.TinkersRebornItemBlock;
import mctbl.tinkersreborn.tools.blocks.PartBuilderBlock;

public class TinkersRebornPartBuilderItemBlock extends TinkersRebornItemBlock {

    public TinkersRebornPartBuilderItemBlock(Block b) {
        super(b, "tinkersreborn.PartBuilder", PartBuilderBlock.materials);
        this.setHasSubtypes(true);
        this.setMaxDamage(0);
    }

    @Override
    public String getUnlocalizedName(ItemStack itemstack) {
        return "tinkersreborn.PartBuilder";
    }
}
