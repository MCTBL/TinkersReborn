package mctbl.tinkersreborn.plugins.TConstruct.recipe;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;

import mctbl.tinkersreborn.library.TinkersRebornRegistry;
import mctbl.tinkersreborn.library.materials.MaterialStatusType;
import mctbl.tinkersreborn.library.materials.TinkersRebornMaterial;
import mctbl.tinkersreborn.tools.TinkersRebornTools;
import mctbl.tinkersreborn.tools.items.BoltCore;
import mctbl.tinkersreborn.tools.items.TinkersRebornToolPart;
import tconstruct.library.TConstructRegistry;
import tconstruct.library.tools.DualMaterialToolPart;
import tconstruct.library.tools.ToolMaterial;
import tconstruct.tools.TinkerTools;
import tconstruct.weaponry.TinkerWeaponry;

public class TConstructToolPartConvertRecipe implements IRecipe {

    private static final Map<Item, TinkersRebornToolPart> convertMap = new HashMap<>();

    public TConstructToolPartConvertRecipe() {
        convertMap.put(TinkerTools.toolRod, TinkersRebornTools.rod);
        convertMap.put(TinkerTools.toolShard, TinkersRebornTools.shard);
        convertMap.put(TinkerTools.pickaxeHead, TinkersRebornTools.pickaxeHead);
        convertMap.put(TinkerTools.shovelHead, TinkersRebornTools.shovelHead);
        convertMap.put(TinkerTools.hatchetHead, TinkersRebornTools.axeHead);
        convertMap.put(TinkerTools.binding, TinkersRebornTools.binding);
        convertMap.put(TinkerTools.toughBinding, TinkersRebornTools.toughbind);
        convertMap.put(TinkerTools.toughRod, TinkersRebornTools.toughrod);
        convertMap.put(TinkerTools.largePlate, TinkersRebornTools.largeplate);
        convertMap.put(TinkerTools.swordBlade, TinkersRebornTools.swordBlade);
        convertMap.put(TinkerTools.wideGuard, TinkersRebornTools.largeGuard);
        convertMap.put(TinkerTools.handGuard, TinkersRebornTools.mediumGuard);
        convertMap.put(TinkerTools.crossbar, TinkersRebornTools.crossbar);
        convertMap.put(TinkerTools.knifeBlade, TinkersRebornTools.knifeBlade);
        convertMap.put(TinkerTools.fullGuard, TinkersRebornTools.fullGuard);
        convertMap.put(TinkerTools.frypanHead, TinkersRebornTools.frypanHead);
        convertMap.put(TinkerTools.signHead, TinkersRebornTools.shieldCore);
        convertMap.put(TinkerTools.chiselHead, TinkersRebornTools.chiselHead);
        convertMap.put(TinkerTools.scytheBlade, TinkersRebornTools.scytheHead);
        convertMap.put(TinkerTools.broadAxeHead, TinkersRebornTools.lumberaxeHead);
        convertMap.put(TinkerTools.excavatorHead, TinkersRebornTools.excavatorHead);
        convertMap.put(TinkerTools.largeSwordBlade, TinkersRebornTools.largeSwordBlade);
        convertMap.put(TinkerTools.hammerHead, TinkersRebornTools.hammerHead);
        convertMap.put(TinkerWeaponry.bowstring, TinkersRebornTools.bowString);
        convertMap.put(TinkerWeaponry.arrowhead, TinkersRebornTools.arrowhead);
        convertMap.put(TinkerWeaponry.fletching, TinkersRebornTools.fletching);
        convertMap.put(TinkerWeaponry.partShuriken, TinkersRebornTools.shuriken);
        convertMap.put(TinkerWeaponry.partArrowShaft, TinkersRebornTools.arrowShaft);
        convertMap.put(TinkerWeaponry.partBowLimb, TinkersRebornTools.bowLimb);
        convertMap.put(TinkerWeaponry.partCrossbowLimb, TinkersRebornTools.crossbowLimb);
        convertMap.put(TinkerWeaponry.partCrossbowBody, TinkersRebornTools.crossbowBody);
        convertMap.put(TinkerWeaponry.partBolt, TinkersRebornTools.boltCore);
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

        return convertMap.containsKey(inputStack.getItem());
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

        return getConvertTinkersRebornItemStack(inputStack);
    }

    @Nullable
    public static ItemStack getConvertTinkersRebornItemStack(ItemStack inputStack) {
        TinkersRebornToolPart toolPartByPartName = convertMap.get(inputStack.getItem());
        if (convertMap.containsKey(inputStack.getItem()) && toolPartByPartName != null
            && inputStack.getItem() instanceof tconstruct.library.util.IToolPart ticPart) {
            TinkersRebornMaterial tinkersRebornMaterial = TinkersRebornMaterial.UNKNOWN;

            if (ticPart == TinkerWeaponry.bowstring || ticPart == TinkerWeaponry.fletching) {
                tinkersRebornMaterial = processTConstructCustomMaterial(inputStack);
            } else {
                ToolMaterial ticMaterial = TConstructRegistry.toolMaterials.get(ticPart.getMaterialID(inputStack));
                if (ticMaterial != null) {
                    tinkersRebornMaterial = TinkersRebornRegistry
                        .getMaterialByIdentifier(ticMaterial.materialName.toLowerCase());
                    if (ticPart instanceof DualMaterialToolPart tcBoltCore
                        && toolPartByPartName instanceof BoltCore boltCore) {
                        // 2 is head
                        TinkersRebornMaterial tinkersRebornMaterial2 = TinkersRebornRegistry.getMaterialByIdentifier(
                            TConstructRegistry.toolMaterials.get(tcBoltCore.getMaterialID2(inputStack)).materialName
                                .toLowerCase());
                        tinkersRebornMaterial = tinkersRebornMaterial.hasStats(MaterialStatusType.SHAFT)
                            ? tinkersRebornMaterial
                            : TinkersRebornTools.woodMaterial;
                        return boltCore.getNewPartWithMaterial(tinkersRebornMaterial2, tinkersRebornMaterial);
                    }
                }
            }

            return toolPartByPartName.getNewPartWithMaterial(tinkersRebornMaterial);
        }
        return null;
    }

    /**
     * special process for fletching and bow string
     * 
     * @param inputStack
     * @return
     */
    private static TinkersRebornMaterial processTConstructCustomMaterial(ItemStack inputStack) {
        String tinkersRebornMaterialName = "";
        final Item toolItem = inputStack.getItem();
        if (toolItem instanceof tconstruct.tools.items.Bowstring) {
            tinkersRebornMaterialName = "string";
        } else if (toolItem instanceof tconstruct.tools.items.Fletching) {
            tinkersRebornMaterialName = switch (inputStack.getItemDamage()) {
                case 1 -> "leaf";
                case 4 -> "slimeleaf";
                default -> "feather";
            };
        }
        return TinkersRebornRegistry.getMaterialByIdentifier(tinkersRebornMaterialName);
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
