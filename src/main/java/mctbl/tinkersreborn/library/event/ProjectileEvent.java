package mctbl.tinkersreborn.library.event;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MovingObjectPosition;
import net.minecraftforge.common.MinecraftForge;

import cpw.mods.fml.common.eventhandler.Cancelable;
import mctbl.tinkersreborn.library.entity.EntityProjectileBase;
import mctbl.tinkersreborn.library.utils.BlockPos;

public class ProjectileEvent extends TinkersRebornEvent {

    public final Entity projectileEntity;
    /** Might be null if the entity is a vanilla or other mods entity */
    @Nullable
    public final EntityProjectileBase projectile;

    public ProjectileEvent(Entity projectile) {
        this.projectileEntity = projectile;
        if (projectile instanceof EntityProjectileBase) {
            this.projectile = (EntityProjectileBase) projectile;
        } else {
            this.projectile = null;
        }
    }

    @Cancelable
    public static class OnLaunch extends ProjectileEvent {

        @Nullable
        public final ItemStack launcher;

        @Nullable
        public final EntityLivingBase shooter;

        public OnLaunch(Entity projectile, ItemStack launcher, EntityLivingBase shooter) {
            super(projectile);
            this.launcher = launcher;
            this.shooter = shooter;
        }

        public static boolean fireEvent(Entity projectile, ItemStack launcher, EntityLivingBase shooter) {
            return !MinecraftForge.EVENT_BUS.post(new OnLaunch(projectile, launcher, shooter));
        }
    }

    /** When a projectile hits a block */
    public static class OnHitBlock extends ProjectileEvent {

        public final float speed;
        public final BlockPos pos;
        public final Block block;
        public final int meta;

        public OnHitBlock(EntityProjectileBase projectile, float speed, BlockPos pos, Block block, int meta) {
            super(projectile);
            this.speed = speed;
            this.pos = pos;
            this.block = block;
            this.meta = meta;
        }

        public static void fireEvent(EntityProjectileBase projectile, float speed, BlockPos pos, Block block,
            int meta) {
            MinecraftForge.EVENT_BUS.post(new OnHitBlock(projectile, speed, pos, block, meta));
        }
    }

    @Cancelable
    public static class TinkerProjectileImpactEvent extends TinkersRebornEvent {

        public final EntityProjectileBase projectile;
        public final MovingObjectPosition hitResult;
        public final ItemStack ammoStack;

        public TinkerProjectileImpactEvent(EntityProjectileBase projectile, MovingObjectPosition hitResult,
            ItemStack ammoStack) {
            this.projectile = projectile;
            this.hitResult = hitResult;
            this.ammoStack = ammoStack;
        }

        public static boolean fireEvent(EntityProjectileBase projectile, MovingObjectPosition hit, ItemStack ammo) {
            return !MinecraftForge.EVENT_BUS.post(new TinkerProjectileImpactEvent(projectile, hit, ammo));

        }
    }
}
