package mctbl.tinkersreborn.tools.inventory.slots;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryCraftResult;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import cpw.mods.fml.common.FMLCommonHandler;
import mctbl.tinkersreborn.tools.inventory.ContainerToolStation;

public class SlotToolStationOut extends Slot {

    public ContainerToolStation parent;
    public boolean isToolForDeconstruction = false;

    public SlotToolStationOut(int index, int xPosition, int yPosition, ContainerToolStation container) {
        super(new InventoryCraftResult(), index, xPosition, yPosition);

        this.parent = container;
    }

    // @Override
    // public boolean isItemValid(ItemStack stack) {
    // return parent.getInputSlotContents()
    // .isEmpty() // input slots are empty
    // && !isStackEmpty(stack)
    // && stack.getItem() instanceof ToolCore // is tool
    // && !stack.isItemDamaged()
    // && !ToolTagsHelper.isBroken(stack) // undamaged
    // && parent.getBuildableTools()
    // .contains(stack.getItem()) // can be built in the current table
    // && !isSealedArtifact(stack) // is not a sealed artifact
    // && parent.getSelectedTool() == null; // on the default screen and not a tool building screen or the tool
    // // that is built
    // }
    //
    // @Override
    // public void putStack(@Nonnull ItemStack stack) {
    // super.putStack(stack);
    // // trigger craft matrix update and sync when a tool is placed in the output slot
    // if (isItemValid(stack)) {
    // this.isToolForDeconstruction = true;
    // parent.onCraftMatrixChanged(parent.getTile());
    // parent.detectAndSendChanges();
    // }
    // }

    @Override
    public boolean isItemValid(ItemStack stack) {
        return false;
    }

    @Override
    public void onPickupFromSlot(EntityPlayer playerIn, ItemStack stack) {
        FMLCommonHandler.instance()
            .firePlayerCraftingEvent(playerIn, stack, parent.getTile());
        parent.onResultTaken(playerIn, stack);
        stack.onCrafting(playerIn.getEntityWorld(), playerIn, 1);
        super.onPickupFromSlot(playerIn, stack);
    }

    private boolean isSealedArtifact(ItemStack stack) {
        return true;
        // TODO what is this?
        // NBTTagCompound modifierTag = TinkerUtil.getModifierTag(stack, "tconevo.artifact");
        // return ModifierNBT.readTag(modifierTag).level == 1;
    }
}
