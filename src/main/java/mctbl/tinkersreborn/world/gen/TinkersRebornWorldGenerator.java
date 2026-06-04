package mctbl.tinkersreborn.world.gen;

import java.util.Random;

import cpw.mods.fml.common.IWorldGenerator;
import mctbl.tinkersreborn.TinkersRebornConfig;
import mctbl.tinkersreborn.common.TinkersRebornGeneral;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.feature.WorldGenMinable;

public class TinkersRebornWorldGenerator implements IWorldGenerator {
    
    WorldGenMinable copper;
    WorldGenMinable tin;
    WorldGenMinable aluminum;
    WorldGenMinable cobalt;
    WorldGenMinable ardite;
    
    public TinkersRebornWorldGenerator() {
        copper = new WorldGenMinable(TinkersRebornGeneral.oreSlag, 3, 8, Blocks.stone);
        tin = new WorldGenMinable(TinkersRebornGeneral.oreSlag, 4, 8, Blocks.stone);
        aluminum = new WorldGenMinable(TinkersRebornGeneral.oreSlag, 5, 6, Blocks.stone);

        cobalt = new WorldGenMinable(TinkersRebornGeneral.oreSlag, 1, 3, Blocks.netherrack);
        ardite = new WorldGenMinable(TinkersRebornGeneral.oreSlag, 2, 3, Blocks.netherrack);
    }

    @Override
    public void generate(Random random, int chunkX, int chunkZ, World world, IChunkProvider chunkGenerator,
	    IChunkProvider chunkProvider) {
        if (world.provider.isHellWorld) {
            generateNether(random, chunkX * 16, chunkZ * 16, world);
        } else if (world.provider.terrainType != WorldType.FLAT) {
            generateSurface(random, chunkX * 16, chunkZ * 16, world);
        }
    }
    
    void generateSurface(Random random, int xChunk, int zChunk, World world) {
        String biomeName = world.getWorldChunkManager().getBiomeGenAt(xChunk, zChunk).biomeName;

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
}
