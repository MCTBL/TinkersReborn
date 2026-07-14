package mctbl.tinkersreborn;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.world.World;

import mctbl.tinkersreborn.common.particle.ParticleAttackHammer;
import mctbl.tinkersreborn.common.particle.ParticleAttackHatchet;
import mctbl.tinkersreborn.common.particle.Particles;
import mctbl.tinkersreborn.common.particle.TinkersRebornParticle;

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
            // case LONGSWORD_ATTACK:
            // return new ParticleAttackLongsword(world, x, y, z, xSpeed, ySpeed, zSpeed, mc.getTextureManager());
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
}
