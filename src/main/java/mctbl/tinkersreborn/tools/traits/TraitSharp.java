package mctbl.tinkersreborn.tools.traits;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingEvent;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import mctbl.tinkersreborn.TinkersReborn;
import mctbl.tinkersreborn.common.particle.TinkersRebornParticle;
import mctbl.tinkersreborn.library.entity.TinkersEntityProperties;
import mctbl.tinkersreborn.library.tools.traits.AbstractTrait;

public class TraitSharp extends AbstractTrait {

    public static final String SHARP_KEY = "sharp";

    public TraitSharp() {
        super(SHARP_KEY, 0xA0A0A0);
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Override
    public void afterHit(ItemStack tool, EntityLivingBase player, EntityLivingBase target, float damageDealt,
        boolean wasCritical, boolean wasHit) {
        if (wasHit && target.isEntityAlive() && !player.worldObj.isRemote) {
            target.setLastAttacker(player);

            TinkersEntityProperties props = TinkersEntityProperties.getProps(target);
            props.apply(SHARP_KEY, 136, 0);

        }
    }

    @SubscribeEvent
    public void updateDot(LivingEvent.LivingUpdateEvent event) {
        if (!event.entity.worldObj.isRemote) {
            TinkersEntityProperties props = TinkersEntityProperties.getProps(event.entityLiving);
            if (props.isActive(SHARP_KEY)) {
                if (props.getRemainingTicks(SHARP_KEY) % 15 == 0) {
                    dealDamage(event.entityLiving, props.getLevel(SHARP_KEY));
                }
            }
        }
    }

    protected static void dealDamage(EntityLivingBase target, int level) {
        EntityLivingBase lastAttacker = target.getLastAttacker();
        DamageSource source;
        if (lastAttacker != null) {
            source = new EntityDamageSource("bleed", lastAttacker);
        } else {
            source = new DamageSource("bleed");
        }

        int hurtResistantTime = target.hurtResistantTime;
        attackEntitySecondary(source, (level + 1f) / 3f, target, true, true);
        TinkersReborn.proxy.spawnEffectParticle(TinkersRebornParticle.Type.HEART_BLOOD, target, 1);
        target.hurtResistantTime = hurtResistantTime;
    }

}
