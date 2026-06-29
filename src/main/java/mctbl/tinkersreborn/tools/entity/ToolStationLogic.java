package mctbl.tinkersreborn.tools.entity;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import org.lwjgl.util.Point;

import mctbl.tinkersreborn.library.entity.TinkersRebornInventoryLogic;
import mctbl.tinkersreborn.library.utils.BlockPos;
import mctbl.tinkersreborn.tools.gui.GuiToolStation;
import mctbl.tinkersreborn.tools.gui.ToolBuildGuiInfo;
import mctbl.tinkersreborn.tools.inventory.ContainerToolStation;
import mctbl.tinkersreborn.util.TinkersRebornUtils;

public class ToolStationLogic extends TinkersRebornInventoryLogic implements ISidedInventory {

    public ItemStack previousTool;
    public String toolName;

    public ToolStationLogic() {
        super(6);
        toolName = "";
    }

    public ToolStationLogic(int slots) {
        super(slots);
        toolName = "";
    }

    @Override
    public String getDefaultName() {
        return "tinkersreborn.ToolStation";
    }

    @Override
    public Container getGuiContainer(InventoryPlayer inventoryplayer, World world, int x, int y, int z) {
        return new ContainerToolStation(inventoryplayer, this);
    }

    @Override
    public GuiContainer getGui(InventoryPlayer inventoryplayer, World world, int x, int y, int z) {
        return new GuiToolStation(inventoryplayer, world, BlockPos.of(x, y, z), this);
    }

    @Override
    public void markDirty() {
        if (this.worldObj != null) {
            this.blockMetadata = this.worldObj.getBlockMetadata(this.xCoord, this.yCoord, this.zCoord);
            this.worldObj.markTileEntityChunkModified(this.xCoord, this.yCoord, this.zCoord, this);
        }
    }

    @Override
    public boolean canUpdate() {
        return false;
    }

    @Override
    public int[] getAccessibleSlotsFromSide(int side) {
        return new int[0];
    }

    @Override
    public boolean canInsertItem(int i, ItemStack itemstack, int j) {
        return false;
    }

    @Override
    public boolean canExtractItem(int i, ItemStack itemstack, int j) {
        return false;
    }

    @Override
    public String getInventoryName() {
        return TinkersRebornUtils.translate(this.getDefaultName() + ".name");
    }

    @Override
    public boolean hasCustomInventoryName() {
        return false;
    }

    @Override
    public void openInventory() {}

    @Override
    public void closeInventory() {}

    @Override
    public List<DisplayItem> getDisplayItems() {
        // TODO use selected info slot position
        List<DisplayItem> items = new ArrayList<>();
        List<Point> points = ToolBuildGuiInfo.repairInfo.positions;
        for (int i = 0; i < getSizeInventory(); i++) {
            ItemStack stack = getStackInSlot(i);
            if (stack == null) continue;

            float x = (33 - points.get(i)
                .getX()) / 61f;
            float z = (42 - points.get(i)
                .getY()) / 61f;
            float s = i == 0 ? 0.7F : 0.5f;

            items.add(new DisplayItem(stack, x, 1.0F, z, s, -1));
        }
        return items;
    }

}
