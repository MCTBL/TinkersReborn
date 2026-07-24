package mctbl.tinkersreborn.library.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IProjectile;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;

import com.google.common.collect.Multimap;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.registry.IEntityAdditionalSpawnData;
import io.netty.buffer.ByteBuf;
import mctbl.tinkersreborn.library.TinkersRebornRegistry;
import mctbl.tinkersreborn.library.event.ProjectileEvent;
import mctbl.tinkersreborn.library.event.ProjectileEvent.TinkerProjectileImpactEvent;
import mctbl.tinkersreborn.library.event.Sounds;
import mctbl.tinkersreborn.library.tools.AmmoCore;
import mctbl.tinkersreborn.library.tools.BowCore;
import mctbl.tinkersreborn.library.tools.IModifier;
import mctbl.tinkersreborn.library.tools.ToolCore;
import mctbl.tinkersreborn.library.tools.traits.IProjectileTrait;
import mctbl.tinkersreborn.library.utils.BlockPos;
import mctbl.tinkersreborn.tools.modifiers.ModReinforced;
import mctbl.tinkersreborn.util.AmmoHelper;
import mctbl.tinkersreborn.util.TinkersRebornUtils;
import mctbl.tinkersreborn.util.ToolTags;
import mctbl.tinkersreborn.util.ToolTagsHelper;

// have to base this on EntityArrow, otherwise minecraft does derp things because everything is handled based on class.
public class EntityProjectileBase extends EntityArrow implements IEntityAdditionalSpawnData {

    public static final String TAG_AMMO = "ammo";
    public static final String TAG_LAUNCHER = "launcher";
    public static final String TAG_POWER = "power";

    protected static final UUID PROJECTILE_POWER_MODIFIER = UUID.fromString("c6aefc21-081a-4c4a-b076-8f9d6cef9122");
    // projectiles tend to land about this far from any given block face
    private static final AxisAlignedBB ON_BLOCK_AABB = AxisAlignedBB
        .getBoundingBox(-0.05D, -0.05D, -0.05D, 0.05D, 0.05D, 0.05D);

    // public TinkerProjectileHandler tinkerProjectile = new
    // TinkerProjectileHandler();
    private ItemStack ammoStack = null; // parent
    private ItemStack launcherStack = null; // launcher
    private float power = 1f; // power

    public boolean bounceOnNoDamage = true;
    public boolean defused = false; // if this is true it wont hit any entities anymore

    public boolean inGround;

    public Block inTile;
    public byte inData;

    public int xTile;
    public int yTile;
    public int zTile;

    public int ticksInGround;
    public int ticksInAir;
    public boolean noGravity;

    public EntityProjectileBase(World world) {
        super(world);

        init();
    }

    public EntityProjectileBase(World world, double d, double d1, double d2) {
        this(world);
        this.setPosition(d, d1, d2);
    }

    public EntityProjectileBase(World world, EntityPlayer player, float speed, float inaccuracy, float power,
        ItemStack stack, ItemStack launchingStack) {
        this(world);

        this.shootingEntity = player;

        // 1 allow / 2 only creative
        this.canBePickedUp = player.capabilities.isCreativeMode ? 2 : 1;

        // stuff from the arrow
        this.setLocationAndAngles(
            player.posX,
            player.posY + player.getEyeHeight(),
            player.posZ,
            player.rotationYaw,
            player.rotationPitch);

        this.setPosition(this.posX, this.posY, this.posZ);
        this.yOffset = 0.0F;
        this.motionX = -MathHelper.sin(this.rotationYaw / 180.0F * (float) Math.PI)
            * MathHelper.cos(this.rotationPitch / 180.0F * (float) Math.PI);
        this.motionZ = +MathHelper.cos(this.rotationYaw / 180.0F * (float) Math.PI)
            * MathHelper.cos(this.rotationPitch / 180.0F * (float) Math.PI);
        this.motionY = -MathHelper.sin(this.rotationPitch / 180.0F * (float) Math.PI);
        this.setThrowableHeading(this.motionX, this.motionY, this.motionZ, speed, inaccuracy);

        // our stuff
        this.ammoStack = stack;
        this.launcherStack = launchingStack;
        this.power = power;

        for (IProjectileTrait trait : this.getAmmoTraits()) {
            trait.onLaunch(this, world, player);
        }
    }

