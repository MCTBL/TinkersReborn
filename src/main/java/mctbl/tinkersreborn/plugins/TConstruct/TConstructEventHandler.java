package mctbl.tinkersreborn.plugins.TConstruct;

import net.minecraft.block.Block;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;
import net.minecraftforge.event.world.ChunkDataEvent;
import net.minecraftforge.event.world.WorldEvent;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.GameRegistry.UniqueIdentifier;
import mctbl.tinkersreborn.TinkersReborn;

public class TConstructEventHandler {

    @SubscribeEvent
    public void checkAndReplaceBlockInWorld(ChunkDataEvent.Load event) {
        if (event.world.isRemote) {
            return;
        }
        final Chunk chunk = event.getChunk();

        ExtendedBlockStorage[] storages = chunk.getBlockStorageArray();

        for (ExtendedBlockStorage storage : storages) {
            if (storage == null) {
                continue;
            }

            int baseY = storage.getYLocation();

            for (int localX = 0; localX < 16; localX++) {
                for (int localY = 0; localY < 16; localY++) {
                    for (int localZ = 0; localZ < 16; localZ++) {
                        Block block = storage.getBlockByExtId(localX, localY, localZ);
                        int metadata = storage.getExtBlockMetadata(localX, localY, localZ);

                        if (isTConstructBlock(block)) {
                            TinkersReborn.LOG.info("find {}:{}", block.getLocalizedName(), metadata);
                        }
                        // int worldX = chunk.xPosition * 16 + localX;
                        // int worldY = baseY + localY;
                        // int worldZ = chunk.zPosition * 16 + localZ;
                    }
                }
            }
        }
    }

    public static boolean isTConstructBlock(Block block) {
        if (block == null) {
            return false;
        }

        UniqueIdentifier identifier = GameRegistry.findUniqueIdentifierFor(block);

        return identifier != null && "TConstruct".equalsIgnoreCase(identifier.modId);
    }

    @SubscribeEvent
    public void checkAndReplaceBlockInWorld(WorldEvent.Load worldLoad) {

    }

}
