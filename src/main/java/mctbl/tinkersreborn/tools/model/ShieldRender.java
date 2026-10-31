package mctbl.tinkersreborn.tools.model;

import org.lwjgl.opengl.GL11;

import mctbl.tinkersreborn.library.tools.ShieldCore;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;

public class ShieldRender extends ToolRender {

    @Override
    protected void specialAnimation(ItemRenderType type, ItemStack item, EntityPlayer player) {
        if (item.getItem() instanceof ShieldCore core && player.isUsingItem()) {
            switch (type) {
            case EQUIPPED_FIRST_PERSON:
                float smoothProgress = calcProgressSmooth(player, core);
                GL11.glTranslatef(0.4F, -1.2F + 0.6F * smoothProgress, 0.45F);
                GL11.glRotatef(-80.0F + 20.0F * (1.0F - smoothProgress), 1, 0, 0);
                GL11.glRotatef(48.0F, 0, 1, 0);
                GL11.glRotatef(80.0F, 0, 0, 1);
                GL11.glTranslatef(0.5F, 0.5F, 0.5F);
                break;
            case EQUIPPED:
                GL11.glTranslatef(1.95F - 0.5F, 0.85F - 0.5F, -0.4F - 0.5F);
                GL11.glRotatef(92.0F, 1, 0, 0);
                GL11.glRotatef(204.0F, 0, 1, 0);
                GL11.glRotatef(0.0F, 0, 0, 1);
                GL11.glTranslatef(0.5F, 0.5F, 0.5F);
                break;
            case ENTITY:
                break;
            case FIRST_PERSON_MAP:
                break;
            case INVENTORY:
                break;
            default:
                break;
            }
        }
    }
    
    private float calcProgressSmooth(EntityPlayer player, ShieldCore core) {
        float partialTicks = Minecraft.getMinecraft().timer.renderPartialTicks;
        float useTime = player.getItemInUseDuration() + partialTicks;
        float prepareTime = Math.max(1.0F, core.blockPrepareTime() * 1.0F);
        float progress = MathHelper.clamp_float(useTime / prepareTime, 0.0F, 1.0F);
        return progress * progress * (3.0F - 2.0F * progress);
    }

}
