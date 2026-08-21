package mctbl.tinkersreborn.library.gui;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mctbl.tinkersreborn.TinkersReborn;
import mctbl.tinkersreborn.common.manuals.TinkersRebornTurnPageButton;
import mctbl.tinkersreborn.common.manuals.TinkersRebornTurnPageButton.ButtonType;
import mctbl.tinkersreborn.library.manuals.AbstractManualPage;
import mctbl.tinkersreborn.library.manuals.ManualBookData;

@SideOnly(Side.CLIENT)
public class GuiManual extends GuiScreen {

    private static final ResourceLocation bookTexture = new ResourceLocation(
        "tinkersreborn",
        "textures/gui/book/book.png");

    private static final float GUIMAXPERCENTAGE = 0.85f; // The maximum percentage that the manual GUI can occupy
    public static int COVER_WIDTH = 206;
    public static int COVER_HEIGHT = 200;
    public static int GUI_MARGIN = 12;

    protected int bookTotalPages;
    protected Queue<Integer> jumpFromPage = new LinkedList<>();
    protected int currentPage;
    protected int previousRenderPage;
    protected ManualBookData bookData;

    public float scale = 1.0f;
    protected int guiLeft;
    protected int guiTop;
    /*
     * [0, 19]
     */
    protected int manualTicks;

    private TinkersRebornTurnPageButton buttonNextPage;
    private TinkersRebornTurnPageButton buttonPreviousPage;
    private TinkersRebornTurnPageButton buttonHomePage;
    private TinkersRebornTurnPageButton buttonBackToJumpFrom;

    public final float backgroundR;
    public final float backgroundG;
    public final float backgroundB;

    public RenderItem renderItem = new RenderItem();

    public GuiManual(ManualBookData bookData) {
        this.bookData = bookData;
        this.currentPage = 0;
        this.previousRenderPage = 0;

        int backGroundColor = bookData.getDefinition()
            .getColor();

        this.backgroundR = ((backGroundColor >> 16) & 0xFF) / 255.0f;
        this.backgroundG = ((backGroundColor >> 8) & 0xFF) / 255.0f;
        this.backgroundB = (backGroundColor & 0xFF) / 255.0f;
    }

    @Override
    public void updateScreen() {
        super.updateScreen();
        this.manualTicks = this.manualTicks++ % 20;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        if (this.previousRenderPage != this.currentPage) {
            this.previousRenderPage = this.currentPage;
            this.mc.getSoundHandler()
                .playSound(PositionedSoundRecord.func_147673_a(new ResourceLocation(TinkersReborn.MODID, "turn_page")));
        }

        this.drawDefaultBackground();

        int manualMouseX = this.screenToManualX(mouseX);
        int manualMouseY = this.screenToManualY(mouseY);

        GL11.glPushMatrix();
        GL11.glPushAttrib(
            GL11.GL_ENABLE_BIT | GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT | GL11.GL_TEXTURE_BIT);

        try {
            GL11.glTranslatef(this.guiLeft, this.guiTop, 0.0F);
            GL11.glScalef(this.scale, this.scale, 1.0F);

            this.setupManualRenderState();
            this.drawManualBackground();
            this.drawManualControls(manualMouseX, manualMouseY);

            this.drawManualPages(manualMouseX, manualMouseY, partialTicks);
        } finally {
            GL11.glPopAttrib();
            GL11.glPopMatrix();
        }

        super.drawScreen(mouseX, mouseY, partialTicks);

        // drawManualTooltip(mouseX, mouseY, manualMouseX, manualMouseY);
    }

    @Override
    public void initGui() {
        super.initGui();
        this.bookData.getPages()
            .forEach(AbstractManualPage::setupTranslate);
        this.manualTicks = 0;

        this.scale = Math.max(
            0.99f,
            Math.min(
                (this.width - GUI_MARGIN * 2) * GUIMAXPERCENTAGE / (COVER_WIDTH * 2),
                (this.height - GUI_MARGIN * 2) * GUIMAXPERCENTAGE / COVER_HEIGHT));

        int renderedWidth = Math.round(COVER_WIDTH * 2 * scale);
        int renderedHeight = Math.round(COVER_HEIGHT * scale);

        this.guiLeft = (this.width - renderedWidth) / 2;
        this.guiTop = (this.height - renderedHeight) / 2;

        this.buttonNextPage = new TinkersRebornTurnPageButton(1, COVER_WIDTH * 2 - 40, 172, ButtonType.nextPage);
        this.buttonPreviousPage = new TinkersRebornTurnPageButton(2, 22, 172, ButtonType.previousPage);
        this.buttonHomePage = new TinkersRebornTurnPageButton(3, 4, 5, ButtonType.homePage);
        this.buttonBackToJumpFrom = new TinkersRebornTurnPageButton(4, COVER_WIDTH - 9, 178, ButtonType.backToJumpFrom);

        this.bookTotalPages = this.bookData.getPages()
            .size();
    }

