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
    private Map<Item, int[]> reorderMap;

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

        convertMap.put(TinkerWeaponry.shortbow, TinkersRebornTools.shortBow); // 1
        convertMap.put(TinkerWeaponry.longbow, TinkersRebornTools.longBow); // 1
        convertMap.put(TinkerWeaponry.arrowAmmo, TinkersRebornTools.arrow); // 2
        convertMap.put(TinkerWeaponry.boltAmmo, TinkersRebornTools.bolt); // 2
        convertMap.put(TinkerWeaponry.crossbow, TinkersRebornTools.crossBow); // 2

        reorderMap = new HashMap<>();
        reorderMap.put(TinkerTools.mattock, new int[] { 0, 2, 1 });
        reorderMap.put(TinkerTools.hammer, new int[] { 0, 2, 3, 1 });
        reorderMap.put(TinkerTools.lumberaxe, new int[] { 0, 2, 1, 3 });
        reorderMap.put(TinkerTools.excavator, new int[] { 0, 2, 1, 3 });
        reorderMap.put(TinkerTools.scythe, new int[] { 0, 1, 3, 2 });
        reorderMap.put(TinkerWeaponry.shortbow, new int[] { 1, 0, 2 });
        reorderMap.put(TinkerWeaponry.longbow, new int[] { 1, 0, 2, 3 });
        reorderMap.put(TinkerWeaponry.crossbow, new int[] { 1, 2, 0, 3 });
        reorderMap.put(TinkerWeaponry.arrowAmmo, new int[] { 1, 0, 2 });
        reorderMap.put(TinkerWeaponry.boltAmmo, new int[] { 1, 0, 2 });

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
        final Item tcToolCore = ticStack.getItem();
        final ToolCore targetTool = convertMap.get(tcToolCore);

        List<TinkersRebornMaterial> materialList = new ArrayList<>();
        final NBTTagCompound tcDataCompound = ticStack.getTagCompound()
            .getCompoundTag("InfiTool");
        for (int idx = 0; idx < NBTNameList.length; idx++) {
            String name = NBTNameList[idx];
            if (tcDataCompound.hasKey(name)) {
                if ((tcToolCore == TinkerWeaponry.shortbow && idx == 1)
                    || (tcToolCore == TinkerWeaponry.longbow && idx == 1)
                    || (tcToolCore == TinkerWeaponry.arrowAmmo && idx == 2)
                    || (tcToolCore == TinkerWeaponry.boltAmmo && idx == 2)
                    || (tcToolCore == TinkerWeaponry.crossbow && idx == 2)) {
                    materialList.add(processTConstructCustomMaterial(tcToolCore, tcDataCompound.getInteger(name)));
                } else {
                    ToolMaterial ticMaterial = TConstructRegistry.toolMaterials.get(tcDataCompound.getInteger(name));
                    materialList
                        .add(TinkersRebornRegistry.getMaterialByIdentifier(ticMaterial.materialName.toLowerCase()));
                }
            } else {
                materialList.add(TinkersRebornMaterial.UNKNOWN);
            }
        }

        if (reorderMap.containsKey(tcToolCore)) {
            materialList = reorderMaterialList(materialList, reorderMap.get(tcToolCore));
        }

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

    private static TinkersRebornMaterial processTConstructCustomMaterial(Item tcToolCore, int material) {
        String tinkersRebornMaterialName = "";
        if (tcToolCore == TinkerWeaponry.shortbow || tcToolCore == TinkerWeaponry.longbow
            || tcToolCore == TinkerWeaponry.crossbow) {
            tinkersRebornMaterialName = "string";
        } else if (tcToolCore == TinkerWeaponry.boltAmmo || tcToolCore == TinkerWeaponry.arrowAmmo) {
            tinkersRebornMaterialName = switch (material) {
                case 1 -> "leaf";
                case 4 -> "slimeleaf";
                default -> "feather";
            };
        }
        return TinkersRebornRegistry.getMaterialByIdentifier(tinkersRebornMaterialName);
    }

    private List<TinkersRebornMaterial> reorderMaterialList(List<TinkersRebornMaterial> oldList, int[] order) {
        if (oldList.size() < order.length) {
            return oldList;
        }
        List<TinkersRebornMaterial> result = new ArrayList<>(oldList.size());
        for (int idx : order) {
            result.add(oldList.get(idx));
        }
        return result;
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
