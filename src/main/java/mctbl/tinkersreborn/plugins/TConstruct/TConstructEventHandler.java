package mctbl.tinkersreborn.plugins.TConstruct;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.world.ChunkDataEvent;
import net.minecraftforge.event.world.WorldEvent;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.GameRegistry.UniqueIdentifier;
import mctbl.tinkersreborn.library.utils.BlockReplacement;
import mctbl.tinkersreborn.plugins.TConstruct.recipe.TConstructToolPartConvertRecipe;
import mctbl.tinkersreborn.util.ColorUtil;
import mctbl.tinkersreborn.util.TinkersRebornUtils;

public class TConstructEventHandler {

    private final Map<Block, Map<Integer, BlockReplacement>> replacements = new HashMap<>();

    public static final int MIGRATION_VERSION = 1;
    public static final String TAG_MIGRATION_VERSION = "TConstruct_convert_to_TinkersReborn";

    @SubscribeEvent
    public void checkAndReplaceBlockInWorld(ChunkDataEvent.Load event) {
        final World world = event.world;
        if (world.isRemote) {
            return;
        }

        final Chunk chunk = event.getChunk();
        final NBTTagCompound data = event.getData();

        if (data.getInteger(TAG_MIGRATION_VERSION) >= MIGRATION_VERSION) {
            return;
        }

        ExtendedBlockStorage[] storages = chunk.getBlockStorageArray();

        boolean needSave = false;
        for (ExtendedBlockStorage storage : storages) {
            if (storage == null) {
                continue;
            }

            for (int localX = 0; localX < 16; localX++) {
                for (int localY = 0; localY < 16; localY++) {
                    for (int localZ = 0; localZ < 16; localZ++) {
                        Block block = storage.getBlockByExtId(localX, localY, localZ);
                        int metadata = storage.getExtBlockMetadata(localX, localY, localZ);
                        needSave |= tryReplaceTConstructBlock(block, metadata, storage, localX, localY, localZ);
                    }
                }
            }
        }

        data.setInteger(TAG_MIGRATION_VERSION, MIGRATION_VERSION);

        if (needSave) {
            chunk.setChunkModified();
        }
    }

    /**
     * replace block in world
     *
     * @param block
     * @param metadata
     * @param storage
     * @param localX
     * @param localY
     * @param localZ
     */
    private boolean tryReplaceTConstructBlock(Block block, int metadata, ExtendedBlockStorage storage, int localX,
        int localY, int localZ) {
        if (block == null) {
            return false;
        }
        if (replacements.containsKey(block) && replacements.get(block)
            .containsKey(metadata)) {
            final BlockReplacement blockReplacement = replacements.get(block)
                .get(metadata);
            storage.func_150818_a(localX, localY, localZ, blockReplacement.target);
            storage.setExtBlockMetadata(localX, localY, localZ, blockReplacement.metadata);
            return true;
        }

        final UniqueIdentifier identifier = GameRegistry.findUniqueIdentifierFor(block);
        if (isTConstruct(identifier)) {
            final String blockName = identifier.toString() + (metadata != 0 ? ":" + metadata : "");
            String targetBlockName = TConstructHandlerConfig.tinkerConstructBlockReplaceMap
                .getOrDefault(blockName, null);
            if (targetBlockName != null) {
                final String[] targetList = targetBlockName.split(":");
                int meta = 0;
                if (targetList.length == 3) {
                    targetBlockName = targetList[0] + ":" + targetList[1];
                    meta = Integer.parseInt(targetList[2]);
                }

                final Block targetBlock = TinkersRebornUtils.getBlockByIdentifier(targetBlockName);
                if (targetBlock != null) {
                    replacements.computeIfAbsent(block, t -> new HashMap<>())
                        .put(metadata, new BlockReplacement(targetBlock, meta));
                    storage.func_150818_a(localX, localY, localZ, targetBlock);
                    storage.setExtBlockMetadata(localX, localY, localZ, meta);
                    return true;
                }
            }
        }
        return false;
    }

