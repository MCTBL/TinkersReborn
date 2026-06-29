package mctbl.tinkersreborn.tools.gui;

import static mctbl.tinkersreborn.util.TinkersRebornUtils.translate;

import java.util.List;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.Point;

import com.google.common.collect.Lists;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mctbl.tinkersreborn.TinkersReborn;
import mctbl.tinkersreborn.common.network.TinkerNetwork;
import mctbl.tinkersreborn.library.TinkersRebornRegistry;
import mctbl.tinkersreborn.library.gui.GuiElement;
import mctbl.tinkersreborn.library.gui.GuiElementScalable;
import mctbl.tinkersreborn.library.gui.GuiModule;
import mctbl.tinkersreborn.library.gui.Icons;
import mctbl.tinkersreborn.library.tools.IModifier;
import mctbl.tinkersreborn.library.tools.IModifyable;
import mctbl.tinkersreborn.library.tools.IToolPart;
import mctbl.tinkersreborn.library.tools.ToolCore;
import mctbl.tinkersreborn.library.tools.ToolCore.ToolPartRecord;
import mctbl.tinkersreborn.library.tools.modifiers.ModifierNBT;
import mctbl.tinkersreborn.library.utils.BlockPos;
import mctbl.tinkersreborn.tools.entity.ToolStationLogic;
import mctbl.tinkersreborn.tools.gui.module.GuiInfoPanel;
import mctbl.tinkersreborn.tools.inventory.ContainerTinkerStation;
import mctbl.tinkersreborn.tools.inventory.ContainerToolStation;
import mctbl.tinkersreborn.tools.inventory.slots.SlotToolStationIn;
import mctbl.tinkersreborn.tools.items.TinkersRebornToolPart;
import mctbl.tinkersreborn.tools.network.ToolStationSelectionPacket;
import mctbl.tinkersreborn.tools.network.ToolStationTextPacket;
import mctbl.tinkersreborn.util.TinkersRebornUtils;
import mctbl.tinkersreborn.util.TinkersStr;
import mctbl.tinkersreborn.util.ToolTagsHelper;

@SideOnly(Side.CLIENT)
public class GuiToolStation extends GuiTinkerStation {

    private static final ResourceLocation BACKGROUND = new ResourceLocation(
        TinkersReborn.MODID,
        "textures/gui/toolstation.png");

    private static final GuiElement TextFieldActive = new GuiElement(0, 210, 102, 12, 256, 256);
    private static final GuiElement ItemCover = new GuiElement(176, 18, 80, 64);
    private static final GuiElement SlotBackground = new GuiElement(176, 0, 18, 18);
    private static final GuiElement SlotBorder = new GuiElement(194, 0, 18, 18);

    // private static final GuiElement ArrowLeft = new GuiElement(8, 241, 8, 15,
    // 256, 256);
    private static final GuiElement ArrowRight = new GuiElement(0, 241, 8, 15, 256, 256);

    private static final GuiElement SlotSpaceTop = new GuiElement(0, 174 + 2, 18, 2);
    private static final GuiElement SlotSpaceBottom = new GuiElement(0, 174, 18, 2);
    private static final GuiElement PanelSpaceL = new GuiElement(0, 174, 5, 4);
    private static final GuiElement PanelSpaceR = new GuiElement(9, 174, 9, 4);

    private static final GuiElement BeamLeft = new GuiElement(0, 180, 2, 7);
    private static final GuiElement BeamRight = new GuiElement(131, 180, 2, 7);
    private static final GuiElementScalable BeamCenter = new GuiElementScalable(2, 180, 129, 7);

    private static final int Table_slot_count = 6;

    protected GuiElement buttonDecorationTop = SlotSpaceTop;
    protected GuiElement buttonDecorationBot = SlotSpaceBottom;
    protected GuiElement panelDecorationL = PanelSpaceL;
    protected GuiElement panelDecorationR = PanelSpaceR;

    protected GuiElement beamL = new GuiElement(0, 0, 0, 0);
    protected GuiElement beamR = new GuiElement(0, 0, 0, 0);
    protected GuiElementScalable beamC = new GuiElementScalable(0, 0, 0, 0);

