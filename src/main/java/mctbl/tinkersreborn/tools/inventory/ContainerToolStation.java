package mctbl.tinkersreborn.tools.inventory;

import static mctbl.tinkersreborn.util.TinkersRebornUtils.isStackEmpty;
import static mctbl.tinkersreborn.util.TinkersRebornUtils.translate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.world.WorldServer;

import mctbl.tinkersreborn.TinkersReborn;
import mctbl.tinkersreborn.common.network.TinkerNetwork;
import mctbl.tinkersreborn.library.TinkerGuiException;
import mctbl.tinkersreborn.library.TinkersRebornRegistry;
import mctbl.tinkersreborn.library.crafting.ToolBuilderHelper;
import mctbl.tinkersreborn.library.event.Sounds;
import mctbl.tinkersreborn.library.gui.container.BaseContainer;
import mctbl.tinkersreborn.library.tools.IModifyable;
import mctbl.tinkersreborn.library.tools.IRepairable;
import mctbl.tinkersreborn.library.tools.ToolCore;
import mctbl.tinkersreborn.library.tools.ToolCore.ToolPartRecord;
import mctbl.tinkersreborn.tools.entity.ToolStationLogic;
import mctbl.tinkersreborn.tools.gui.GuiToolStation;
import mctbl.tinkersreborn.tools.inventory.slots.SlotToolStationIn;
import mctbl.tinkersreborn.tools.inventory.slots.SlotToolStationOut;
import mctbl.tinkersreborn.tools.network.ToolStationSelectionPacket;
import mctbl.tinkersreborn.tools.network.ToolStationTextPacket;
import mctbl.tinkersreborn.util.ToolTagsHelper;

public class ContainerToolStation extends ContainerTinkerStation<ToolStationLogic> {

    private final EntityPlayer player;
    protected SlotToolStationOut out;
    protected ToolCore selectedTool; // needed for newly opened containers to sync
    protected int activeSlots;
    public String toolName = "";

    public ContainerToolStation(InventoryPlayer playerInventory, ToolStationLogic tile) {
        super(tile);
        this.player = playerInventory.player;

        // input slots
        int i;
        for (i = 0; i < tile.getSizeInventory(); i++) {
            addSlotToContainer(new SlotToolStationIn(tile, i, 0, 0, this));
        }

        // output slot
        out = new SlotToolStationOut(i, 124, 38, this);
        addSlotToContainer(out);
        this.addPlayerInventory(playerInventory, 8, 84 + 8);

        onCraftMatrixChanged(playerInventory);

    }

    public List<ItemStack> getInputSlotContents() {
        List<ItemStack> contents = new ArrayList<>();
        for (Slot slotIn : inventorySlots) {
            if (slotIn instanceof SlotToolStationIn && !isStackEmpty(slotIn.getStack())) {
                contents.add(slotIn.getStack());
            }
        }
        return contents;
    }

    public ItemStack getResult() {
        return out.getStack();
    }

    @Override
    protected void syncNewContainer(EntityPlayerMP player) {
        this.activeSlots = tile.getSizeInventory();
        TinkerNetwork.sendTo(new ToolStationSelectionPacket(null, tile.getSizeInventory()), player);
    }

    @Override
    protected void syncWithOtherContainer(BaseContainer<ToolStationLogic> otherContainer, EntityPlayerMP player) {
        this.syncWithOtherContainer((ContainerToolStation) otherContainer, player);
    }

    protected void syncWithOtherContainer(ContainerToolStation otherContainer, EntityPlayerMP player) {
        // set same selection as other container
        this.setToolSelection(otherContainer.selectedTool, otherContainer.activeSlots);
        this.setToolName(otherContainer.toolName);
        // also send the data to the player
        TinkerNetwork
            .sendTo(new ToolStationSelectionPacket(otherContainer.selectedTool, otherContainer.activeSlots), player);
        if (otherContainer.toolName != null && !otherContainer.toolName.isEmpty()) {
            TinkerNetwork.sendTo(new ToolStationTextPacket(otherContainer.toolName), player);
        }
    }

