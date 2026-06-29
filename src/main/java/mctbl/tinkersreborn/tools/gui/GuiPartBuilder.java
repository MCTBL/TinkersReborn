package mctbl.tinkersreborn.tools.gui;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mctbl.tinkersreborn.TinkersReborn;
import mctbl.tinkersreborn.common.network.TinkerNetwork;
import mctbl.tinkersreborn.library.TinkersRebornRegistry;
import mctbl.tinkersreborn.library.gui.GuiDynButtons;
import mctbl.tinkersreborn.library.gui.GuiElement;
import mctbl.tinkersreborn.library.materials.TinkersRebornMaterial;
import mctbl.tinkersreborn.library.utils.BlockPos;
import mctbl.tinkersreborn.tools.entity.PartBuilderLogic;
import mctbl.tinkersreborn.tools.gui.module.GuiInfoPanel;
import mctbl.tinkersreborn.tools.inventory.ContainerPartBuilder;
import mctbl.tinkersreborn.tools.inventory.ContainerTinkerStation;
import mctbl.tinkersreborn.tools.items.TinkersRebornToolPart;
import mctbl.tinkersreborn.tools.network.PartBuilderSelectionPacket;
import mctbl.tinkersreborn.util.ColorUtil;
import mctbl.tinkersreborn.util.TinkersRebornUtils;
import mctbl.tinkersreborn.util.TinkersStr;

@SideOnly(Side.CLIENT)
public class GuiPartBuilder extends GuiTinkerStation {

    private static final ResourceLocation BACKGROUND = new ResourceLocation(
        TinkersReborn.MODID,
        "textures/gui/partbuilder.png");

    private static final GuiElement PatternOutline = new GuiElement(177, 70, 18, 18, 256, 256);
    private static final GuiElement MaterialOutline = new GuiElement(177, 88, 18, 18, 256, 256);

    protected GuiInfoPanel materialInfo;
    protected GuiDynButtons partSelector;

    public GuiPartBuilder(InventoryPlayer inventoryplayer, PartBuilderLogic tile, World world, int x, int y, int z) {
        super(
            world,
            BlockPos.of(x, y, z),
            (ContainerTinkerStation<?>) tile.getGuiContainer(inventoryplayer, world, x, y, z));
        materialInfo = new GuiInfoPanel(this, inventorySlots);
        this.addModule(materialInfo);
        materialInfo.ySizeBias(83);

        partSelector = new GuiDynButtons(this, new ArrayList<>(TinkersRebornRegistry.toolPartNameMap.values()));
        this.addModuleFirst(partSelector);
    }

    @Override
    public void initGui() {
        super.initGui();

        this.updateDisplay();
    }

    public void onPartSelection(TinkersRebornToolPart toolPart) {
        ((ContainerPartBuilder) inventorySlots).toolPartSelected(toolPart);

        // update the server (and others)
        TinkerNetwork.sendToServer(new PartBuilderSelectionPacket(toolPart));
        this.updateDisplay();
    }

    public void onPartSelectionPacket(PartBuilderSelectionPacket packet) {
        this.updateDisplay();
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        drawBackground(BACKGROUND);
        this.drawSlotIcons();
        partSelector.update(mouseX, mouseY);
        super.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);
    }

    protected void drawSlotIcons() {
        PatternOutline.draw(this.guiLeft + 8, this.guiTop + 35);
        ContainerPartBuilder container = (ContainerPartBuilder) inventorySlots;
        if (container.getInputMaterial() == null) {
            MaterialOutline.draw(this.guiLeft + 29, this.guiTop + 35);
        }
    }

    @Override
    public void updateDisplay() {
        ContainerPartBuilder container = (ContainerPartBuilder) inventorySlots;
        if (container.getInputMaterial() != null) {
            List<String> materialInfoText = new ArrayList<>();

            materialInfoText.add(
                ColorUtil.addGold(
                    String.format(
                        TinkersStr.patternToolTip.toString(),
                        container.getSelectedToolPart()
                            .getCost() / TinkersRebornMaterial.VALUE_Ingot)));
            if (container.getMaterialValue() >= 1) {
                materialInfoText.add(
                    ColorUtil.addAqua(
                        String.format(
                            TinkersStr.partCrafterMaterialValue.toString(),
                            container.getMaterialValue() / TinkersRebornMaterial.VALUE_Ingot)));
            }

            materialInfo.setCaption(
                ColorUtil.encodeColor(container.getInputMaterial().materialTextColor) + container.getInputMaterial()
                    .localizedPrefix());
            materialInfo.setText(materialInfoText);
        } else {
            materialInfo.setCaption(TinkersRebornUtils.translate("tinkersreborn.PartBuilder.name"));
            materialInfo.setText(TinkersStr.partCrafterInfo.toString());
        }
    }

}