    private void setupManualRenderState() {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_BLEND);

        OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0);

        GL11.glDisable(GL11.GL_LIGHTING);
    }

    private void drawManualBackground() {
        this.mc.getTextureManager()
            .bindTexture(bookTexture);
        GL11.glColor4f(backgroundR, backgroundG, backgroundB, 1.0F);
        func_146110_a(0, 0, 0.0F, 0.0F, COVER_WIDTH * 2, COVER_HEIGHT, 512.0F, 512.0F);

        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        func_146110_a(0, 0, 0.0F, 200, COVER_WIDTH * 2, COVER_HEIGHT, 512.0F, 512.0F);
    }

    private void drawManualPages(int manualMouseX, int manualMouseY, float partialTicks) {
        int leftIndex = this.currentPage;

        int rightIndex = leftIndex + 1;
        //
        List<AbstractManualPage> pages = this.bookData.getPages();
        if (leftIndex < pages.size()) {
            pages.get(leftIndex)
                .renderPage(19, 17, manualMouseX - 19, manualMouseY - 17, partialTicks, this.manualTicks, this);
        }

        if (rightIndex < pages.size()) {
            pages.get(rightIndex)
                .renderPage(213, 17, manualMouseX - 213, manualMouseY - 17, partialTicks, this.manualTicks, this);
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        super.mouseClicked(mouseX, mouseY, mouseButton);

        int manualX = this.screenToManualX(mouseX);
        int manualY = this.screenToManualY(mouseY);
        if (this.buttonNextPage.contains(manualX, manualY)) {
            this.currentPage = Math.min(this.bookTotalPages - 1, this.currentPage + 2);
        } else if (this.buttonPreviousPage.contains(manualX, manualY)) {
            this.currentPage = Math.max(0, this.currentPage - 2);
        } else if (this.buttonHomePage.contains(manualX, manualY)) {
            this.currentPage = 0;
        } else if (this.buttonBackToJumpFrom.contains(manualX, manualY)) {
            TinkersReborn.LOG.info("buttonBackToJumpFrom clicked");
        }

    }

    @Override
    public void handleMouseInput() {
        int scrollAmount = Mouse.getEventDWheel();

        if (scrollAmount > 0 && currentPage > 0) {
            this.currentPage = Math.max(0, this.currentPage - 2);
        } else if (scrollAmount < 0 && currentPage + 2 < this.bookTotalPages) {
            this.currentPage = Math.min(this.bookTotalPages - 1, this.currentPage + 2);
        } else {
            super.handleMouseInput();
        }
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) {
        // right arrow is 205
        // left arrow is 203
        if (keyCode == 205) {
            this.currentPage = Math.min(this.bookTotalPages - 1, this.currentPage + 2);
        } else if (keyCode == 203) {
            this.currentPage = Math.max(0, this.currentPage - 2);
        } else {
            super.keyTyped(typedChar, keyCode);
        }
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    private void drawManualControls(int manualMouseX, int manualMouseY) {
        this.updateManualControlState();

        this.buttonNextPage.drawButton(this.mc, manualMouseX, manualMouseY);
        this.buttonPreviousPage.drawButton(this.mc, manualMouseX, manualMouseY);
        this.buttonHomePage.drawButton(this.mc, manualMouseX, manualMouseY);
        this.buttonBackToJumpFrom.drawButton(this.mc, manualMouseX, manualMouseY);
    }

    private void updateManualControlState() {
        this.buttonNextPage.visible = this.currentPage + 2 < this.bookTotalPages;
        this.buttonPreviousPage.visible = this.currentPage > 0;
        this.buttonHomePage.visible = this.currentPage != 0;
        this.buttonBackToJumpFrom.visible = !this.jumpFromPage.isEmpty();
    }

    private int screenToManualX(int screenX) {
        return MathHelper.floor_float((screenX - this.guiLeft) / this.scale);
    }

    private int screenToManualY(int screenY) {
        return MathHelper.floor_float((screenY - this.guiTop) / this.scale);
    }

    private int manualToScreenX(int manualX) {
        return this.guiLeft + Math.round(manualX * this.scale);
    }

    private int manualToScreenY(int manualY) {
        return this.guiTop + Math.round(manualY * this.scale);
    }
}
