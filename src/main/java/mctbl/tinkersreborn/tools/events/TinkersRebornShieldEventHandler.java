package mctbl.tinkersreborn.tools.events;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Vec3;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import mctbl.tinkersreborn.TinkersReborn;
import mctbl.tinkersreborn.common.network.TinkerNetwork;
import mctbl.tinkersreborn.library.event.Sounds;
import mctbl.tinkersreborn.library.tools.ShieldCore;
import mctbl.tinkersreborn.tools.network.EntityMovementChangePacket;
import mctbl.tinkersreborn.tools.traits.TraitSpiky;
import mctbl.tinkersreborn.util.ShieldHelper;
import mctbl.tinkersreborn.util.ToolTagsHelper;

public class TinkersRebornShieldEventHandler {

    @SubscribeEvent
    public void playerBlockOrHurtEvent(LivingHurtEvent event) {
        boolean isPlayerGettingDamaged = event.entityLiving instanceof EntityPlayer;
        boolean isClient = event.entityLiving.worldObj.isRemote;
        boolean isReflectedDamage = TraitSpiky.isThornsDamage(event.source);

        if (!isPlayerGettingDamaged || isClient || isReflectedDamage) {
            return;
        }

        final EntityPlayer player = (EntityPlayer) event.entityLiving;
        ItemStack tool = player.isUsingItem() ? player.getCurrentEquippedItem() : null;
        if (ToolTagsHelper.isBroken(tool)) return;

        if (ShieldHelper.isShield(tool) && !event.isCanceled()
            && tool.getItem() instanceof ShieldCore shield
            && ShieldHelper.isWithinBlockingAngle(player, event.source, shield.blockingAngel())
            && !event.source.isProjectile()) {
            // got hit by something: reduce damage
            int damage = event.ammount < 2f ? 1 : Math.round(event.ammount / 2f);

            // reduce damage. After this event the damage will be halved again because we're blocking so we have to
            // factor this in
            event.ammount = (event.ammount * (1.0F - shield.damageReduction()));
            ShieldHelper.preventNextKnockback(player);

            // reflect damage
            if (event.source.getSourceOfDamage() != null) {
                event.source.getSourceOfDamage()
                    .attackEntityFrom(DamageSource.causeThornsDamage(player), event.ammount / 2f);
                damage = damage * 3 / 2;
            }

            // blocking success
            ToolTagsHelper.getTraitsOrdered(tool)
                .forEach(trait -> trait.onBlock(tool, player, event));

            ToolTagsHelper.damageTool(tool, damage, player);
            Sounds.playSoundForAll(player, Sounds.blocking, 1.0f, 0.8f + 0.4f * TinkersReborn.random.nextFloat());
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
        if (ToolTagsHelper.isBroken(tool)) return;

        if (ShieldHelper.isShield(tool) && !event.isCanceled()
            && tool.getItem() instanceof ShieldCore shield
            && ShieldHelper.isWithinBlockingAngle(player, event.source, shield.blockingAngel())
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
            }
            TinkerNetwork.sendToAll(new EntityMovementChangePacket(projectile));

            if (projectile instanceof EntityArrow) {
                // the inverse is done when the event is cancelled in arrows etc.
                // we reverse it so it has no effect. yay
                projectile.motionX /= -0.10000000149011612D;
                projectile.motionY /= -0.10000000149011612D;
                projectile.motionZ /= -0.10000000149011612D;
            }

            // notify clients from change, otherwise people will get veeeery confused

            // use durability equal to the damage prevented
            ToolTagsHelper.damageTool(tool, (int) event.ammount, player);
            Sounds.playSoundForAll(player, Sounds.blocking, 1.0f, 0.8f + 0.4f * TinkersReborn.random.nextFloat());
        }
    }
}
