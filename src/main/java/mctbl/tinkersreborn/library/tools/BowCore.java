package mctbl.tinkersreborn.library.tools;

import java.util.List;
import java.util.UUID;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Items;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.ArrowLooseEvent;
import net.minecraftforge.event.entity.player.ArrowNockEvent;

import com.google.common.collect.Multimap;

import mctbl.tinkersreborn.library.event.ProjectileEvent;
import mctbl.tinkersreborn.library.event.Sounds;
import mctbl.tinkersreborn.library.event.TinkerToolEvent;
import mctbl.tinkersreborn.library.materials.TinkersRebornMaterial;
import mctbl.tinkersreborn.tools.Category;
import mctbl.tinkersreborn.util.AmmoHelper;
import mctbl.tinkersreborn.util.TinkersRebornUtils;
import mctbl.tinkersreborn.util.ToolTagsHelper;

public abstract class BowCore extends ToolCore {

    protected static final UUID LAUNCHER_BONUS_DAMAGE = UUID.fromString("066b8892-d2ac-4bae-ac22-26f9f91a02ee");
    protected static final UUID LAUNCHER_DAMAGE_MODIFIER = UUID.fromString("4f76565a-3845-4a09-ba8f-92a37937a7c3");

    protected static final ResourceLocation PROPERTY_PULL_PROGRESS = new ResourceLocation("pull");
    protected static final ResourceLocation PROPERTY_IS_PULLING = new ResourceLocation("pulling");

    protected BowCore(String toolTypeName, int partAmount) {
        super(toolTypeName, partAmount);
        this.categoryTags.add(Category.LAUNCHER);
    }

    protected ProjectileLauncherNBT getData(ItemStack stack) {
        return ProjectileLauncherNBT.from(stack);
    }

    @Override
    public abstract ProjectileLauncherNBT buildToolTag(List<TinkersRebornMaterial> materials);

    /* Stuff to override */
    protected float baseInaccuracy() {
        return 0f;
    }

    protected float baseProjectileSpeed() {
        return 3f;
    }

    public int getDrawTime() {
        return 20;
    }

    public float getDrawbackProgress(ItemStack itemstack, EntityPlayer entityIn) {
        if (itemstack.getItem() == BowCore.this) {
            int timePassed = itemstack.getMaxItemUseDuration() - entityIn.getItemInUseCount();
            return getDrawbackProgress(itemstack, timePassed);
        } else {
            return 0f;
        }
    }

    protected float getDrawbackProgress(ItemStack itemStack, int timePassed) {
        float drawProgress = ProjectileLauncherNBT.from(itemStack).drawSpeed * (float) timePassed;
        return Math.min(1f, drawProgress / (getDrawTime() * 1.0F));
    }

    @Override
    public EnumAction getItemUseAction(ItemStack stack) {
        return EnumAction.bow;
    }

    @Override
    public int getMaxItemUseDuration(ItemStack stack) {
        return 72000;
    }

    public ItemStack findAmmo(ItemStack weapon, EntityLivingBase player) {
        return AmmoHelper.findAmmoFromInventory(getAmmoItems(), player);
    }

    public ItemStack getAmmoToRender(ItemStack weapon, EntityLivingBase player) {
        if (ToolTagsHelper.isBroken(weapon)) {
            return null;
        }
        return findAmmo(weapon, player);
    }

    protected abstract List<Item> getAmmoItems();

