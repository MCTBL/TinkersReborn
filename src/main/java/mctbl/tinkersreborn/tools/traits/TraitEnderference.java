package mctbl.tinkersreborn.tools.traits;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.EnderTeleportEvent;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import mctbl.tinkersreborn.library.entity.TinkersEntityProperties;
import mctbl.tinkersreborn.library.tools.traits.AbstractTrait;

public class TraitEnderference extends AbstractTrait {

    public TraitEnderference() {
        super("enderference", EnumChatFormatting.DARK_AQUA);
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Override
    public void onHit(ItemStack tool, EntityLivingBase player, EntityLivingBase target, float damage,
        boolean isCritical) {
        if (!player.worldObj.isRemote && target instanceof EntityEnderman) {
            TinkersEntityProperties props = TinkersEntityProperties.getProps(target);
            if (props != null) {
                props.apply(identifier, 100, 1);
            }
        }
    }

    @Override
    public void afterHit(ItemStack tool, EntityLivingBase player, EntityLivingBase target, float damageDealt,
        boolean wasCritical, boolean wasHit) {
        if (!player.worldObj.isRemote && !wasHit) {
            TinkersEntityProperties props = TinkersEntityProperties.getProps(target);
            if (props != null) {
                props.remove(identifier);
            }
        }
    }

    @SubscribeEvent
    public void onEnderTeleport(EnderTeleportEvent event) {
        TinkersEntityProperties props = TinkersEntityProperties.getProps(event.entityLiving);
        if (props != null && props.isActive(identifier)) {
            event.setCanceled(true);
        }
    }

    public boolean isActivate(EntityLivingBase target) {
        if (!target.worldObj.isRemote) {
            TinkersEntityProperties props = TinkersEntityProperties.getProps(target);
            if (props != null) {
                return props.isActive(identifier);
            }
        }
        return false;
    }

}
