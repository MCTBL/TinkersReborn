package mctbl.tinkersreborn.tools.traits;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;

import mctbl.tinkersreborn.library.crafting.ToolBuilderHelper;
import mctbl.tinkersreborn.library.event.Sounds;
import mctbl.tinkersreborn.library.tools.IToolMod;
import mctbl.tinkersreborn.library.tools.traits.AbstractTrait;
import mctbl.tinkersreborn.tools.TinkersRebornModifiers;

public class TraitSqueaky extends AbstractTrait {

    public TraitSqueaky() {
        super("squeaky", EnumChatFormatting.YELLOW);
    }

    @Override
    public boolean canApplyTogether(IToolMod otherModifier) {
        return !otherModifier.getIdentifier()
            .equals(TinkersRebornModifiers.modSilktouch.getIdentifier())
            && !otherModifier.getIdentifier()
                .equals(TinkersRebornModifiers.modLuck.getIdentifier());
    }

    @Override
    public boolean canApplyTogether(Enchantment enchantment) {
        return enchantment != Enchantment.looting && enchantment != Enchantment.fortune;
    }

    @Override
    public void applyEffect(NBTTagCompound rootCompound, NBTTagCompound modifierTag) {
        super.applyEffect(rootCompound, modifierTag);

        // add silktouch if it's not present
        ToolBuilderHelper.addEnchantment(rootCompound, Enchantment.silkTouch);
    }

    @Override
    public float damage(ItemStack tool, EntityLivingBase player, EntityLivingBase target, float damage, float newDamage,
        boolean isCritical) {
        // ALWAYS ZERO DAMAGE >:C
        return 0f;
    }

    @Override
    public void afterHit(ItemStack tool, EntityLivingBase player, EntityLivingBase target, float damageDealt,
        boolean wasCritical, boolean wasHit) {
        Sounds.playSoundForAll(player, Sounds.toy_squeak, 1.0f, 0.8f + 0.4f * random.nextFloat());
    }

    @Override
    public int getPriority() {
        // Always apply last to ensure no damage
        return Integer.MIN_VALUE;
    }
}
