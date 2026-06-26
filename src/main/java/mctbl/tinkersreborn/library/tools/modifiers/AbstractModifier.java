package mctbl.tinkersreborn.library.tools.modifiers;

import static mctbl.tinkersreborn.util.TinkersRebornUtils.canTranslate;
import static mctbl.tinkersreborn.util.TinkersRebornUtils.translate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.collect.ImmutableList;

import mctbl.tinkersreborn.TinkersReborn;
import mctbl.tinkersreborn.library.TinkerGuiException;
import mctbl.tinkersreborn.library.TinkersRebornRegistry;
import mctbl.tinkersreborn.library.tools.IModifier;
import mctbl.tinkersreborn.library.tools.IToolMod;
import mctbl.tinkersreborn.library.utils.EntityLivingBaseReflector;
import mctbl.tinkersreborn.library.utils.RecipeMatchRegistry;
import mctbl.tinkersreborn.util.TinkersRebornUtils;
import mctbl.tinkersreborn.util.ToolTags;
import mctbl.tinkersreborn.util.ToolTagsHelper;

public abstract class AbstractModifier extends RecipeMatchRegistry implements IModifier {

    public static final Logger LOG = LogManager.getLogger(TinkersReborn.MODID + "Modifier");

    public static final String LOC_Name = "modifier.%s.name";
    public static final String LOC_Desc = "modifier.%s.desc";
    public static final String LOC_Extra = "modifier.%s.extra";

    protected static final Random random = TinkersReborn.random;

    public final String identifier;

    protected final List<ModifierAspect> aspects = new LinkedList<>();

    public AbstractModifier(String identifier) {
        this.identifier = TinkersRebornUtils.sanitizeLocalizationString(identifier);

        TinkersRebornRegistry.addModifierAndTrait(this);
    }

    @Override
    public String getIdentifier() {
        return identifier;
    }

    @Override
    public boolean isHidden() {
        return false;
    }

    protected void addAspects(ModifierAspect... aspects) {
        this.aspects.addAll(Arrays.asList(aspects));
    }

    @Override
    public final boolean canApply(ItemStack stack, ItemStack original) throws TinkerGuiException {

        Set<Integer> enchantments = EnchantmentHelper.getEnchantments(stack)
            .keySet();

        List<NBTTagCompound> modifiersTagList = ToolTagsHelper.getModifiersList(stack);
        Map<String, List<NBTTagCompound>> compoundMap = modifiersTagList.stream()
            .collect(Collectors.groupingBy(c -> c.getString(ToolTags.TYPE)));
        List<NBTTagCompound> traits = compoundMap.getOrDefault(ToolTags.TYPETRAITS, new ArrayList<>());
        for (int i = 0; i < traits.size(); i++) {
            String id = traits.get(i)
                .getString(ToolTags.IDENTIFIER);
            IModifier trait = TinkersRebornRegistry.getModifierAndTrait(id);
            if (trait != null) {
                if (!canApplyTogether(trait) || !trait.canApplyTogether(this)) {
                    throw new TinkerGuiException(
                        String.format(
                            translate("gui.error.incompatible_trait"),
                            this.getLocalizedName(),
                            trait.getLocalizedName()));
                }
                canApplyWithEnchantment(trait, enchantments);
            }
        }

        List<NBTTagCompound> modifiers = compoundMap.getOrDefault(ToolTags.TYPEMODIFIERS, new ArrayList<>());
        for (int i = 0; i < modifiers.size(); i++) {
            String id = modifiers.get(i)
                .getString(ToolTags.IDENTIFIER);
            IModifier mod = TinkersRebornRegistry.getModifierAndTrait(id);
            if (mod != null) {
                if (!canApplyTogether(mod) || !mod.canApplyTogether(this)) {
                    throw new TinkerGuiException(
                        String.format(
                            translate("gui.error.incompatible_modifiers"),
                            this.getLocalizedName(),
                            mod.getLocalizedName()));
                }
                canApplyWithEnchantment(mod, enchantments);
            }
        }

        canApplyWithEnchantment(this, enchantments);

        // aspects
        for (ModifierAspect aspect : aspects) {
            if (!aspect.canApply(stack, original)) {
                return false;
            }
        }

        return canApplyCustom(stack);
    }

    private static void canApplyWithEnchantment(IToolMod iToolMod, Set<Integer> enchantments)
        throws TinkerGuiException {
        for (Integer idx : enchantments) {
            Enchantment enchantment = Enchantment.enchantmentsList[idx];
            if (!iToolMod.canApplyTogether(enchantment)) {
                String enchName = translate(enchantment.getName());
                throw new TinkerGuiException(
                    String.format(
                        translate("gui.error.incompatible_enchantments"),
                        iToolMod.getLocalizedName(),
                        enchName));
            }
        }
    }

    @Override
    public boolean canApplyTogether(Enchantment enchantment) {
        return true;
    }

    @Override
    public boolean canApplyTogether(IToolMod otherModifier) {
        return true;
    }

    protected boolean canApplyCustom(ItemStack stack) throws TinkerGuiException {
        return true;
    }

    @Override
    public void updateNBT(NBTTagCompound modifierTag) {
        // nothing to do in most cases, aspects handle the updating for most modifier
    }

    @Override
    public void apply(ItemStack stack) {
        NBTTagCompound root = ToolTagsHelper.getToolBaseNBTSafe(stack);
        apply(root);
        ToolTagsHelper.setToolBaseNBTSafe(stack, root);
    }

