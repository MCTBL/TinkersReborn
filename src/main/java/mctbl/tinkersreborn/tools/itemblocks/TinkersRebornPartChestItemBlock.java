package mctbl.tinkersreborn.tools.itemblocks;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class TinkersRebornPartChestItemBlock extends ItemBlock {

    public TinkersRebornPartChestItemBlock(Block b) {
        super(b);
        this.setMaxDamage(0);
        this.setUnlocalizedName("tinkersreborn.PartChest");
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        return "tinkersreborn.PartChest";
    }

}
