package mctbl.tinkersreborn.tools.gui;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.world.World;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mctbl.tinkersreborn.common.gui.GuiGeneric;
import mctbl.tinkersreborn.library.gui.GuiElementScalable;
import mctbl.tinkersreborn.library.gui.GuiScalingChest;
import mctbl.tinkersreborn.library.utils.BlockPos;
import mctbl.tinkersreborn.tools.entity.CastChestLogic;
import mctbl.tinkersreborn.tools.inventory.ContainerCastChest;
import mctbl.tinkersreborn.tools.inventory.ContainerTinkerStation;

@SideOnly(Side.CLIENT)
public class GuiCastChest extends GuiTinkerStation {

    protected static final GuiElementScalable background = GuiGeneric.slotEmpty;

    public GuiScalingChest guiInventory;

    public static int xSize = 194;
    public static int ySize = 168;

    public GuiCastChest(InventoryPlayer inventoryplayer, CastChestLogic tile, World world, int x, int y, int z) {
        super(
            world,
            BlockPos.of(x, y, z),
            (ContainerTinkerStation<?>) tile.getGuiContainer(inventoryplayer, world, x, y, z));

        // we use the sideinventory class for the inventory itself
        // it doesn't contain the player inventory
        guiInventory = new GuiScalingChest(
            this,
            container.getSubContainer(ContainerCastChest.DynamicChestInventory.class));
        addModule(guiInventory);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        drawBackground(BLANK_BACK);

        guiInventory.update(mouseX, mouseY);

        super.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);

    }

}
