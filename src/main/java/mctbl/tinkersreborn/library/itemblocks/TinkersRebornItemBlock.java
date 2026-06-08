package mctbl.tinkersreborn.library.itemblocks;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

import com.google.gson.annotations.SerializedName;

import mctbl.tinkersreborn.TinkersReborn;

public class TinkersRebornItemBlock extends ItemBlock {

    public String[] blockType;

    @SerializedName(value = "secondUnlocalizedName")
    public String unlocalizedName;
    public String append;

    public TinkersRebornItemBlock(Block b, String itemBlockUnlocalizedName, String[] blockTypes) {
        super(b);
        if (itemBlockUnlocalizedName.isEmpty()) this.unlocalizedName = super.getUnlocalizedName();
        else this.unlocalizedName = itemBlockUnlocalizedName;
        this.blockType = blockTypes;
        this.append = "";
    }

    public TinkersRebornItemBlock(Block b, String itemBlockUnlocalizedName, String appendToEnd, String[] blockTypes) {
        super(b);
        this.unlocalizedName = itemBlockUnlocalizedName;
        this.blockType = blockTypes;
        this.append = "." + appendToEnd;
    }

    public int getMetadata(int meta) {
        return meta;
    }

    public String getUnlocalizedName(ItemStack itemstack) {
        if (this.blockType.length == 1) {
            return this.unlocalizedName;
        }
        int pos = itemstack.getItemDamage();
        try {
            return (new StringBuilder()).append(unlocalizedName)
                .append(".")
                .append(blockType[pos])
                .append(append)
                .toString();
        } catch (ArrayIndexOutOfBoundsException ex) {
            TinkersReborn.LOG
                .warn("[MultiItemBlock] Caught array index error in getUnlocalizedName: " + ex.getMessage());
            TinkersReborn.LOG.warn("[MultiItemBlock] Returning unlocalized name: " + getUnlocalizedName());
            return getUnlocalizedName();
        }
    }
}
