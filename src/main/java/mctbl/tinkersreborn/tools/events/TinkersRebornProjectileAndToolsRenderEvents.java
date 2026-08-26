package mctbl.tinkersreborn.tools.events;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraftforge.client.event.FOVUpdateEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mctbl.tinkersreborn.library.client.ICrosshair;
import mctbl.tinkersreborn.library.client.ICustomCrosshairUser;
import mctbl.tinkersreborn.library.tools.BowCore;
import mctbl.tinkersreborn.library.tools.IAoeTool;
import mctbl.tinkersreborn.library.tools.ToolCore;
import mctbl.tinkersreborn.library.utils.BlockPos;
import mctbl.tinkersreborn.util.TinkersRebornUtils;

@SideOnly(Side.CLIENT)
public final class TinkersRebornProjectileAndToolsRenderEvents {

    private static final Minecraft mc = Minecraft.getMinecraft();

    /** Line width for AOE selection boxes. Vanilla uses 2.0F; adjust to taste. */
    private static final float AOE_BOX_LINE_WIDTH = 2.0F;

    @SubscribeEvent
    public void onCrosshairRender(RenderGameOverlayEvent.Pre event) {
        if (event.type != RenderGameOverlayEvent.ElementType.CROSSHAIRS) {
            return;
        }

        EntityPlayer entityPlayer = mc.thePlayer;
        ItemStack itemStack = getItemstack(entityPlayer);

        if (itemStack == null) {
            return;
        }

        ICustomCrosshairUser customCrosshairUser = (ICustomCrosshairUser) itemStack.getItem();
        ICrosshair crosshair = customCrosshairUser.getCrosshair(itemStack, entityPlayer);

        if (crosshair == ICrosshair.DEFAULT) {
            return;
        }

        float width = event.resolution.getScaledWidth();
        float height = event.resolution.getScaledHeight();

        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
        crosshair
            .render(customCrosshairUser.getCrosshairState(itemStack, entityPlayer), width, height, event.partialTicks);
        GL11.glPopAttrib();

        event.setCanceled(true);
        // restore gui texture for following draw calls
        mc.getTextureManager()
            .bindTexture(Gui.icons);
    }

    @SubscribeEvent
    public void onRenderPlayer(RenderPlayerEvent.Pre event) {
        if (event.entityPlayer.getItemInUse() == null) return;

        if (event.entityPlayer.getItemInUse()
            .getItem() instanceof BowCore) {
            event.renderer.modelBipedMain.aimedBow = true;
            event.renderer.modelArmor.aimedBow = true;
            event.renderer.modelArmorChestplate.aimedBow = true;
        }
    }

    @SubscribeEvent
    public void onAimZoom(FOVUpdateEvent event) {
        if (!event.entity.isUsingItem() || !(event.entity.getItemInUse()
            .getItem() instanceof BowCore bowCore)) return;

        float progress = bowCore.getDrawbackProgress(event.entity.getItemInUse(), event.entity);
        event.newfov = 1f - (progress * progress) * bowCore.getZoomLevel();
    }

    @SubscribeEvent
    public void renderExtraBlockBreak(RenderWorldLastEvent event) {
        Minecraft mc = Minecraft.getMinecraft();
        EntityPlayer player = mc.thePlayer;
        World world = player.getEntityWorld();
        ItemStack tool = player.getHeldItem();

        if (TinkersRebornUtils.isStackEmpty(tool) || !(tool.getItem() instanceof ToolCore)) {
            return;
        }

        Entity renderEntity = mc.renderViewEntity;
        if (renderEntity != null && tool.getItem() instanceof IAoeTool aoeTool) {
            MovingObjectPosition mop = ((ToolCore) tool.getItem())
                .getMovingObjectPositionFromPlayer(world, player, false);
            if (mop != null && mop.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
                BlockPos origin = BlockPos.of(mop.blockX, mop.blockY, mop.blockZ);
                List<BlockPos> extraBlocks = aoeTool.getAOEBlocks(tool, world, player, origin);
                if (!extraBlocks.isEmpty()) {
                    drawAoeSelectionBoxes(player, extraBlocks, event.partialTicks);
                }
            }
        }
    }

    /**
     * Draw black wireframe boxes around AOE blocks, matching vanilla's block
     * selection outline style.
     */
    private void drawAoeSelectionBoxes(EntityPlayer player, List<BlockPos> blocks, float partialTicks) {
        double dx = player.lastTickPosX + (player.posX - player.lastTickPosX) * partialTicks;
        double dy = player.lastTickPosY + (player.posY - player.lastTickPosY) * partialTicks;
        double dz = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * partialTicks;

        GL11.glPushMatrix();
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glDepthMask(false);
        GL11.glLineWidth(AOE_BOX_LINE_WIDTH);
        GL11.glColor4f(0.0F, 0.0F, 0.0F, 0.4F);

        Tessellator tess = Tessellator.instance;

        for (BlockPos pos : blocks) {
            double minX = pos.x - dx;
            double minY = pos.y - dy;
            double minZ = pos.z - dz;
            double maxX = minX + 1.0D;
            double maxY = minY + 1.0D;
            double maxZ = minZ + 1.0D;

            // bottom face
            tess.startDrawing(GL11.GL_LINE_LOOP);
            tess.addVertex(minX, minY, minZ);
            tess.addVertex(maxX, minY, minZ);
            tess.addVertex(maxX, minY, maxZ);
            tess.addVertex(minX, minY, maxZ);
            tess.draw();

            // top face
            tess.startDrawing(GL11.GL_LINE_LOOP);
            tess.addVertex(minX, maxY, minZ);
            tess.addVertex(maxX, maxY, minZ);
            tess.addVertex(maxX, maxY, maxZ);
            tess.addVertex(minX, maxY, maxZ);
            tess.draw();

            // four vertical edges
            tess.startDrawing(GL11.GL_LINES);
            tess.addVertex(minX, minY, minZ);
            tess.addVertex(minX, maxY, minZ);
            tess.addVertex(maxX, minY, minZ);
            tess.addVertex(maxX, maxY, minZ);
            tess.addVertex(maxX, minY, maxZ);
            tess.addVertex(maxX, maxY, maxZ);
            tess.addVertex(minX, minY, maxZ);
            tess.addVertex(minX, maxY, maxZ);
            tess.draw();
        }

        GL11.glDepthMask(true);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glPopMatrix();
    }

    private ItemStack getItemstack(EntityPlayer entityPlayer) {
        ItemStack itemStack = null;
        if (isValidItem(entityPlayer.getHeldItem())) {
            itemStack = entityPlayer.getHeldItem();
        }
        return itemStack;
    }

    private boolean isValidItem(ItemStack itemStack) {
        return itemStack != null && itemStack.getItem() instanceof ICustomCrosshairUser;
    }
}
