package mctbl.tinkersreborn.smeltery.entity;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.world.World;

import mctbl.tinkersreborn.library.entity.TinkersRebornSearedMultiBlockLogic;
import mctbl.tinkersreborn.smeltery.TinkersRebornSmeltery;
import mctbl.tinkersreborn.smeltery.gui.GuiFurnace;
import mctbl.tinkersreborn.smeltery.inventory.ContainerFurnace;
import mctbl.tinkersreborn.util.TinkersRebornUtils;

public class FurnaceLogic extends TinkersRebornSearedMultiBlockLogic {

    public FurnaceLogic() {
        super("furnace", TinkersRebornSmeltery.furnaceController);
    }

    @Override
    public int getInventoryStackLimit() {
        return 16;
    }

    @Override
    protected boolean hasTopLayer() {
        return true;
    }

    @Override
    protected boolean hasBottmLayer() {
        return true;
    }

    @Override
    protected void heatItems() {
        boolean heatedItem = false;
        boolean triedRefuel = false;
        for (int i = 0; i < getSizeInventory(); i++) {
            ItemStack stack = getStackInSlot(i);
            if (!TinkersRebornUtils.isStackEmpty(stack)) {
                // heat item if possible
                if (itemTempRequired[i] > 0) {
                    // fuel is present, turn up the heat
                    if (fuelReleaseTicks > 0) {
                        // if the temperature is high enough for the slot
                        if (canHeat(i)) {
                            // are we done heating?
                            if (itemTemperatures[i] >= itemTempRequired[i]) {
                                if (onItemFinishedHeating(stack, i)) {
                                    itemTemperatures[i] = 0;
                                    itemTempRequired[i] = 0;
                                }
                            }
                            // otherwise turn up the heat
                            else {
                                itemTemperatures[i] += heatSlot(i);
                                heatedItem = true;
                            }
                        }
                    } else if (!triedRefuel) {
                        // out of fuel, try to consume more right now
                        // so we don't miss this tick's heating
                        this.needsFuel = true;
                        this.consumeFuel();
                        triedRefuel = true;
                        if (fuelReleaseTicks > 0) {
                            // fuel acquired, retry this slot
                            i--;
                            continue;
                        }
                        // truly out of fuel, nothing more we can do
                        break;
                    } else {
                        // already tried refueling this tick and failed, give up
                        break;
                    }
                }
            } else {
                itemTemperatures[i] = 0;
            }
        }

        if (heatedItem) {
            fuelReleaseTicks--;
        }
        updateIfChanged(heatedItem);
    }

    @Override
    public boolean canHeat(int index) {
        ItemStack stack = getStackInSlot(index);
        return FurnaceRecipes.smelting()
            .getSmeltingResult(stack) != null;
    }

    /**
     * Calculate the heat required for the given slot
     *
     * @param index
     */
    @Override
    protected void updateTempRequired(int index) {
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
            int amount = result.stackSize == 0 ? 1 : result.stackSize;
            result.stackSize = stack.stackSize * amount;
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
        int w = Math.max(1, this.maxPos.getX() - this.minPos.getX());
        int h = Math.max(1, this.maxPos.getY() - this.minPos.getY());
        int z = Math.max(1, this.maxPos.getZ() - this.minPos.getZ());
        return 9 + (3 * w * h * z);
    }

    @Override
    public void checkSteppingingValid() {
        super.checkSteppingingValid();
    }

    @Override
    public void stepNextInnerPos() {
        super.stepNextInnerPos();
    }

    @Override
    public Container getGuiContainer(InventoryPlayer inventoryplayer, World world, int x, int y, int z) {
        if (!getActive()) return null;
        return new ContainerFurnace(inventoryplayer, this);
    }

    @Override
    public GuiContainer getGui(InventoryPlayer inventoryplayer, World world, int x, int y, int z) {
        if (!getActive()) return null;
        return new GuiFurnace((ContainerFurnace) getGuiContainer(inventoryplayer, world, x, y, z), this);
    }
}
