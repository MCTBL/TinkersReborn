package mctbl.tinkersreborn;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import cpw.mods.fml.common.network.IGuiHandler;
import cpw.mods.fml.common.network.NetworkRegistry;
import mctbl.tinkersreborn.common.network.AbstractPacket;
import mctbl.tinkersreborn.common.network.SpawnParticlePacket;
import mctbl.tinkersreborn.common.network.TinkerNetwork;
import mctbl.tinkersreborn.common.particle.Particles;
import mctbl.tinkersreborn.common.particle.TinkersRebornParticle;
import mctbl.tinkersreborn.library.entity.TinkersRebornInventoryLogic;

public class CommonProxy implements IGuiHandler {

    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        TileEntity tileEntity = world.getTileEntity(x, y, z);
        if (tileEntity instanceof TinkersRebornInventoryLogic tinker) {
            return tinker.getGuiContainer(player.inventory, world, x, y, z);
        }
        return null;
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        TileEntity tileEntity = world.getTileEntity(x, y, z);
        if (tileEntity instanceof TinkersRebornInventoryLogic tinker) {
            return tinker.getGui(player.inventory, world, x, y, z);
        }
        return null;
    }

    public void spawnEffectParticle(TinkersRebornParticle.Type type, Entity entity, int count) {
        spawnParticle(
            Particles.EFFECT,
            entity.worldObj,
            entity.posX,
            entity.posY + entity.height * 0.5f,
            entity.posZ,
            0d,
            1d,
            0d,
            count,
            type.ordinal());
    }

    public void spawnEffectParticle(TinkersRebornParticle.Type type, World world, double x, double y, double z,
        int count) {
        spawnParticle(Particles.EFFECT, world, x, y, z, 0d, -1d, 0d, count, type.ordinal());
    }

    public void spawnParticle(Particles particleType, World world, double x, double y, double z, int... data) {
        spawnParticle(particleType, world, x, y, z, 0d, 0d, 0d, data);
    }

    public void spawnParticle(Particles particleType, World world, double x, double y, double z, double xSpeed,
        double ySpeed, double zSpeed, int... data) {
        // 32*32 = 1024 = vanilla particle range
        NetworkRegistry.TargetPoint point = new NetworkRegistry.TargetPoint(world.provider.dimensionId, x, y, z, 32);
        AbstractPacket packet = new SpawnParticlePacket(particleType, x, y, z, xSpeed, ySpeed, zSpeed, data);
        TinkerNetwork.sendToAllAround(packet, point);
    }
}
