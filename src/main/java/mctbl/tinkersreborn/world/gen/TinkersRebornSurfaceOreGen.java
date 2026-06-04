package mctbl.tinkersreborn.world.gen;

import static net.minecraft.world.biome.BiomeGenBase.extremeHills;
import static net.minecraft.world.biome.BiomeGenBase.extremeHillsEdge;
import static net.minecraftforge.event.terraingen.DecorateBiomeEvent.Decorate.EventType.SAND;

import java.util.Random;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import mctbl.tinkersreborn.TinkersRebornConfig;
import mctbl.tinkersreborn.common.TinkersRebornGeneral;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.event.terraingen.DecorateBiomeEvent.Decorate;

public class TinkersRebornSurfaceOreGen {

    private final SurfaceOreGen ironSurface = new SurfaceOreGen(TinkersRebornGeneral.oreGravel, 0, 12, true);
    private final SurfaceOreGen goldSurface = new SurfaceOreGen(TinkersRebornGeneral.oreGravel, 1, 20, true);
    private final SurfaceOreGen copperSurface = new SurfaceOreGen(TinkersRebornGeneral.oreGravel, 2, 12, true);
    private final SurfaceOreGen tinSurface = new SurfaceOreGen(TinkersRebornGeneral.oreGravel, 3, 12, true);
    private final SurfaceOreGen aluminumSurface = new SurfaceOreGen(TinkersRebornGeneral.oreGravel, 4, 12, true);

    private static final ImmutableCollection<BiomeGenBase> EXTRA_ORE_BIOMES = ImmutableList
            .of(extremeHills, extremeHillsEdge);

    @SubscribeEvent
    public void onDecorateEvent(Decorate e) {
        // Trigger just before sand pass one--which comes just after vanilla ore
        // generation.
        if (e.type != SAND) return;

        BiomeGenBase biome = e.world.getWorldChunkManager().getBiomeGenAt(e.chunkX, e.chunkZ);
        int iterations = EXTRA_ORE_BIOMES.contains(biome) ? 2 : 1;
        for (int i = 0; i < iterations; i++) {
            generateSurfaceOres(e.rand, e.chunkX, e.chunkZ, e.world);
        }
    }

    private void generateSurfaceOres(Random random, int xChunk, int zChunk, World world) {
        if (random == null) return;

        int xPos, yPos, zPos;
        if (TinkersRebornConfig.generateIronSurface && random.nextInt(TinkersRebornConfig.ironsRarity) == 0) {
            xPos = xChunk + random.nextInt(16);
            yPos = 128;
            zPos = zChunk + random.nextInt(16);
            ironSurface.generate(world, random, xPos, yPos, zPos);
        }
        if (TinkersRebornConfig.generateGoldSurface && random.nextInt(TinkersRebornConfig.goldsRarity) == 0) {
            xPos = xChunk + random.nextInt(16);
            yPos = 128;
            zPos = zChunk + random.nextInt(16);
            goldSurface.generate(world, random, xPos, yPos, zPos);
        }
        if (TinkersRebornConfig.generateCopperSurface && random.nextInt(TinkersRebornConfig.coppersRarity) == 0) {
            xPos = xChunk + random.nextInt(16);
            yPos = 128;
            zPos = zChunk + random.nextInt(16);
            copperSurface.generate(world, random, xPos, yPos, zPos);
        }
        if (TinkersRebornConfig.generateTinSurface && random.nextInt(TinkersRebornConfig.tinsRarity) == 0) {
            xPos = xChunk + random.nextInt(16);
            yPos = 128;
            zPos = zChunk + random.nextInt(16);
            tinSurface.generate(world, random, xPos, yPos, zPos);
        }
        if (TinkersRebornConfig.generateAluminumSurface && random.nextInt(TinkersRebornConfig.aluminumsRarity) == 0) {
            xPos = xChunk + random.nextInt(16);
            yPos = 128;
            zPos = zChunk + random.nextInt(16);
            aluminumSurface.generate(world, random, xPos, yPos, zPos);
        }
    }
}
