package mctbl.tinkersreborn.tools.events;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import mctbl.tinkersreborn.library.tools.ShieldCore;
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
            && player.getItemInUseDuration() >= shield.blockPrepareTime()
            && ShieldHelper.isWithinBlockingAngle(player, event.source, shield.blockingAngel())
            && !event.source.isProjectile()) {
            shield.handleBlocking(tool, player, event);
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
            && player.getItemInUseDuration() >= shield.blockPrepareTime()
            && ShieldHelper.isWithinBlockingAngle(player, event.source, shield.blockingAngel())
            && event.source.isProjectile()) {
            shield.handleArrowReflection(tool, player, event);
        }
    }
}