    @SubscribeEvent
    public void onPlayerLogin(PlayerLoggedInEvent event) {
        tryReplaceInventoryContents(event.player.inventory);
    }

    /**
     * replace item stack in inventory
     *
     * @param inv
     */
    public static void tryReplaceInventoryContents(IInventory inv) {
        if (inv == null) {
            return;
        }
        for (int idx = 0; idx < inv.getSizeInventory(); idx++) {
            final ItemStack stackInSlot = inv.getStackInSlot(idx);
            if (TinkersRebornUtils.isStackEmpty(stackInSlot)) {
                continue;
            }
            final UniqueIdentifier identifier = GameRegistry.findUniqueIdentifierFor(stackInSlot.getItem());

            if (isTConstruct(identifier)) {
                final String itemName = identifier.toString()
                    + (stackInSlot.getItemDamage() != 0 ? ":" + stackInSlot.getItemDamage() : "");
                String targetItemName = TConstructHandlerConfig.tinkerConstructItemReplaceMap
                    .getOrDefault(itemName, null);
                if (targetItemName != null) {
                    replaceStackInSlot(inv, idx, targetItemName);
                } else {
                    // not in replace map, try tool part auto convert
                    ItemStack convertTinkersRebornItemStack = TConstructToolPartConvertRecipe
                        .getConvertTinkersRebornItemStack(stackInSlot);
                    replaceStackInSlot(inv, idx, convertTinkersRebornItemStack);
                }
            }
        }
    }

    public static void tryReplaceItemStackArray(ItemStack[] array) {
        if (array == null) {
            return;
        }
        for (int idx = 0; idx < array.length; idx++) {
            final ItemStack stackInSlot = array[idx];
            if (TinkersRebornUtils.isStackEmpty(stackInSlot)) {
                continue;
            }
            final UniqueIdentifier identifier = GameRegistry.findUniqueIdentifierFor(stackInSlot.getItem());

            if (isTConstruct(identifier)) {
                final String itemName = identifier.toString()
                    + (stackInSlot.getItemDamage() != 0 ? ":" + stackInSlot.getItemDamage() : "");
                String targetItemName = TConstructHandlerConfig.tinkerConstructItemReplaceMap
                    .getOrDefault(itemName, null);
                if (targetItemName != null) {
                    replaceStackInSlot(array, idx, targetItemName);
                } else {
                    // not in replace map, try tool part auto convert
                    ItemStack convertTinkersRebornItemStack = TConstructToolPartConvertRecipe
                        .getConvertTinkersRebornItemStack(stackInSlot);
                    replaceStackInSlot(array, idx, convertTinkersRebornItemStack);
                }
            }
        }
    }

    private static boolean isTConstruct(final UniqueIdentifier identifier) {
        return identifier != null && "TConstruct".equalsIgnoreCase(identifier.modId);
    }

    /**
     * replace item stack in inventory
     *
     * @param inv
     * @param idx
     * @param targetItemName
     */
    private static void replaceStackInSlot(IInventory inv, int idx, String targetItemName) {
        replaceStackInSlot(inv, idx, TinkersRebornUtils.getItemStackByIdentifier(targetItemName));
    }

    private static void replaceStackInSlot(IInventory inv, int idx, ItemStack targetStack) {
        if (targetStack != null) {
            targetStack.stackSize = inv.getStackInSlot(idx).stackSize;
            inv.setInventorySlotContents(idx, targetStack);
            inv.markDirty();
        }
    }

    private static void replaceStackInSlot(ItemStack[] array, int idx, String targetItemName) {
        replaceStackInSlot(array, idx, TinkersRebornUtils.getItemStackByIdentifier(targetItemName));
    }

    private static void replaceStackInSlot(ItemStack[] array, int idx, ItemStack targetStack) {
        if (targetStack != null) {
            targetStack.stackSize = array[idx].stackSize;
            array[idx] = targetStack;
        }
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