    public void setToolSelection(ToolCore tool, int activeSlots) {
        if (activeSlots > tile.getSizeInventory()) {
            activeSlots = tile.getSizeInventory();
        }

        this.activeSlots = activeSlots;
        this.selectedTool = tool;

        for (int i = 0; i < tile.getSizeInventory(); i++) {
            Slot slot = inventorySlots.get(i);
            // set part info for the slot
            if (slot instanceof SlotToolStationIn) {
                SlotToolStationIn slotToolPart = (SlotToolStationIn) slot;

                slotToolPart.setRestriction(null);

                // deactivate not needed slots
                if (i >= activeSlots) {
                    slotToolPart.deactivate();
                }
                // activate the other slots and set toolpart if possible
                else {
                    slotToolPart.activate();
                    if (tool != null) {
                        List<ToolPartRecord> pmts = tool.getToolComponentsParts();
                        if (i < pmts.size()) {
                            slotToolPart.setRestriction(pmts.get(i));
                        }
                    }
                }

                if (world.isRemote) {
                    slotToolPart.updateIcon();
                }
            }
        }
    }

    public void setToolName(String name) {
        this.toolName = name;

        if (world.isRemote) {
            GuiScreen screen = Minecraft.getMinecraft().currentScreen;
            if (screen instanceof GuiToolStation) {
                ((GuiToolStation) screen).textField.setText(name);
            }
        }

        onCraftMatrixChanged(tile);
        if (out.getHasStack()) {
            if (name != null && !name.isEmpty()) {
                out.inventory.getStackInSlot(0)
                    .setStackDisplayName(name);
            } else {
                out.inventory.getStackInSlot(0)
                    .func_135074_t();
            }
        }
    }

    // update crafting - called whenever the content of an input slot changes
    @Override
    public void onCraftMatrixChanged(IInventory inventoryIn) {
        // reset gui state
        updateGUI();
        try {
            ItemStack result = null;
            // 1. try repairing
            result = repairTool(false);
            // 2. try swapping tool parts
            if (isStackEmpty(result)) {
                result = replaceToolParts(false);
            }
            // 3. try modifying
            if (isStackEmpty(result)) {
                result = modifyTool(false);
            }
            // 4. try renaming
            if (isStackEmpty(result)) {
                result = renameTool();
            }
            // 5. try building a new tool
            if (isStackEmpty(result)) {
                result = buildTool();
            }

            // if a crafting operation produced a result, set it in the output slot
            if (!isStackEmpty(result)) {
                out.inventory.setInventorySlotContents(0, result);
            } else {
                // no crafting result and no valid tool for deconstruction
                out.inventory.setInventorySlotContents(0, null);
            }
            updateGUI();
        } catch (TinkerGuiException e) {
            // error ;(
            out.inventory.setInventorySlotContents(0, null);
            this.error(e.getMessage());
        }

        // sync output with other open containers on the server
        if (!this.world.isRemote) {
            WorldServer server = (WorldServer) this.world;
            for (EntityPlayer player : server.playerEntities) {
                if (player.openContainer != this && player.openContainer instanceof ContainerToolStation
                    && this.sameGui((ContainerToolStation) player.openContainer)) {
                    ((ContainerToolStation) player.openContainer).out.inventory
                        .setInventorySlotContents(0, out.getStack());
                }
            }
        }
    }

    // Called when the crafting result is taken out of its slot
    public void onResultTaken(EntityPlayer playerIn, ItemStack stack) {
        boolean resultTaken = false;

        try {
            resultTaken = !isStackEmpty(repairTool(true)) || !isStackEmpty(replaceToolParts(true))
                || !isStackEmpty(modifyTool(true))
                || !isStackEmpty(renameTool());
        } catch (TinkerGuiException e) {
            // no error updating needed
            e.printStackTrace();
        }

        if (resultTaken) {
            updateSlotsAfterToolAction();
        } else {

            // calculate the result again (serverside)
            try {
                ItemStack tool = buildTool();

                // we built a tool
                if (!isStackEmpty(tool)) {
                    // remove 1 of each in the slots
                    // it's guaranteed that each slot that has an item has used exactly 1 item to
                    // build the tool
                    for (int i = 0; i < tile.getSizeInventory(); i++) {
                        tile.decrStackSize(i, 1);
                    }

                    setToolName("");
                }
            } catch (TinkerGuiException e) {
                // no error updating needed
                e.printStackTrace();
            }
        }
        onCraftMatrixChanged(null);

        this.playCraftSound(playerIn);
    }

