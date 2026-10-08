package mctbl.tinkersreborn.smeltery.model;

import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL14;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mctbl.tinkersreborn.library.itemblocks.ItemBlocklike;
import mctbl.tinkersreborn.smeltery.entity.CastingBlockLogic;
import mctbl.tinkersreborn.tools.entity.FancyEntityItem;

/* Special renderer, only used for drawing tools */

@SideOnly(Side.CLIENT)
public class CastingBasinSpecialRender extends TileEntitySpecialRenderer {

    @Override
    public void renderTileEntityAt(TileEntity logic, double var2, double var4, double var6, float var8) {
        this.render((CastingBlockLogic) logic, var2, var4, var6, var8);
    }

    public void render(CastingBlockLogic logic, double posX, double posY, double posZ, float var8) {
        GL11.glPushMatrix();
        float biasX = (float) (posX - 0.5F);
        float biasY = (float) (posY - 0.5F);
        float biasZ = (float) (posZ - 0.5F);
        GL11.glTranslatef(biasX, biasY, biasZ);

        ItemStack stack = logic.getStackInSlot(0);
        if (stack != null) renderItem(logic, stack);

        stack = logic.getStackInSlot(1);
        if (stack != null) {
            renderItem(logic, stack);
        } else if (logic.getProgress() > 0) {
            renderItem(logic, logic.getRecipeOutput(), true);
        }

        GL11.glPopMatrix();
    }

    static void renderItem(CastingBlockLogic logic, ItemStack stack) {
        renderItem(logic, stack, false);
    }

    static void renderItem(CastingBlockLogic logic, ItemStack stack, boolean isCooling) {
        FancyEntityItem entityitem = new FancyEntityItem(logic.getWorldObj(), 0.0D, 0.0D, 0.0D, stack);
        entityitem.getEntityItem().stackSize = 1;
        entityitem.hoverStart = 0.0F;
        GL11.glPushMatrix();
        GL11.glTranslatef(1F, 0.675F, 1.0F);

        float rotationY = switch (logic.getForgeDirection()) {
            case NORTH -> 90F;
            case SOUTH -> 270F;
            case WEST -> 180F;
            default -> 0F;
        };

        GL11.glRotatef(rotationY, 0F, 1F, 0F);
        GL11.glScalef(1.75F, 1.75F, 1.75F);

        if (stack.getItem() instanceof ItemBlock) {
            GL11.glScalef(1.6F, 1.6F, 1.6F);
            GL11.glTranslatef(0F, 0.045F, 0F);
        } else if (!(stack.getItem() instanceof ItemBlocklike)) {
            GL11.glRotatef(90F, 1F, 0F, 0F);
            GL11.glRotatef(90F, 0F, 0F, 1F);
            GL11.glScalef(0.75F, 0.75F, 0.75F);
            GL11.glTranslatef(0F, -0.235F, -0.36F);
        }

        if (isCooling) {
            doRenderWhenCooling(logic, entityitem);
        } else {
            doRender(entityitem);
        }

        GL11.glPopMatrix();
    }

    static void doRenderWhenCooling(CastingBlockLogic logic, FancyEntityItem entityItem) {
        float prevBrightnessX = OpenGlHelper.lastBrightnessX;
        float prevBrightnessY = OpenGlHelper.lastBrightnessY;
        // temperature tint fades from hot orange (0xFF8020) to white over the last 25%
        // of cooling. Hotter than the 1.20.1 0xB06020 so it reads as molten metal.
        int progress4 = (int) (4 * 255 * logic.getProgress());
        int temperature = progress4 > 3 * 255 ? (4 * 255 - progress4) : 255;
        float r = 1f - temperature * (1f - 0xFF / 255f) / 255f;
        float g = 1f - temperature * (1f - 0x80 / 255f) / 255f;
        float b = 1f - temperature * (1f - 0x20 / 255f) / 255f;
        // Block stays fully OPAQUE; cooling is shown purely as a color change (warm tint
        // fading to white). src is multiplied by the (r,g,b) constant color with alpha=1, so
        // GL_ONE_MINUS_CONSTANT_ALPHA = 0 and the background does not show through. Depth test
        // stays on (walls occlude correctly); the liquid is hidden in the chunk render during
        // cooling (see CastingBlockRender) so it does not occlude the block.
        GL11.glEnable(GL11.GL_BLEND);
        GL14.glBlendColor(r, g, b, 1f);
        GL11.glBlendFunc(GL11.GL_CONSTANT_COLOR, GL11.GL_ONE_MINUS_CONSTANT_ALPHA);
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240f, 240f);

        doRender(entityItem);

        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, prevBrightnessX, prevBrightnessY);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glDisable(GL11.GL_BLEND);

    }

    static void doRender(FancyEntityItem entityItem) {
        RenderItem.renderInFrame = true;
        RenderManager.instance.renderEntityWithPosYaw(entityItem, 0.0D, 0.0D, 0.0D, 0.0F, 0.0F);
        RenderItem.renderInFrame = false;
    }
}
