package mctbl.tinkersreborn.world.gen;

import java.util.Random;

import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.feature.WorldGenMinable;

import cpw.mods.fml.common.IWorldGenerator;
import mctbl.tinkersreborn.TinkersRebornConfig;
import mctbl.tinkersreborn.common.TinkersRebornGeneral;

public class TinkersRebornWorldGenerator implements IWorldGenerator {

    WorldGenMinable copper;
    WorldGenMinable tin;
    WorldGenMinable aluminum;
    WorldGenMinable cobalt;
    WorldGenMinable ardite;

    OreberryBushGen ironBush;
    OreberryBushGen goldBush;
    OreberryBushGen copperBush;
    OreberryBushGen tinBush;
    OreberryBushGen aluminumBush;
    OreberryBushGen essenceBush;

    public TinkersRebornWorldGenerator() {
        copper = new WorldGenMinable(TinkersRebornGeneral.oreSlag, 2, 8, Blocks.stone);
        tin = new WorldGenMinable(TinkersRebornGeneral.oreSlag, 3, 8, Blocks.stone);
        aluminum = new WorldGenMinable(TinkersRebornGeneral.oreSlag, 4, 6, Blocks.stone);

        cobalt = new WorldGenMinable(TinkersRebornGeneral.oreSlag, 0, 3, Blocks.netherrack);
        ardite = new WorldGenMinable(TinkersRebornGeneral.oreSlag, 1, 3, Blocks.netherrack);

        ironBush = new OreberryBushGen(TinkersRebornGeneral.oreberryBush, 0, 12);
        goldBush = new OreberryBushGen(TinkersRebornGeneral.oreberryBush, 1, 6);
        copperBush = new OreberryBushGen(TinkersRebornGeneral.oreberryBush, 2, 12);
        tinBush = new OreberryBushGen(TinkersRebornGeneral.oreberryBush, 3, 12);
        aluminumBush = new OreberryBushGen(TinkersRebornGeneral.oreberryBush, 4, 14);
        essenceBush = new OreberryBushGen(TinkersRebornGeneral.oreberryBush, 5, 8);
    }

    @Override
    public void generate(Random random, int chunkX, int chunkZ, World world, IChunkProvider chunkGenerator,
        IChunkProvider chunkProvider) {
        if (world.provider.isHellWorld) {
            generateNether(random, chunkX * 16, chunkZ * 16, world);
        } else if (world.provider.terrainType != WorldType.FLAT) {
            generateSurface(random, chunkX * 16, chunkZ * 16, world);
            if (world.provider.isSurfaceWorld()) generateOreBushes(random, chunkX * 16, chunkZ * 16, world);
        }
    }

    void generateSurface(Random random, int xChunk, int zChunk, World world) {
        String biomeName = world.getWorldChunkManager()
            .getBiomeGenAt(xChunk, zChunk).biomeName;

        generateUndergroundOres(random, xChunk, zChunk, world);

        if (biomeName.contains("Extreme Hills")) {
            generateUndergroundOres(random, xChunk, zChunk, world);
        }
    }

    void generateUndergroundOres(Random random, int xChunk, int zChunk, World world) {
        int xPos, yPos, zPos;
        if (TinkersRebornConfig.generateCopper) {
            for (int q = 0; q <= TinkersRebornConfig.copperDensity; q++) {
                xPos = xChunk + random.nextInt(16);
                // 20 ~ 60
                yPos = 20 + random.nextInt(40);
                zPos = zChunk + random.nextInt(16);
                copper.generate(world, random, xPos, yPos, zPos);
            }
        }
        if (TinkersRebornConfig.generateTin) {
            for (int q = 0; q <= TinkersRebornConfig.tinDensity; q++) {
                xPos = xChunk + random.nextInt(16);
                // 0 ~ 40
                yPos = random.nextInt(40);
                zPos = zChunk + random.nextInt(16);
                tin.generate(world, random, xPos, yPos, zPos);
            }
        }
        if (TinkersRebornConfig.generateAluminum) {
            for (int q = 0; q <= TinkersRebornConfig.aluminumDensity; q++) {
                xPos = xChunk + random.nextInt(16);
                // 0 ~ 64
                yPos = random.nextInt(64);
                zPos = zChunk + random.nextInt(16);
                aluminum.generate(world, random, xPos, yPos, zPos);
            }
        }
    }

