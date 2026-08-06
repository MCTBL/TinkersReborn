package mctbl.tinkersreborn.common.player;

import java.lang.ref.WeakReference;
import java.util.UUID;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

import mctbl.tinkersreborn.common.items.HeartCanister;
import mctbl.tinkersreborn.util.TinkersRebornUtils;

public class PlayerHeartCanisterExtended implements IInventory {

    public ItemStack[] inventory = new ItemStack[3];
    public WeakReference<EntityPlayer> parent;
    public UUID globalID = UUID.fromString("92F277C9-731F-4464-B222-9D32BA3C884F");

    public PlayerHeartCanisterExtended init(EntityPlayer player) {
        this.parent = new WeakReference<>(player);
        return this;
    }

    @Override
    public int getSizeInventory() {
        return inventory.length;
    }

    public boolean isStackInSlot(int slot) {
        return TinkersRebornUtils.isStackEmpty(inventory[slot]);
    }

    @Override
    public ItemStack getStackInSlot(int slotIn) {
        return inventory[slotIn];
    }

    @Override
    public ItemStack decrStackSize(int index, int count) {
        if (inventory[index] != null) {
            if (inventory[index].stackSize <= count) {
                ItemStack stack = inventory[index];
                inventory[index] = null;
                return stack;
            }
            ItemStack split = inventory[index].splitStack(count);
            if (inventory[index].stackSize == 0) {
                inventory[index] = null;
            }
            EntityPlayer player = parent.get();
            TinkersRebornPlayerStats stats = TinkersRebornPlayerStats.get(player);
            recalculateHealth(player, stats);
            return split;
        } else {
            return null;
        }
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int index) {
        return null;
    }

    @Override
    public void setInventorySlotContents(int index, ItemStack stack) {
        inventory[index] = stack;
        if (stack != null && stack.stackSize > getInventoryStackLimit()) {
            stack.stackSize = getInventoryStackLimit();
        }

        EntityPlayer player = parent.get();
        TinkersRebornPlayerStats stats = TinkersRebornPlayerStats.get(player);
        recalculateHealth(player, stats);

    }

    @Override
    public String getInventoryName() {
        return "";
    }

    @Override
    public boolean hasCustomInventoryName() {
        return false;
    }

    @Override
    public int getInventoryStackLimit() {
        return 64;
    }

    @Override
    public void markDirty() {
        EntityPlayer player = parent.get();
        TinkersRebornPlayerStats stats = TinkersRebornPlayerStats.get(player);
        recalculateHealth(player, stats);
    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer player) {
        return true;
    }

    @Override
    public void openInventory() {

    }

    @Override
    public void closeInventory() {

    }

    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack) {
        return false;
    }

    /* Save/Load */
    public void saveToNBT(NBTTagCompound tagCompound) {
        NBTTagList tagList = new NBTTagList();
        NBTTagCompound invSlot;

        for (int i = 0; i < this.inventory.length; ++i) {
            if (this.inventory[i] != null) {
                invSlot = new NBTTagCompound();
                invSlot.setByte("Slot", (byte) i);
                this.inventory[i].writeToNBT(invSlot);
                tagList.appendTag(invSlot);
            }
        }

        tagCompound.setTag("Inventory", tagList);
    }

    public void readFromNBT(NBTTagCompound tagCompound) {
        if (tagCompound != null) {
            NBTTagList tagList = tagCompound.getTagList("Inventory", 10);
            for (int i = 0; i < tagList.tagCount(); ++i) {
                NBTTagCompound nbttagcompound = tagList.getCompoundTagAt(i);
                int j = nbttagcompound.getByte("Slot") & 255;
                ItemStack itemstack = ItemStack.loadItemStackFromNBT(nbttagcompound);

                if (itemstack != null) {
                    this.inventory[j] = itemstack;
                }
            }
        }
    }

    public void recalculateHealth(EntityPlayer player, TinkersRebornPlayerStats stats) {
        if (inventory[0] != null || inventory[1] != null || inventory[2] != null) {
            int bonusHP = 0;
            for (int i = 0; i < 3; i++) {
                ItemStack stack = inventory[i];
                if (stack != null && stack.getItem() instanceof HeartCanister heart) {
                    bonusHP += heart.getHealthBoost(stack);
                }
            }
            int prevHealth = stats.bonusHealth;
            stats.bonusHealth = bonusHP;

            int healthChange = bonusHP - prevHealth;
            if (healthChange != 0) {
                IAttributeInstance attributeinstance = player.getAttributeMap()
                    .getAttributeInstance(SharedMonsterAttributes.maxHealth);
                try {
                    attributeinstance.removeModifier(attributeinstance.getModifier(globalID));
                } catch (Exception ignored) {
                    // XD
                }
                attributeinstance
                    .applyModifier(new AttributeModifier(globalID, "tinkersreborn.heartCanister", bonusHP, 0));
            }
        } else if (parent != null && parent.get() != null) {
            int prevHealth = stats.bonusHealth;
            int bonusHP = 0;
            stats.bonusHealth = bonusHP;
            int healthChange = bonusHP - prevHealth;
            if (healthChange != 0) {
                IAttributeInstance attributeinstance = player.getAttributeMap()
                    .getAttributeInstance(SharedMonsterAttributes.maxHealth);
                try {
                    attributeinstance.removeModifier(attributeinstance.getModifier(globalID));
                } catch (Exception ignored) {
                    // XD
                }
            }
        }
    }
}
