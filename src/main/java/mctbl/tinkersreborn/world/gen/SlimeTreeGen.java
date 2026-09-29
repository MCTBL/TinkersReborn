package mctbl.tinkersreborn.world.gen;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraftforge.common.util.ForgeDirection;

import mctbl.tinkersreborn.common.TinkersRebornGeneral;

public class SlimeTreeGen extends WorldGenerator {

    public final int minTreeHeight;
    public final int treeHeightRange;
    public final int metaWood;
    public final int metaLeaves;
    public final boolean seekHeight;
    public final boolean notify;

    public SlimeTreeGen(boolean notify, int treeHeight, int treeRange, int woodMeta, int leavesMeta) {
        super(notify);
        this.notify = notify;
        this.minTreeHeight = treeHeight;
        this.treeHeightRange = treeRange;
        this.metaWood = woodMeta;
        this.metaLeaves = leavesMeta;
        this.seekHeight = !notify;
    }

    @Override
    public boolean generate(World world, Random random, int xPos, int yPos, int zPos) {
        int height = random.nextInt(this.treeHeightRange) + this.minTreeHeight;
        if (seekHeight) {
            yPos = findGround(world, xPos, yPos, zPos);
            if (yPos == -1) return false;
        }

        if (yPos >= 1 && yPos + height + 1 <= 256) {
            Block soil = world.getBlock(xPos, yPos - 1, zPos);
            boolean isSoil = (soil != null && soil
                .canSustainPlant(world, xPos, yPos - 1, zPos, ForgeDirection.UP, TinkersRebornGeneral.slimeSapling));

            if (isSoil) {
                soil.onPlantGrow(world, xPos, yPos - 1, zPos, xPos, yPos, zPos);
                placeCanopy(world, random, xPos, yPos, zPos, height);
                placeTrunk(world, xPos, yPos, zPos, height);
                placeVines(world, random, xPos, yPos, zPos, height);
                return true;
            }
        }
        return false;
    }

    boolean checkClear(World world, int x, int y, int z, int treeHeight) {
        for (int yPos = 0; yPos < treeHeight + 1; yPos++) {
            int range = 1;

            if (yPos == 0) range = 0;
            else if (yPos >= treeHeight - 1) range = 2;

            for (int xPos = range; xPos <= range; xPos++) {
                for (int zPos = range; zPos <= range; zPos++) {
                    Block blockID = world.getBlock(x + xPos, y + yPos, z + zPos);
                    if (blockID != null && blockID != TinkersRebornGeneral.slimeSapling
                        && !blockID.isLeaves(world, x + xPos, y + yPos, z + zPos)) return false;
                }
            }
        }
        return true;
    }

    int findGround(World world, int x, int y, int z) {
        int ret = -1;
        int height = y;
        do {
            Block heightID = world.getBlock(x, height, z);
            if ((heightID == TinkersRebornGeneral.slimeGrass) && !world.getBlock(x, height + 1, z)
                .isOpaqueCube()) {
                ret = height + 1;
                break;
            }
            height--;
        } while (height > 0);
        return ret;
    }

