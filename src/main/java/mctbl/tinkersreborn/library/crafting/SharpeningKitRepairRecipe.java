package mctbl.tinkersreborn.library.crafting;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;
import net.minecraftforge.oredict.RecipeSorter;

import mctbl.tinkersreborn.library.tools.IRepairable;
import mctbl.tinkersreborn.library.tools.ToolCore;
import mctbl.tinkersreborn.tools.items.SharpeningKit;
import mctbl.tinkersreborn.util.TinkersRebornUtils;

public class SharpeningKitRepairRecipe implements IRecipe {

    static {
        RecipeSorter.register(
            "tinkersreborn:sharpening_kit_repair",
            SharpeningKitRepairRecipe.class,
            RecipeSorter.Category.SHAPELESS,
            "after:minecraft:shapeless");
    }

    @Override
    public boolean matches(InventoryCrafting inventory, World world) {
        return getResult(inventory) != null;
    }

    @Override
    public ItemStack getCraftingResult(InventoryCrafting inventory) {
        return getResult(inventory);
    }

    private ItemStack getResult(InventoryCrafting inventory) {
        ItemStack tool = null;
        List<ItemStack> repairItems = new ArrayList<>();

        for (int i = 0; i < inventory.getSizeInventory(); i++) {
            ItemStack stack = inventory.getStackInSlot(i);

            if (TinkersRebornUtils.isStackEmpty(stack)) {
                repairItems.add(null);
                continue;
            }

            if (stack.getItem() instanceof ToolCore && stack.getItem() instanceof IRepairable) {

                if (tool != null || stack.stackSize != 1) {
                    return null;
                }

                tool = stack;
                repairItems.add(null);
                continue;
            }

            if (stack.getItem() instanceof SharpeningKit) {
                ItemStack kit = stack.copy();

                kit.stackSize = 1;
                repairItems.add(kit);
                continue;
            }

            return null;
        }

        if (tool == null) {
            return null;
        }

        int kitCount = 0;
        for (ItemStack stack : repairItems) {
            if (!TinkersRebornUtils.isStackEmpty(stack)) {
                kitCount++;
            }
        }

        if (kitCount != 1) {
            return null;
        }

        return ToolBuilderHelper.tryRepairTool(repairItems, tool, false);
    }

    @Override
    public int getRecipeSize() {
        return 2;
    }

    @Override
    public ItemStack getRecipeOutput() {
        return null;
    }
}
