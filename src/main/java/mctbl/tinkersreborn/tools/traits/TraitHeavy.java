package mctbl.tinkersreborn.tools.traits;

import java.util.UUID;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.item.ItemStack;

import com.google.common.collect.Multimap;

import mctbl.tinkersreborn.library.tools.traits.AbstractTrait;

public class TraitHeavy extends AbstractTrait {

    protected static final UUID KNOCKBACK_MODIFIER = UUID.fromString("1EAFEDF2-1597-4E12-B13D-AAA2B08527CD");

    public TraitHeavy() {
        super("heavy", 0x4D4968);
    }

    @Override
    public void getAttributeModifiers(ItemStack stack, Multimap<String, AttributeModifier> attributeMap) {
        attributeMap.put(
            SharedMonsterAttributes.knockbackResistance.getAttributeUnlocalizedName(),
            new AttributeModifier(KNOCKBACK_MODIFIER, "Trait heavy", 1.0, 0));
    }
}
