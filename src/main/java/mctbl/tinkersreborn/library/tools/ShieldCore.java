package mctbl.tinkersreborn.library.tools;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

import mctbl.tinkersreborn.TinkersReborn;
import mctbl.tinkersreborn.common.network.TinkerNetwork;
import mctbl.tinkersreborn.library.TinkersRebornRegistry;
import mctbl.tinkersreborn.library.event.Sounds;
import mctbl.tinkersreborn.tools.Category;
import mctbl.tinkersreborn.tools.network.EntityMovementChangePacket;
import mctbl.tinkersreborn.util.ShieldHelper;
import mctbl.tinkersreborn.util.ToolTagsHelper;

public abstract class ShieldCore extends ToolCore {

    protected ShieldCore(String toolTypeName, int partAmount) {
        super(toolTypeName, partAmount);
        this.setCreativeTab(TinkersRebornRegistry.shieldTab);
        addCategory(Category.SHIELD);
    }

    @Override
    public float damageCutoff() {
        return 2.0F;
    }

    @Override
    public float damagePotential() {
        return 0.5F;
    }

    @Override
    public int getMaxItemUseDuration(ItemStack stack) {
        return 72000;
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        if (ToolTagsHelper.isBroken(stack)) {
            return stack;
        }

        if (ShieldHelper.isDisable(player)) {
            player.addChatMessage(
                new ChatComponentText(
                    EnumChatFormatting.RED + "block still need"
                        + ShieldHelper.disableTicks(player)
                        + " ticks to recover"));
            player.stopUsingItem();
            return stack;
        }

        if (player.isUsingItem()) {
            return stack;
        }

        player.setItemInUse(stack, getMaxItemUseDuration(stack));

        return stack;
    }

    public void handleBlocking(ItemStack shieldStack, EntityPlayer player, LivingHurtEvent event) {
        boolean isBreak = event.ammount >= this.breakLimit();

        // got hit by something: reduce damage
        int damage = event.ammount < 2f ? 1 : Math.round(event.ammount / 2f);

        // reduce damage. After this event the damage will be halved again because we're blocking so we have to
        // factor this in
        float reduction = this.damageReduction() / (isBreak ? 2.0F : 1.0F);

        event.ammount = (event.ammount * (1.0F - reduction));

        // reflect damage
        if (event.source.getSourceOfDamage() != null && !isBreak) {
            event.source.getSourceOfDamage()
                .attackEntityFrom(DamageSource.causeThornsDamage(player), event.ammount / 2f);
            damage = damage * 3 / 2;
        } else if (isBreak) {
            damage *= 2;
        }

        // blocking success
        ToolTagsHelper.getTraitsOrdered(shieldStack)
            .forEach(trait -> trait.onBlock(shieldStack, player, event));

        if (!isBreak) {
            ShieldHelper.preventNextKnockback(player);
            Sounds.playSoundForAll(player, Sounds.blocking, 1.0f, 0.8f + 0.4f * TinkersReborn.random.nextFloat());
        } else {
            ShieldHelper.setPlayerBreaking(player, this.breakingRecoverTime());
            Sounds.playSoundForAll(player, "random.break", 1.0f, 0.8f + 0.4f * TinkersReborn.random.nextFloat());
        }

        ToolTagsHelper.damageTool(shieldStack, damage, player);
    }

    public void handleArrowReflection(ItemStack shieldStack, EntityPlayer player, LivingAttackEvent event) {
        Entity projectile = event.source.getSourceOfDamage();
        Vec3 motion = Vec3.createVectorHelper(projectile.motionX, projectile.motionY, projectile.motionZ);
        Vec3 look = player.getLookVec();
        // this gives a factor of how much we're looking at the incoming arrow
        double strength = -look.dotProduct(motion.normalize());
        // we're looking away. oh no.
        if (strength < 0.1) {
            return;
        }

        // caught that bastard! block it!
        event.setCanceled(true);

        // and return it to the sender
        // calc speed of the projectile
        double speed = projectile.motionX * projectile.motionX + projectile.motionY * projectile.motionY
            + projectile.motionZ * projectile.motionZ;
        speed = Math.sqrt(speed);
        speed += 0.2f; // we add a bit speed

        // and redirect it to where the player is looking
        projectile.motionX = look.xCoord * speed;
        projectile.motionY = look.yCoord * speed;
        projectile.motionZ = look.zCoord * speed;

        projectile.rotationYaw = (float) (Math.atan2(projectile.motionX, projectile.motionZ) * 180.0D / Math.PI);
        projectile.rotationPitch = (float) (Math.atan2(projectile.motionY, speed) * 180.0D / Math.PI);

        // special treatement for arrows
        if (projectile instanceof EntityArrow arrow) {
            arrow.shootingEntity = player;
        }

        // notify clients from change, otherwise people will get veeeery confused
        TinkerNetwork.sendToAll(new EntityMovementChangePacket(projectile));

        if (projectile instanceof EntityArrow) {
            // the inverse is done when the event is cancelled in arrows etc.
            // we reverse it so it has no effect. yay
            projectile.motionX /= -0.10000000149011612D;
            projectile.motionY /= -0.10000000149011612D;
            projectile.motionZ /= -0.10000000149011612D;
        }

        // use durability equal to the damage prevented
        ToolTagsHelper.damageTool(shieldStack, (int) event.ammount, player);
        Sounds.playSoundForAll(player, Sounds.blocking, 1.0f, 0.8f + 0.4f * TinkersReborn.random.nextFloat());
    }

    @Override
    public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        // has to be done in onUpdate because onTickUsing is too early and gets
        // overwritten. bleh.
        this.preventSlowDown(entityIn, this.walkingSpeedReduction());

        super.onUpdate(stack, worldIn, entityIn, itemSlot, isSelected);
    }

    public abstract float blockingAngel();

    public abstract float damageReduction();

    public abstract float walkingSpeedReduction();

    public abstract float breakLimit();

    public abstract int breakingRecoverTime();

    public abstract int blockPrepareTime();
}
