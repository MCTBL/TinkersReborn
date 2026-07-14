package mctbl.tinkersreborn;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import mctbl.tinkersreborn.common.particle.ParticleAttackBroadsword;
import mctbl.tinkersreborn.common.particle.ParticleAttackHammer;
import mctbl.tinkersreborn.common.particle.ParticleAttackHatchet;
import mctbl.tinkersreborn.common.particle.ParticleAttackLongsword;
import mctbl.tinkersreborn.common.particle.Particles;
import mctbl.tinkersreborn.common.particle.TinkersRebornParticle;
import mctbl.tinkersreborn.util.TinkersRebornUtils;

public class ClientProxy extends CommonProxy {

    private static final Minecraft mc = Minecraft.getMinecraft();

    public void spawnParticle(Particles particleType, World world, double x, double y, double z, double xSpeed,
        double ySpeed, double zSpeed, int... data) {
        // if(Config.disableAllParticles) return;

        EntityFX effect = createParticle(particleType, mc.theWorld, x, y, z, xSpeed, ySpeed, zSpeed, data);
        mc.effectRenderer.addEffect(effect);

        if (particleType == Particles.EFFECT && data[0] > 1) {
            for (int i = 0; i < data[0] - 1; i++) {
                effect = createParticle(particleType, mc.theWorld, x, y, z, xSpeed, ySpeed, zSpeed, data);
                mc.effectRenderer.addEffect(effect);
            }
        }
    }

    public static EntityFX createParticle(Particles type, World world, double x, double y, double z, double xSpeed,
        double ySpeed, double zSpeed, int... data) {
        switch (type) {
            // entities
            // case BLUE_SLIME:
            // return new EntitySlimeFx(world, x, y, z, TinkerCommons.matSlimeBallBlue.getItem(),
            // TinkerCommons.matSlimeBallBlue.getItemDamage());
            // case PURPLE_SLIME:
            // return new EntitySlimeFx(world, x, y, z, TinkerCommons.matSlimeBallPurple.getItem(),
            // TinkerCommons.matSlimeBallPurple.getItemDamage());
            // // attack
            // case CLEAVER_ATTACK:
            // return new ParticleAttackCleaver(world, x, y, z, xSpeed, ySpeed, zSpeed, mc.getTextureManager());
            case LONGSWORD_ATTACK:
                return new ParticleAttackLongsword(world, x, y, z, xSpeed, ySpeed, zSpeed);
            case BROADSWORD_ATTACK:
                return new ParticleAttackBroadsword(world, x, y, z, xSpeed, ySpeed, zSpeed);
            // case RAPIER_ATTACK:
            // return new ParticleAttackRapier(world, x, y, z, xSpeed, ySpeed, zSpeed, mc.getTextureManager());
            case HATCHET_ATTACK:
                return new ParticleAttackHatchet(world, x, y, z, xSpeed, ySpeed, zSpeed);
            // case LUMBERAXE_ATTACK:
            // return new ParticleAttackLumberAxe(world, x, y, z, xSpeed, ySpeed, zSpeed, mc.getTextureManager());
            // case FRYPAN_ATTACK:
            // return new ParticleAttackFrypan(world, x, y, z, xSpeed, ySpeed, zSpeed, mc.getTextureManager());
            case HAMMER_ATTACK:
                return new ParticleAttackHammer(world, x, y, z, xSpeed, ySpeed, zSpeed);
            // effects
            case EFFECT:
                return new TinkersRebornParticle(data[1], world, x, y, z, xSpeed, ySpeed, zSpeed);
            // case ENDSPEED:
            // return new ParticleEndspeed(world, x, y, z, xSpeed, ySpeed, zSpeed);
            default:
                return null;
        }

    }

    @Override
    public void preventPlayerSlowdown(Entity player, float originalSpeed, Item item) {
        // has to be done in onUpdate because onTickUsing is too early and gets overwritten. bleh.
        if (player instanceof EntityPlayerSP playerSP) {
            ItemStack usingItem = playerSP.getItemInUse();
            if (!TinkersRebornUtils.isStackEmpty(usingItem) && usingItem.getItem() == item) {
                // no slowdown from charging it up
                playerSP.movementInput.moveForward *= originalSpeed * 5.0F;
                playerSP.movementInput.moveStrafe *= originalSpeed * 5.0F;
            }
        }
    }

}
