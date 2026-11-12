package mctbl.tinkersreborn.plugins.TConstruct.recipe;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;

public class TConstructNBTBlockConvertRecipe implements IRecipe {

    @Override
    public boolean matches(InventoryCrafting inv, World world) {
        ItemStack inputStack = null;
        int count = 0;

        for (int i = 0; i < inv.getSizeInventory(); i++) {
            ItemStack stack = inv.getStackInSlot(i);
            if (stack != null) {
                count++;
                inputStack = stack;
            }
        }

        if (count != 1) {
            return false;
        }

        return false;
    }

    @Override
    public ItemStack getCraftingResult(InventoryCrafting inv) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int getRecipeSize() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public ItemStack getRecipeOutput() {
        // TODO Auto-generated method stub
        return null;
    }

}
