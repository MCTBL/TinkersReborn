package mctbl.tinkersreborn.tools.items.tools;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

import mctbl.tinkersreborn.TinkersReborn;
import mctbl.tinkersreborn.common.particle.Particles;
import mctbl.tinkersreborn.library.materials.MaterialStatusType;
import mctbl.tinkersreborn.library.materials.TinkersRebornMaterial;
import mctbl.tinkersreborn.library.tools.SwordCore;
import mctbl.tinkersreborn.library.tools.ToolNBT;
import mctbl.tinkersreborn.tools.TinkersRebornTools;
import mctbl.tinkersreborn.tools.gui.ToolBuildGuiInfo;

public class LongSword extends SwordCore {

    public static final float DURABILITY_MODIFIER = 1.05f;

    public LongSword() {
        super("LongSword", 3);

        this.componentsParts
            .add(new ToolPartRecord(TinkersRebornTools.swordBlade, MaterialStatusType.HEAD, "_longsword_blade"));
        this.componentsParts
            .add(new ToolPartRecord(TinkersRebornTools.rod, MaterialStatusType.HANDLE, "_longsword_handle"));
        this.componentsParts
            .add(new ToolPartRecord(TinkersRebornTools.fullGuard, MaterialStatusType.EXTRA, "_longsword_accessory"));
    }

    @Override
    public float damagePotential() {
        return 1.1f;
    }

    @Override
    public float damageCutoff() {
        return 18f;
    }

    @Override
    public EnumAction getItemUseAction(ItemStack stack) {
        return EnumAction.bow;
    }

    @Override
    public int getMaxItemUseDuration(ItemStack stack) {
        return 200;
    }

    @Override
    public boolean dealDamage(ItemStack stack, EntityLivingBase player, Entity entity, float damage) {
        boolean hit = super.dealDamage(stack, player, entity, damage);
        // slash particle
        if (hit) {
            TinkersReborn.proxy.spawnAttackParticle(Particles.LONGSWORD_ATTACK, player, 0.7d);
        }
        return hit;
    }

    @Override
    public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        // has to be done in onUpdate because onTickUsing is too early and gets
        // overwritten. bleh.
        TinkersReborn.proxy.preventPlayerSlowdown(entityIn, 0.9f, this);
        super.onUpdate(stack, worldIn, entityIn, itemSlot, isSelected);
    }

    @Override
    public void onPlayerStoppedUsing(ItemStack stack, World world, EntityPlayer player, int timeLeft) {
        int time = this.getMaxItemUseDuration(stack) - timeLeft;
        if (time > 5) {
            player.addExhaustion(0.2F);
            player.setSprinting(true);

            float increase = (float) (0.02 * time + 0.2);
            if (increase > 0.56f) {
                increase = 0.56f;
            }
            player.motionY += increase;

            float speed = 0.05F * time;
            if (speed > 0.925f) {
                speed = 0.925f;
            }
            player.motionX = (double) (-MathHelper.sin(player.rotationYaw / 180.0F * (float) Math.PI)
                * MathHelper.cos(player.rotationPitch / 180.0F * (float) Math.PI)
                * speed);
            player.motionZ = (double) (MathHelper.cos(player.rotationYaw / 180.0F * (float) Math.PI)
                * MathHelper.cos(player.rotationPitch / 180.0F * (float) Math.PI)
                * speed);
        }

        super.onPlayerStoppedUsing(stack, world, player, timeLeft);
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        if (player.onGround) {
            player.setItemInUse(stack, this.getMaxItemUseDuration(stack));
        }
        return stack;
    }

    @Override
    public float getRepairModifierForPart(int index) {
        return DURABILITY_MODIFIER;
    }

    @Override
    public ToolNBT buildToolTag(List<TinkersRebornMaterial> materials) {
        ToolNBT data = super.buildToolTag(materials);

        data.attack += 0.5f;
        data.durability *= DURABILITY_MODIFIER;

        return data;
    }

    @Override
    public ToolBuildGuiInfo getToolBuildGuiInfo() {
        if (this.toolBuildGuiInfo == null) {
            this.toolBuildGuiInfo = new ToolBuildGuiInfo(this).addSlotPosition(33 + 20 - 5, 42 - 20 + 4) // blade
                .addSlotPosition(33 - 20 - 1, 42 + 20) // handle
                .addSlotPosition(33 - 2 - 1, 42 + 2); // guard
        }
        return this.toolBuildGuiInfo;
    }
}
