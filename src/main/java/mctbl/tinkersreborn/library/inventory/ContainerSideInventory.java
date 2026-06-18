package mctbl.tinkersreborn.library.inventory;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

import mctbl.tinkersreborn.library.gui.container.BaseContainer;

public class ContainerSideInventory<T extends TileEntity> extends BaseContainer<T> {

    public final int columns;
    public final int slotCount;

    public ContainerSideInventory(T tile, int x, int y, int columns) {
        this(tile, null, x, y, columns);
    }

    public ContainerSideInventory(T tile, ForgeDirection dir, int x, int y, int columns) {
        super(tile, dir);

        this.columns = columns;
        this.slotCount = inventory.getSizeInventory();

        int rows = slotCount / columns;
        if (slotCount % columns != 0) {
            rows++;
        }

        int index = 0;
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < columns; c++) {
                if (index >= slotCount) {
                    break;
                }
                this.addSlotToContainer(createSlot(inventory, index, x + c * 18, y + r * 18));
                index++;
            }
        }
    }

    protected Slot createSlot(IInventory inventory, int index, int x, int y) {
        return new Slot(inventory, index, x, y);
    }

    public int getSlotCount() {
        return slotCount;
    }

    public int getSizeInventory() {
        return inventory.getSizeInventory();
    }
}
