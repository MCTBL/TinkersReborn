package mctbl.tinkersreborn.tools.traits;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.EnderTeleportEvent;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import mctbl.tinkersreborn.TinkersReborn;
import mctbl.tinkersreborn.library.potion.TinkersRebornPotion;
import mctbl.tinkersreborn.library.tools.traits.AbstractTrait;

public class TraitEnderference extends AbstractTrait {

    public static TinkersRebornPotion Enderference = new TinkersRebornPotion(
        new ResourceLocation(TinkersReborn.MODID, "enderference"),
        true,
        false,
        0x21985f);

    public TraitEnderference() {
        super("enderference", EnumChatFormatting.DARK_AQUA);

        MinecraftForge.EVENT_BUS.register(this);
    }

    @Override
    public void onHit(ItemStack tool, EntityLivingBase player, EntityLivingBase target, float damage,
        boolean isCritical) {
        if (target instanceof EntityEnderman) {
            PotionEffect effect = new PotionEffect(Enderference.getId(), 100, 1, false);
            target.addPotionEffect(effect);
        }
    }

    @Override
    public void afterHit(ItemStack tool, EntityLivingBase player, EntityLivingBase target, float damageDealt,
        boolean wasCritical, boolean wasHit) {
        if (!wasHit) {
            target.removePotionEffect(Enderference.getId());
        }
    }

    @SubscribeEvent
    public void onEnderTeleport(EnderTeleportEvent event) {
        if (Enderference.getLevel(event.entityLiving) > 0) {
            event.setCanceled(true);
        }
    }
}