    @Override
    public ItemStack onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer player) {
        if (!ToolTagsHelper.isBroken(itemStackIn)) {
            boolean hasAmmo = !TinkersRebornUtils.isStackEmpty(findAmmo(itemStackIn, player));
            ArrowNockEvent event = new ArrowNockEvent(player, itemStackIn);
            MinecraftForge.EVENT_BUS.post(event);
            if (event.isCanceled()) {
                return event.result;
            } else if (hasAmmo) {
                player.setItemInUse(itemStackIn, this.getMaxItemUseDuration(itemStackIn));
            }
        }
        return itemStackIn;
    }

    @Override
    public void onPlayerStoppedUsing(ItemStack stack, World worldIn, EntityPlayer player, int timeLeft) {
        if (ToolTagsHelper.isBroken(stack)) {
            return;
        }
        ItemStack ammo = findAmmo(stack, player);
        if (TinkersRebornUtils.isStackEmpty(ammo) && !player.capabilities.isCreativeMode) {
            return;
        }
        if (TinkersRebornUtils.isStackEmpty(ammo)) {
            ammo = new ItemStack(Items.arrow);
        }

        int useTime = this.getMaxItemUseDuration(stack) - timeLeft;

        ArrowLooseEvent event = new ArrowLooseEvent(player, stack, useTime);
        MinecraftForge.EVENT_BUS.post(event);
        if (event.isCanceled() || event.charge < 5) {
            return;
        }

        shootProjectile(ammo, stack, worldIn, player, useTime);

        player.addStat(StatList.objectUseStats[Item.getIdFromItem(this)], 1);

        // needs to be done manually for the overrides to work out correctly
        // since TiC tools don't get updated by default due to their custom equip check
        // this interferes with the item properties since it gets the wrong itemstack
        // causing animations not to work
        // TinkerRangedWeapons.proxy.updateEquippedItemForRendering(entityLiving.getActiveHand());
        ToolTagsHelper.setResetFlag(stack, true);
    }

    public void shootProjectile(ItemStack ammoIn, @Nonnull ItemStack bow, World worldIn, EntityPlayer player,
        int useTime) {
        float progress = getDrawbackProgress(bow, useTime);
        float power = getArrowVelocity((int) (progress * 20f)) * progress * baseProjectileSpeed();
        power *= ProjectileLauncherNBT.from(bow).range;

        if (!worldIn.isRemote) {
            TinkerToolEvent.OnBowShoot event = TinkerToolEvent.OnBowShoot
                .fireEvent(bow, ammoIn, player, useTime, baseInaccuracy());

            // copied because consumeAmmo can delete vanilla stacks
            ItemStack ammoStackToShoot = ammoIn.copy();

            for (int i = 0; i < event.projectileCount; i++) {
                boolean usedAmmo = false;
                if (i == 0 || event.consumeAmmoPerProjectile) {
                    usedAmmo = consumeAmmo(ammoIn, player);
                }
                float inaccuracy = event.getBaseInaccuracy();
                if (i > 0) {
                    inaccuracy += event.bonusInaccuracy;
                }
                EntityArrow projectile = getProjectileEntity(
                    ammoStackToShoot,
                    bow,
                    worldIn,
                    player,
                    power,
                    inaccuracy,
                    progress * progress,
                    usedAmmo);

                if (projectile != null && ProjectileEvent.OnLaunch.fireEvent(projectile, bow, player)) {
                    if (progress >= 1f) {
                        projectile.setIsCritical(true);
                    }
                    if (!player.capabilities.isCreativeMode) {
                        ToolTagsHelper.damageTool(bow, 1, player);
                    }
                    worldIn.spawnEntityInWorld(projectile);
                }
            }
        }

        playShootSound(power, player);
    }

    public void playShootSound(float power, EntityPlayer entityPlayer) {
        Sounds.playSoundForAll(
            entityPlayer,
            "random.bow",
            1.0F,
            1.0F / (itemRand.nextFloat() * 0.4F + 1.2F) + power * 0.5F);
    }

    /**
     * Gets the velocity of the arrow entity from the bow's charge
     */
    public static float getArrowVelocity(int charge) {
        float f = charge * 1.0F / 20.0F;
        f = (f * f + f * 2.0F) / 3.0F;

        return MathHelper.clamp_float(f, 0, 1.0F);
    }

    public boolean consumeAmmo(ItemStack ammo, EntityPlayer player) {
        // no ammo consumption in creative
        if (player.capabilities.isCreativeMode) {
            return false;
        }

        if (ammo.getItem() instanceof AmmoCore ammoCore) {
            return ammoCore.useAmmo(ammo, player);
        } else {
            ammo.stackSize -= 1;
            if (TinkersRebornUtils.isStackEmpty(ammo)) {
                // player.inventory.deleteStack(ammo);
            }
            return true;
        }
    }

    public EntityArrow getProjectileEntity(ItemStack ammo, ItemStack bow, World world, EntityPlayer player, float power,
        float inaccuracy, float progress, boolean usedAmmo) {
        if (ammo.getItem() instanceof AmmoCore ammoCore) {
            return ammoCore.getProjectile(ammo, bow, world, player, power, inaccuracy, progress, usedAmmo);
        } else if (ammo.getItem() == Items.arrow) {
            EntityArrow projectile = new EntityArrow(world, player, power);

            double rotationYaw = player.rotationYaw;
            double rotationPitch = player.rotationPitch;

            double motionX = -MathHelper.sin((float) (rotationYaw / 180.0F * Math.PI))
                * MathHelper.cos((float) (rotationPitch / 180.0F * Math.PI));
            double motionZ = MathHelper.cos((float) (rotationYaw / 180.0F * Math.PI))
                * MathHelper.cos((float) (rotationPitch / 180.0F * Math.PI));
            double motionY = -MathHelper.sin((float) (rotationPitch / 180.0F * Math.PI));
            projectile.setThrowableHeading(motionX, motionY, motionZ, power * 1.5F, inaccuracy);

            projectile.motionX += player.motionX;
            projectile.motionZ += player.motionZ;
            if (!player.onGround) {
                projectile.motionY += player.motionY;
            }

            projectile.canBePickedUp = usedAmmo ? 1 : 0;

            return projectile;
        }
        // shizzle-foo, this fizzles too!
        return null;
    }

    public abstract float baseProjectileDamage();

    public abstract float projectileDamageModifier();

    public void modifyProjectileAttributes(Multimap<String, AttributeModifier> projectileAttributes,
        @Nullable ItemStack launcher, ItemStack projectile, float power) {
        double dmg = baseProjectileDamage() * power;
        dmg += ProjectileLauncherNBT.from(launcher).bonusDamage;
        if (dmg != 0) {
            projectileAttributes.put(
                SharedMonsterAttributes.attackDamage.getAttributeUnlocalizedName(),
                new AttributeModifier(LAUNCHER_BONUS_DAMAGE, "Launcher bonus damage", dmg, 0));
        }
        if (projectileDamageModifier() != 0f) {
            projectileAttributes.put(
                SharedMonsterAttributes.attackDamage.getAttributeUnlocalizedName(),
                new AttributeModifier(
                    LAUNCHER_DAMAGE_MODIFIER,
                    "Launcher damage modifier",
                    projectileDamageModifier() - 1f,
                    1));
        }
    }
}
