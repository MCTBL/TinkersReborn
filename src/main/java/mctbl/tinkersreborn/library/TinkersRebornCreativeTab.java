package mctbl.tinkersreborn.library;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class TinkersRebornCreativeTab extends CreativeTabs {

    ItemStack display;

    public TinkersRebornCreativeTab(String label) {
        super(label);
    }

    public TinkersRebornCreativeTab init(ItemStack stack) {
        display = stack;
        return this;
    }

    @Override
    public ItemStack getIconItemStack() {
        return display;
    }

    public Item getTabIconItem() {
        return display.getItem();
    }

}
