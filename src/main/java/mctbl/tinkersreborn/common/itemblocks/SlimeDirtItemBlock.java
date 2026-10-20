package mctbl.tinkersreborn.common.itemblocks;

import mctbl.tinkersreborn.TinkersRebornConfig;
import mctbl.tinkersreborn.library.itemblocks.TinkersRebornItemBlock;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;

public class SlimeDirtItemBlock extends TinkersRebornItemBlock {

    public SlimeDirtItemBlock(Block b) {
        super(b, "tinkersreborn.slime.dirt", TinkersRebornConfig.slimeTypes);
        this.setMaxDamage(0);
        this.setHasSubtypes(true);
    }

    @Override
    public String getUnlocalizedName(ItemStack itemstack) {
        return this.unlocalizedName;
    }
}
