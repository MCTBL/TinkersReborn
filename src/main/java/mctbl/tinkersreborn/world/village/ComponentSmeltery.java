package mctbl.tinkersreborn.world.village;

import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureComponent;
import net.minecraft.world.gen.structure.StructureVillagePieces;
import net.minecraft.world.gen.structure.StructureVillagePieces.Start;

import mctbl.tinkersreborn.smeltery.TinkersRebornSmeltery;

public class ComponentSmeltery extends StructureVillagePieces.House1 {

    private int averageGroundLevel = -1;

    public ComponentSmeltery() {}

    public ComponentSmeltery(Start villagePiece, int componentType, Random random, StructureBoundingBox boundingBox,
        int direction) {
        super();
        this.coordBaseMode = direction;
        this.boundingBox = boundingBox;
    }

    public static ComponentSmeltery buildComponent(Start villagePiece, @SuppressWarnings("rawtypes") List pieces,
        Random random, int x, int y, int z, int direction, int componentType) {
        StructureBoundingBox box = StructureBoundingBox
            .getComponentToAddBoundingBox(x, y, z, 0, 0, 0, 9, 3, 7, direction);
        return canVillageGoDeeper(box) && StructureComponent.findIntersecting(pieces, box) == null
            ? new ComponentSmeltery(villagePiece, componentType, random, box, direction)
            : null;
    }

    @Override
    public boolean addComponentParts(World world, Random random, StructureBoundingBox box) {
        if (this.averageGroundLevel < 0) {
            this.averageGroundLevel = this.getAverageGroundLevel(world, box);
            if (this.averageGroundLevel < 0) return true;
            this.boundingBox.offset(0, this.averageGroundLevel - this.boundingBox.maxY + 2, 0);
        }

        // ground
        this.fillWithBlocks(world, box, 0, 0, 0, 8, 0, 6, Blocks.stonebrick, Blocks.stonebrick, false);
        // clear
        this.fillWithBlocks(world, box, 0, 1, 0, 8, 3, 6, Blocks.air, Blocks.air, false);

        this.fillWithMetaBlocks(
            world,
            box,
            2,
            0,
            1,
            6,
            2,
            5,
            TinkersRebornSmeltery.smelteryBlock,
            0,
            TinkersRebornSmeltery.smelteryBlock,
            0,
            false);
        this.fillWithBlocks(world, box, 3, 1, 2, 5, 2, 4, Blocks.air, Blocks.air, false);

        this.placeBlockAtCurrentPosition(world, TinkersRebornSmeltery.searedBlock, 0, 1, 1, 2, box);
        this.placeBlockAtCurrentPosition(world, TinkersRebornSmeltery.searedBlock, 2, 1, 1, 4, box);
        this.placeBlockAtCurrentPosition(world, TinkersRebornSmeltery.searedBlock, 0, 7, 1, 2, box);
        this.placeBlockAtCurrentPosition(world, TinkersRebornSmeltery.searedBlock, 2, 7, 1, 4, box);

        for (int z = 1; z < 6; z++) {
            for (int x = 0; x < 9; x++) {
                this.clearCurrentPositionBlocksUpwards(world, x, 9, z, box);
                this.func_151554_b(world, Blocks.stonebrick, 0, x, -1, z, box);
            }
        }
        for (int z = 0; z < 7; z++) {
            for (int x = 1; x < 8; x++) {
                this.clearCurrentPositionBlocksUpwards(world, x, 9, z, box);
                this.func_151554_b(world, Blocks.stonebrick, 0, x, -1, z, box);
            }
        }
        return true;
    }

    private void fillWithMetaBlocks(World world, StructureBoundingBox box, int minX, int minY, int minZ, int maxX,
        int maxY, int maxZ, Block placeBlock, int placeMeta, Block replaceBlock, int replaceMeta,
        boolean alwaysReplace) {
        Block rotatedPlaceBlock = this.func_151558_b(placeBlock, placeMeta);
        int rotatedPlaceMeta = this.func_151557_c(placeBlock, placeMeta);
        Block rotatedReplaceBlock = this.func_151558_b(replaceBlock, replaceMeta);
        int rotatedReplaceMeta = this.func_151557_c(replaceBlock, replaceMeta);
        super.fillWithMetadataBlocks(
            world,
            box,
            minX,
            minY,
            minZ,
            maxX,
            maxY,
            maxZ,
            rotatedPlaceBlock,
            rotatedPlaceMeta,
            rotatedReplaceBlock,
            rotatedReplaceMeta,
            alwaysReplace);
    }
}
