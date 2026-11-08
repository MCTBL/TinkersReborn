package mctbl.tinkersreborn.plugins.TConstruct;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.world.ChunkDataEvent;
import net.minecraftforge.event.world.WorldEvent;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.GameRegistry.UniqueIdentifier;
import mctbl.tinkersreborn.TinkersReborn;
import mctbl.tinkersreborn.util.ColorUtil;
import mctbl.tinkersreborn.util.TinkersRebornUtils;

public class TConstructEventHandler {

    @SubscribeEvent
    public void checkAndReplaceBlockInWorld(ChunkDataEvent.Load event) {
        final World world = event.world;
        if (world.isRemote) {
            return;
        }

        final Chunk chunk = event.getChunk();

        ExtendedBlockStorage[] storages = chunk.getBlockStorageArray();

        for (ExtendedBlockStorage storage : storages) {
            if (storage == null) {
                continue;
            }

            for (int localX = 0; localX < 16; localX++) {
                for (int localY = 0; localY < 16; localY++) {
                    for (int localZ = 0; localZ < 16; localZ++) {
                        Block block = storage.getBlockByExtId(localX, localY, localZ);
                        int metadata = storage.getExtBlockMetadata(localX, localY, localZ);
                        tryReplaceTConstructBlock(block, metadata, storage, localX, localY, localZ);
                    }
                }
            }
        }
        chunk.setChunkModified();
    }

    private void tryReplaceTConstructBlock(Block block, int metadata, ExtendedBlockStorage storage, int localX,
        int localY, int localZ) {
        if (block == null) {
            return;
        }
        final UniqueIdentifier identifier = GameRegistry.findUniqueIdentifierFor(block);
        if (identifier != null && "TConstruct".equalsIgnoreCase(identifier.modId)) {
            final String blockName = identifier.toString() + (metadata != 0 ? ":" + metadata : "");
            String targetBlockName = TConstructHandlerConfig.tinkerConstructReplace.getOrDefault(blockName, null);
            TinkersReborn.LOG.debug("find {}", blockName);
            if (targetBlockName != null) {
                TinkersReborn.LOG.debug("{}  ->  {}", blockName, targetBlockName);
                final String[] targetList = targetBlockName.split(":");
                int meta = 0;
                if (targetList.length == 3) {
                    targetBlockName = targetList[0] + ":" + targetList[1];
                    meta = Integer.parseInt(targetList[2]);
                }
                final Block targetBlock = getBlockByIdentifier(targetBlockName);
                if (targetBlock != null) {
                    storage.func_150818_a(localX, localY, localZ, targetBlock);
                    storage.setExtBlockMetadata(localX, localY, localZ, meta);
                }
            }
        }
    }

    @Nullable
    private Block getBlockByIdentifier(String identifier) {
        if (identifier == null || identifier.isEmpty()) return null;
        Block block = (Block) Block.blockRegistry.getObject(identifier);
        if (block == null && !identifier.contains(":")) {
            block = (Block) Block.blockRegistry.getObject("minecraft:" + identifier);
        }
        return block;
    }

    @SubscribeEvent
    public void mentionTConstructNeedsConvert(ItemTooltipEvent e) {
        final UniqueIdentifier identifier = GameRegistry.findUniqueIdentifierFor(e.itemStack.getItem());
        if (identifier != null && "TConstruct".equalsIgnoreCase(identifier.modId)) {
            e.toolTip.add(ColorUtil.addRed(TinkersRebornUtils.translate("tinkersreborn.TConstructConvert.tooltip1")));
            e.toolTip.add(ColorUtil.addRed(TinkersRebornUtils.translate("tinkersreborn.TConstructConvert.tooltip2")));
        }
    }

    @SubscribeEvent
    public void checkAndReplaceBlockInWorld(WorldEvent.Load worldLoad) {

    }
}
