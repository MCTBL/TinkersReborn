package mctbl.tinkersreborn.tools.items.tools;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

import mctbl.tinkersreborn.TinkersReborn;
import mctbl.tinkersreborn.common.particle.Particles;
import mctbl.tinkersreborn.common.particle.TinkersRebornParticle;
import mctbl.tinkersreborn.library.materials.MaterialStatusType;
import mctbl.tinkersreborn.library.materials.TinkersRebornMaterial;
import mctbl.tinkersreborn.library.tools.SwordCore;
import mctbl.tinkersreborn.library.tools.ToolNBT;
import mctbl.tinkersreborn.tools.TinkersRebornTools;
import mctbl.tinkersreborn.tools.gui.ToolBuildGuiInfo;

public class Rapier extends SwordCore {

    public static final float DURABILITY_MODIFIER = 0.8f;

    public Rapier() {
        super("Rapier", 3);

        this.componentsParts
            .add(new ToolPartRecord(TinkersRebornTools.swordBlade, MaterialStatusType.HEAD, "_rapier_blade"));
        this.componentsParts
            .add(new ToolPartRecord(TinkersRebornTools.rod, MaterialStatusType.HANDLE, "_rapier_handle"));
        this.componentsParts
            .add(new ToolPartRecord(TinkersRebornTools.crossbar, MaterialStatusType.EXTRA, "_rapier_accessory"));
    }

    @Override
    public float damagePotential() {
        return 0.55f;
    }

    @Override
    public float damageCutoff() {
        return 13f;
    }

    @Override
    public float knockback() {
        return 0.6f;
    }

    @Override
    public boolean dealDamage(ItemStack stack, EntityLivingBase player, Entity entity, float damage) {
        boolean hit;
        if (player instanceof EntityPlayer p) {
            hit = dealHybridDamage(DamageSource.causePlayerDamage(p), entity, damage);
        } else {
            hit = dealHybridDamage(DamageSource.causeMobDamage(player), entity, damage);
        }

        if (hit) {
            TinkersReborn.proxy.spawnAttackParticle(Particles.RAPIER_ATTACK, player, 0.8d);
        }

        return hit;
    }

    // changes the passed in damagesource, but the default method calls we use
    // always create a new object
    public static boolean dealHybridDamage(DamageSource source, Entity target, float damage) {
        if (target instanceof EntityLivingBase) {
            damage /= 2f;
        }

        // half damage normal, half damage armor bypassing
        boolean hit = target.attackEntityFrom(source, damage);
        if (hit && target instanceof EntityLivingBase targetLiving) {
            // reset things to deal damage again
            targetLiving.hurtResistantTime = 0;
            targetLiving.attackEntityFrom(source.setDamageBypassesArmor(), damage);

            int count = Math.round(damage / 2f);
            if (count > 0) {
                TinkersReborn.proxy.spawnEffectParticle(TinkersRebornParticle.Type.HEART_ARMOR, targetLiving, count);
            }
        }
        return hit;
    }

    @Override
    public ItemStack onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer playerIn) {
        if (playerIn.onGround) {
            playerIn.addExhaustion(0.1f);
            playerIn.motionY += 0.32;
            float f = 0.5F;
            playerIn.motionX = MathHelper.sin(playerIn.rotationYaw / 180.0F * (float) Math.PI)
                * MathHelper.cos(playerIn.rotationPitch / 180.0F * (float) Math.PI)
                * f;
            playerIn.motionZ = -MathHelper.cos(playerIn.rotationYaw / 180.0F * (float) Math.PI)
                * MathHelper.cos(playerIn.rotationPitch / 180.0F * (float) Math.PI)
                * f;
        }
        return itemStackIn;
    }

    @Override
    public float getRepairModifierForPart(int index) {
        return DURABILITY_MODIFIER;
    }

    @Override
    public ToolNBT buildToolTag(List<TinkersRebornMaterial> materials) {
        ToolNBT data = super.buildToolTag(materials);
        data.durability *= DURABILITY_MODIFIER;
        return data;
    }

    @Override
    public ToolBuildGuiInfo getToolBuildGuiInfo() {
        if (this.toolBuildGuiInfo == null) {
            this.toolBuildGuiInfo = new ToolBuildGuiInfo(this).addSlotPosition(33 - 20 + 5, 42 - 20 + 4) // blade
                .addSlotPosition(33 + 20 - 1, 42 + 20) // handle
                .addSlotPosition(33 - 2 + 1, 42 + 2); // guard
        }
        return this.toolBuildGuiInfo;
    }
}
