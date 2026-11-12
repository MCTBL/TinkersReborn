package mctbl.tinkersreborn.plugins.TConstruct.recipe;

import javax.annotation.Nullable;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;

import mctbl.tinkersreborn.tools.items.Pattern;

public class TConstructPatternConvertRecipe implements IRecipe {

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

        return inputStack.getItem() instanceof tconstruct.tools.items.Pattern;
    }

    @Override
    public ItemStack getCraftingResult(InventoryCrafting inv) {
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
            return null;
        }

        return convertTConstructPattern(inputStack);
    }

    @Override
    public int getRecipeSize() {
        return 1;
    }

    @Override
    public ItemStack getRecipeOutput() {
        return null;
    }

    @Nullable
    public static ItemStack convertTConstructPattern(ItemStack tcPattern) {
        final Item item = tcPattern.getItem();
        if (item == null) return null;

        if (item.getClass() == tconstruct.tools.items.Pattern.class
            || item.getClass() == tconstruct.smeltery.items.ClayPattern.class) {
            return Pattern.newStackWithIdentifier(Pattern.PATTERN_BLANK);
        }

        return null;
    }

}
