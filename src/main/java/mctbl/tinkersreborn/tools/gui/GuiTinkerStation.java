package mctbl.tinkersreborn.tools.gui;

import net.minecraft.block.Block;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import org.apache.commons.lang3.tuple.Pair;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mctbl.tinkersreborn.TinkersReborn;
import mctbl.tinkersreborn.library.blocks.ITinkersToolStationBlock;
import mctbl.tinkersreborn.library.entity.TinkersRebornInventoryLogic;
import mctbl.tinkersreborn.library.gui.GuiElement;
import mctbl.tinkersreborn.library.gui.GuiMultiModule;
import mctbl.tinkersreborn.library.gui.Icons;
import mctbl.tinkersreborn.library.gui.container.ContainerMultiModule;
import mctbl.tinkersreborn.library.utils.BlockPos;
import mctbl.tinkersreborn.tools.gui.module.GuiTinkerTabs;
import mctbl.tinkersreborn.tools.inventory.ContainerTinkerStation;

@SideOnly(Side.CLIENT)
// Takes care of the tinker station pseudo-multiblock
public class GuiTinkerStation extends GuiMultiModule {

    public static final ResourceLocation BLANK_BACK = new ResourceLocation(
        TinkersReborn.MODID,
        "textures/gui/blank.png");

    protected final ContainerMultiModule<?> container;

    protected GuiTinkerTabs tinkerTabs;
    private final World world;

    public GuiTinkerStation(World world, BlockPos pos, ContainerTinkerStation<?> container) {
        super(container);

        this.world = world;
        this.container = container;

        tinkerTabs = new GuiTinkerTabs(this, container);
        addModule(tinkerTabs);

        // add tab data
        if (container.hasCraftingStation) {
            for (Pair<BlockPos, Block> pair : container.tinkerStationBlocks) {
                Block block = pair.getRight();
                BlockPos blockPos = pair.getLeft();

                ItemStack stack = block.getDrops(world, blockPos.x, blockPos.y, blockPos.z, 0, 0)
                    .get(0);
                tinkerTabs.addTab(stack, blockPos);
            }
        }

        // preselect the correct tab
        for (int i = 0; i < tinkerTabs.tabData.size(); i++) {
            if (tinkerTabs.tabData.get(i)
                .equals(pos)) {
                tinkerTabs.tabs.selected = i;
            }
        }
    }

    protected void drawIcon(Slot slot, GuiElement element) {
        this.mc.getTextureManager()
            .bindTexture(Icons.ICON);
        element.draw(slot.xDisplayPosition + this.cornerX - 1, slot.yDisplayPosition + this.cornerY - 1);
    }

    protected void drawIconEmpty(Slot slot, GuiElement element) {
        if (slot.getHasStack()) {
            return;
        }
        drawIcon(slot, element);
    }

    public void onTabSelection(int selection) {
        if (selection < 0 || selection > tinkerTabs.tabData.size()) {
            return;
        }

        BlockPos pos = tinkerTabs.tabData.get(selection);
        Block b = world.getBlock(pos.x, pos.y, pos.z);
        if (b instanceof ITinkersToolStationBlock) {
            TileEntity te = world.getTileEntity(pos.x, pos.y, pos.z);
            if (te instanceof TinkersRebornInventoryLogic) {
                // TinkerNetwork.sendToServer(new TinkerStationTabPacket(pos));
            }

            // sound!
            mc.getSoundHandler()
                .playSound(PositionedSoundRecord.func_147674_a(new ResourceLocation("gui.button.press"), 1.0F));
        }
    }

    public void error(String message) {}

    public void warning(String message) {}

    public void updateDisplay() {}
}
