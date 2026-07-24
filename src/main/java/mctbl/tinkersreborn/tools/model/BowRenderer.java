package mctbl.tinkersreborn.tools.model;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

import org.lwjgl.opengl.GL11;

import mctbl.tinkersreborn.library.tools.AmmoCore;
import mctbl.tinkersreborn.library.tools.BowCore;
import mctbl.tinkersreborn.tools.TinkersRebornTools;
import mctbl.tinkersreborn.tools.entity.EntityArrow;

public class BowRenderer extends ToolRender {

    private static final EntityArrow dummy = new EntityArrow(null);

    private static final ItemStack vanillaArrow = new ItemStack(Items.arrow);

    @Override
    protected void specialAnimation(ItemRenderType type, ItemStack item) {
        EntityPlayer player = Minecraft.getMinecraft().thePlayer;

        GL11.glTranslatef(0.5f, 0.5f, 0);
        GL11.glScalef(0.5f, 0.5f, 0.5f);

        if (type == ItemRenderType.EQUIPPED) {
            // GL11.glTranslatef(0.0F, 0.125F, 0.3125F);
            GL11.glTranslatef(0.2F, -0.4F, 0.2f);
            GL11.glRotatef(-20.0F, 0.0F, 1.0F, 0.0F);
            // GL11.glScalef(f1, -f1, f1);
            // GL11.glRotatef(-100.0F, 1.0F, 0.0F, 0.0F);
            GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
        }

        // drawback animation
        if (type == ItemRenderType.EQUIPPED_FIRST_PERSON && player.isUsingItem()
            && item.getItem() instanceof BowCore bow) {
            float progress = bow.getDrawbackProgress(item, player);

            // GL11.glScalef(1.2f, 1.2f, 1.2f);
            GL11.glRotatef(-18.0F, 0.0F, 0.0F, 1.0F); // tilts the bow forward a bit
            GL11.glRotatef(-6.0F, 0.0F, 1.0F, 0.0F); // rotates the bow so it faces more forward
            GL11.glRotatef(8.0F, 1.0F, 0.0F, 0.0F); // rotates the bow forward
            GL11.glTranslatef(-0.9F, 0.2F, -1.0F);

            // this does the "zoom closer to the bow" thing
            GL11.glTranslatef(progress * -0.1f, progress * -0.125f, 0.0F);

            // we're crazy, so.. render the arrow =D
            ItemStack ammo = bow.findAmmo(item, player); // we know it's a
                                                         // projectile weapon, it's
                                                         // a bow!
            if (ammo != null) {
                if (ammo.getItem() == Items.arrow) dummy.setEntityItem(vanillaArrow);
                else dummy.setEntityItem(ammo);
                Render renderer = RenderManager.instance.getEntityClassRenderObject(EntityArrow.class);

                GL11.glPushMatrix();
                // adjust position
                GL11.glScalef(2, 2, 2); // bigger
                GL11.glRotatef(90, 0, 1, 0); // rotate it into the same direction as the bow
                GL11.glRotatef(15, 0, 1, 0); // rotate it a bit more so it's not directly inside the bow
                GL11.glRotatef(-45, 1, 0, 0); // sprite is rotated by 45° in the graphics, correct that
                GL11.glTranslatef(0.05f, 0, 0); // same as the not-inside-bow-rotation

                // move the arrow with the charging process
                float offset = 0.075f - 0.15f * progress;

                GL11.glTranslatef(0, 0, offset);

                // render iiit
                renderer.doRender(dummy, 0, 0, 0, 0, 0);
                GL11.glPopMatrix();
            }
        } else if (type == ItemRenderType.EQUIPPED && player.isUsingItem() && item.getItem() instanceof BowCore bow) {
            // we're crazy, so.. render the arrow =D
            ItemStack ammo = bow.findAmmo(item, player); // we know it's a
                                                         // projectile weapon, it's
                                                         // a bow!
            if (ammo != null) {
                if (ammo.getItem() == Items.arrow || !(ammo.getItem() instanceof AmmoCore))
                    dummy.setEntityItem(vanillaArrow);
                else dummy.setEntityItem(ammo);
                Render renderer = RenderManager.instance.getEntityClassRenderObject(EntityArrow.class);

                GL11.glPushMatrix();
                // adjust position
                GL11.glScalef(2, 2, 2); // bigger
                GL11.glRotatef(90, 0, 1, 0); // rotate it into the same direction as the bow
                GL11.glRotatef(15, 0, 1, 0); // rotate it a bit more so it's not directly inside the bow
                GL11.glRotatef(-45, 1, 0, 0); // sprite is rotated by 45° in the graphics, correct that
                GL11.glTranslatef(0.05f, 0, 0); // same as the not-inside-bow-rotation

                // move the arrow with the charging process
                float offset = 0.075f - 0.15f * 1;

                GL11.glTranslatef(0, 0, offset);

                // render iiit
                renderer.doRender(dummy, 0, 0, 0, 0, 0);
                GL11.glPopMatrix();
            }
        }

        if (item.getItem() == TinkersRebornTools.longBow) GL11.glScalef(2.3f, 2.3f, 1.0f);
        else if (item.getItem() == TinkersRebornTools.shortBow) GL11.glScalef(1.7f, 1.7f, 1.0f);

        GL11.glTranslatef(-0.5f, -0.5f, 0f);
    }
}
