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
public class CastingTableSpecialRenderer extends TileEntitySpecialRenderer {

    /**
     * The original TiC cast textures used a 14x14 opaque area inside a 16x16
     * texture. TinkersReborn casts use the complete 16x16 area, so they need to
     * be scaled by 14 / 16 to retain the same physical size on the table.
     */
    private static final float CAST_SCALE = 14F / 16F;

    /**
     * Moves the cast roughly one rendered texture pixel toward the back of the
     * casting table. This is applied in the item's local plane, so it follows the
     * table's facing instead of becoming a fixed world-direction offset.
     */
    private static final float CAST_POSITION_OFFSET = 1F / 32F;

    /**
     * Center of the 2D item quad produced by RenderItem while renderInFrame is
     * enabled. RenderItem centers the quad at y = 0.25, translates it by -0.05,
     * then applies its internal 0.5128205 scale.
     */
    private static final float ITEM_QUAD_CENTER_Y = (0.25F - 0.05F) * 0.5128205F;

    @Override
    public void renderTileEntityAt(TileEntity logic, double x, double y, double z, float partialTicks) {
        this.render((CastingBlockLogic) logic, x, y, z, partialTicks);
    }

    public void render(CastingBlockLogic logic, double posX, double posY, double posZ, float partialTicks) {
        GL11.glPushMatrix();
        float biasX = (float) (posX - 0.5F);
        float biasY = (float) (posY - 0.5F);
        float biasZ = (float) (posZ - 0.5F);
        GL11.glTranslatef(biasX, biasY, biasZ);
        this.renderItem(logic);
        GL11.glPopMatrix();
    }

    private void renderItem(CastingBlockLogic logic) {
        ItemStack stack = logic.getStackInSlot(0);

        if (stack != null) renderItem(logic, stack);

        stack = logic.getStackInSlot(1);

        if (stack != null) {
            renderItem(logic, stack);
        } else if (logic.getProgress() > 0) {
            renderItem(logic, logic.getRecipeOutput(), true);
        }
    }

    static void renderItem(CastingBlockLogic logic, ItemStack stack) {
        renderItem(logic, stack, false);
    }

    static void renderItem(CastingBlockLogic logic, ItemStack stack, boolean isCooling) {
        if (stack == null) {
            return;
        }
        FancyEntityItem entityitem = new FancyEntityItem(logic.getWorldObj(), 0.0D, 0.0D, 0.0D, stack);
        entityitem.getEntityItem().stackSize = 1;
        entityitem.hoverStart = 0.0F;
        GL11.glPushMatrix();
        GL11.glTranslatef(1F, 1.48F, 0.55F);

        float rotationY = switch (logic.getForgeDirection()) {
            case SOUTH -> 180F;
            case WEST -> 90F;
            case EAST -> 270F;
            default -> 0F;
        };
        GL11.glRotatef(rotationY, 0F, 1F, 0F);

        switch (logic.getForgeDirection()) {
            case SOUTH -> GL11.glTranslatef(0F, 0F, -0.9F);
            case WEST -> GL11.glTranslatef(-0.45F, 0F, -0.45F);
            case EAST -> GL11.glTranslatef(0.45F, 0F, -0.45F);
            default -> {
                // nothing
            }
        }

        GL11.glRotatef(90F, 1F, 0F, 0F);
        GL11.glScalef(2F, 2F, 2F);

        if (stack.getItem() instanceof ItemBlock || stack.getItem() instanceof ItemBlocklike) {
            GL11.glRotatef(-90F, 0F, 0F, 1F);
            GL11.glRotatef(90F, -1F, 0F, 0F);
            GL11.glTranslatef(-0.2275F, -0.1F, 0F);
        }

        // The original table transform leaves full-size cast artwork visually
        // about one pixel too close to the front edge.
        GL11.glTranslatef(0F, CAST_POSITION_OFFSET, 0F);

        // Scale around the center of the item plane. Scaling around the origin
        // would make the cast smaller but also move it toward one edge of the
        // table. Z is intentionally left unchanged to preserve item thickness.
        GL11.glTranslatef(0F, ITEM_QUAD_CENTER_Y, 0F);
        GL11.glScalef(CAST_SCALE, CAST_SCALE, 1F);
        GL11.glTranslatef(0F, -ITEM_QUAD_CENTER_Y, 0F);

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
        int progress4 = (int) (4 * 255 * logic.getProgress());
        float itemAlpha = (progress4 / 4) / 255f;
        int temperature = progress4 > 3 * 255 ? (4 * 255 - progress4) : 255;
        float r = 1f - temperature * (1f - 0xFF / 255f) / 255f;
        float g = 1f - temperature * (1f - 0x80 / 255f) / 255f;
        float b = 1f - temperature * (1f - 0x20 / 255f) / 255f;
        // Uniform tint + fade: src is multiplied by (r,g,b,alpha) as a constant color and
        // blended over the background by alpha. Depth test stays ENABLED so the model sorts
        // its own faces (no blocky self-overlap) and walls/liquid occlude it correctly.
        GL11.glEnable(GL11.GL_BLEND);
        GL14.glBlendColor(r * itemAlpha, g * itemAlpha, b * itemAlpha, itemAlpha);
        GL11.glBlendFunc(GL11.GL_CONSTANT_COLOR, GL11.GL_ONE_MINUS_CONSTANT_ALPHA);
        // full-bright so the hot tint glows instead of being dimmed by world light
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
