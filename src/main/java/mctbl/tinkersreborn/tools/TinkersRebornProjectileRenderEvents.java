package mctbl.tinkersreborn.tools;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.FOVUpdateEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mctbl.tinkersreborn.library.client.ICrosshair;
import mctbl.tinkersreborn.library.client.ICustomCrosshairUser;
import mctbl.tinkersreborn.library.tools.BowCore;

@SideOnly(Side.CLIENT)
public final class TinkersRebornProjectileRenderEvents {

    private static final Minecraft mc = Minecraft.getMinecraft();

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
