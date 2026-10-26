package mctbl.tinkersreborn.util;

import java.util.IdentityHashMap;
import java.util.Map;
import java.util.UUID;

import net.minecraft.entity.Entity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraftforge.common.MinecraftForge;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLServerStoppingEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import mctbl.tinkersreborn.library.tools.ShieldCore;

/** Common state and calculations shared by shield event handlers. */
public final class ShieldHelper {

    private static final Map<EntityPlayer, Boolean> KNOCKBACK_GUARDS = new IdentityHashMap<>();
    private static final UUID KNOCKBACK_MODIFIER_UUID = UUID.fromString("5c4e6e76-8f2a-4f12-9f2f-0d54e9a8a721");
    private static boolean registered;

    private ShieldHelper() {}

    public static boolean isShield(ItemStack stack) {
        return stack != null && stack.getItem() instanceof ShieldCore;
    }

    public static boolean isWithinBlockingAngle(EntityPlayer player, DamageSource source, double fullAngleDegrees) {
        Entity direct = source == null ? null : source.getSourceOfDamage();
        if (direct == null || direct == player) return false;

        double x;
        double z;
        if (direct instanceof EntityArrow) {
            x = -direct.motionX;
            z = -direct.motionZ;
        } else {
            x = direct.posX - player.posX;
            z = direct.posZ - player.posZ;
        }

        double lengthSquared = x * x + z * z;
        if (lengthSquared < 1.0E-8D) return false;

        double yaw = Math.toRadians(player.rotationYaw);
        double lookX = -Math.sin(yaw);
        double lookZ = Math.cos(yaw);
        double dot = (lookX * x + lookZ * z) / Math.sqrt(lengthSquared);
        return dot >= Math.cos(Math.toRadians(fullAngleDegrees * 0.5D));
    }

    /** Prevents the knockback generated later in attackEntityFrom for this hit. */
    public static void preventNextKnockback(EntityPlayer player) {
        if (player == null || player.worldObj.isRemote) return;
        ensureRegistered();
        IAttributeInstance attribute = player.getEntityAttribute(SharedMonsterAttributes.knockbackResistance);
        if (!KNOCKBACK_GUARDS.containsKey(player)) {
            AttributeModifier modifier = new AttributeModifier(
                KNOCKBACK_MODIFIER_UUID,
                "TinkersReborn shield knockback guard",
                1.0D,
                0).setSaved(false);
            if (attribute.getModifier(KNOCKBACK_MODIFIER_UUID) == null) {
                attribute.applyModifier(modifier);
            }
            KNOCKBACK_GUARDS.put(player, Boolean.TRUE);
        }
    }

    private static void ensureRegistered() {
        if (!registered) {
            ShieldHelper helper = new ShieldHelper();
            MinecraftForge.EVENT_BUS.register(helper);
            FMLCommonHandler.instance()
                .bus()
                .register(helper);
            registered = true;
        }
    }

    private static void clearKnockbackGuard(EntityPlayer player) {
        if (player == null) return;
        IAttributeInstance attribute = player.getEntityAttribute(SharedMonsterAttributes.knockbackResistance);
        AttributeModifier modifier = attribute.getModifier(KNOCKBACK_MODIFIER_UUID);
        if (modifier != null) attribute.removeModifier(modifier);
        KNOCKBACK_GUARDS.remove(player);
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || event.player.worldObj.isRemote) return;
        if (KNOCKBACK_GUARDS.containsKey(event.player)) clearKnockbackGuard(event.player);
    }

    @SubscribeEvent
    public void onPlayerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event) {
        clearKnockbackGuard(event.player);
    }

    @SubscribeEvent
    public void onPlayerChangedDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        clearKnockbackGuard(event.player);
    }

    @SubscribeEvent
    public void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        clearKnockbackGuard(event.player);
    }

    @Mod.EventHandler
    public void onServerStopping(FMLServerStoppingEvent event) {
        for (EntityPlayer player : KNOCKBACK_GUARDS.keySet()
            .toArray(new EntityPlayer[0])) {
            clearKnockbackGuard(player);
        }
    }

}
