package mctbl.tinkersreborn.common.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;

import mctbl.tinkersreborn.library.TinkersRebornRegistry;
import mctbl.tinkersreborn.library.blocks.TinkersRebornBlock;

public class SlimeSand extends TinkersRebornBlock {

    public SlimeSand() {
        super(Material.gourd, "tinkersreborn.slimesand", 3.0F, new String[] { "slimesand", "slimesandblue" });
        this.setHarvestLevel("shovel", -1);
        this.setLightOpacity(0);
        this.stepSound = Block.soundTypeSand;
        this.setCreativeTab(TinkersRebornRegistry.blockTab);
    }

    @Override
    public void onEntityCollidedWithBlock(World world, int x, int y, int z, Entity entity) {
        entity.motionX *= 0.4;
        entity.motionZ *= 0.4;
        if (entity instanceof EntityLivingBase e) {
            e.addPotionEffect(new PotionEffect(Potion.weakness.id, 1));
            e.addPotionEffect(new PotionEffect(Potion.jump.id, 1, 1));
        }
    }

}
