package mctbl.tinkersreborn.tools.traits;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

import mctbl.tinkersreborn.library.tools.traits.AbstractTrait;
import mctbl.tinkersreborn.util.ToolTagsHelper;

public class TraitFlammable extends AbstractTrait {

    public TraitFlammable() {
        super("flammable", 0xFF960D);
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Override
    public void onPlayerHurt(ItemStack tool, EntityPlayer player, EntityLivingBase attacker, LivingHurtEvent event) {
        attacker.setFire(3);
    }

    @Override
    public void onBlock(ItemStack tool, EntityPlayer player, LivingHurtEvent event) {
        // block fire damage
        if (event.source.isFireDamage()) {
            event.setCanceled(true);

            ToolTagsHelper.damageTool(tool, 3, player);
        }

        if (event.source.getSourceOfDamage() != null) {
            event.source.getSourceOfDamage()
                .setFire(3);
        }
    }
}