    protected void init() {

    }

    public boolean isDefused() {
        return defused;
    }

    protected void defuse() {
        this.defused = true;
    }

    protected List<IProjectileTrait> getAmmoTraits() {
        List<IProjectileTrait> projectileTraitList = new ArrayList<>();
        if (ammoStack != null) {
            List<NBTTagCompound> list = ToolTagsHelper.getModifiersList(ammoStack);
            for (int i = 0; i < list.size(); i++) {
                IModifier trait = TinkersRebornRegistry.getModifierAndTrait(
                    list.get(i)
                        .getString(ToolTags.IDENTIFIER));
                if (trait instanceof IProjectileTrait pt) {
                    projectileTraitList.add(pt);
                }
            }
        }
        return projectileTraitList;
    }

    protected void playHitBlockSound(float speed, Block block) {
        Material material = block.getMaterial();

        if (material == Material.wood) {
            this.playSound(Sounds.wood_hit, 1f, 1f);
        } else if (material == Material.rock) {
            this.playSound(Sounds.stone_hit, 1f, 1f);
        }

        this.playSound(block.stepSound.soundName, 0.8f, 1.0f);
    }

    protected void playHitEntitySound() {
        this.playSound("random.bowhit", 1.0F, 1.2F / (this.rand.nextFloat() * 0.2F + 0.9F));
    }

    /**
     * How deep the item enters stuff it hits. Best experiment.
     */
    public double getStuckDepth() {
        return 0.4f;
    }

    protected void onEntityHit(Entity entityHit) {
        setDead();
    }

    protected float getSpeed() {
        return MathHelper
            .sqrt_double(this.motionX * this.motionX + this.motionY * this.motionY + this.motionZ * this.motionZ);
    }

    public void onHitBlock(MovingObjectPosition mop) {
        Block block = this.worldObj.getBlock(mop.blockX, mop.blockY, mop.blockZ);
        int meta = this.worldObj.getBlockMetadata(mop.blockX, mop.blockY, mop.blockZ);

        this.xTile = mop.blockX;
        this.yTile = mop.blockY;
        this.zTile = mop.blockZ;
        this.inTile = block;
        this.inData = (byte) meta;

        this.motionX = ((float) (mop.hitVec.xCoord - this.posX));
        this.motionY = ((float) (mop.hitVec.yCoord - this.posY));
        this.motionZ = ((float) (mop.hitVec.zCoord - this.posZ));
        float speed = getSpeed();
        this.posX -= this.motionX / speed * 0.05000000074505806D;
        this.posY -= this.motionY / speed * 0.05000000074505806D;
        this.posZ -= this.motionZ / speed * 0.05000000074505806D;

        playHitBlockSound(speed, block);

        ProjectileEvent.OnHitBlock.fireEvent(this, speed, BlockPos.of(mop.blockX, mop.blockY, mop.blockZ), block, meta);

        this.inGround = true;
        this.arrowShake = 7;
        this.setIsCritical(false);

        if (block.getMaterial() != Material.air) {
            block.onEntityCollidedWithBlock(worldObj, mop.blockX, mop.blockY, mop.blockZ, this);
        }

        defuse(); // defuse it so it doesn't hit stuff anymore, being weird
    }

