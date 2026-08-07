package mctbl.tinkersreborn.tools.gui;

import java.util.Collections;
import java.util.List;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import codechicken.nei.VisiblityData;
import codechicken.nei.api.INEIGuiHandler;
import codechicken.nei.api.TaggedInventoryArea;
import cpw.mods.fml.common.Optional;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mctbl.tinkersreborn.library.gui.GuiModule;
import mctbl.tinkersreborn.library.gui.GuiSideInventory;
import mctbl.tinkersreborn.library.inventory.ContainerSideInventory;
import mctbl.tinkersreborn.library.utils.BlockPos;
import mctbl.tinkersreborn.tools.entity.CraftingStationLogic;
import mctbl.tinkersreborn.tools.inventory.ContainerCraftingStation;
import mctbl.tinkersreborn.tools.inventory.ContainerTinkerStation;

@SideOnly(Side.CLIENT)
@Optional.Interface(iface = "codechicken.nei.api.INEIGuiHandler", modid = "NotEnoughItems")
public class GuiCraftingStation extends GuiTinkerStation implements INEIGuiHandler {

    private static final ResourceLocation BACKGROUND = new ResourceLocation(
        "textures/gui/container/crafting_table.png");
    protected final CraftingStationLogic tile;
    protected GuiModule sideInventory;

    public GuiCraftingStation(InventoryPlayer playerInv, World world, BlockPos pos, CraftingStationLogic tile) {
        super(world, pos, (ContainerTinkerStation<?>) tile.getGuiContainer(playerInv, world, pos.x, pos.y, pos.z));

        this.tile = tile;

        if (inventorySlots instanceof ContainerCraftingStation container) {
            ContainerSideInventory<?> chestContainer = container.getSubContainer(ContainerSideInventory.class);
            if (chestContainer != null) {
                // if(chestContainer.getTile() instanceof TileEntityChest) {
                // // Fix: chests don't update their single/double chest status clientside once accessed
                // ((TileEntityChest) chestContainer.getTile()).doubleChestHandler = null;
                // }
                sideInventory = new GuiSideInventory(
                    this,
                    chestContainer,
                    chestContainer.getSlotCount(),
                    chestContainer.columns);
                this.addModule(sideInventory);
            }
        }
    }

    public boolean isSlotInChestInventory(Slot slot) {
        GuiModule module = getModuleForSlot(slot.slotNumber);
        return module instanceof GuiSideInventory;
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        drawBackground(BACKGROUND);

        super.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);
    }

    // NEI
    @Override
    public VisiblityData modifyVisiblity(GuiContainer gui, VisiblityData currentVisibility) {
        return currentVisibility;
    }

    @Override
    public Iterable<Integer> getItemSpawnSlots(GuiContainer gui, ItemStack item) {
        return Collections.emptyList();
    }

    @Override
    public List<TaggedInventoryArea> getInventoryAreas(GuiContainer gui) {
        return Collections.emptyList();
    }

    @Override
    public boolean handleDragNDrop(GuiContainer gui, int mousex, int mousey, ItemStack draggedStack, int button) {
        return false;
    }

    @Override
    public boolean hideItemPanelSlot(GuiContainer gui, int x, int y, int w, int h) {
        int guiXStart = guiLeft - (sideInventory != null ? sideInventory.xSize() : 0) + 4;
        int guiXEnd = guiLeft + xSize - 4;
        int guiYStart = guiTop + 4;
        int guiYEnd = guiTop + ySize - 4;
        return x + w >= guiXStart && x <= guiXEnd && y + h >= guiYStart && y <= guiYEnd;
    }
}
