package mctbl.tinkersreborn.tools.traits;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

import mctbl.tinkersreborn.library.tools.traits.AbstractTrait;

public class TraitStiff extends AbstractTrait {

    public TraitStiff() {
        super("stiff", 0xA0A0A0);
    }

    @Override
    public void onBlock(ItemStack tool, EntityPlayer player, LivingHurtEvent event) {
        event.ammount = Math.max(1f, event.ammount - 1f);
    }

}
