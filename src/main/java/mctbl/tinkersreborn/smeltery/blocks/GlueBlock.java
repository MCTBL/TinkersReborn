package mctbl.tinkersreborn.smeltery.blocks;

import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

import mctbl.tinkersreborn.library.TinkersRebornRegistry;
import mctbl.tinkersreborn.library.blocks.TinkersRebornBlock;

public class GlueBlock extends TinkersRebornBlock {

    public GlueBlock() {
        super(Material.ground, "tinkersreborn.glueblock", 4.0f, new String[] { "glue" });
        this.setCreativeTab(TinkersRebornRegistry.blockTab);
    }

    @Override
    public String getUnlocalizedName() {
        return "tinkersreborn.glueblock";
    }

    @Override
    public void onEntityCollidedWithBlock(World world, int x, int y, int z, Entity entity) {
        entity.motionX *= 0.1;
        entity.motionZ *= 0.1;

        if (entity instanceof EntityLivingBase living) {
            // Well you'd feel ill too standing on glue...
            living.addPotionEffect(new PotionEffect(Potion.hunger.getId(), 20, 4));

            // Glue is sticky stuff
            living.addPotionEffect(new PotionEffect(Potion.moveSlowdown.getId(), 30, 4));
        }
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
        return Blocks.soul_sand.getCollisionBoundingBoxFromPool(world, x, y, z);
    }
}
