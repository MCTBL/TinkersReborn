package mctbl.tinkersreborn.plugins.TConstruct.recipe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;

import mctbl.tinkersreborn.TinkersReborn;
import mctbl.tinkersreborn.plugins.TConstruct.TConstructEventHandler;
import mctbl.tinkersreborn.tools.TinkersRebornTools;
import tconstruct.tools.TinkerTools;

public class TConstructNBTBlockConvertRecipe implements IRecipe {

    private static final Map<ItemStack, ItemStack> convertMap = new HashMap<>();

    public TConstructNBTBlockConvertRecipe() {
        convertMap.put(new ItemStack(TinkerTools.toolStationWood, 1, 5), new ItemStack(TinkersRebornTools.castChest));
        convertMap.put(new ItemStack(TinkerTools.toolStationWood, 1, 6), new ItemStack(TinkersRebornTools.partChest));
        convertMap.put(new ItemStack(TinkerTools.craftingSlabWood, 1, 4), new ItemStack(TinkersRebornTools.castChest));
        convertMap.put(new ItemStack(TinkerTools.craftingSlabWood, 1, 5), new ItemStack(TinkersRebornTools.partChest));
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

        return checkContains(inputStack) != null;
    }

    private ItemStack checkContains(ItemStack stack) {
        for (Entry<ItemStack, ItemStack> entry : convertMap.entrySet()) {
            if (entry.getKey()
                .getItem()
                .equals(stack.getItem())
                && entry.getKey()
                    .getItemDamage() == stack.getItemDamage()) {
                return entry.getValue();
            }
        }
        return null;
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

        ItemStack targetStack = checkContains(inputStack);
        if (targetStack != null) {
            targetStack = targetStack.copy();
            targetStack.setTagCompound(convertNBT(inputStack.getTagCompound()));
        }
        return targetStack;
    }

    private NBTTagCompound convertNBT(NBTTagCompound nbtBase) {
        try {
            if (nbtBase == null) {
                return new NBTTagCompound();
            }
            NBTTagCompound inventory = nbtBase.getCompoundTag("Inventory");
            NBTTagList nbttaglist = inventory.getTagList("Items", 10);

            List<ItemStack> tempList = new ArrayList<>();
            for (int i = 0; i < nbttaglist.tagCount(); ++i) {
                tempList.add(ItemStack.loadItemStackFromNBT(nbttaglist.getCompoundTagAt(i)));
            }
            final ItemStack[] array = tempList.toArray(new ItemStack[0]);

            TConstructEventHandler.tryReplaceItemStackArray(array);

            final NBTTagCompound baseCompound = new NBTTagCompound();
            final NBTTagCompound invNBT = new NBTTagCompound();
            final NBTTagList targetItems = new NBTTagList();

            for (int i = 0; i < array.length; ++i) {
                if (array[i] != null) {
                    NBTTagCompound nbttagcompound1 = new NBTTagCompound();
                    nbttagcompound1.setShort("Slot", (short) i);
                    array[i].writeToNBT(nbttagcompound1);
                    targetItems.appendTag(nbttagcompound1);
                }
            }
            invNBT.setTag("Items", targetItems);
            baseCompound.setTag("Inventory", invNBT);
            return baseCompound;
        } catch (Exception e) {
            TinkersReborn.LOG.error("found error when TConstructNBTBlockConvertRecipe trying convertNBT", e);
            return new NBTTagCompound();
        }
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
