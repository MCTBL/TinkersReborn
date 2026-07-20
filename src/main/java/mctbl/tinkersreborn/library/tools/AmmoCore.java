package mctbl.tinkersreborn.library.tools;

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
import net.minecraft.world.World;

import com.google.common.collect.Multimap;

import mctbl.tinkersreborn.library.entity.EntityProjectileBase;
import mctbl.tinkersreborn.tools.TinkersRebornTraits;
import mctbl.tinkersreborn.tools.traits.TraitEnderference;
import mctbl.tinkersreborn.util.ToolTagsHelper;

public abstract class AmmoCore extends ToolCore {

    public static final String DAMAGE_TYPE_PROJECTILE = "arrow";
    protected int durabilityPerAmmo;

    protected AmmoCore(String toolTypeName, int partAmount) {
        super(toolTypeName, partAmount);
        durabilityPerAmmo = 10;
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
    abstract EntityProjectileBase getProjectile(ItemStack stack, @Nonnull ItemStack launcher, World world,
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
}