    public void onHitEntity(MovingObjectPosition mop) {
        ItemStack item = this.ammoStack;
        ItemStack launcher = this.launcherStack;
        boolean bounceOff = false;
        Entity entityHit = mop.entityHit;
        // deal damage if we have everything
        if (item.getItem() instanceof ToolCore && this.shootingEntity instanceof EntityLivingBase) {
            EntityLivingBase attacker = (EntityLivingBase) this.shootingEntity;
            // EntityLivingBase target = (EntityLivingBase) raytraceResult.entityHit;

            // find the actual itemstack in the players inventory
            ItemStack inventoryItem = AmmoHelper.getMatchingItemstackFromInventory(item, attacker, false);
            if (TinkersRebornUtils.isStackEmpty(inventoryItem) || inventoryItem.getItem() != item.getItem()) {
                // backup, use saved itemstack
                inventoryItem = item;
            }

            // for the sake of dealing damage we always ensure that the impact itemstack has
            // the correct broken state
            // since the ammo stack can break while the arrow travels/if it's the last arrow
            boolean brokenStateDiffers = ToolTagsHelper.isBroken(inventoryItem) != ToolTagsHelper.isBroken(item);
            if (brokenStateDiffers) {
                toggleBroken(inventoryItem);
            }

            Multimap<String, AttributeModifier> projectileAttributes = null;
            // remove stats from held items
            if (!this.worldObj.isRemote) {
                unequip(attacker, launcher);

                // apply stats from projectile
                if (item.getItem() instanceof AmmoCore ammoCore) {
                    projectileAttributes = ammoCore.getProjectileAttributeModifier(inventoryItem);

                    if (launcher.getItem() instanceof BowCore bowCore) {
                        bowCore.modifyProjectileAttributes(projectileAttributes, launcherStack, ammoStack, power);
                    }

                    // factor in power
                    projectileAttributes.put(
                        SharedMonsterAttributes.attackDamage.getAttributeUnlocalizedName(),
                        new AttributeModifier(PROJECTILE_POWER_MODIFIER, "Weapon damage multiplier", power - 1f, 2));

                    attacker.getAttributeMap()
                        .applyAttributeModifiers(projectileAttributes);
                }
                // deal the damage
                float speed = MathHelper.sqrt_double(
                    this.motionX * this.motionX + this.motionY * this.motionY + this.motionZ * this.motionZ);
                bounceOff = !dealDamage(speed, inventoryItem, attacker, entityHit);
                if (!bounceOff) {
                    for (IProjectileTrait trait : this.getAmmoTraits()) {
                        trait.afterHit(this, this.worldObj, inventoryItem, attacker, entityHit, speed);
                    }

                    // if on fire, set the entity on fire, like vanilla arrows
                    if (this.isBurning() && !(entityHit instanceof EntityEnderman)) {
                        entityHit.setFire(5);
                    }
                }
                if (brokenStateDiffers) {
                    toggleBroken(inventoryItem);
                }

                // remove stats from projectile
                // apply stats from projectile
                if (item.getItem() instanceof IProjectile) {
                    assert projectileAttributes != null;
                    attacker.getAttributeMap()
                        .removeAttributeModifiers(projectileAttributes);
                }

                // readd stats from held items
                equip(attacker, launcher);
            }

            if (!bounceOff) {
                onEntityHit(entityHit);
            }
        }

        if (bounceOff) {
            if (!bounceOnNoDamage) {
                this.setDead();
            }

            // bounce off if we didn't deal damage
            this.motionX *= -0.10000000149011612D;
            this.motionY *= -0.10000000149011612D;
            this.motionZ *= -0.10000000149011612D;
            this.rotationYaw += 180.0F;
            this.prevRotationYaw += 180.0F;
            // this.ticksInAir = 0;
        }

        playHitEntitySound();
    }

    private void toggleBroken(ItemStack stack) {
        ToolTagsHelper.setBroken(stack, !ToolTagsHelper.isBroken(stack));
    }

    private void unequip(EntityLivingBase entity, ItemStack stack) {
        if (stack != null && stack.getItem() instanceof ToolCore) {
            Multimap<String, AttributeModifier> modifiers = stack.getItem()
                .getAttributeModifiers(stack);
            entity.getAttributeMap()
                .removeAttributeModifiers(modifiers);
        }
    }

    private void equip(EntityLivingBase entity, ItemStack stack) {
        if (stack != null && stack.getItem() instanceof ToolCore) {
            Multimap<String, AttributeModifier> modifiers = stack.getItem()
                .getAttributeModifiers(stack);
            entity.getAttributeMap()
                .applyAttributeModifiers(modifiers);
        }
    }

