package mctbl.tinkersreborn.common.itemblocks;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

import mctbl.tinkersreborn.TinkersRebornConfig;
import mctbl.tinkersreborn.library.itemblocks.TinkersRebornItemBlock;

public class SlimeSaplingItemBlock extends TinkersRebornItemBlock {

    private final Block b;

    public SlimeSaplingItemBlock(Block b) {
        super(b, "tinkersreborn.slime.sapling", TinkersRebornConfig.slimeTypes);
        this.b = b;
        this.setMaxDamage(0);
        this.setHasSubtypes(true);
    }

    @Override
    public IIcon getIconFromDamage(int meta) {
        return b.getIcon(0, meta);
    }

    @Override
    public String getUnlocalizedName(ItemStack itemstack) {
        return this.unlocalizedName;
    }
}
