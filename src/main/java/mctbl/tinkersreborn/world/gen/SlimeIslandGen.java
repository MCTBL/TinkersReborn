package mctbl.tinkersreborn.world.gen;

import java.awt.geom.Ellipse2D;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import net.minecraft.world.WorldProviderSurface;
import net.minecraft.world.WorldType;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.ChunkProviderFlat;
import net.minecraft.world.gen.feature.WorldGenerator;

import cpw.mods.fml.common.IWorldGenerator;
import mctbl.tinkersreborn.TinkersRebornConfig;
import mctbl.tinkersreborn.common.TinkersRebornGeneral;

public class SlimeIslandGen extends WorldGenerator implements IWorldGenerator {

    /** Percentage-like jaggedness allowance used while eroding the underside. */
    protected static final int erosionIrregularity = 1;
    protected final Block slimeDirt = TinkersRebornGeneral.slimeDirt;
    protected final Block slimeGrass = TinkersRebornGeneral.slimeGrass;
    protected final Block[] slimeFluids = new Block[] { TinkersRebornGeneral.blueSlimeFluidBlock,
        TinkersRebornGeneral.purpleSlimeFluidBlock };
    protected final int[][] slimeGelMetaList = new int[][] { { 0, 1 }, { 2 } };

    public SlimeIslandGen() {
        // do nothinig
    }

    @Override
    public void generate(Random random, int chunkX, int chunkZ, World world, IChunkProvider chunkGenerator,
        IChunkProvider chunkProvider) {
        if ((chunkGenerator instanceof ChunkProviderFlat || world.provider.terrainType == WorldType.FLAT)) {
            return;
        }

        if (world.provider instanceof WorldProviderSurface && random.nextInt(TinkersRebornConfig.islandRarity) == 0) {
            int islandTypeRoll = random.nextInt(10);
            int islandType = 0;
            if (islandTypeRoll <= 1) {
                // purple
                islandType = 2;
            } else if (islandTypeRoll <= 6) {
                // blue
                islandType = 1;
            } else {
                // green
                islandType = 0;
            }
            generateIsland(world, random, chunkX * 16, chunkZ * 16, islandType);
        }
    }

    /** Generates one floating island and all of its decorations. */
    public void generateIsland(World world, Random random, int islandOriginX, int islandOriginZ, int islandType) {
        int islandWidth = random.nextInt(13) + 20;
        int islandBaseY = 50 + world.getHeightValue(islandOriginX, islandOriginZ) + random.nextInt(50);
        int islandDepth = random.nextInt(13) + 20;
        int islandHeight = 12;
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

        // Erode the underside. A block survives only when the four blocks above
        // its side-neighbours are still dirt; the small random chance adds jagged edges.
        int bottomErosionDepth = 8;
        for (int localX = 0; localX <= islandWidth; localX++) {
            for (int localZ = 0; localZ <= islandDepth; localZ++) {
                for (int localY = 0; localY >= -bottomErosionDepth; localY--) {
                    int worldX = localX + islandOriginX;
                    int worldY = localY + islandBaseY + bottomErosionDepth;
                    int worldZ = localZ + islandOriginZ;
                    boolean hasDirtAboveAllSides = world.getBlock(worldX - 1, worldY + 1, worldZ) == slimeDirt
                        && world.getBlock(worldX + 1, worldY + 1, worldZ) == slimeDirt
                        && world.getBlock(worldX, worldY + 1, worldZ - 1) == slimeDirt
                        && world.getBlock(worldX - 1, worldY + 1, worldZ + 1) == slimeDirt;
                    if (!hasDirtAboveAllSides || random.nextInt(100) <= erosionIrregularity) {
                        world.setBlock(worldX, worldY, worldZ, Blocks.air, 0, 2);
                    }
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
        SlimeTreeGen trees = new SlimeTreeGen(false, 5, 4, islandType);
        for (int i = 0; i < 3; i++) {
            trees.generate(
                world,
                random,
                islandOriginX + random.nextInt(islandWidth),
                islandBaseY + islandHeight + 3,
                islandOriginZ + random.nextInt(islandDepth));
        }
    }

    /** Carves a 16x16x8 ellipsoidal pool into the island. */
    public void generateSlimePool(World world, Random random, int poolCenterX, int poolCenterY, int poolCenterZ,
        int islandType) {
        Block liquidBlock = slimeFluids[islandType == 2 ? 1 : 0];
        int[] possibleGelMetas = slimeGelMetaList[islandType == 2 ? 1 : 0];

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
        if (liquidBlock.getMaterial() == Material.water) {
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
                                .getMaterial() != Material.water) {
                            world.setBlock(
                                poolOriginX + localX,
                                poolOriginY + localY,
                                poolOriginZ + localZ,
                                TinkersRebornGeneral.slimeGel,
                                possibleGelMetas[random.nextInt(possibleGelMetas.length)],
                                2);
                        }
                    }
                }
            }
        }
    }

    @Override
    public boolean generate(World world, Random random, int unusedX, int unusedY, int unusedZ) {
        // This class uses IWorldGenerator.generate(...) as its real entry point.
        // WorldGenerator.generate(...) is required by the superclass but is unused.
        return true;
    }
}
