package mctbl.tinkersreborn.tools.events;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import mctbl.tinkersreborn.TinkersRebornConfig;
import mctbl.tinkersreborn.common.network.TinkerNetwork;
import mctbl.tinkersreborn.library.tools.ShieldCore;
import mctbl.tinkersreborn.library.tools.leveling.ToolLevelingHelper;
import mctbl.tinkersreborn.tools.network.EntityMovementChangePacket;
import mctbl.tinkersreborn.tools.traits.TraitSpiky;
import mctbl.tinkersreborn.util.ToolTagsHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Vec3;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

public class TinkersRebornShieldEventHandler {

    @SubscribeEvent
    public void playerBlockOrHurtEvent(LivingHurtEvent event) {
        if (TinkersRebornConfig.toolLevelingEnable) {
            ToolLevelingHelper.onHurt(event);
        }

        boolean isPlayerGettingDamaged = event.entityLiving instanceof EntityPlayer;
        boolean isClient = event.entityLiving.worldObj.isRemote;
        boolean isReflectedDamage = TraitSpiky.isThornsDamage(event.source);

        if (!isPlayerGettingDamaged || isClient || isReflectedDamage) {
            return;
        }

        final EntityPlayer player = (EntityPlayer) event.entityLiving;
        ItemStack tool = player.isUsingItem() ? player.getCurrentEquippedItem() : null;

        if (isShield(tool) && !ToolTagsHelper.isBroken(tool)
            && !event.isCanceled()
            && tool.getItem() instanceof ShieldCore shield
            && isDamageInBlockingAngel(player, event.source, shield.blockingAngel())
            && !event.source.isProjectile()) {
            // is blocking
            ToolTagsHelper.getTraitsOrdered(tool)
                .forEach(trait -> trait.onBlock(tool, player, event));
            
            // use durability equal to the damage prevented
            ToolTagsHelper.damageTool(tool, (int) event.ammount, player);
        }
    }

    @SubscribeEvent
    public void tryReflectProjectile(LivingAttackEvent event) {
        boolean isPlayerGettingDamaged = event.entityLiving instanceof EntityPlayer;
        boolean isClient = event.entityLiving.worldObj.isRemote;
        boolean isReflectedDamage = TraitSpiky.isThornsDamage(event.source);

        if (!isPlayerGettingDamaged || isClient || isReflectedDamage) {
            return;
        }

        final EntityPlayer player = (EntityPlayer) event.entityLiving;
        ItemStack tool = player.isUsingItem() ? player.getCurrentEquippedItem() : null;
        if (isShield(tool) && !ToolTagsHelper.isBroken(tool)
            && !event.isCanceled()
            && tool.getItem() instanceof ShieldCore shield
            && isDamageInBlockingAngel(player, event.source, shield.blockingAngel())
            && event.source.isProjectile()) {
            Entity projectile = event.source.getSourceOfDamage();
            Vec3 motion = Vec3.createVectorHelper(projectile.motionX, projectile.motionY, projectile.motionZ);
            Vec3 look = player.getLookVec();
            // this gives a factor of how much we're looking at the incoming arrow
            double strength = -look.dotProduct(motion.normalize());
            // we're looking away. oh no.
            if (strength < 0.1) {
                return;
            }

            // caught that bastard! block it!
            event.setCanceled(true);

            // and return it to the sender
            // calc speed of the projectile
            double speed = projectile.motionX * projectile.motionX + projectile.motionY * projectile.motionY
                + projectile.motionZ * projectile.motionZ;
            speed = Math.sqrt(speed);
            speed += 0.2f; // we add a bit speed

            // and redirect it to where the player is looking
            projectile.motionX = look.xCoord * speed;
            projectile.motionY = look.yCoord * speed;
            projectile.motionZ = look.zCoord * speed;

            projectile.rotationYaw = (float) (Math.atan2(projectile.motionX, projectile.motionZ) * 180.0D / Math.PI);
            projectile.rotationPitch = (float) (Math.atan2(projectile.motionY, speed) * 180.0D / Math.PI);

            // special treatement for arrows
            if (projectile instanceof EntityArrow arrow) {
                arrow.shootingEntity = player;

                // the inverse is done when the event is cancelled in arrows etc.
                // we reverse it so it has no effect. yay
                projectile.motionX /= -0.10000000149011612D;
                projectile.motionY /= -0.10000000149011612D;
                projectile.motionZ /= -0.10000000149011612D;
            }

            // notify clients from change, otherwise people will get veeeery confused
            TinkerNetwork.sendToAll(new EntityMovementChangePacket(projectile));

            // use durability equal to the damage prevented
            ToolTagsHelper.damageTool(tool, (int) event.ammount, player);
        }
    }

    private boolean isShield(ItemStack stack) {
        return stack != null && stack.getItem() instanceof ShieldCore;
    }

    private boolean isDamageInBlockingAngel(EntityPlayer player, DamageSource source, double fullAngleDegrees) {

        Entity direct = source.getSourceOfDamage();

        double towardSourceX;
        double towardSourceZ;

        if (direct instanceof EntityArrow) {
            towardSourceX = -direct.motionX;
            towardSourceZ = -direct.motionZ;

            if (towardSourceX * towardSourceX + towardSourceZ * towardSourceZ < 1.0E-8) {
                return false;
            }
        } else {
            if (direct == null || direct == player) {
                return false;
            }

            towardSourceX = direct.posX - player.posX;
            towardSourceZ = direct.posZ - player.posZ;
        }

        return isWithinShieldAngle(player, towardSourceX, towardSourceZ, fullAngleDegrees);
    }

    private boolean isWithinShieldAngle(EntityPlayer player, double towardSourceX, double towardSourceZ,
        double fullAngleDegrees) {

        double yaw = Math.toRadians(player.rotationYaw);

        double lookX = -Math.sin(yaw);
        double lookZ = Math.cos(yaw);

        double sourceLengthSq = towardSourceX * towardSourceX + towardSourceZ * towardSourceZ;

        if (sourceLengthSq < 1.0E-8) {
            return false;
        }

        double dot = (lookX * towardSourceX + lookZ * towardSourceZ) / Math.sqrt(sourceLengthSq);

        double halfAngleRadians = Math.toRadians(fullAngleDegrees * 0.5);

        return dot >= Math.cos(halfAngleRadians);
    }
}
