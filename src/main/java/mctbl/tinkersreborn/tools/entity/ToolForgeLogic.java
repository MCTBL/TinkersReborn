package mctbl.tinkersreborn.tools.entity;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import mctbl.tinkersreborn.library.utils.BlockPos;
import mctbl.tinkersreborn.tools.gui.GuiToolForge;
import mctbl.tinkersreborn.tools.inventory.ContainerToolForge;

public class ToolForgeLogic extends ToolStationLogic {

    public ItemStack previousTool;
    public String toolName;

    public ToolForgeLogic() {
        super(5);
        toolName = "";
    }

    @Override
    public String getDefaultName() {
        return "tinkersreborn.ToolForge";
    }

    @Override
    public Container getGuiContainer(InventoryPlayer inventoryplayer, World world, int x, int y, int z) {
        return new ContainerToolForge(inventoryplayer, this);
    }

    @Override
    public GuiContainer getGui(InventoryPlayer inventoryplayer, World world, int x, int y, int z) {
        return new GuiToolForge(inventoryplayer, world, BlockPos.of(x, y, z), this);
    }

}