    protected GuiButtonsToolStation buttons;
    protected int activeSlots; // how many of the available slots are active

    public GuiTextField textField;

    protected GuiInfoPanel toolInfo;
    protected GuiInfoPanel traitInfo;

    public ToolBuildGuiInfo currentInfo = ToolBuildGuiInfo.repairInfo;

    public GuiToolStation(InventoryPlayer playerInv, World world, BlockPos pos, ToolStationLogic tile) {
        super(world, pos, (ContainerTinkerStation<?>) tile.getGuiContainer(playerInv, world, pos.x, pos.y, pos.z));

        buttons = new GuiButtonsToolStation(this, inventorySlots);
        this.addModule(buttons);
        toolInfo = new GuiInfoPanel(this, inventorySlots);
        this.addModule(toolInfo);
        traitInfo = new GuiInfoPanel(this, inventorySlots);
        this.addModule(traitInfo);

        toolInfo.yOffset = 5;
        traitInfo.yOffset = toolInfo.ySize() + 9;

        this.ySize = 174;

        wood();
    }

    @Override
    public void initGui() {
        buttons.xOffset = -2;
        buttons.yOffset = beamC.h + buttonDecorationTop.h + 4;
        toolInfo.xOffset = 2;
        toolInfo.yOffset = beamC.h + panelDecorationL.h;
        traitInfo.xOffset = toolInfo.xOffset;
        traitInfo.yOffset = toolInfo.yOffset + toolInfo.ySize() + 4;

        super.initGui();
        Keyboard.enableRepeatEvents(true);

        // workaround to line up the tabs on switching even though the GUI is a tad
        // higher
        this.guiTop += 4;
        this.cornerY += 4;

        textField = new GuiTextField(fontRendererObj, cornerX + 70, cornerY + 7, 92, 12);
        textField.setEnableBackgroundDrawing(false);
        textField.setMaxStringLength(40);

        for (GuiModule module : modules) {
            module.guiTopBias(+4);
        }

        updateGUI();
    }

    public void updateGUI() {
        int i;
        for (i = 0; i < activeSlots; i++) {
            Point point = currentInfo.positions.get(i);

            Slot slot = inventorySlots.getSlot(i);
            slot.xDisplayPosition = point.getX();
            slot.yDisplayPosition = point.getY();
        }

        // remaining slots
        int stillFilled = 0;
        for (; i < Table_slot_count; i++) {
            Slot slot = inventorySlots.getSlot(i);

            if (slot.getHasStack()) {
                slot.xDisplayPosition = 87 + 20 * stillFilled;
                slot.yDisplayPosition = 62;
                stillFilled++;
            } else {
                // todo: slot.disable
                slot.xDisplayPosition = 0;
                slot.yDisplayPosition = 0;
            }
        }

        this.updateDisplay();
    }

