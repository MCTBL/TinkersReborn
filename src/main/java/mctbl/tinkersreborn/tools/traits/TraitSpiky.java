package mctbl.tinkersreborn.tools.traits;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

import mctbl.tinkersreborn.TinkersReborn;
import mctbl.tinkersreborn.common.particle.TinkersRebornParticle;
import mctbl.tinkersreborn.library.tools.traits.AbstractTrait;
import mctbl.tinkersreborn.util.ToolTagsHelper;

public class TraitSpiky extends AbstractTrait {

    public TraitSpiky() {
        super("spiky", EnumChatFormatting.DARK_GREEN);
    }

    @Override
    public void onPlayerHurt(ItemStack tool, EntityPlayer player, EntityLivingBase attacker, LivingHurtEvent event) {
        if (!player.worldObj.isRemote) dealSpikyDamage(false, tool, player, attacker, event);
    }

    @Override
    public void onBlock(ItemStack tool, EntityPlayer player, LivingHurtEvent event) {
        if (!player.worldObj.isRemote) {
            Entity target = event.source.getSourceOfDamage();
            dealSpikyDamage(true, tool, player, target, event);
        }
    }

    private void dealSpikyDamage(boolean isBlocking, ItemStack tool, EntityPlayer player, Entity target,
        LivingHurtEvent event) {
        if (target instanceof EntityLivingBase && target.isEntityAlive()
            && target != player
            && !isThornsDamage(event.source)) {
            float damage = ToolTagsHelper.getActualAttackDamage(tool, player);
            if (!isBlocking) {
                damage /= 2;
            }
            EntityDamageSource damageSource = new EntityDamageSource(DamageSource.cactus.damageType, player);
            damageSource.setDamageBypassesArmor();
            damageSource.setDamageIsAbsolute();
            // damageSource.setIsThornsDamage();

            int oldHurtResistantTime = target.hurtResistantTime;
            if (attackEntitySecondary(damageSource, damage, target, true, false)) {
                TinkersReborn.proxy.spawnEffectParticle(TinkersRebornParticle.Type.HEART_CACTUS, target, 1);
            }
            target.hurtResistantTime = oldHurtResistantTime; // reset to old time
        }
    }

    public static boolean isThornsDamage(DamageSource damageSource) {
        return damageSource instanceof EntityDamageSource eds
            && (eds.damageType.equals("thorns") || eds.damageType.equals("cactus"));
    }
}
