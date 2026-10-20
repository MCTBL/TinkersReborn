package mctbl.tinkersreborn.common.itemblocks;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;

import mctbl.tinkersreborn.TinkersRebornConfig;
import mctbl.tinkersreborn.library.itemblocks.TinkersRebornItemBlock;

public class SlimeTallGrassItem extends TinkersRebornItemBlock {

    public SlimeTallGrassItem(Block b) {
        super(b, "tinkersreborn.slime.grass.tall", TinkersRebornConfig.slimeTypes);
        this.setMaxDamage(0);
        this.setHasSubtypes(true);
    }

    @Override
    public int getColorFromItemStack(ItemStack stack, int renderPass) {
        return TinkersRebornConfig.slimeColors[stack.getItemDamage() & 3];
    }

    @Override
    public String getUnlocalizedName(ItemStack itemstack) {
        return this.unlocalizedName;
    }
}
