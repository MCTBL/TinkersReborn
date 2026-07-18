package mctbl.tinkersreborn.tools;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.world.BlockEvent;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import mctbl.tinkersreborn.library.entity.TinkersEntityProperties;
import mctbl.tinkersreborn.library.tools.IAoeTool;
import mctbl.tinkersreborn.library.tools.TinkerToolEvent;
import mctbl.tinkersreborn.library.tools.ToolCore;
import mctbl.tinkersreborn.library.utils.BlockPos;
import mctbl.tinkersreborn.util.TinkersRebornUtils;
import mctbl.tinkersreborn.util.ToolTagsHelper;

public class TinkersRebornToolsEventsHandler {

    /** Line width for AOE selection boxes. Vanilla uses 2.0F; adjust to taste. */
    private static final float AOE_BOX_LINE_WIDTH = 2.0F;

    @SubscribeEvent
    public void tinkersToolTooltipEvent(ItemTooltipEvent e) {
        // use this to prevent vailnila durability display
        if (e.itemStack.getItemDamage() != 0 && e.itemStack.getItem() instanceof ToolCore) {
            for (int idx = e.toolTip.size() - 1; idx >= 0; idx--) {
                if (e.toolTip.get(idx)
                    .startsWith("Durability: ")) {
                    e.toolTip.remove(e.toolTip.get(idx));
                    break;
                }
            }
        }
    }

    @SubscribeEvent
    public void onLivingUpdate(LivingEvent.LivingUpdateEvent event) {
        if (event.entityLiving.worldObj.isRemote) return;

        TinkersEntityProperties props = TinkersEntityProperties.getProps(event.entityLiving);
        if (props != null) {
            props.tick();
        }
    }

    @SubscribeEvent
    public void onEntityConstructing(EntityEvent.EntityConstructing event) {
        if (event.entity instanceof EntityLivingBase) {
            event.entity.registerExtendedProperties(TinkersEntityProperties.IDENTIFIER, new TinkersEntityProperties());
        }
    }

    @SubscribeEvent
    public void mineSpeed(PlayerEvent.BreakSpeed event) {
        ItemStack tool = event.entityPlayer.inventory.getCurrentItem();

        if (isTool(tool) && !ToolTagsHelper.isBroken(tool)) {
            ToolTagsHelper.getTraitsOrdered(tool)
                .forEach(trait -> trait.miningSpeed(tool, event));
        }
    }

    @SubscribeEvent
    public void blockBreak(BlockEvent.BreakEvent event) {
        ItemStack tool = event.getPlayer().inventory.getCurrentItem();

        if (isTool(tool) && !ToolTagsHelper.isBroken(tool)) {
            ToolTagsHelper.getTraitsOrdered(tool)
                .forEach(trait -> trait.beforeBlockBreak(tool, event));
        }
    }

    @SubscribeEvent
    public void blockDropEvent(BlockEvent.HarvestDropsEvent event) {
        if (event.harvester == null) {
            return;
        }
        ItemStack tool = event.harvester.getHeldItem();

        if (isTool(tool) && !ToolTagsHelper.isBroken(tool)) {
            ToolTagsHelper.getTraitsOrdered(tool)
                .forEach(trait -> trait.blockHarvestDrops(tool, event));
        }
    }

    /**
     * Handles the onBlock or the onPlayerHurt trait callback. Note that only one of the two is called!
     */
    // @SubscribeEvent
    // public void playerBlockOrHurtEvent(LivingHurtEvent event) {
    // boolean isPlayerGettingDamaged = event.getEntityLiving() instanceof EntityPlayer;
    // boolean isClient = event.getEntityLiving().getEntityWorld().isRemote;
    // boolean isReflectedDamage = event.getSource() instanceof EntityDamageSource && ((EntityDamageSource)
    // event.getSource()).getIsThornsDamage();
    //
    // if(!isPlayerGettingDamaged || isClient || isReflectedDamage) {
    // return;
    // }
    // final EntityPlayer player = (EntityPlayer) event.getEntityLiving();
    // Entity attacker = event.getSource().getTrueSource();
    //
    // List<ItemStack> heldTools = new ArrayList<>();
    // for(ItemStack tool : event.getEntity().getHeldEquipment()) {
    // if(isTool(tool) && !ToolHelper.isBroken(tool)) {
    // heldTools.add(tool);
    // }
    // }
    //
    // // first handle block
    // if(player.isActiveItemStackBlocking()) {
    // // we allow block traits to affect both main and offhand
    // for(ItemStack tool : heldTools) {
    // if(!event.isCanceled()) {
    // TinkerUtil.getTraitsOrdered(tool).forEach(trait -> trait.onBlock(tool, player, event));
    // }
    // }
    // }
    // // else handle living hurt
    // else if(attacker instanceof EntityLivingBase && !attacker.isDead) {
    // // we allow block traits to affect both main and offhand
    // for(ItemStack tool : heldTools) {
    // if(!event.isCanceled()) {
    // TinkerUtil.getTraitsOrdered(tool).forEach(trait -> trait.onPlayerHurt(tool, player, (EntityLivingBase) attacker,
    // event));
    // }
    // }
    // }
    // }

    @SubscribeEvent
    public void onRepair(TinkerToolEvent.OnRepair event) {
        ItemStack tool = event.itemStack;

        ToolTagsHelper.getTraitsOrdered(tool)
            .forEach(trait -> trait.onRepair(tool, event.amount));
    }

    private boolean isTool(ItemStack stack) {
        return stack != null && stack.getItem() instanceof ToolCore;
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

}
