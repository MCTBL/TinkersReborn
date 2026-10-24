package mctbl.tinkersreborn.common.blocks.slime;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import mctbl.tinkersreborn.common.TinkersRebornGeneral;
import mctbl.tinkersreborn.library.TinkersRebornRegistry;
import mctbl.tinkersreborn.library.blocks.TinkersRebornBlock;

public class SlimeGel extends TinkersRebornBlock {

    public SlimeGel() {
        super(
            Material.sponge,
            "tinkersreborn.slime.gel",
            0.5f,
            new String[] { "slime/slimeblock_blue", "slime/slimeblock_green", "slime/slimeblock_purple",
                "slime/slimeblock_magma" });
        this.setCreativeTab(TinkersRebornRegistry.blockTab);
        this.setHarvestLevel("axe", 0, 1);
        this.setLightOpacity(0);
        this.setStepSound(TinkersRebornGeneral.slimeStep);
    }

    @Override
    public boolean getEnableStats() {
        return false;
    }

    @Override
    public int getMobilityFlag() {
        return 0;
    }

    @Override
    public void onEntityCollidedWithBlock(World world, int x, int y, int z, Entity entity) {
        if (entity.motionY < 0) {
            if (entity.motionY < -0.08F) {
                Block block = this;
                world.playSoundEffect(
                    x + 0.5F,
                    y + 0.5F,
                    z + 0.5F,
                    block.stepSound.getStepResourcePath(),
                    (block.stepSound.getVolume()) / 2.0F,
                    block.stepSound.getPitch() * 0.65F);
            }
            entity.motionY *= -1.2F;
            if (entity instanceof EntityLivingBase entityLiving) {
                entityLiving.addPotionEffect(new PotionEffect(Potion.jump.id, 2, 2));
            }
        }
        entity.fallDistance = 0;
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
        return AxisAlignedBB.getBoundingBox(x, y, z, x + 1.0D, y + 0.9D, z + 1.0D);
    }

    @Override
    public boolean canSustainLeaves(IBlockAccess world, int x, int y, int z) {
        return true;
    }

    @Override
    public boolean isWood(IBlockAccess world, int x, int y, int z) {
        return true;
    }

    @Override
    public int getFlammability(IBlockAccess world, int x, int y, int z, ForgeDirection face) {
        return world.getBlockMetadata(x, y, z) == 3 ? 0 : super.getFlammability(world, x, y, z, face);
    }
}
