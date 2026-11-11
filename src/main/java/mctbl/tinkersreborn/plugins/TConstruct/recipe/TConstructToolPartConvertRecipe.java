package mctbl.tinkersreborn.plugins.TConstruct.recipe;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;

import com.google.common.base.CaseFormat;

import mctbl.tinkersreborn.library.TinkersRebornRegistry;
import mctbl.tinkersreborn.library.materials.TinkersRebornMaterial;
import mctbl.tinkersreborn.tools.items.TinkersRebornToolPart;
import tconstruct.library.TConstructRegistry;
import tconstruct.library.tools.ToolMaterial;

public class TConstructToolPartConvertRecipe implements IRecipe {

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

        return inputStack.getItem() instanceof tconstruct.library.tools.DynamicToolPart;
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
        if (inputStack.getItem() instanceof tconstruct.library.tools.DynamicToolPart ticPart) {
            ToolMaterial ticMaterial = TConstructRegistry.toolMaterials.get(ticPart.getMaterialID(inputStack));
            if (ticMaterial != null) {
                TinkersRebornMaterial tinkersRebornMaterial = TinkersRebornRegistry
                    .getMaterialByIdentifier(ticMaterial.materialName.toLowerCase());
                TinkersRebornToolPart toolPartByPartName = TinkersRebornRegistry
                    .getToolPartByPartName(CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, ticPart.partName));
                if (tinkersRebornMaterial != null && toolPartByPartName != null) {
                    return toolPartByPartName.getNewPartWithMaterial(tinkersRebornMaterial);
                }
            }
        }

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