    // returns true if it was successful
    public boolean dealDamage(float speed, ItemStack item, EntityLivingBase attacker, Entity target) {
        return ToolTagsHelper.attackEntity(item, (ToolCore) item.getItem(), attacker, target, this);
    }

    @Override
    public void setVelocity(double x, double y, double z) {
        // don't do anything, we set it ourselves at spawn
        // Mojangs code has a hard cap of 3.9 speed, but our projectiles can go faster,
        // which desyncs client and server speeds
        // Packet that's causing it: S12PacketEntityVelocity
    }

    @Override
    public void onUpdate() {
        // call the entity update routine
        // luckily we can call this directly and take the arrow-code, since we'd have to
        // call super.onUpdate otherwise. Which would not work.
        this.onEntityUpdate();
        for (IProjectileTrait trait : this.getAmmoTraits()) {
            trait.onProjectileUpdate(this, this.worldObj, launcherStack);
        }

        // boioioiooioing
        if (this.arrowShake > 0) {
            --this.arrowShake;
        }

        // If we don't have our rotation set correctly, infer it from our motion
        // direction
        if (this.prevRotationPitch == 0.0F && this.prevRotationYaw == 0.0F) {
            float f = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionZ * this.motionZ);
            this.prevRotationYaw = this.rotationYaw = (float) (Math.atan2(this.motionX, this.motionZ) * 180.0D
                / Math.PI);
            this.prevRotationPitch = this.rotationPitch = (float) (Math.atan2(this.motionY, f) * 180.0D / Math.PI);
        }

        // we previously hit something. Check if the block is still there.
        Block hitBlock = this.worldObj.getBlock(this.xTile, this.yTile, this.zTile);
        int hitMeta = this.worldObj.getBlockMetadata(this.xTile, this.yTile, this.zTile);
        if (hitBlock.getMaterial() != Material.air) {
            AxisAlignedBB axisalignedbb = hitBlock
                .getCollisionBoundingBoxFromPool(this.worldObj, this.xTile, this.yTile, this.zTile);
            if (axisalignedbb != null && axisalignedbb.offset(this.xTile, this.yTile, this.zTile)
                .isVecInside(Vec3.createVectorHelper(this.posX, this.posY, this.posZ))) {
                this.inGround = true;
            }
        }

