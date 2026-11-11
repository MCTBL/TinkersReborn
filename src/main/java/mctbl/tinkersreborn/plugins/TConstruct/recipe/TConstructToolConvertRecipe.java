package mctbl.tinkersreborn.plugins.TConstruct.recipe;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;

import mctbl.tinkersreborn.tools.TinkersRebornTools;
import tconstruct.tools.TinkerTools;

public class TConstructToolConvertRecipe implements IRecipe {

    private Map<Item, Item> convertMap;

    public TConstructToolConvertRecipe() {
        convertMap = new HashMap<>();
        convertMap.put(TinkerTools.pickaxe, TinkersRebornTools.pickaxe);
    }

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

        return inputStack.getItem() instanceof tconstruct.library.tools.ToolCore;
    }

    @Override
    public ItemStack getCraftingResult(InventoryCrafting inv) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int getRecipeSize() {
        return 1;
    }

    @Override
    public ItemStack getRecipeOutput() {
        return null;
    }

}
