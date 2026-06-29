package mctbl.tinkersreborn.tools.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mctbl.tinkersreborn.TinkersReborn;
import mctbl.tinkersreborn.library.gui.GuiElement;
import mctbl.tinkersreborn.library.gui.container.ContainerMultiModule;
import mctbl.tinkersreborn.library.utils.BlockPos;
import mctbl.tinkersreborn.tools.entity.PartBuilderLogic;
import mctbl.tinkersreborn.tools.gui.module.GuiInfoPanel;
import mctbl.tinkersreborn.tools.inventory.ContainerPartBuilder;
import mctbl.tinkersreborn.tools.inventory.ContainerTinkerStation;
import mctbl.tinkersreborn.util.ColorUtil;
import mctbl.tinkersreborn.util.TinkersRebornUtils;
import mctbl.tinkersreborn.util.TinkersStr;

@SideOnly(Side.CLIENT)
public class GuiPartBuilder extends GuiTinkerStation {

    private static final ResourceLocation BACKGROUND = new ResourceLocation(
        TinkersReborn.MODID,
        "textures/gui/partbuilder.png");

    private static final GuiElement ScarollBar = new GuiElement(177, 0, 12, 15, 256, 256);
    private static final GuiElement ScarollBarHolding = new GuiElement(189, 0, 12, 15, 256, 256);
    private static final GuiElement ButtonBackGround = new GuiElement(177, 16, 18, 18, 256, 256);
    private static final GuiElement ButtonBackGroundCeleted = new GuiElement(177, 34, 18, 18, 256, 256);
    private static final GuiElement PatternOutline = new GuiElement(177, 70, 18, 18, 256, 256);
    private static final GuiElement MaterialOutline = new GuiElement(177, 88, 18, 18, 256, 256);

    protected GuiInfoPanel materialInfo;

    public GuiPartBuilder(InventoryPlayer inventoryplayer, PartBuilderLogic tile, World world, int x, int y, int z) {
        super(
            world,
            BlockPos.of(x, y, z),
            (ContainerTinkerStation<?>) tile.getGuiContainer(inventoryplayer, world, x, y, z));
        materialInfo = new GuiInfoPanel(this, inventorySlots);
        this.addModule(materialInfo);
        materialInfo.ySizeBias(83);
    }

    @Override
    public void initGui() {
        // TODO Auto-generated method stub
        super.initGui();

        this.updateDisplay();
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        drawBackground(BACKGROUND);
        this.drawSlotIcons();

        super.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);
    }

    protected void drawSlotIcons() {
        PatternOutline.draw(this.guiLeft + 8, this.guiTop + 35);
        ContainerPartBuilder container = (ContainerPartBuilder) inventorySlots;
        if (container.material == null) {
            MaterialOutline.draw(this.guiLeft + 29, this.guiTop + 35);
        }
    }

    @Override
    public void updateDisplay() {
        // tool info of existing or tool to build
        ContainerPartBuilder container = (ContainerPartBuilder) inventorySlots;
        if (container.material != null) {
            materialInfo.setCaption(
                ColorUtil.encodeColor(container.material.materialTextColor) + container.material.localizedPrefix());
            if (container.materialCount >= 1) {
                materialInfo
                    .setText(String.format(TinkersStr.partCrafterMaterialValue.toString(), container.materialCount));
            } else {
                materialInfo.setText("");
            }
        } else {
            materialInfo.setCaption(TinkersRebornUtils.translate("tinkersreborn.PartBuilder.name"));
            materialInfo.setText(TinkersStr.partCrafterInfo.toString());
        }
    }

    @Override
    protected void drawContainerName() {
        ContainerMultiModule<?> multiContainer = (ContainerMultiModule<?>) this.inventorySlots;
        String localizedName = multiContainer.getInventoryDisplayName();
        if (localizedName != null) {
            GL11.glScaled(0.8F, 0.8F, 0.8F);
            this.fontRendererObj.drawString(localizedName, 5, (int) (6 / 0.8f), 0x404040);
            GL11.glScaled(1.25F, 1.25F, 1.25F);
        }
    }

    @Override
    protected void drawPlayerInventoryName() {
        String localizedName = Minecraft.getMinecraft().thePlayer.inventory.getInventoryName();
        GL11.glScaled(0.8F, 0.8F, 0.8F);
        this.fontRendererObj
            .drawString(TinkersRebornUtils.translate(localizedName), 5, (int) ((this.ySize - 96 + 2) / 0.8f), 0x404040);
        GL11.glScaled(1.25F, 1.25F, 1.25F);
    }
}
