package mctbl.tinkersreborn.smeltery.entity;

import java.util.Arrays;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.world.World;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mctbl.tinkersreborn.library.entity.TinkersRebornHeatableMultiBlockLogic;
import mctbl.tinkersreborn.smeltery.TinkersRebornSmeltery;
import mctbl.tinkersreborn.smeltery.gui.GuiFurnace;
import mctbl.tinkersreborn.smeltery.inventory.ContainerFurnace;
import mctbl.tinkersreborn.util.TinkersRebornUtils;

public class FurnaceLogic extends TinkersRebornHeatableMultiBlockLogic {

    private static final int MAX_FURNACE_SIZE = 9;

    public FurnaceLogic() {
        super("furnace", TinkersRebornSmeltery.furnaceController);

        this.validWall.addAll(
            Arrays.asList(
                this.controller,
                TinkersRebornSmeltery.smelteryBlock,
                TinkersRebornSmeltery.lavaTank,
                TinkersRebornSmeltery.itemIOHatch));
        this.validCeiling.addAll(
            Arrays.asList(
                TinkersRebornSmeltery.smelteryBlock,
                TinkersRebornSmeltery.lavaTank,
                TinkersRebornSmeltery.itemIOHatch));
        this.validFloor.addAll(
            Arrays.asList(
                TinkersRebornSmeltery.smelteryBlock,
                TinkersRebornSmeltery.lavaTank,
                TinkersRebornSmeltery.itemIOHatch));
    }

    @Override
    protected int getMaxMultiSize() {
        return MAX_FURNACE_SIZE;
    }

    @Override
    protected boolean hasCeilingLayer() {
        return true;
    }

    @Override
    protected boolean hasFrame() {
        return true;
    }

    @Override
    protected boolean hasFloorLayer() {
        return true;
    }

    @Override
    public int getInventoryStackLimit() {
        return 64;
    }

    @Override
    public boolean isItemValidForSlot(int slot, ItemStack itemstack) {
        if (!super.isItemValidForSlot(slot, itemstack)) return false;
        if (itemstack == null) return false;
        ItemStack result = FurnaceRecipes.smelting()
            .getSmeltingResult(itemstack);
        if (result == null || result.stackSize <= 0) return false;
        ItemStack existing = this.getStackInSlot(slot);
        int cap = Math.min(16, 64 / result.stackSize);
        int amount = itemstack.stackSize + (existing != null ? existing.stackSize : 0);
        return amount <= cap;
    }

    /**
     * Calculate the heat required for the given slot
     *
     * @param index
     */
    @Override
    public void updateTempRequired(int index) {
        ItemStack stack = getStackInSlot(index);
        if (!TinkersRebornUtils.isStackEmpty(stack)) {
            if (FurnaceRecipes.smelting()
                .getSmeltingResult(stack) != null) {
                int base = 200;
                float temp = base * stack.stackSize / 4f;

                if (stack.getItem() instanceof ItemFood) {
                    temp *= 0.8f;
                }

                setTempRequiredForSlot(index, (int) temp);
                if (fuelReleaseTicks <= 0) {
                    consumeFuel();
                }
                return;
            }
        }
        setTempRequiredForSlot(index, 0);
    }

    /**
     * Called when an item finished heating up. Return true if the processing was
     * successful, then the heating data will be cleared.
     *
     * @param stack
     * @param slot
     */
    @Override
    protected boolean onItemFinishedHeating(ItemStack stack, int slot) {
        ItemStack result = FurnaceRecipes.smelting()
            .getSmeltingResult(stack);
        if (result != null) {
            result = result.copy();
            int amount = stack.stackSize;
            result.stackSize *= amount;
            setInventorySlotContents(slot, result);
            return true;
        }
        return false;
    }

    @Override
    public float getProgress(int index) {
        if (index >= itemTemperatures.length) {
            return 0f;
        }
        return Math.min(1f, (float) itemTemperatures[index] / itemTempRequired[index]);
    }

    @Override
    protected int calculateInnerBlockCount() {
        return super.calculateInnerBlockCount() * 3 + 9;
    }

    @Override
    public Container getGuiContainer(InventoryPlayer inventoryplayer, World world, int x, int y, int z) {
        return new ContainerFurnace(inventoryplayer, this);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public GuiContainer getGui(InventoryPlayer inventoryplayer, World world, int x, int y, int z) {
        return new GuiFurnace((ContainerFurnace) getGuiContainer(inventoryplayer, world, x, y, z), this);
    }
}