    void placeCanopy(World world, Random random, int xPos, int yPos, int zPos, int height) {
        for (int i = 0; i < 4; i++) {
            placeDiamondLayer(world, xPos, yPos + height - i, zPos, i + 1);
        }

        this.setBlockAndMetadata(world, xPos + 4, yPos + height - 3, zPos, Blocks.air, 0);
        this.setBlockAndMetadata(world, xPos - 4, yPos + height - 3, zPos, Blocks.air, 0);
        this.setBlockAndMetadata(world, xPos, yPos + height - 3, zPos + 4, Blocks.air, 0);
        this.setBlockAndMetadata(world, xPos, yPos + height - 3, zPos - 4, Blocks.air, 0);
        this.setBlockAndMetadata(world, xPos + 1, yPos + height - 3, zPos + 1, Blocks.air, 0);
        this.setBlockAndMetadata(world, xPos + 1, yPos + height - 3, zPos - 1, Blocks.air, 0);
        this.setBlockAndMetadata(world, xPos - 1, yPos + height - 3, zPos + 1, Blocks.air, 0);
        this.setBlockAndMetadata(world, xPos - 1, yPos + height - 3, zPos - 1, Blocks.air, 0);

        // Drippers
        this.setBlockAndMetadata(
            world,
            xPos + 3,
            yPos + height - 4,
            zPos,
            TinkersRebornGeneral.slimeLeaves,
            this.metaLeaves);
        this.setBlockAndMetadata(
            world,
            xPos - 3,
            yPos + height - 4,
            zPos,
            TinkersRebornGeneral.slimeLeaves,
            this.metaLeaves);
        this.setBlockAndMetadata(
            world,
            xPos,
            yPos + height - 4,
            zPos - 3,
            TinkersRebornGeneral.slimeLeaves,
            this.metaLeaves);
        this.setBlockAndMetadata(
            world,
            xPos,
            yPos + height - 4,
            zPos + 3,
            TinkersRebornGeneral.slimeLeaves,
            this.metaLeaves);
        this.setBlockAndMetadata(
            world,
            xPos + 2,
            yPos + height - 4,
            zPos + 2,
            TinkersRebornGeneral.slimeLeaves,
            this.metaLeaves);
        this.setBlockAndMetadata(
            world,
            xPos + 2,
            yPos + height - 4,
            zPos - 2,
            TinkersRebornGeneral.slimeLeaves,
            this.metaLeaves);
        this.setBlockAndMetadata(
            world,
            xPos - 2,
            yPos + height - 4,
            zPos + 2,
            TinkersRebornGeneral.slimeLeaves,
            this.metaLeaves);
        this.setBlockAndMetadata(
            world,
            xPos - 2,
            yPos + height - 4,
            zPos - 2,
            TinkersRebornGeneral.slimeLeaves,
            this.metaLeaves);

        this.setBlockAndMetadata(
            world,
            xPos + 3,
            yPos + height - 5,
            zPos,
            TinkersRebornGeneral.slimeLeaves,
            this.metaLeaves);
        this.setBlockAndMetadata(
            world,
            xPos - 3,
            yPos + height - 5,
            zPos,
            TinkersRebornGeneral.slimeLeaves,
            this.metaLeaves);
        this.setBlockAndMetadata(
            world,
            xPos,
            yPos + height - 5,
            zPos - 3,
            TinkersRebornGeneral.slimeLeaves,
            this.metaLeaves);
        this.setBlockAndMetadata(
            world,
            xPos,
            yPos + height - 5,
            zPos + 3,
            TinkersRebornGeneral.slimeLeaves,
            this.metaLeaves);
        this.setBlockAndMetadata(
            world,
            xPos + 2,
            yPos + height - 5,
            zPos + 2,
            TinkersRebornGeneral.slimeLeaves,
            this.metaLeaves);
        this.setBlockAndMetadata(
            world,
            xPos + 2,
            yPos + height - 5,
            zPos - 2,
            TinkersRebornGeneral.slimeLeaves,
            this.metaLeaves);
        this.setBlockAndMetadata(
            world,
            xPos - 2,
            yPos + height - 5,
            zPos + 2,
            TinkersRebornGeneral.slimeLeaves,
            this.metaLeaves);
        this.setBlockAndMetadata(
            world,
            xPos - 2,
            yPos + height - 5,
            zPos - 2,
            TinkersRebornGeneral.slimeLeaves,
            this.metaLeaves);
    }

    void placeDiamondLayer(World world, int xPos, int yPos, int zPos, int range) {
        for (int x = -range; x <= range; x++) {
            for (int z = -range; z <= range; z++) {
                if (Math.abs(x) + Math.abs(z) <= range) {
                    this.setBlockAndMetadata(
                        world,
                        x + xPos,
                        yPos,
                        z + zPos,
                        TinkersRebornGeneral.slimeLeaves,
                        this.metaLeaves);
                }
            }
        }
    }

    void placeTrunk(World world, int xPos, int yPos, int zPos, int height) {
        for (int localHeight = 0; localHeight < height; ++localHeight) {
            Block blockID = world.getBlock(xPos, yPos + localHeight, zPos);

            if (blockID == Blocks.air || blockID == null || blockID.isLeaves(world, xPos, yPos + localHeight, zPos)) {
                this.setBlockAndMetadata(
                    world,
                    xPos,
                    yPos + localHeight,
                    zPos,
                    TinkersRebornGeneral.slimeGel,
                    this.metaWood);
            }
        }
    }

    void placeVines(World world, Random random, int xPos, int yPos, int zPos, int height) {
        int top = yPos + height;
        int bottom = Math.max(yPos + height - 5, yPos);
        for (int y = bottom; y <= top; y++) {
            for (int x = xPos - 4; x <= xPos + 4; x++) {
                for (int z = zPos - 4; z <= zPos + 4; z++) {
                    if (world.getBlock(x, y, z) != TinkersRebornGeneral.slimeLeaves || random.nextInt(4) != 0) continue;
                    switch (random.nextInt(4)) {
                        case 0:
                            placeVine(world, random, x + 1, y, z, 2);
                            break;
                        case 1:
                            placeVine(world, random, x - 1, y, z, 8);
                            break;
                        case 2:
                            placeVine(world, random, x, y, z + 1, 4);
                            break;
                        default:
                            placeVine(world, random, x, y, z - 1, 1);
                            break;
                    }
                }
            }
        }
    }

    void placeVine(World world, Random random, int x, int y, int z, int metadata) {
        // 5 vine at most, 100% => 50% => 33.33% => 25% => 20%
        for (int i = 0; i < 5; i++) {
            if (world.isAirBlock(x, y - i, z)) {
                if (random.nextInt(i + 1) == 0) {
                    world.setBlock(x, y - i, z, TinkersRebornGeneral.slimeVine, metadata, 2);
                } else {
                    break;
                }
            }
        }
    }

    protected void setBlockAndMetadata(World world, int x, int y, int z, Block blockID, int blockMeta) {
        Block block = world.getBlock(x, y, z);
        if (block == null || block.canPlaceBlockAt(world, x, y, z)) {
            if (this.notify) {
                world.setBlock(x, y, z, blockID, blockMeta, 3);
            } else {
                world.setBlock(x, y, z, blockID, blockMeta, 2);
            }
        }
    }

}