    void generateNether(Random random, int xChunk, int zChunk, World world) {
        int xPos, yPos, zPos;
        if (TinkersRebornConfig.generateCobalt) {
            for (int i = 0; i < TinkersRebornConfig.cobaltDensity; i++) {
                xPos = xChunk + random.nextInt(16);
                // 32 ~ 96
                yPos = random.nextInt(64) + 32;
                zPos = zChunk + random.nextInt(16);
                cobalt.generate(world, random, xPos, yPos, zPos);
            }
            for (int i = 0; i < TinkersRebornConfig.cobaltDensity; i++) {
                xPos = xChunk + random.nextInt(16);
                // 0 ~ 128
                yPos = random.nextInt(128);
                zPos = zChunk + random.nextInt(16);
                cobalt.generate(world, random, xPos, yPos, zPos);
            }
        }
        if (TinkersRebornConfig.generateArdite) {
            for (int i = 0; i < TinkersRebornConfig.arditeDensity; i++) {
                xPos = xChunk + random.nextInt(16);
                yPos = random.nextInt(64) + 32;
                zPos = zChunk + random.nextInt(16);
                ardite.generate(world, random, xPos, yPos, zPos);
            }
            for (int i = 0; i < TinkersRebornConfig.arditeDensity; i++) {
                xPos = xChunk + random.nextInt(16);
                yPos = random.nextInt(128);
                zPos = zChunk + random.nextInt(16);
                ardite.generate(world, random, xPos, yPos, zPos);
            }
        }
    }

    void generateOreBushes(Random random, int xChunk, int zChunk, World world) {
        int xPos, yPos, zPos;
        if (TinkersRebornConfig.generateIronBush && random.nextInt(TinkersRebornConfig.ironBushRarity + 1) == 0) {
            for (int i = 0; i < TinkersRebornConfig.ironBushDensity; i++) {
                xPos = xChunk + random.nextInt(16);
                yPos = 32;
                zPos = zChunk + random.nextInt(16);
                yPos = findAdequateLocation(world, xPos, yPos, zPos, 32, 0);
                if (yPos != -1) {
                    ironBush.generate(world, random, xPos, yPos, zPos);
                }
            }
        }

        if (TinkersRebornConfig.generateGoldBush && random.nextInt(TinkersRebornConfig.goldBushRarity + 1) == 0) {
            for (int i = 0; i < TinkersRebornConfig.goldBushDensity; i++) {
                xPos = xChunk + random.nextInt(16);
                yPos = 16;
                zPos = zChunk + random.nextInt(16);
                yPos = findAdequateLocation(world, xPos, yPos, zPos, 32, 0);
                if (yPos != -1) {

                    goldBush.generate(world, random, xPos, yPos, zPos);
                }
            }
        }

        if (TinkersRebornConfig.generateCopperBush && random.nextInt(TinkersRebornConfig.copperBushRarity + 1) == 0) {
            for (int i = 0; i < TinkersRebornConfig.copperBushDensity; i++) {
                xPos = xChunk + random.nextInt(16);
                yPos = 40;
                zPos = zChunk + random.nextInt(16);
                yPos = findAdequateLocation(world, xPos, yPos, zPos, 60, 20);
                if (yPos != -1) {
                    copperBush.generate(world, random, xPos, yPos, zPos);
                }
            }
        }

        if (TinkersRebornConfig.generateTinBush && random.nextInt(TinkersRebornConfig.tinBushRarity + 1) == 0) {
            for (int i = 0; i < TinkersRebornConfig.tinBushDensity; i++) {
                xPos = xChunk + random.nextInt(16);
                yPos = 20;
                zPos = zChunk + random.nextInt(16);
                yPos = findAdequateLocation(world, xPos, yPos, zPos, 40, 0);
                if (yPos != -1) {
                    tinBush.generate(world, random, xPos, yPos, zPos);
                }
            }
        }

        if (TinkersRebornConfig.generateAluminumBush
            && random.nextInt(TinkersRebornConfig.aluminumBushRarity + 1) == 0) {
            for (int i = 0; i < TinkersRebornConfig.aluminumBushDensity; i++) {
                xPos = xChunk + random.nextInt(16);
                yPos = 30;
                zPos = zChunk + random.nextInt(16);
                yPos = findAdequateLocation(world, xPos, yPos, zPos, 60, 0);
                if (yPos != -1) {
                    aluminumBush.generate(world, random, xPos, yPos, zPos);
                }
            }
        }

        if (TinkersRebornConfig.generateEssenceBush && random.nextInt(TinkersRebornConfig.essenceBushRarity + 1) == 0) {
            for (int i = 0; i < TinkersRebornConfig.essenceBushDensity; i++) {
                xPos = xChunk + random.nextInt(16);
                yPos = 48;
                zPos = zChunk + random.nextInt(16);
                yPos = findAdequateLocation(world, xPos, yPos, zPos, 32, 0);
                if (yPos != -1) {
                    essenceBush.generate(world, random, xPos, yPos, zPos);
                }
            }
        }
    }

    private int findAdequateLocation(World world, int x, int y, int z, int heightLimit, int depthLimit) {
        int height = y;
        do {
            if (world.getBlock(x, height, z) == Blocks.air && world.getBlock(x, height + 1, z) != Blocks.air)
                return height + 1;
            height++;
        } while (height < heightLimit);

        height = y;
        do {
            if (world.getBlock(x, height, z) == Blocks.air && world.getBlock(x, height - 1, z) != Blocks.air)
                return height - 1;
            height--;
        } while (height > depthLimit);

        return -1;
    }
}
