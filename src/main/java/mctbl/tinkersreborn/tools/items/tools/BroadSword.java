package mctbl.tinkersreborn.tools.items.tools;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.util.MathHelper;

import mctbl.tinkersreborn.TinkersReborn;
import mctbl.tinkersreborn.common.particle.Particles;
import mctbl.tinkersreborn.library.materials.MaterialStatusType;
import mctbl.tinkersreborn.library.materials.TinkersRebornMaterial;
import mctbl.tinkersreborn.library.tools.SwordCore;
import mctbl.tinkersreborn.library.tools.ToolNBT;
import mctbl.tinkersreborn.tools.TinkersRebornTools;
import mctbl.tinkersreborn.tools.gui.ToolBuildGuiInfo;
import mctbl.tinkersreborn.util.ToolTagsHelper;

public class BroadSword extends SwordCore {

    public static final float DURABILITY_MODIFIER = 1.5f;

    public BroadSword() {
        super("BroadSword", 3);

        this.componentsParts
            .add(new ToolPartRecord(TinkersRebornTools.swordBlade, MaterialStatusType.HEAD, "_sword_blade"));
        this.componentsParts
            .add(new ToolPartRecord(TinkersRebornTools.rod, MaterialStatusType.HANDLE, "_sword_handle"));
        this.componentsParts
            .add(new ToolPartRecord(TinkersRebornTools.largeGuard, MaterialStatusType.EXTRA, "_sword_accessory"));
    }

    @Override
    public boolean dealDamage(ItemStack stack, EntityLivingBase player, Entity entity, float damage) {
        // deal damage first
        boolean hit = super.dealDamage(stack, player, entity, damage);
        // and then sweep
        if (hit && !ToolTagsHelper.isBroken(stack)) {
            // sweep code from EntityPlayer#attackTargetEntityWithCurrentItem()
            // basically: no crit, no sprinting and has to stand on the ground for sweep.
            // Also has to move regularly slowly
            double d0 = (double) (player.distanceWalkedModified - player.prevDistanceWalkedModified);

            boolean flag = player.fallDistance > 0.0F && !player.onGround
                && !player.isOnLadder()
                && !player.isInWater()
                && !player.isPotionActive(Potion.blindness)
                && !player.isRiding();
            if (!player.isSprinting() && !flag && player.onGround && d0 < (double) player.getAIMoveSpeed()) {
                for (EntityLivingBase entitylivingbase : player.worldObj
                    .getEntitiesWithinAABB(EntityLivingBase.class, entity.boundingBox.addCoord(1.0D, 0.25D, 1.0D))) {
                    if (entitylivingbase != player && entitylivingbase != entity
                        && !player.isOnSameTeam(entitylivingbase)
                        && player.getDistanceSqToEntity(entitylivingbase) < 9.0D) {
                        entitylivingbase.knockBack(
                            player,
                            0.4F,
                            (double) MathHelper.sin(player.rotationYaw * 0.017453292F),
                            (double) (-MathHelper.cos(player.rotationYaw * 0.017453292F)));
                        super.dealDamage(stack, player, entitylivingbase, 1f);
                    }
                }

                // player.worldObj.playSound(null, player.posX, player.posY, player.posZ,
                // SoundEvents.ENTITY_PLAYER_ATTACK_SWEEP, player.getSoundCategory(), 1.0F,
                // 1.0F);
                if (player instanceof EntityPlayer) {
                    TinkersReborn.proxy.spawnAttackParticle(Particles.BROADSWORD_ATTACK, player, 0.7d);
                }
            }
        }

        return hit;
    }

    @Override
    public float getRepairModifierForPart(int index) {
        return DURABILITY_MODIFIER;
    }

    @Override
    public float damagePotential() {
        return 1.0F;
    }

    @Override
    public ToolNBT buildToolTag(List<TinkersRebornMaterial> materials) {
        ToolNBT data = super.buildToolTag(materials);

        // 2 base damage, like vanilla swords
        data.attack += 1f;
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