    /**
     * Removes the tool in the input slot and fixes all stacks that have stacksize 0 after being used up.
     */
    private void updateSlotsAfterToolAction() {
        // perfect, items already got removed but we still have to clean up 0-stacks and remove the tool
        tile.setInventorySlotContents(0, null); // slot where the tool was
        for (int i = 1; i < tile.getSizeInventory(); i++) {
            if (isStackEmpty(tile.getStackInSlot(i))) {
                tile.setInventorySlotContents(i, null);
            }
        }
    }

    protected void playCraftSound(EntityPlayer player) {
        Sounds.playSoundForAll(player, Sounds.saw, 0.8f, 0.8f + 0.4f * TinkersReborn.random.nextFloat());
    }

    private ItemStack repairTool(boolean remove) {
        ItemStack repairable = getToolStack();

        // modifying possible?
        if (isStackEmpty(repairable) || !(repairable.getItem() instanceof IRepairable)) {
            return null;
        }

        return ToolBuilderHelper.tryRepairTool(getInputs(), repairable, remove);
    }

    private ItemStack replaceToolParts(boolean remove) throws TinkerGuiException {
        ItemStack tool = getToolStack();

        if (isStackEmpty(tool) || !(tool.getItem() instanceof ToolCore)) {
            return null;
        }

        List<ItemStack> inputs = getInputs();
        ItemStack result = ToolBuilderHelper.tryReplaceToolParts(tool, inputs, remove);
        if (!isStackEmpty(result)) {
            // TODO
            // TinkerCraftingEvent.ToolPartReplaceEvent.fireEvent(result, player, inputs);
        }
        return result;
    }

    private ItemStack modifyTool(boolean remove) throws TinkerGuiException {
        ItemStack modifyable = getToolStack();

        // modifying possible?
        if (isStackEmpty(modifyable) || !(modifyable.getItem() instanceof IModifyable)) {
            return null;
        }
        List<ItemStack> inputs = getInputs();
        ItemStack result = ToolBuilderHelper.tryModifyTool(inputs, modifyable, remove);

        if (!isStackEmpty(result)) {
            // TinkerCraftingEvent.ToolModifyEvent.fireEvent(result, player, modifyable.copy());
        }
        return result;
    }

    private ItemStack renameTool() throws TinkerGuiException {
        ItemStack tool = getToolStack();

        // modifying possible?
        // new name not same as old
        // or have name and new name is empty
        if (isStackEmpty(tool) || !(tool.getItem() instanceof ToolCore)
            || (tool.hasDisplayName() && tool.getDisplayName()
                .equals(toolName) || (!tool.hasDisplayName() && toolName.isEmpty()))) {
            return null;
        }

        ItemStack result = tool.copy();
        if (ToolTagsHelper.isToolNoRenaem(result)) {
            throw new TinkerGuiException(translate("gui.error.no_rename"));
        }

        if (toolName.isEmpty()) {
            // reset name
            result.func_135074_t();
        } else {
            // set new name
            result.setStackDisplayName(toolName);
        }

        return result;
    }

    private ItemStack buildTool() throws TinkerGuiException {
        ItemStack result = ToolBuilderHelper.buildTool(toolName, getBuildableTools(), tile.getInventory());
        if (!isStackEmpty(result)) {

            // TODO
            // TinkerCraftingEvent.ToolCraftingEvent.fireEvent(result, player, input);

        }
        return result;
    }

    public List<ToolCore> getBuildableTools() {
        return TinkersRebornRegistry.getToolStationCraftingList();
    }

    private ItemStack getToolStack() {
        return inventorySlots.get(0)
            .getStack();
    }

    private List<ItemStack> getInputs() {
        List<ItemStack> input = new ArrayList<>(Collections.nCopies(tile.getSizeInventory() - 1, null));
        for (int i = 1; i < tile.getSizeInventory(); i++) {
            input.set(i - 1, tile.getStackInSlot(i));
        }
        return input;
    }

    @Override
    public boolean canMergeSlot(ItemStack stack, Slot slot) {
        if (slot == out && slot instanceof SlotToolStationIn) {
            return false;
        }

        return super.canMergeSlot(stack, slot);
    }

    public ToolCore getSelectedTool() {
        return selectedTool;
    }

}
