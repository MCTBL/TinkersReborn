package mctbl.tinkersreborn.common.blocks.slime;

import java.util.Random;

import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

import mctbl.tinkersreborn.common.TinkersRebornGeneral;
import mctbl.tinkersreborn.common.entity.BlueSlime;
import mctbl.tinkersreborn.common.entity.KingBlueSlime;
import mctbl.tinkersreborn.library.entity.SlimeBase;
import mctbl.tinkersreborn.smeltery.blocks.TinkersRebornFluid;
import mctbl.tinkersreborn.smeltery.blocks.TinkersRebornFluidBlock;

public class SlimeFluid extends TinkersRebornFluidBlock {

    public SlimeFluid(TinkersRebornFluid fluid) {
        super(fluid);
        this.setStepSound(TinkersRebornGeneral.slimeStep);
    }

    @Override
    public void updateTick(World world, int x, int y, int z, Random rand) {
        super.updateTick(world, x, y, z, rand);
        if (rand.nextInt(100) == 0 && isSourceBlock(world, x, y, z)
            && world.checkNoEntityCollision(
                AxisAlignedBB.getBoundingBox(x - 1.0D, y - 1.0D, z - 1.0D, x + 2.0D, y + 2.0D, z + 2.0D))) {
            SlimeBase slime;
            if (rand.nextInt(300) == 0) {
                slime = new KingBlueSlime(world);
            } else {
                slime = new BlueSlime(world);
            }
            slime.setPosition(x + 0.5D, y + 1.5D, z + 0.5D);
            world.spawnEntityInWorld(slime);
        }
    }

}
