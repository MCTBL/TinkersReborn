package mctbl.tinkersreborn.library.tools;

import java.util.LinkedList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.EntityDamageSourceIndirect;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;

import com.google.common.collect.Multimap;

import mctbl.tinkersreborn.library.entity.EntityProjectileBase;
import mctbl.tinkersreborn.library.materials.AbstractMaterialStats;
import mctbl.tinkersreborn.tools.TinkersRebornTraits;
import mctbl.tinkersreborn.tools.materials.FletchingMaterialStats;
import mctbl.tinkersreborn.tools.materials.HeadMaterialStats;
import mctbl.tinkersreborn.tools.traits.TraitEnderference;
import mctbl.tinkersreborn.util.ColorUtil;
import mctbl.tinkersreborn.util.TinkersRebornUtils;
import mctbl.tinkersreborn.util.TinkersStr;
import mctbl.tinkersreborn.util.ToolTagsHelper;

/**
 * This class is a tool that has ammo. Durability works like usually, but ammo
 * is abstracted on top of durability. So durability controls the interaction
 * with materials, and ammo-ratio controls the interaction betweer durability
 * and ammo
 */
public abstract class AmmoCore extends ToolCore {

    public static final String DAMAGE_TYPE_PROJECTILE = "arrow";
    protected int durabilityPerAmmo;

    protected AmmoCore(String toolTypeName, int partAmount) {
        super(toolTypeName, partAmount);
        durabilityPerAmmo = 10;
    }

    public int getDurabilityPerAmmo() {
        return durabilityPerAmmo;
    }

    public int getCurrentAmmo(ItemStack stack) {
        if (ToolTagsHelper.isBroken(stack)) {
            return 0;
        }
        return ToolTagsHelper.getCurrentDurability(stack) / durabilityPerAmmo;
    }

    public int getMaxAmmo(ItemStack stack) {
        return ToolTagsHelper.getMaxDurability(stack) / durabilityPerAmmo;
    }

    public void setAmmo(int count, ItemStack stack) {
        // we are setting ammo remaining, but damage of 0 is full ammo
        stack.setItemDamage((getMaxAmmo(stack) - count) * durabilityPerAmmo);
    }

    public boolean addAmmo(ItemStack stack, EntityLivingBase player) {
        int ammo = getCurrentAmmo(stack);
        if (ammo < getMaxAmmo(stack)) {
            ToolTagsHelper.healTool(stack, durabilityPerAmmo, null);
            return true;
        } else {
            return false;
        }
    }

    public boolean useAmmo(ItemStack stack, @Nullable EntityLivingBase player) {
        int ammo = getCurrentAmmo(stack);
        if (ammo > 0) {
            ToolTagsHelper.damageTool(stack, durabilityPerAmmo, player);
            int newAmmo = getCurrentAmmo(stack);
            if (newAmmo <= 0) {
                ToolTagsHelper.breakTool(stack, player);
            }
            // in case we're creative or a trait like obsidian's prevented the damage
            return newAmmo < ammo;
        } else {
            return false;
        }
    }

    /**
     * Gets the projectile to fire, matching the itemstacks data.
     */
    public abstract EntityProjectileBase getProjectile(ItemStack stack, @Nonnull ItemStack launcher, World world,
        EntityPlayer player, float speed, float inaccuracy, float power, boolean usedAmmo);

    public Multimap<String, AttributeModifier> getProjectileAttributeModifier(ItemStack stack) {
        // return the standard damage map
        return super.getAttributeModifiers(stack);
    }

    public boolean dealDamageRanged(ItemStack stack, Entity projectile, EntityLivingBase player, Entity entity,
        float damage) {
        DamageSource damageSource = new EntityDamageSourceIndirect(DAMAGE_TYPE_PROJECTILE, projectile, player)
            .setProjectile();

        // friggin vanilla hardcode
        if (entity instanceof EntityEnderman enderMan
            && ((TraitEnderference) TinkersRebornTraits.enderference).isActivate(enderMan)) {
            damageSource = new DamageSourceProjectileForEndermen(DAMAGE_TYPE_PROJECTILE, projectile, player);
        }

        return entity.attackEntityFrom(damageSource, damage);
    }

