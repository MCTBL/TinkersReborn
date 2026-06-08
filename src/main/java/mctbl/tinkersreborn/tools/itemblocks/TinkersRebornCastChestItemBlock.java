package mctbl.tinkersreborn.tools.itemblocks;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class TinkersRebornCastChestItemBlock extends ItemBlock {

    public TinkersRebornCastChestItemBlock(Block b) {
        super(b);
        this.setMaxDamage(0);
        this.setUnlocalizedName("tinkersreborn.CastChest");
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        return "tinkersreborn.CastChest";
    }
}
