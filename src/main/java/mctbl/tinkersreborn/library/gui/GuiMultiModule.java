package mctbl.tinkersreborn.library.gui;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mctbl.tinkersreborn.library.gui.container.ContainerMultiModule;

@SideOnly(Side.CLIENT)
public class GuiMultiModule extends GuiContainer { // implements INEIGuiHandler {

    protected List<GuiModule> modules = new ArrayList<>();

    public int cornerX;
    public int cornerY;
    public int realWidth;
    public int realHeight;

    private Slot hoveredSlot;

    public GuiMultiModule(ContainerMultiModule container) {
        super(container);

        realWidth = -1;
        realHeight = -1;
    }

    protected void addModule(GuiModule module) {
        modules.add(module);
    }

    public List<Rectangle> getModuleAreas() {
        List<Rectangle> areas = new ArrayList<Rectangle>(modules.size());
        for (GuiModule module : modules) {
            areas.add(module.getArea());
        }
        return areas;
    }

    @Override
    public void initGui() {
        if (realWidth > -1) {
            // has to be reset before calling initGui so the position is getting retained
            xSize = realWidth;
            ySize = realHeight;
        }
        super.initGui();

        this.cornerX = this.guiLeft;
        this.cornerY = this.guiTop;
        this.realWidth = xSize;
        this.realHeight = ySize;

        for (GuiModule module : modules) {
            updateSubmodule(module);
        }

    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        for (GuiModule module : modules) {
            module.handleDrawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);
        }
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        drawContainerName();
        drawPlayerInventoryName();

        for (GuiModule module : modules) {
            // set correct state for the module
            GL11.glPushMatrix();
            GL11.glTranslatef(-this.guiLeft, -this.guiTop, 0.0F);
            GL11.glTranslatef(module.guiLeft(), module.guiTop(), 0.0F);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            module.handleDrawGuiContainerForegroundLayer(mouseX, mouseY);
            GL11.glPopMatrix();
        }
    }

    protected void drawBackground(ResourceLocation background) {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager()
            .bindTexture(background);
        this.drawTexturedModalRect(cornerX, cornerY, 0, 0, realWidth, realHeight);
    }

    protected void drawContainerName() {
        ContainerMultiModule multiContainer = (ContainerMultiModule) this.inventorySlots;
        String localizedName = multiContainer.getInventoryDisplayName();
        if (localizedName != null) {
            this.fontRendererObj.drawString(localizedName, 8, 6, 0x404040);
        }
    }

    protected void drawPlayerInventoryName() {
        String localizedName = Minecraft.getMinecraft().thePlayer.inventory.getInventoryName();
        this.fontRendererObj.drawString(localizedName, 8, this.ySize - 96 + 2, 0x404040);
    }

    @Override
    public void setWorldAndResolution(Minecraft mc, int width, int height) {
        super.setWorldAndResolution(mc, width, height);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        int oldX = guiLeft;
        int oldY = guiTop;
        int oldW = xSize;
        int oldH = ySize;

        guiLeft = cornerX;
        guiTop = cornerY;
        xSize = realWidth;
        ySize = realHeight;
        super.drawScreen(mouseX, mouseY, partialTicks);

        this.updateHoverSlot(mouseX, mouseY);

        this.renderHoveredToolTip(mouseX, mouseY);
        guiLeft = oldX;
        guiTop = oldY;
        xSize = oldW;
        ySize = oldH;
    }

    public void updateHoverSlot(int mouseX, int mouseY) {
        for (Slot s : this.inventorySlots.inventorySlots) {
            if (s.func_111238_b()
                && this.func_146978_c(s.xDisplayPosition, s.yDisplayPosition, 16, 16, mouseX, mouseY)) {
                this.hoveredSlot = s;
                return;
            }
        }
    }

    public boolean func_146978_c(int left, int top, int right, int bottom, int pointX, int pointY) {
        pointX -= this.cornerX;
        pointY -= this.cornerY;
        return pointX >= left - 1 && pointX < left + right + 1 && pointY >= top - 1 && pointY < top + bottom + 1;
    }

    public void renderHoveredToolTip(int mouseX, int mouseY) {
        if (this.mc.thePlayer.inventory.getItemStack() == null && this.hoveredSlot != null
            && this.hoveredSlot.getStack() != null) {
            this.renderToolTip(this.hoveredSlot.getStack(), mouseX, mouseY);
        }
    }

    protected void updateSubmodule(GuiModule module) {
        module.updatePosition(this.cornerX, this.cornerY, this.realWidth, this.realHeight);
        module.mc = this.mc;

        if (module.guiLeft() < this.guiLeft) {
            this.xSize += this.guiLeft - module.guiLeft();
            this.guiLeft = module.guiLeft();
        }
        if (module.guiTop() < this.guiTop) {
            this.ySize += this.guiTop - module.guiTop();
            this.guiTop = module.guiTop();
        }
        if (module.guiRight() > this.guiLeft + this.xSize) {
            xSize = module.guiRight() - this.guiLeft;
        }
        if (module.guiBottom() > this.guiTop + this.ySize) {
            ySize = module.guiBottom() - this.guiTop;
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        GuiModule module = getModuleForPoint(mouseX, mouseY);
        if (module != null) {
            if (module.handleMouseClicked(mouseX, mouseY, mouseButton)) {
                return;
            }
        }
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
        GuiModule module = getModuleForPoint(mouseX, mouseY);
        if (module != null) {
            if (module.handleMouseClickMove(mouseX, mouseY, clickedMouseButton, timeSinceLastClick)) {
                return;
            }
        }

        super.mouseClickMove(mouseX, mouseY, clickedMouseButton, timeSinceLastClick);
    }

    protected GuiModule getModuleForPoint(int x, int y) {
        for (GuiModule module : modules) {
            if (this.func_146978_c(
                module.guiLeft(),
                module.guiTop(),
                module.guiRight(),
                module.guiBottom(),
                x + this.cornerX,
                y + this.cornerY)) {
                return module;
            }
        }

        return null;
    }

    protected GuiModule getModuleForSlot(int slotNumber) {
        return getModuleForContainer(getContainer().getSlotContainer(slotNumber));
    }

    protected GuiModule getModuleForContainer(Container container) {
        for (GuiModule module : modules) {
            if (module.inventorySlots == container) {
                return module;
            }
        }

        return null;
    }

    protected ContainerMultiModule getContainer() {
        return (ContainerMultiModule) inventorySlots;
    }

    public void renderItemIntoGui(ItemStack stack, int xPos, int yPos) {
        itemRender.renderItemAndEffectIntoGUI(fontRendererObj, this.mc.getTextureManager(), stack, xPos, yPos);
    }

    public RenderItem getRenderItem() {
        return itemRender;
    }

    public int guiLeft() {
        return this.guiLeft;
    }

    public void guiLeftBias(int bias) {
        this.guiLeft += bias;
    }

    public int guiTop() {
        return this.guiTop;
    }

    public void guiTopBias(int bias) {
        this.guiTop += bias;
    }

    public int xSize() {
        return this.xSize;
    }

    public void xSizeBias(int bias) {
        this.xSize += bias;
    }

    public int ySize() {
        return this.ySize;
    }

    public void ySizeBias(int bias) {
        this.ySize += bias;
    }
}
