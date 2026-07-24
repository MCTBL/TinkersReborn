package mctbl.tinkersreborn.tools.model;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import org.lwjgl.opengl.GL11;

import mctbl.tinkersreborn.tools.entity.EntityBolt;
import mctbl.tinkersreborn.tools.items.tools.Bolt;
import mctbl.tinkersreborn.tools.items.tools.CrossBow;

public class CrossBowRender extends ToolRender {

    private static final EntityBolt dummy = new EntityBolt(null);

    @Override
    protected void specialAnimation(ItemRenderType type, ItemStack item) {
        EntityPlayer player = Minecraft.getMinecraft().thePlayer;
        CrossBow crossbow = (CrossBow) item.getItem();

        GL11.glTranslatef(0.5f, 0.5f, 0);
        GL11.glScalef(0.5f, 0.5f, 0.5f);

        GL11.glScalef(1.5f, 1.5f, 1.5f);

        if (type == ItemRenderType.EQUIPPED_FIRST_PERSON) {
            // we're crazy, so.. render the arrow =D
            ItemStack ammo = crossbow.findAmmo(item, player);

            if (crossbow.isLoaded(item)) {
                GL11.glTranslatef(0.0f, 0.0f, -0.3f);
                GL11.glRotatef(80f, 1f, 0f, 0f);
                GL11.glRotatef(15f, 0f, 1f, 0f);
                GL11.glRotatef(-20, 0, 0, 1);
            } else {
                GL11.glScalef(1.1f, 1.1f, 1.1f);
                GL11.glTranslatef(0.1f, 0f, 0f);
                GL11.glRotatef(50f, 1f, 0f, 0f);
            }

            if (ammo != null && ammo.hasTagCompound()) {
                float progress = crossbow.getDrawbackProgress(item, player);
                dummy.setEntityItem(ammo);
                if (!(ammo.getItem() instanceof Bolt)) {
                    dummy.setEntityItem(null);
                    return;
                }

                Render renderer = RenderManager.instance.getEntityClassRenderObject(EntityBolt.class);

                GL11.glPushMatrix();
                // adjust position
                // GL11.glScalef(2, 2, 2); // bigger
                GL11.glRotatef(95, 0, 1, 0); // rotate it into the same direction as the bow
                // GL11.glRotatef(15, 0, 1, 0); // rotate it a bit more so it's not directly inside the bow
                GL11.glRotatef(-45, 1, 0, 0); // sprite is rotated by 45° in the graphics, correct that
                GL11.glTranslatef(0.05f, 0, 0); // same as the not-inside-bow-rotation

                // move the arrow with the charging process
                float offset = -0.15f;

                // only render bolt when is loaded or player is right clicking~
                if (player.getItemInUse() == item || crossbow.isLoaded(item)) {
                    GL11.glTranslatef(0, 0, offset * progress);

                    // render iiit
                    renderer.doRender(dummy, 0, 0, 0, 0, 0);
                }
                GL11.glPopMatrix();
            }
        }

        if (type == ItemRenderType.EQUIPPED) {
            GL11.glTranslatef(0.25f, 0, 0);
            GL11.glRotatef(45.0F, 0.0F, 0.0F, 1.0F);
        }

        GL11.glTranslatef(-0.5f, -0.5f, 0f);
    }
}
