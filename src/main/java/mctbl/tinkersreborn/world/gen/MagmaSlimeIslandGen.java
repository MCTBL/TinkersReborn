package mctbl.tinkersreborn.world.gen;

import java.awt.geom.Ellipse2D;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import net.minecraft.world.WorldProviderHell;
import net.minecraft.world.WorldType;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.ChunkProviderFlat;

import mctbl.tinkersreborn.TinkersRebornConfig;
import mctbl.tinkersreborn.common.TinkersRebornGeneral;

public class MagmaSlimeIslandGen extends SlimeIslandGen {

    public MagmaSlimeIslandGen() {
        // still do nothing
    }

    private boolean isLava(World world, int x, int y, int z) {
        return world.getBlock(x, y, z) == Blocks.lava;
    }

    @Override
    public void generate(Random random, int chunkX, int chunkZ, World world, IChunkProvider chunkGenerator,
        IChunkProvider chunkProvider) {
        if ((chunkGenerator instanceof ChunkProviderFlat || world.provider.terrainType == WorldType.FLAT)) {
            return;
        }

        if (world.provider instanceof WorldProviderHell && random.nextInt(TinkersRebornConfig.magmaIslandRarity) == 0) {
            int x = chunkX * 16 + 4 + random.nextInt(8);
            int z = chunkZ * 16 + 4 + random.nextInt(8);
            int y = 31;
            if (isLava(world, x, y, z) && isLava(world, x + 1, y, z)
                && isLava(world, x - 1, y, z)
                && isLava(world, x, y, z + 1)
                && isLava(world, x, y, z - 1)) {
                generateIsland(world, random, chunkX * 16, chunkZ * 16, 3);
            }
        }
    }

    /** Generates one floating island and all of its decorations. */
    public void generateIsland(World world, Random random, int islandOriginX, int islandOriginZ, int islandType) {
        int islandWidth = random.nextInt(13) + 20;
        int islandDepth = random.nextInt(13) + 20;
        int islandHeight = 12;
        int islandBaseY = 32 - islandHeight;
        Ellipse2D.Double islandFootprint = new Ellipse2D.Double(0, 0, islandWidth, islandDepth);

        int grassMeta = switch (islandType) {
            case 0, 1 -> 2;
            case 3 -> 3;
            default -> 1;
        };

        // Fill an elliptical cylinder with slime dirt. The later passes carve its
        // top and bottom into a rough, natural-looking floating island.
        for (int localX = 0; localX <= islandWidth; localX++) {
            for (int localZ = 0; localZ <= islandDepth; localZ++) {
                for (int localY = 0; localY <= islandHeight; localY++) {
                    if (islandFootprint.contains(localX, localZ)) {
                        world.setBlock(
                            localX + islandOriginX,
                            localY + islandBaseY,
                            localZ + islandOriginZ,
                            slimeDirt,
                            islandType,
                            2);
                    }
                }
            }
        }

        // Erode the underside from both ends of the island.
        // Each iteration processes a position and its centrally mirrored counterpart.
        // The full scan intentionally revisits surviving blocks, matching TiC2.
        int bottomErosionDepth = 8;
        for (int localX = 0; localX <= islandWidth; localX++) {
            for (int localZ = 0; localZ <= islandDepth; localZ++) {
                for (int erosionStep = 0; erosionStep <= bottomErosionDepth; erosionStep++) {
                    int worldY = islandBaseY + bottomErosionDepth - erosionStep;
                    erodeBottomBlock(world, random, islandOriginX + localX, worldY, islandOriginZ + localZ, islandType);
                    erodeBottomBlock(
                        world,
                        random,
                        islandOriginX + islandWidth - localX,
                        worldY,
                        islandOriginZ + islandDepth - localZ,
                        islandType);
                }
            }
        }

        // Erode the top edge from the bottom upward. A top block is removed when
        // the layer directly below it is no longer supported on all four sides.
        int topErosionDepth = 3;
        for (int localX = 0; localX <= islandWidth; localX++) {
            for (int localZ = 0; localZ <= islandDepth; localZ++) {
                for (int localY = 1; localY <= topErosionDepth; localY++) {
                    int worldX = localX + islandOriginX;
                    int worldY = localY + islandBaseY + islandHeight - topErosionDepth + 1;
                    int worldZ = localZ + islandOriginZ;
                    boolean hasDirtBelowAllSides = world.getBlock(worldX - 1, worldY - 1, worldZ) == slimeDirt
                        && world.getBlock(worldX + 1, worldY - 1, worldZ) == slimeDirt
                        && world.getBlock(worldX, worldY - 1, worldZ - 1) == slimeDirt
                        && world.getBlock(worldX - 1, worldY - 1, worldZ + 1) == slimeDirt;
                    if (!hasDirtBelowAllSides) {
                        world.setBlock(worldX, worldY, worldZ, Blocks.air, 0, 2);
                    }
                }
            }
        }

        // Turn exposed dirt into grass. Only the upper erosion band is scanned,
        // because deeper layers cannot be exposed to the sky by this algorithm.
        for (int localX = 0; localX <= islandWidth; localX++) {
            for (int localZ = 0; localZ <= islandDepth; localZ++) {
                for (int localY = 0; localY <= topErosionDepth; localY++) {
                    int worldX = localX + islandOriginX;
                    int worldY = localY + islandBaseY + islandHeight - topErosionDepth;
                    int worldZ = localZ + islandOriginZ;
                    if (world.getBlock(worldX, worldY, worldZ) == slimeDirt) {
                        Block blockAbove = world.getBlock(worldX, worldY + 1, worldZ);
                        if (blockAbove == null || blockAbove == Blocks.air) {
                            world.setBlock(worldX, worldY, worldZ, slimeGrass, islandType, 2);
                        }
                    }
                }
            }
        }

        // Add the pool first, then plants and trees on the island's upper surface.
        generateSlimePool(
            world,
            random,
            islandOriginX + islandWidth / 2,
            islandBaseY + islandHeight,
            islandOriginZ + islandDepth / 2,
            islandType);

        PlantGen tallGrass = new PlantGen(
            TinkersRebornGeneral.slimeTallGrass,
            grassMeta,
            128,
            islandWidth,
            1,
            islandDepth,
            false);
        tallGrass.generate(world, random, islandOriginX, islandBaseY + islandHeight + 1, islandOriginZ);
        SlimeTreeGen trees = slimeTreeGen[islandType];
        for (int i = 0; i < 3; i++) {
            trees.generate(
                world,
                random,
                islandOriginX + random.nextInt(islandWidth),
                islandBaseY + islandHeight + 3,
                islandOriginZ + random.nextInt(islandDepth));
        }

        // Restore air gaps below the lava sea surface across the entire scanned area.
        // Top erosion also visits positions outside the elliptical island footprint.
        for (int localX = 0; localX <= islandWidth; localX++) {
            for (int localZ = 0; localZ <= islandDepth; localZ++) {
                int worldX = islandOriginX + localX;
                int worldZ = islandOriginZ + localZ;

                for (int worldY = islandBaseY; worldY <= 31; worldY++) {
                    if (world.isAirBlock(worldX, worldY, worldZ)) {
                        world.setBlock(worldX, worldY, worldZ, Blocks.lava, 0, 2);
                    }
                }
            }
        }
    }