    @Override
    public void updateDisplay() {
        // tool info of existing or tool to build
        ContainerToolStation container = (ContainerToolStation) inventorySlots;
        ItemStack toolStack = container.getResult();

        if (TinkersRebornUtils.isStackEmpty(toolStack)) {
            toolStack = inventorySlots.getSlot(0)
                .getStack();
        }

        if (toolStack != null && toolStack.getItem() instanceof IModifyable) {
            // current tool to build or repair/modify
            if (toolStack.getItem() instanceof ToolCore tool) {
                toolInfo.setCaption(tool.getLocalizedToolName());
                toolInfo.setText(tool.getInformation(toolStack));
            } else {
                toolInfo.setCaption(toolStack.getDisplayName());
                toolInfo.setText();
            }

            traitInfo.setCaption(TinkersStr.toolStationTraitTitle.toString());

            List<String> mods = Lists.newLinkedList();
            List<String> tips = Lists.newLinkedList();

            List<NBTTagCompound> tagList = ToolTagsHelper.getModifiersList(toolStack);
            for (NBTTagCompound tag : tagList) {
                ModifierNBT data = ModifierNBT.readTag(tag);

                // get matching modifier
                IModifier modifier = TinkersRebornRegistry.getModifierAndTrait(data.identifier);
                if (modifier == null || modifier.isHidden()) {
                    continue;
                }

                mods.add(data.getColorString() + modifier.getTooltip(tag, true));
                tips.add(data.getColorString() + modifier.getLocalizedDesc());
            }

            if (mods.isEmpty()) {
                mods.add(TinkersStr.toolStationNoTrait.toString());
            }

            traitInfo.setText(mods, tips);
        } else if (TinkersRebornUtils.isStackEmpty(currentInfo.tool)) {
            // repair info
            toolInfo.setCaption(TinkersStr.toolStationRepairTitle.toString());
            toolInfo.setText(TinkersStr.toolStationRepairDesc.toString());

            traitInfo.setCaption(null);
            String c = EnumChatFormatting.DARK_GRAY.toString();
            // looks like a hammer
            String[] art = new String[] { c + "", c + "", c + "       .", c + "     /( _________",
                c + "     |  >:=========`", c + "     )(  ", c + "     \"\"" };
            traitInfo.setText(art);

        } else {
            // tool build info
            ToolCore tool = (ToolCore) currentInfo.tool.getItem();
            toolInfo.setCaption(tool.getLocalizedToolName());
            toolInfo.setText(tool.getLocalizedDescription());

            // Components
            List<String> text = Lists.newLinkedList();
            List<ToolPartRecord> pms = tool.getToolComponentsParts();
            for (int i = 0; i < pms.size(); i++) {
                ToolPartRecord tpr = pms.get(i);
                StringBuilder sb = new StringBuilder();

                ItemStack slotStack = container.getSlot(i)
                    .getStack();
                if (!tpr.isValid(slotStack)) {
                    sb.append(EnumChatFormatting.RED);

                    // is an item in the slot?
                    if (!TinkersRebornUtils.isStackEmpty(slotStack) && slotStack.getItem() instanceof IToolPart itp) {
                        if (tpr.isValidItem(itp)) {
                            // the item has an invalid material
                            warning(translate("gui.error.wrong_material_part"));
                        }
                    }
                }

                sb.append(" * ");
                TinkersRebornToolPart tp = tpr.toolPart();
                sb.append(tp.getItemStackDisplayName(new ItemStack(tp)));
                sb.append("/");

                sb.deleteCharAt(sb.length() - 1); // removes last '/'
                text.add(sb.toString());
            }
            traitInfo.setCaption(TinkersStr.toolStationComponentTitle.toString());
            traitInfo.setText(text.toArray(new String[text.size()]));
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        textField.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) {
        if (!textField.isFocused()) {
            super.keyTyped(typedChar, keyCode);
        } else {
            if (keyCode == 1) {
                this.mc.thePlayer.closeScreen();
            }

            textField.textboxKeyTyped(typedChar, keyCode);
            TinkerNetwork.sendToServer(new ToolStationTextPacket(textField.getText()));
            ((ContainerToolStation) container).setToolName(textField.getText());
        }
    }

    @Override
    public void updateScreen() {
        super.updateScreen();
        textField.updateCursorCounter();
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        drawBackground(BACKGROUND);

        // looks like there's a weird case where this is called before init? Not
        // reproducible but meh.
        if (textField != null) {
            if (textField.isFocused()) {
                TextFieldActive.draw(cornerX + 68, cornerY + 6);
            }

            // draw textfield
            textField.drawTextBox();
        }

        // int xOff = 3;
        // int yOff = 6;

        int x = 0;
        int y = 0;

        // draw the item background
        final float scale = 3.7f;
        final float xOff = 10f;
        final float yOff = 22f;
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glPushMatrix();
        GL11.glTranslatef(xOff, yOff, 0);
        GL11.glScalef(scale, scale, 1.0f);
        {
            int logoX = (int) (this.cornerX / scale);
            int logoY = (int) (this.cornerY / scale);

            if (currentInfo != null) {
                if (!TinkersRebornUtils.isStackEmpty(currentInfo.tool)) {
                    itemRender.renderItemAndEffectIntoGUI(
                        fontRendererObj,
                        this.mc.getTextureManager(),
                        currentInfo.tool,
                        logoX,
                        logoY);
                } else if (currentInfo == ToolBuildGuiInfo.repairInfo) {
                    this.mc.getTextureManager()
                        .bindTexture(Icons.ICON);
                    Icons.ICON_Anvil.draw(logoX, logoY);
                }
            }
        }
        GL11.glPopMatrix();

        // rebind gui texture since itemstack drawing sets it to something else
        this.mc.getTextureManager()
            .bindTexture(BACKGROUND);

        // reset state after item drawing
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_ALPHA_TEST);
        RenderHelper.disableStandardItemLighting();
        GL11.glDisable(GL11.GL_DEPTH_TEST);

        // draw the halftransparent "cover" over the item
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 0.82f);
        ItemCover.draw(this.cornerX + 7, this.cornerY + 18);

        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        ArrowRight.draw(cornerX + 104, cornerY + 38);