    public static class DamageSourceProjectileForEndermen extends EntityDamageSource {

        public final Entity projectile;

        public DamageSourceProjectileForEndermen(String damageTypeIn, Entity projectile, Entity damageSourceEntityIn) {
            super(damageTypeIn, damageSourceEntityIn);
            this.projectile = projectile;
        }

        @Override
        public Entity getSourceOfDamage() {
            return projectile;
        }

    }

    protected ItemStack getProjectileStack(ItemStack itemStack, World world, EntityPlayer player, boolean usedAmmo) {
        ItemStack reference = itemStack.copy();
        reference.stackSize = 1;
        setAmmo(1, reference);

        // prevent a positive feedback loop with picking up ammo + durability retaining
        // modifiers like reinforced
        if (!player.capabilities.isCreativeMode && !world.isRemote && !usedAmmo) {
            setAmmo(0, reference);
        }

        // never broken
        ToolTagsHelper.unbreakTool(reference);
        return reference;
    }

    @Override
    public boolean onLeftClickEntity(ItemStack stack, EntityPlayer player, Entity entity) {
        return false;
    }

    @Override
    public boolean hitEntity(ItemStack stack, EntityLivingBase p_77644_2_, EntityLivingBase p_77644_3_) {
        return false;
    }

    @Override
    public List<String> getInformation(ItemStack stack, EntityPlayer player, boolean isTooltip) {
        List<String> list = new LinkedList<>();

        if (stack.getItem() instanceof AmmoCore ammoItem) {
            if (ToolTagsHelper.isBroken(stack) && !isTooltip) {
                list.add(
                    String.format(
                        "%s: %s%s%s",
                        TinkersStr.ammoName,
                        EnumChatFormatting.DARK_RED,
                        EnumChatFormatting.BOLD,
                        TinkersStr.empty));
            } else {
                list.add(formatAmmo(ammoItem.getCurrentAmmo(stack), ammoItem.getMaxAmmo(stack)));
            }
        }

        float attack = ToolTagsHelper.getActualAttackDamage(stack, player);
        list.add(HeadMaterialStats.formatAttack(attack));
        list.add(FletchingMaterialStats.formatAccuracy(ProjectileNBT.from(stack).accuracy));

        int freeModifier = ToolTagsHelper.getModifierSlots(stack) + ToolTagsHelper.getExtraModifier(stack)
            - ToolTagsHelper.getUsedModifiers(stack);
        if (freeModifier > 0) {
            list.add(String.format("%s: %d", TinkersStr.modifierToolTip.toString(), freeModifier));
        }
        if (!isTooltip) {
            list.addAll(getModifierInfo(stack));
        }

        return list;
    }

    @Override
    public String getItemStackDisplayName(ItemStack stack) {
        if (stack.hasDisplayName()) {
            return stack.getTagCompound()
                .getCompoundTag("display")
                .getString("Name");
        }

        String toolBaseName = getLocalizedToolName();
        String materialName = ToolTagsHelper.getToolBaseMaterialsList(stack)
            .get(1)
            .localizedPrefix();

        return String.format(TOOLNAMEFORMATTER, materialName, toolBaseName);
    }

    public static String formatAmmo(int durability, int ref) {
        return String.format(
            "%s: %s%s%s/%s%s",
            TinkersStr.ammoName,
            ColorUtil.valueToColorCode((float) durability / (float) ref),
            TinkersRebornUtils.df.format(durability),
            EnumChatFormatting.GRAY.toString(),
            AbstractMaterialStats.COLOR_Durability,
            TinkersRebornUtils.df.format(ref)) + EnumChatFormatting.RESET;
    }

}
