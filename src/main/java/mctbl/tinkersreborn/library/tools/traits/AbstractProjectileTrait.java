package mctbl.tinkersreborn.library.tools.traits;

import java.util.List;

import javax.annotation.Nullable;

import com.google.common.collect.ImmutableList;

import mctbl.tinkersreborn.library.entity.EntityProjectileBase;
import mctbl.tinkersreborn.library.tools.modifiers.IModifierDisplay;
import mctbl.tinkersreborn.library.tools.modifiers.ModifierTrait;
import mctbl.tinkersreborn.library.utils.RecipeMatch;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public abstract class AbstractProjectileTrait extends ModifierTrait implements IProjectileTrait, IModifierDisplay {

    protected AbstractProjectileTrait(String identifier, int color) {
        super(identifier, color);
    }

    @Override
    public void onLaunch(EntityProjectileBase projectileBase, World world, @Nullable EntityLivingBase shooter) {

    }

    @Override
    public void onProjectileUpdate(EntityProjectileBase projectile, World world, ItemStack toolStack) {

    }

    @Override
    public void onMovement(EntityProjectileBase projectile, World world, double slowdown) {

    }

    @Override
    public void afterHit(EntityProjectileBase projectile, World world, ItemStack ammoStack, EntityLivingBase attacker,
        Entity target, double impactSpeed) {

    }
    
    @Override
    public List<List<ItemStack>> getItems() {
        ImmutableList.Builder<List<ItemStack>> builder = ImmutableList.builder();

        for (RecipeMatch rm : items) {
            List<ItemStack> in = rm.getInputs();
            if (!in.isEmpty()) {
                builder.add(in);
            }
        }

        return builder.build();
    }
}