        // the slot backgrounds
        for (int i = 0; i < activeSlots; i++) {
            Slot slot = inventorySlots.getSlot(i);
            GL11.glColor4f(1.0f, 1.0f, 1.0f, 0.28f);
            SlotBackground
                .draw(x + this.cornerX + slot.xDisplayPosition - 1, y + this.cornerY + slot.yDisplayPosition - 1);
        }

        // full opaque. Draw the borders of the slots
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        for (int i = 0; i < Table_slot_count; i++) {
            Slot slot = inventorySlots.getSlot(i);
            if (slot instanceof SlotToolStationIn && (!((SlotToolStationIn) slot).isDormant() || slot.getHasStack())) {
                SlotBorder
                    .draw(x + this.cornerX + slot.xDisplayPosition - 1, y + this.cornerY + slot.yDisplayPosition - 1);
            }
        }

        this.mc.getTextureManager()
            .bindTexture(Icons.ICON);

        // slot logos
        if (currentInfo == ToolBuildGuiInfo.repairInfo) {
            drawRepairSlotIcons();
        } else if (currentInfo.tool.getItem() instanceof ToolCore) {
            for (int i = 0; i < activeSlots; i++) {
                Slot slot = inventorySlots.getSlot(i);
                if (!(slot instanceof SlotToolStationIn)) {
                    continue;
                }

                IIcon icon = ((SlotToolStationIn) slot).icon;
                if (icon == null || slot.getHasStack()) {
                    continue;
                }

                this.mc.getTextureManager()
                    .bindTexture(TextureMap.locationItemsTexture);
                drawTexturedModelRectFromIcon(
                    x + this.cornerX + slot.xDisplayPosition,
                    y + this.cornerY + slot.yDisplayPosition,
                    icon,
                    16,
                    16);
            }
        }

        this.mc.getTextureManager()
            .bindTexture(BACKGROUND);
        x = buttons.guiLeft() - beamL.w;
        y = cornerY;
        // draw the beams at the top
        x += beamL.draw(x, y);
        x += beamC.drawScaledX(x, y, buttons.xSize());
        beamR.draw(x, y);

        x = toolInfo.guiLeft() - beamL.w;
        x += beamL.draw(x, y);
        x += beamC.drawScaledX(x, y, toolInfo.xSize());
        beamR.draw(x, y);

        // draw the decoration for the buttons
        for (GuiButton button : buttons.getButtonList()) {
            buttonDecorationTop.draw(button.xPosition, button.yPosition - buttonDecorationTop.h);
            // don't draw the bottom for the buttons in the last row
            if (button.id < buttons.getButtonList()
                .size() - 5) {
                buttonDecorationBot.draw(button.xPosition, button.yPosition + button.height);
            }
        }

        // draw the decorations for the panels
        panelDecorationL.draw(toolInfo.guiLeft() + 5, toolInfo.guiTop() - panelDecorationL.h);
        panelDecorationR.draw(toolInfo.guiRight() - 5 - panelDecorationR.w, toolInfo.guiTop() - panelDecorationR.h);
        panelDecorationL.draw(traitInfo.guiLeft() + 5, traitInfo.guiTop() - panelDecorationL.h);
        panelDecorationR.draw(traitInfo.guiRight() - 5 - panelDecorationR.w, traitInfo.guiTop() - panelDecorationR.h);

