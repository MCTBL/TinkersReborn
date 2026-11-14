package mctbl.tinkersreborn.plugins.TConstruct.recipe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import mctbl.tinkersreborn.TinkersRebornConfig;
import mctbl.tinkersreborn.library.TinkersRebornRegistry;
import mctbl.tinkersreborn.library.crafting.ToolBuilderHelper;
import mctbl.tinkersreborn.library.materials.TinkersRebornMaterial;
import mctbl.tinkersreborn.library.tools.ToolCore;
import mctbl.tinkersreborn.library.tools.leveling.ToolLevelingHelper;
import mctbl.tinkersreborn.tools.TinkersRebornTools;
import mctbl.tinkersreborn.util.ToolTagsHelper;
import tconstruct.library.TConstructRegistry;
import tconstruct.library.tools.ToolMaterial;
import tconstruct.tools.TinkerTools;
import tconstruct.weaponry.TinkerWeaponry;

public class TConstructToolConvertRecipe implements IRecipe {

    private final String[] NBTNameList = new String[] { "Head", "Handle", "Accessory", "Extra" };

    private Map<Item, ToolCore> convertMap;

    public TConstructToolConvertRecipe() {
        convertMap = new HashMap<>();
        convertMap.put(TinkerTools.pickaxe, TinkersRebornTools.pickaxe);
        convertMap.put(TinkerTools.shovel, TinkersRebornTools.shovel);
        convertMap.put(TinkerTools.hatchet, TinkersRebornTools.hatchet);
        convertMap.put(TinkerTools.mattock, TinkersRebornTools.mattock);
        convertMap.put(TinkerTools.hammer, TinkersRebornTools.hammer);
        convertMap.put(TinkerTools.excavator, TinkersRebornTools.excavator);
        convertMap.put(TinkerTools.lumberaxe, TinkersRebornTools.lumberAxe);
        convertMap.put(TinkerTools.scythe, TinkersRebornTools.scythe);
        convertMap.put(TinkerTools.broadsword, TinkersRebornTools.broadSword);
        convertMap.put(TinkerTools.longsword, TinkersRebornTools.longSword);
        convertMap.put(TinkerTools.rapier, TinkersRebornTools.rapier);
        convertMap.put(TinkerTools.broadsword, TinkersRebornTools.broadSword);
        convertMap.put(TinkerTools.battlesign, TinkersRebornTools.roundShield);
        convertMap.put(TinkerTools.cleaver, TinkersRebornTools.cleaver);

        convertMap.put(TinkerWeaponry.shortbow, TinkersRebornTools.shortBow);
        convertMap.put(TinkerWeaponry.longbow, TinkersRebornTools.longBow);
        convertMap.put(TinkerWeaponry.arrowAmmo, TinkersRebornTools.arrow);
        convertMap.put(TinkerWeaponry.boltAmmo, TinkersRebornTools.bolt);
        convertMap.put(TinkerWeaponry.crossbow, TinkersRebornTools.crossBow);
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

        return convertTConstructToolToTinkersReborn(inputStack);
    }

    private ItemStack convertTConstructToolToTinkersReborn(final ItemStack ticStack) {
        if (ticStack == null) {
            return null;
        }

        List<TinkersRebornMaterial> materialList = new ArrayList<>();
        final NBTTagCompound tcDataCompound = ticStack.getTagCompound()
            .getCompoundTag("InfiTool");
        for (String name : NBTNameList) {
            if (tcDataCompound.hasKey(name)) {
                ToolMaterial ticMaterial = TConstructRegistry.toolMaterials.get(tcDataCompound.getInteger(name));
                materialList.add(TinkersRebornRegistry.getMaterialByIdentifier(ticMaterial.materialName.toLowerCase()));
            } else {
                materialList.add(TinkersRebornMaterial.UNKNOWN);
            }
        }

        // TODO materialList needs some reorder

        final ToolCore targetTool = convertMap.get(ticStack.getItem());

        final ItemStack newTool = ToolBuilderHelper.buildTool(null, targetTool, materialList);

        newTool.setItemDamage(tcDataCompound.getInteger("Damage"));
        if (TinkersRebornConfig.toolLevelingEnable) {
            final NBTTagCompound levelTag = ToolTagsHelper.getToolLevelingNBTSafe(newTool);
            ToolLevelingHelper.setLevel(levelTag, tcDataCompound.getInteger("ToolLevel"));
            ToolLevelingHelper.setXp(levelTag, tcDataCompound.getLong("ToolEXP"));
            ToolTagsHelper.setToolLevelingNBTSafe(newTool, levelTag);
        }

        return newTool;
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
