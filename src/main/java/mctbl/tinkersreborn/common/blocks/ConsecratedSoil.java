package mctbl.tinkersreborn.common.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.init.Blocks;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

import mctbl.tinkersreborn.library.TinkersRebornRegistry;
import mctbl.tinkersreborn.library.blocks.TinkersRebornBlock;

public class ConsecratedSoil extends TinkersRebornBlock {

    public ConsecratedSoil() {
        super(Material.gourd, "tinkersreborn.consecratedsoil", 3.0F, new String[] { "consecratedsoil" });
        this.setHarvestLevel("shovel", -1);
        this.setLightOpacity(0);
        this.stepSound = Block.soundTypeSand;
        this.setCreativeTab(TinkersRebornRegistry.blockTab);
    }

    @Override
    public void onEntityWalking(World world, int x, int y, int z, Entity entity) {
        if (entity instanceof EntityLivingBase e) {
            if (e.getCreatureAttribute() == EnumCreatureAttribute.UNDEAD) {
                e.attackEntityFrom(DamageSource.magic, 1);
                e.setFire(1);
            }
        }
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
        return Blocks.soul_sand.getCollisionBoundingBoxFromPool(world, x, y, z);
    }

}