    /** Carves a 16x16x8 ellipsoidal pool into the island. */
    public void generateSlimePool(World world, Random random, int poolCenterX, int poolCenterY, int poolCenterZ,
        int islandType) {
        Block liquidBlock = Blocks.lava;

        int poolOriginX = poolCenterX - 8;
        int poolOriginY = poolCenterY - 4;
        int poolOriginZ = poolCenterZ - 8;
        boolean[] poolCells = new boolean[16 * 16 * 8];
        int ellipsoidCount = random.nextInt(4) + 4;

        // Union several random ellipsoids. Their union becomes the irregular pool shape.
        for (int ellipsoidIndex = 0; ellipsoidIndex < ellipsoidCount; ellipsoidIndex++) {
            double radiusX = random.nextDouble() * 6.0D + 3.0D;
            double radiusY = random.nextDouble() * 4.0D + 2.0D;
            double radiusZ = random.nextDouble() * 6.0D + 3.0D;
            double centerX = random.nextDouble() * (16.0D - radiusX - 2.0D) + 1.0D + radiusX / 2.0D;
            double centerY = random.nextDouble() * (8.0D - radiusY - 4.0D) + 2.0D + radiusY / 2.0D;
            double centerZ = random.nextDouble() * (16.0D - radiusZ - 2.0D) + 1.0D + radiusZ / 2.0D;

            for (int localX = 1; localX < 15; localX++) {
                for (int localZ = 1; localZ < 15; localZ++) {
                    for (int localY = 1; localY < 7; localY++) {
                        // Normalized squared distance from the ellipsoid center.
                        double normalizedX = (localX - centerX) / (radiusX / 2.0D);
                        double normalizedY = (localY - centerY) / (radiusY / 2.0D);
                        double normalizedZ = (localZ - centerZ) / (radiusZ / 2.0D);
                        double normalizedDistanceSquared = normalizedX * normalizedX + normalizedY * normalizedY
                            + normalizedZ * normalizedZ;

                        if (normalizedDistanceSquared < 1.0D) {
                            poolCells[(localX * 16 + localZ) * 8 + localY] = true;
                        }
                    }
                }
            }
        }

        // Reject the whole pool if its boundary would cut into another liquid or
        // into non-solid material. This keeps the generation from creating leaks.
        for (int localX = 0; localX < 16; localX++) {
            for (int localZ = 0; localZ < 16; localZ++) {
                for (int localY = 0; localY < 8; localY++) {
                    boolean isBoundaryCell = !poolCells[(localX * 16 + localZ) * 8 + localY]
                        && (localX < 15 && poolCells[((localX + 1) * 16 + localZ) * 8 + localY]
                            || localX > 0 && poolCells[((localX - 1) * 16 + localZ) * 8 + localY]
                            || localZ < 15 && poolCells[(localX * 16 + localZ + 1) * 8 + localY]
                            || localZ > 0 && poolCells[(localX * 16 + localZ - 1) * 8 + localY]
                            || localY < 7 && poolCells[(localX * 16 + localZ) * 8 + localY + 1]
                            || localY > 0 && poolCells[(localX * 16 + localZ) * 8 + localY - 1]);

                    if (isBoundaryCell) {
                        Material boundaryMaterial = world
                            .getBlock(poolOriginX + localX, poolOriginY + localY, poolOriginZ + localZ)
                            .getMaterial();

                        if (localY >= 4 && boundaryMaterial.isLiquid()) {
                            return;
                        }

                        if (localY < 4 && !boundaryMaterial.isSolid()
                            && world.getBlock(poolOriginX + localX, poolOriginY + localY, poolOriginZ + localZ)
                                != liquidBlock) {
                            return;
                        }
                    }
                }
            }
        }

        // Carve the upper half into air and fill the lower half with the slime liquid.
        for (int localX = 0; localX < 16; localX++) {
            for (int localZ = 0; localZ < 16; localZ++) {
                for (int localY = 0; localY < 8; localY++) {
                    if (poolCells[(localX * 16 + localZ) * 8 + localY]) {
                        world.setBlock(
                            poolOriginX + localX,
                            poolOriginY + localY,
                            poolOriginZ + localZ,
                            localY >= 4 ? Blocks.air : liquidBlock);
                    }
                }
            }
        }

        // Grass can appear on dirt exposed through the pool's upper cavity.
        for (int localX = 0; localX < 16; localX++) {
            for (int localZ = 0; localZ < 16; localZ++) {
                for (int localY = 4; localY < 8; localY++) {
                    if (poolCells[(localX * 16 + localZ) * 8 + localY]
                        && world.getBlock(poolOriginX + localX, poolOriginY + localY - 1, poolOriginZ + localZ)
                            == slimeDirt
                        && world.getSavedLightValue(
                            EnumSkyBlock.Sky,
                            poolOriginX + localX,
                            poolOriginY + localY,
                            poolOriginZ + localZ) > 0) {
                        world.setBlock(
                            poolOriginX + localX,
                            poolOriginY + localY - 1,
                            poolOriginZ + localZ,
                            slimeGrass,
                            0,
                            2);
                    }
                }
            }
        }

        // Place decorative slime gel around the pool's solid boundary.
        if (liquidBlock.getMaterial() == Material.lava) {
            for (int localX = 0; localX < 16; localX++) {
                for (int localZ = 0; localZ < 16; localZ++) {
                    for (int localY = 0; localY < 8; localY++) {
                        boolean isBoundaryCell = !poolCells[(localX * 16 + localZ) * 8 + localY]
                            && (localX < 15 && poolCells[((localX + 1) * 16 + localZ) * 8 + localY]
                                || localX > 0 && poolCells[((localX - 1) * 16 + localZ) * 8 + localY]
                                || localZ < 15 && poolCells[(localX * 16 + localZ + 1) * 8 + localY]
                                || localZ > 0 && poolCells[(localX * 16 + localZ - 1) * 8 + localY]
                                || localY < 7 && poolCells[(localX * 16 + localZ) * 8 + localY + 1]
                                || localY > 0 && poolCells[(localX * 16 + localZ) * 8 + localY - 1]);

                        if (isBoundaryCell && (localY < 4 || random.nextInt(2) != 0)
                            && world.getBlock(poolOriginX + localX, poolOriginY + localY, poolOriginZ + localZ)
                                .getMaterial()
                                .isSolid()
                            && world.getBlock(poolOriginX + localX, poolOriginY + localY + 1, poolOriginZ + localZ)
                                .getMaterial() != Material.lava) {
                            if (random.nextBoolean()) {
                                world.setBlock(
                                    poolOriginX + localX,
                                    poolOriginY + localY,
                                    poolOriginZ + localZ,
                                    TinkersRebornGeneral.slimeGel,
                                    3,
                                    2);
                            } else {
                                world.setBlock(
                                    poolOriginX + localX,
                                    poolOriginY + localY,
                                    poolOriginZ + localZ,
                                    TinkersRebornGeneral.bloodBlock,
                                    0,
                                    2);
                            }
                        }
                    }
                }
            }
        }
    }
}
