package mctbl.tinkersreborn.common.blocks;

import java.util.Arrays;
import java.util.stream.Collectors;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.world.IBlockAccess;

import mctbl.tinkersreborn.TinkersRebornConfig;
import mctbl.tinkersreborn.library.TinkersRebornRegistry;
import mctbl.tinkersreborn.library.blocks.TinkersRebornBlock;

public class TinkersRebornMetalBlock extends TinkersRebornBlock {

    public TinkersRebornMetalBlock(Material material, float hardness) {
        super(
            material,
            "tinkersreborn.metalblock",
            hardness,
            Arrays.asList(TinkersRebornConfig.metalTypes)
                .stream()
                .<String>map(t -> "compressed_" + t.toLowerCase())
                .collect(Collectors.toList())
                .toArray(new String[0]));
        this.setStepSound(Block.soundTypeMetal);
        this.setCreativeTab(TinkersRebornRegistry.blockTab);
    }

    @Override
    public boolean isBeaconBase(IBlockAccess worldObj, int x, int y, int z, int beaconX, int beaconY, int beaconZ) {
        return true;
    }

}