        if (this.inGround) {
            updateInGround(hitBlock, hitMeta);
        } else {
            updateInAir();
        }
    }

    // Update while we're stuck in a block
    public void updateInGround(Block block, int meta) {

        // check if it's still the same block or if it is already within tolerance of
        // another hitbox
        // second part prevents it from falling when the block changes but the hitbox
        // does nots
        boolean sameBlock = (block == this.inTile && meta == this.inData);
        boolean collides = this.worldObj.checkBlockCollision(ON_BLOCK_AABB.offset(this.posX, this.posY, this.posZ));
        if (sameBlock || collides) {
            ++this.ticksInGround;

            int despawnTicks = ToolTagsHelper.getToolBaseNBTSafe(this.ammoStack)
                .getBoolean(ModReinforced.TAG_UNBREAKABLE) ? 120 : 1200;
            if (this.ticksInGround >= despawnTicks) {
                this.setDead();
            }
        } else {
            this.inGround = false;
            this.motionX *= this.rand.nextFloat() * 0.2F;
            this.motionY *= this.rand.nextFloat() * 0.2F;
            this.motionZ *= this.rand.nextFloat() * 0.2F;
            this.ticksInGround = 0;
            this.ticksInAir = 0;
        }

    }

    // update while traveling
    public void updateInAir() {
        // tick tock
        ++this.ticksInAir;

        // do a raytrace from old to new position
        Vec3 oldPos = Vec3.createVectorHelper(this.posX, this.posY, this.posZ);
        Vec3 newPos = Vec3
            .createVectorHelper(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);
        MovingObjectPosition raytraceResult = this.worldObj.func_147447_a(oldPos, newPos, false, true, false);

        // raytrace messes with the positions. get new ones! (not anymore since vec3d is
        // all final now?)
        // if we hit something, the collision point is our new position
        if (raytraceResult != null) {
            newPos = Vec3.createVectorHelper(
                raytraceResult.hitVec.xCoord,
                raytraceResult.hitVec.yCoord,
                raytraceResult.hitVec.zCoord);
        }

        Entity entity = this.findEntityOnPath(oldPos, newPos);

        // if we hit something, new collision point!
        if (entity != null) {
            raytraceResult = new MovingObjectPosition(entity);
        }

        // did we hit a player?
        if (raytraceResult != null && raytraceResult.entityHit != null
            && raytraceResult.entityHit instanceof EntityPlayer entityplayer) {

            // can we attack said player?
            if (entityplayer.capabilities.disableDamage
                || this.shootingEntity instanceof EntityPlayer player && !player.canAttackPlayer(entityplayer)) {
                raytraceResult = null;
            }

            // this check should probably done inside of the loop for accuracy..
        }

        // time to hit the object
        if (raytraceResult != null && !MinecraftForge.EVENT_BUS.post(getProjectileImpactEvent(raytraceResult))) {
            if (raytraceResult.entityHit != null) {
                onHitEntity(raytraceResult);
            } else {
                onHitBlock(raytraceResult);
            }
        }

        // crithit particles
        if (this.getIsCritical()) {
            drawCritParticles();
        }

        // MOVEMENT! yay.
        doMoveUpdate();
        // Slowdown
        double slowdown = 1.0d - getSlowdown();

        // bubblez
        if (this.isInWater()) {
            float f3 = 0.25F;
            for (int l = 0; l < 4; ++l) {
                this.worldObj.spawnParticle(
                    "bubble",
                    this.posX - this.motionX * f3,
                    this.posY - this.motionY * f3,
                    this.posZ - this.motionZ * f3,
                    this.motionX,
                    this.motionY,
                    this.motionZ);
            }

            // more slowdown in water
            slowdown *= 0.60d;
        }

        // phshshshshshs
        if (this.isWet()) {
            this.extinguish();
        }

        // minimalistic slowdown!
        this.motionX *= slowdown;
        this.motionY *= slowdown;
        this.motionZ *= slowdown;
        // gravity
        if (!this.noGravity) {
            this.motionY -= getGravity();
        }
        for (IProjectileTrait trait : this.getAmmoTraits()) {
            trait.onMovement(this, this.worldObj, slowdown);
        }
        this.setPosition(this.posX, this.posY, this.posZ);

        // tell blocks we collided with, that we collided with them!
        // doBlockCollisions
        this.func_145775_I();
    }

    @Nullable
    protected Entity findEntityOnPath(@Nonnull Vec3 start, @Nonnull Vec3 end) {
        if (isDefused()) {
            return null;
        }

        AxisAlignedBB searchBox = this.boundingBox.addCoord(this.motionX, this.motionY, this.motionZ)
            .expand(1.0D, 1.0D, 1.0D);
        List<Entity> entities = this.worldObj.getEntitiesWithinAABBExcludingEntity(this, searchBox);

        if (this.shootingEntity != null && this.ticksInAir < 5) {
            entities.remove(this.shootingEntity);
        }

        Entity closestEntity = null;
        double closestDist = Double.MAX_VALUE;

        for (Entity entity : entities) {
            if (!entity.canBeCollidedWith() || (entity == this.shootingEntity && this.ticksInAir < 5)) {
                continue;
            }

            AxisAlignedBB aabb = entity.boundingBox.expand(0.3D, 0.3D, 0.3D);

            MovingObjectPosition mop = aabb.calculateIntercept(start, end);
            boolean hitViaIntercept = mop != null;

            // Also check if the arrow's physical AABB (swept along motion) intersects
            // the entity's AABB. This catches grazing hits where the center-line
            // calculateIntercept misses but the arrow body (0.5 wide) still hits.
            boolean hitViaBody = !hitViaIntercept && entity.boundingBox
                .intersectsWith(this.boundingBox.addCoord(this.motionX, this.motionY, this.motionZ));

            if (hitViaIntercept) {
                double dist = start.squareDistanceTo(mop.hitVec);
                if (dist < closestDist) {
                    closestEntity = entity;
                    closestDist = dist;
                }
            } else if (hitViaBody) {
                // For body hit, estimate distance as distance to entity center
                double dist = start.squareDistanceTo(
                    Vec3.createVectorHelper(
                        (entity.boundingBox.minX + entity.boundingBox.maxX) / 2.0,
                        (entity.boundingBox.minY + entity.boundingBox.maxY) / 2.0,
                        (entity.boundingBox.minZ + entity.boundingBox.maxZ) / 2.0));
                if (dist < closestDist || closestDist == 0.0D) {
                    closestEntity = entity;
                    closestDist = dist;
                }
            }
        }

        return closestEntity;
    }

    protected TinkerProjectileImpactEvent getProjectileImpactEvent(MovingObjectPosition rayTraceResult) {
        return new TinkerProjectileImpactEvent(this, rayTraceResult, ammoStack);
    }

    public void drawCritParticles() {
        for (int k = 0; k < 4; ++k) {
            this.worldObj.spawnParticle(
                "crit",
                this.posX + this.motionX * k / 4.0D,
                this.posY + this.motionY * k / 4.0D,
                this.posZ + this.motionZ * k / 4.0D,
                -this.motionX,
                -this.motionY + 0.2D,
                -this.motionZ);
        }
    }

    protected void doMoveUpdate() {
        this.posX += this.motionX;
        this.posY += this.motionY;
        this.posZ += this.motionZ;
        double f2 = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionZ * this.motionZ);
        this.rotationYaw = (float) (Math.atan2(this.motionX, this.motionZ) * 180.0D / Math.PI);
        this.rotationPitch = (float) (Math.atan2(this.motionY, f2) * 180.0D / Math.PI);

        // normalize rotations
        while (this.rotationPitch - this.prevRotationPitch < -180.0F) {
            this.prevRotationPitch -= 360.0F;
        }

        while (this.rotationPitch - this.prevRotationPitch >= 180.0F) {
            this.prevRotationPitch += 360.0F;
        }

        while (this.rotationYaw - this.prevRotationYaw < -180.0F) {
            this.prevRotationYaw -= 360.0F;
        }

        while (this.rotationYaw - this.prevRotationYaw >= 180.0F) {
            this.prevRotationYaw += 360.0F;
        }

        this.rotationPitch = this.prevRotationPitch + (this.rotationPitch - this.prevRotationPitch) * 0.2F;
        this.rotationYaw = this.prevRotationYaw + (this.rotationYaw - this.prevRotationYaw) * 0.2F;
    }

    public double getSlowdown() {
        return 0.01;
    }

    /**
     * Added to the y-velocity as gravitational pull. Otherwise stuff would simply float midair.
     */
    public double getGravity() {
        return 0.05;
    }

    public ItemStack getEntityItem() {
        return this.ammoStack;
    }

    public void setEntityItem(ItemStack ammoStack) {
        this.ammoStack = ammoStack;
    }

    /**
     * Called by a player entity when they collide with an entity
     */
    @Override
    public void onCollideWithPlayer(EntityPlayer player) {
        if (!this.worldObj.isRemote && this.inGround && this.arrowShake <= 0) {
            boolean pickedUp = (this.canBePickedUp == 2 && player.capabilities.isCreativeMode)
                || (this.canBePickedUp == 1 && pickup(player, this.canBePickedUp != 1));
            if (pickedUp) {
                this.playSound(
                    "random.pop",
                    0.2F,
                    ((this.rand.nextFloat() - this.rand.nextFloat()) * 0.7F + 1.0F) * 2.0F);
                player.onItemPickup(this, 1);
                this.setDead();
            }
        }
    }

    public boolean pickup(EntityLivingBase entity, boolean simulate) {
        ItemStack stack = AmmoHelper.getMatchingItemstackFromInventory(ammoStack, entity, true);
        if (stack != null && stack.getItem() instanceof AmmoCore ammoCore) {
            if (!simulate && ammoCore.getCurrentAmmo(ammoStack) > 0) {
                ToolTagsHelper.unbreakTool(stack);
                ammoCore.addAmmo(stack, entity);
            }
            return true;
        }

        return false;
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound tags) {
        super.writeEntityToNBT(tags);
        NBTTagCompound tag = new NBTTagCompound();
        if (ammoStack != null) {
            tag.setTag(TAG_AMMO, ammoStack.writeToNBT(new NBTTagCompound()));
        }
        if (launcherStack != null) {
            tag.setTag(TAG_LAUNCHER, launcherStack.writeToNBT(new NBTTagCompound()));
        }
        tag.setFloat(TAG_POWER, power);

        tag.setBoolean("inGround", this.inGround);
        tag.setInteger("inTile", Block.getIdFromBlock(this.inTile));
        tag.setByte("inData", this.inData);
        tag.setShort("life", (short) this.ticksInGround);
        tag.setBoolean("noGravity", this.noGravity);

        tag.setInteger("xTile", this.xTile);
        tag.setInteger("yTile", this.yTile);
        tag.setInteger("zTile", this.zTile);

        tags.setTag("item", tag);

    }

    @Override
    public void readEntityFromNBT(NBTTagCompound tags) {
        super.readEntityFromNBT(tags);
        NBTTagCompound nbt = tags.getCompoundTag("item");
        ammoStack = ItemStack.loadItemStackFromNBT(nbt.getCompoundTag(TAG_AMMO));
        // backwards compatibility
        if (TinkersRebornUtils.isStackEmpty(ammoStack)) {
            ammoStack = ItemStack.loadItemStackFromNBT(nbt);
        }
        launcherStack = ItemStack.loadItemStackFromNBT(nbt.getCompoundTag(TAG_LAUNCHER));
        power = nbt.getFloat(TAG_POWER);

        this.inGround = nbt.getBoolean("inGround");
        this.inTile = Block.getBlockById(nbt.getInteger("inTile"));
        this.inData = nbt.getByte("inData");
        this.ticksInGround = nbt.getShort("life");
        this.noGravity = nbt.getBoolean("noGravity");

        this.xTile = nbt.getInteger("xTile");
        this.yTile = nbt.getInteger("yTile");
        this.zTile = nbt.getInteger("zTile");
    }

    @Override
    public void writeSpawnData(ByteBuf data) {
        data.writeFloat(rotationYaw);

        // shooting entity
        int id = shootingEntity == null ? this.getEntityId() : shootingEntity.getEntityId();
        data.writeInt(id);

        // motion stuff. This has to be sent separately since MC seems to do hardcoded stuff to arrows with this
        data.writeDouble(this.motionX);
        data.writeDouble(this.motionY);
        data.writeDouble(this.motionZ);

        ByteBufUtils.writeItemStack(data, this.ammoStack);
        ByteBufUtils.writeItemStack(data, this.launcherStack);
        data.writeFloat(this.power);
    }

    @Override
    public void readSpawnData(ByteBuf data) {
        rotationYaw = data.readFloat();
        shootingEntity = this.worldObj.getEntityByID(data.readInt());

        this.motionX = data.readDouble();
        this.motionY = data.readDouble();
        this.motionZ = data.readDouble();

        this.ammoStack = ByteBufUtils.readItemStack(data);
        this.launcherStack = ByteBufUtils.readItemStack(data);
        this.power = data.readFloat();

        this.posX -= MathHelper.cos(this.rotationYaw / 180.0F * (float) Math.PI) * 0.16F;
        this.posY -= 0.10000000149011612D;
        this.posZ -= MathHelper.sin(this.rotationYaw / 180.0F * (float) Math.PI) * 0.16F;
    }
}
