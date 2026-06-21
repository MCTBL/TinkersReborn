package mctbl.tinkersreborn.tools.inventory.slots;

import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mctbl.tinkersreborn.library.tools.IToolPart;
import mctbl.tinkersreborn.library.tools.ToolCore;
import mctbl.tinkersreborn.library.tools.ToolCore.ToolPartRecord;
import mctbl.tinkersreborn.tools.inventory.ContainerToolStation;

public class SlotToolStationIn extends Slot {

    public boolean dormant;
    public ToolPartRecord restriction;
    public IIcon icon;
    public Container parent;

    public SlotToolStationIn(IInventory inventoryIn, int index, int xPosition, int yPosition, Container parent) {
        super(inventoryIn, index, xPosition, yPosition);
        this.parent = parent;
    }

    @Override
    public void putStack(ItemStack stack) {
        if (this.getSlotIndex() == 0 && stack != null
            && stack.getItem() instanceof ToolCore
            && parent instanceof ContainerToolStation cts) {
            if (stack.hasDisplayName()) {
                String toolName = stack.getDisplayName();
                if (!toolName.isEmpty()) cts.setToolName(toolName);
            } else {
                cts.setToolName("");
            }
        }
        super.putStack(stack);
    }

    @Override
    public void onSlotChanged() {
        // notify container to update craft result
        parent.onCraftMatrixChanged(inventory);
    }

    @Override
    public boolean isItemValid(ItemStack stack) {
        // dormant slots don't take any items, they can only be taken out of
        // we also dont want stuff to be put in while a deconstruction is happening
        if (dormant) {
            return false;
        }

        // otherwise we check if we have item info and restrict it to that
        if (restriction != null) {
            if (stack != null && stack.getItem() instanceof IToolPart part) {
                return isToolPartValid(restriction, part);
            }
            return false;
        }

        // note that we only take the part into account when it's set. This is because
        // it's only ever set clientside
        return super.isItemValid(stack);
    }

    private boolean isToolPartValid(ToolPartRecord restriction, IToolPart part) {
        return restriction.toolPart()
            .equals(part);
    }

    public boolean isDormant() {
        return dormant;
    }

    public void activate() {
        dormant = false;
    }

    public void deactivate() {
        dormant = true;
    }

    public void setRestriction(ToolPartRecord restriction) {
        this.restriction = restriction;
    }

    @SideOnly(Side.CLIENT)
    public void updateIcon() {
        if (restriction != null) {
            icon = restriction.toolPart().outlineIcon;
        } else {
            icon = null;
        }
    }
}
