package mctbl.tinkersreborn.tools.traits;

import java.util.UUID;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.item.ItemStack;

import com.google.common.collect.Multimap;

import mctbl.tinkersreborn.library.tools.traits.AbstractTrait;

public class TraitHeavy extends AbstractTrait {

    protected static final UUID KNOCKBACK_MODIFIER = UUID.fromString("cca17597-84ae-44fe-bf98-ca08a9047079");

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
