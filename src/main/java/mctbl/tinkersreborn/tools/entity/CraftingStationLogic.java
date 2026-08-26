package mctbl.tinkersreborn.tools.entity;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mctbl.tinkersreborn.library.entity.TinkersRebornInventoryLogic;
import mctbl.tinkersreborn.library.utils.BlockPos;
import mctbl.tinkersreborn.tools.gui.GuiCraftingStation;
import mctbl.tinkersreborn.tools.inventory.ContainerCraftingStation;

public class CraftingStationLogic extends TinkersRebornInventoryLogic {

    public CraftingStationLogic() {
        super(9);
    }

    @Override
    public ItemStack decrStackSize(int slot, int quantity) {
        if (slot == 0) {
            for (int i = 1; i < getSizeInventory(); i++) decrStackSize(i, 1);
        }
        return super.decrStackSize(slot, quantity);
    }

    @Override
    public Container getGuiContainer(InventoryPlayer inventoryplayer, World world, int x, int y, int z) {
        return new ContainerCraftingStation(inventoryplayer, this);
    }

    @Override
    public String getDefaultName() {
        return "tinkersreborn.CraftingStation";
    }

    @Override
    @SideOnly(Side.CLIENT)
    public GuiContainer getGui(InventoryPlayer inventoryplayer, World world, int x, int y, int z) {
        return new GuiCraftingStation(inventoryplayer, world, BlockPos.of(x, y, z), this);
    }

    @Override
    public List<DisplayItem> getDisplayItems() {
        List<DisplayItem> items = new ArrayList<>();
        float s = 0.5f;
        float o = 3f / 16f; // we want to move it 3 pixel in a 16 width texture
        for (int i = 0; i < 9; i++) {
            ItemStack stack = getStackInSlot(i);
            if (stack == null) continue;
            float x = +o - (i % 3) * o;
            float y = 1.0f;
            float z = +o - (i / 3) * o;

            items.add(new DisplayItem(stack, x, y, z, s, 0));
        }

        return items;
    }
}
