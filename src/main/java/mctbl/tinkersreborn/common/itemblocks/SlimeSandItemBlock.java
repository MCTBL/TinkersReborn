package mctbl.tinkersreborn.common.itemblocks;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import mctbl.tinkersreborn.TinkersRebornConfig;
import mctbl.tinkersreborn.library.itemblocks.TinkersRebornItemBlock;

public class SlimeSandItemBlock extends TinkersRebornItemBlock {

    public SlimeSandItemBlock(Block b) {
        super(b, "tinkersreborn.slime.sand", TinkersRebornConfig.slimeTypes);
        this.setMaxDamage(0);
        this.setHasSubtypes(true);
    }

    @Override
    public String getUnlocalizedName(ItemStack itemstack) {
        return this.unlocalizedName;
    }
}
