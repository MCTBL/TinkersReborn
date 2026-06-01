package mctbl.tinkersreborn.smeltery.itemblocks;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import mctbl.tinkersreborn.library.itemblocks.TinkersRebornItemBlock;

public class SearedTableItemBlock extends TinkersRebornItemBlock {

    public static final String[] blockTypes = { "Table", "Faucet", "Basin" };

    public SearedTableItemBlock(Block b) {
        super(b, "tinkersreborn.SearedBlock", blockTypes);
        this.setMaxDamage(0);
        this.setHasSubtypes(true);
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> list, boolean advanced) {

    }
}