        // continue as usual and hope that the drawing state is not completely wrecked
        super.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);
    }

    protected void drawRepairSlotIcons() {
        for (int i = 0; i < activeSlots; i++) {
            drawRepairSlotIcon(i);
        }
    }

    protected void drawRepairSlotIcon(int i) {
        GuiElement icon = null;
        Slot slot = inventorySlots.getSlot(i);
        // only empty solts get the logo since something else than the displayed thing
        // might be in there.
        // which would look weird.
        if (slot.getHasStack()) {
            return;
        }

        if (i == 0) {
            icon = Icons.ICON_Pickaxe;
        } else if (i == 1) {
            icon = Icons.ICON_Dust;
        } else if (i == 2) {
            icon = Icons.ICON_Lapis;
        } else if (i == 3) {
            icon = Icons.ICON_Ingot;
        } else if (i == 4) {
            icon = Icons.ICON_Gem;
        } else if (i == 5) {
            icon = Icons.ICON_Quartz;
        }

        if (icon != null) {
            drawIconEmpty(slot, icon);
        }
    }

    public List<ToolCore> getBuildableItems() {
        return TinkersRebornRegistry.toolStationCrafting;
    }

    public void onToolSelection(ToolBuildGuiInfo info) {
        activeSlots = Math.min(info.positions.size(), Table_slot_count);
        currentInfo = info;

        ToolCore tool = null;
        if (info == ToolBuildGuiInfo.repairInfo) {
            tool = null;
        } else if (info.tool.getItem() instanceof ToolCore t) {
            tool = t;
        }

        ((ContainerToolStation) inventorySlots).setToolSelection(tool, activeSlots);
        // update the server (and others)
        TinkerNetwork.sendToServer(new ToolStationSelectionPacket(tool, activeSlots));
        updateGUI();
    }

    public void onToolSelectionPacket(ToolStationSelectionPacket packet) {
        ToolBuildGuiInfo info;
        if (packet.tool == null) {
            info = ToolBuildGuiInfo.repairInfo;
        } else {
            info = packet.tool.getToolBuildGuiInfo();
        }

        activeSlots = packet.activeSlots;
        currentInfo = info;

        buttons.setSelectedButtonByTool(currentInfo.tool);

        updateGUI();
    }

    protected void wood() {
        toolInfo.wood();
        traitInfo.wood();

        buttonDecorationTop = SlotSpaceTop.shift(SlotSpaceTop.w, 0);
        buttonDecorationBot = SlotSpaceBottom.shift(SlotSpaceBottom.w, 0);
        panelDecorationL = PanelSpaceL.shift(18, 0);
        panelDecorationR = PanelSpaceR.shift(18, 0);

        buttons.wood();

        beamL = BeamLeft;
        beamR = BeamRight;
        beamC = BeamCenter;
    }

    protected void metal() {
        toolInfo.metal();
        traitInfo.metal();

        buttonDecorationTop = SlotSpaceTop.shift(SlotSpaceTop.w * 2, 0);
        buttonDecorationBot = SlotSpaceBottom.shift(SlotSpaceBottom.w * 2, 0);
        panelDecorationL = PanelSpaceL.shift(18 * 2, 0);
        panelDecorationR = PanelSpaceR.shift(18 * 2, 0);

        buttons.metal();

        beamL = BeamLeft.shift(0, BeamLeft.h);
        beamR = BeamRight.shift(0, BeamRight.h);
        beamC = BeamCenter.shift(0, BeamCenter.h);
    }

    @Override
    public void error(String message) {
        toolInfo.setCaption(TinkersStr.errorTitle.toString());
        toolInfo.setText(message);
        traitInfo.setCaption(null);
        traitInfo.setText();
    }

    @Override
    public void warning(String message) {
        toolInfo.setCaption(TinkersStr.warningTitle.toString());
        toolInfo.setText(message);
        traitInfo.setCaption(null);
        traitInfo.setText();
    }
}