    @Override
    public void apply(NBTTagCompound root) {
        // add the modifier to its data
        // have the modifier itself save its data

        NBTTagCompound modifierTag = ToolTagsHelper.getModifierTag(root, this.identifier);

        // update NBT through aspects
        for (ModifierAspect aspect : aspects) {
            aspect.updateNBT(root, modifierTag);
        }

        updateNBT(modifierTag);

        // some modifiers might not save data, don't save them
        if (!modifierTag.hasNoTags()) {
            // but if they do, ensure that the identifier is correct
            ModifierNBT data = ModifierNBT.readTag(modifierTag);
            if (!identifier.equals(data.identifier)) {
                data.identifier = identifier;
                data.write(modifierTag);
            }
        }

        if (!ToolTagsHelper.hasModifier(root, this.identifier)) {
            ToolTagsHelper.getModifiersTagList(root)
                .appendTag(modifierTag);
        }

        applyEffect(root, modifierTag);
    }

    @Override
    public String getTooltip(NBTTagCompound modifierTag, boolean detailed) {
        StringBuilder sb = new StringBuilder();

        // ModifierNBT data = ModifierNBT.readTag(modifierTag);

        sb.append(getLocalizedName());
        // if (data.level > 1) {
        // sb.append(" ");
        // sb.append(TinkersRebornUtils.getRomanNumeral(data.level));
        // }

        return sb.toString();
    }

    public String getLeveledTooltip(NBTTagCompound modifierTag, boolean detailed) {
        ModifierNBT data = ModifierNBT.readInteger(modifierTag);
        return getLeveledTooltip(data.level, detailed ? " " + data.extraInfo : "");
    }

    public String getLeveledTooltip(int level, String suffix) {
        // the most important function in the whole file!

        String basic = getLocalizedName(); // backup
        if (level == 0) {
            return basic;
        }
        // else if (level > 1) {
        // basic += " " + TinkersRebornUtils.getRomanNumeral(level);
        // }

        for (int i = level; i > 1; i--) {
            if (canTranslate(String.format(LOC_Name + i, getIdentifier()))) {
                basic = translate(String.format(LOC_Name + i, getIdentifier()));
                break;
            }
        }

        if (suffix != null) {
            basic += suffix;
        }
        return basic;
    }

    @Override
    public String getLocalizedName() {
        return translate(String.format(LOC_Name, getIdentifier()));
    }

    @Override
    public String getLocalizedDesc() {
        return translate(String.format(LOC_Desc, getIdentifier()));
    }

    @Override
    public List<String> getExtraInfo(ItemStack tool, NBTTagCompound modifierTag) {
        return ImmutableList.of();
    }

    @Override
    public boolean equalModifier(NBTTagCompound modifierTag1, NBTTagCompound modifierTag2) {
        ModifierNBT data1 = ModifierNBT.readTag(modifierTag1);
        ModifierNBT data2 = ModifierNBT.readTag(modifierTag2);

        return data1.identifier.equals(data2.identifier) && data1.level == data2.level;
    }

    @Override
    public boolean hasTexturePerMaterial() {
        return false;
    }

    protected static boolean attackEntitySecondary(DamageSource source, float damage, Entity entity,
        boolean ignoreInvulv, boolean resetInvulv) {
        return attackEntitySecondary(source, damage, entity, ignoreInvulv, resetInvulv, true);
    }

    protected static boolean attackEntitySecondary(DamageSource source, float damage, Entity entity,
        boolean ignoreInvulv, boolean resetInvulv, boolean noKnockback) {
        Optional<EntityLivingBase> entityLivingBase = Optional.of(entity)
            .filter(e -> e instanceof EntityLivingBase)
            .map(e -> (EntityLivingBase) e);
        Optional<IAttributeInstance> knockbackAttribute = entityLivingBase
            .map(living -> living.getEntityAttribute(SharedMonsterAttributes.knockbackResistance))
            .filter(attribute -> attribute.getModifier(ANTI_KNOCKBACK_MOD.getID()) == null);
        float oldLastDamage = entityLivingBase.map(living -> EntityLivingBaseReflector.getLastDamage(living))
            .orElse(0f);

        if (noKnockback) {
            knockbackAttribute.ifPresent(attribute -> attribute.applyModifier(ANTI_KNOCKBACK_MOD));
        }

        // set hurt resistance time to 0 because we always want to deal damage in traits
        if (ignoreInvulv) {
            entity.hurtResistantTime = 0;
        }
        boolean hit = entity.attackEntityFrom(source, damage);
        // set total received damage, important for AI and stuff
        entityLivingBase.ifPresent(
            living -> EntityLivingBaseReflector
                .setLastDamage(living, EntityLivingBaseReflector.getLastDamage(living) + oldLastDamage));

        // reset hurt resistance time if desired
        if (hit && resetInvulv) {
            entity.hurtResistantTime = 0;
        }

        if (noKnockback) {
            knockbackAttribute.ifPresent(attribute -> attribute.removeModifier(ANTI_KNOCKBACK_MOD));
        }

        return hit;
    }

    @Override
    public boolean hasItemsToApplyWith() {
        return !items.isEmpty();
    }

    private static final AttributeModifier ANTI_KNOCKBACK_MOD = new AttributeModifier("Anti Modifier Knockback", 1f, 0);
}
