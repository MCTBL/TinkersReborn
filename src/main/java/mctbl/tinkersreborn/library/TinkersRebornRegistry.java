package mctbl.tinkersreborn.library;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

public class TinkersRebornRegistry {

    public static TinkersRebornRegistry instance = new TinkersRebornRegistry();

    private TinkersRebornRegistry() {}

    public static TinkersRebornCreativeTab block;
    public static TinkersRebornCreativeTab tools;
    public static TinkersRebornCreativeTab weapons;
    public static TinkersRebornCreativeTab parts;
    
    public void preInit() {
    	this.initCreativeTab();
    }

    public void initCreativeTab() {
        block = new TinkersRebornCreativeTab("TinkersRebornBlocks").init(new ItemStack(Items.cookie));
        tools = new TinkersRebornCreativeTab("TinkersRebornTools").init(new ItemStack(Items.flint_and_steel));
        weapons = new TinkersRebornCreativeTab("TinkersRebornWeapons").init(new ItemStack(Items.diamond_boots));
        parts = new TinkersRebornCreativeTab("TinkersRebornParts").init(new ItemStack(Items.bow));
    }

}
