package mctbl.tinkersreborn.world.village;

import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.WeightedRandom;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureComponent;
import net.minecraft.world.gen.structure.StructureVillagePieces;
import net.minecraft.world.gen.structure.StructureVillagePieces.Start;
import net.minecraftforge.common.ChestGenHooks;

import mctbl.tinkersreborn.common.TinkersRebornGeneral;
import mctbl.tinkersreborn.tools.TinkersRebornTools;

public class ComponentToolWorkshop extends StructureVillagePieces.House1 {

    private int averageGroundLevel = -1;

    public ComponentToolWorkshop() {}

    public ComponentToolWorkshop(Start villagePiece, int componentType, Random random, StructureBoundingBox boundingBox,
        int direction) {
        super();
        this.coordBaseMode = direction;
        this.boundingBox = boundingBox;
    }

    public static ComponentToolWorkshop buildComponent(Start villagePiece, @SuppressWarnings("rawtypes") List pieces,
        Random random, int x, int y, int z, int direction, int componentType) {
        StructureBoundingBox box = StructureBoundingBox
            .getComponentToAddBoundingBox(x, y, z, 0, 0, 0, 7, 6, 7, direction);
        return canVillageGoDeeper(box) && StructureComponent.findIntersecting(pieces, box) == null
            ? new ComponentToolWorkshop(villagePiece, componentType, random, box, direction)
            : null;
    }

    @Override
    public boolean addComponentParts(World world, Random random, StructureBoundingBox box) {
        if (this.averageGroundLevel < 0) {
            this.averageGroundLevel = this.getAverageGroundLevel(world, box);
            if (this.averageGroundLevel < 0) return true;
            this.boundingBox.offset(0, this.averageGroundLevel - this.boundingBox.maxY + 4, 0);
        }

        this.fillWithBlocks(world, box, 0, 0, 0, 6, 0, 6, Blocks.cobblestone, Blocks.cobblestone, false);
        this.fillWithBlocks(world, box, 0, 5, 0, 6, 5, 6, Blocks.fence, Blocks.fence, false);
        this.fillWithBlocks(world, box, 1, 0, 1, 5, 0, 5, Blocks.planks, Blocks.planks, false);
        this.fillWithBlocks(world, box, 2, 0, 2, 4, 0, 4, Blocks.wool, Blocks.wool, false);

        this.fillWithBlocks(world, box, 0, 1, 0, 0, 4, 0, Blocks.log, Blocks.log, false);
        this.fillWithBlocks(world, box, 0, 1, 6, 0, 4, 6, Blocks.log, Blocks.log, false);
        this.fillWithBlocks(world, box, 6, 1, 0, 6, 4, 0, Blocks.log, Blocks.log, false);
        this.fillWithBlocks(world, box, 6, 1, 6, 6, 4, 6, Blocks.log, Blocks.log, false);

        this.fillWithBlocks(world, box, 0, 1, 1, 0, 1, 5, Blocks.planks, Blocks.planks, false);
        this.fillWithBlocks(world, box, 1, 1, 0, 5, 1, 0, Blocks.planks, Blocks.planks, false);
        this.fillWithBlocks(world, box, 6, 1, 1, 6, 1, 5, Blocks.planks, Blocks.planks, false);
        this.fillWithBlocks(world, box, 1, 1, 6, 5, 1, 6, Blocks.planks, Blocks.planks, false);

        this.fillWithBlocks(world, box, 0, 3, 1, 0, 3, 5, Blocks.planks, Blocks.planks, false);
        this.fillWithBlocks(world, box, 1, 3, 0, 5, 3, 0, Blocks.planks, Blocks.planks, false);
        this.fillWithBlocks(world, box, 6, 3, 1, 6, 3, 5, Blocks.planks, Blocks.planks, false);
        this.fillWithBlocks(world, box, 1, 3, 6, 5, 3, 6, Blocks.planks, Blocks.planks, false);

        this.fillWithBlocks(world, box, 0, 4, 1, 0, 4, 5, Blocks.log, Blocks.log, false);
        this.fillWithBlocks(world, box, 1, 4, 0, 5, 4, 0, Blocks.log, Blocks.log, false);
        this.fillWithBlocks(world, box, 6, 4, 1, 6, 4, 5, Blocks.log, Blocks.log, false);
        this.fillWithBlocks(world, box, 1, 4, 6, 5, 4, 6, Blocks.log, Blocks.log, false);

        this.fillWithBlocks(world, box, 1, 1, 1, 5, 5, 5, Blocks.air, Blocks.air, false);
        this.fillWithBlocks(world, box, 1, 4, 1, 5, 4, 5, Blocks.planks, Blocks.planks, false);

        this.placeBlockAtCurrentPosition(world, Blocks.glass_pane, 0, 1, 2, 0, box);
        this.placeBlockAtCurrentPosition(world, Blocks.planks, 0, 2, 2, 0, box);
        this.placeDoorAtCurrentPosition(world, box, random, 3, 1, 0, this.getMetadataWithOffset(Blocks.wooden_door, 1));
        this.placeBlockAtCurrentPosition(world, Blocks.planks, 0, 4, 2, 0, box);
        this.placeBlockAtCurrentPosition(world, Blocks.glass_pane, 0, 5, 2, 0, box);

        for (int x = 1; x <= 5; x++) {
            this.placeBlockAtCurrentPosition(world, x == 3 ? Blocks.planks : Blocks.glass_pane, 0, x, 2, 6, box);
        }
        for (int z = 1; z <= 5; z++) {
            this.placeBlockAtCurrentPosition(world, z == 3 ? Blocks.planks : Blocks.glass_pane, 0, 0, 2, z, box);
            this.placeBlockAtCurrentPosition(world, z == 3 ? Blocks.planks : Blocks.glass_pane, 0, 6, 2, z, box);
        }

        int ladderMeta = this.getMetadataWithOffset(Blocks.ladder, 3);
        for (int y = 1; y <= 4; y++) {
            this.placeBlockAtCurrentPosition(world, Blocks.ladder, ladderMeta, 3, y, 5, box);
        }

        this.placeBlockAtCurrentPosition(world, TinkersRebornTools.toolStation, 0, 1, 1, 1, box);
        this.generateStructureInventoryContents(
            world,
            box,
            random,
            1,
            1,
            2,
            TinkersRebornTools.castChest,
            TinkersRebornGeneral.tinkerHousePatterns);
        this.placeBlockAtCurrentPosition(world, TinkersRebornTools.partBuilder, 0, 1, 1, 3, box);
        this.placeBlockAtCurrentPosition(world, TinkersRebornTools.craftingStation, 0, 1, 1, 4, box);
        this.generateStructureInventoryContents(
            world,
            box,
            random,
            1,
            1,
            5,
            TinkersRebornTools.partChest,
            TinkersRebornGeneral.tinkerHouseParts);

        this.generateStructureChestContents(
            world,
            box,
            random,
            4,
            1,
            5,
            TinkersRebornGeneral.tinkerHouseChest.getItems(random),
            TinkersRebornGeneral.tinkerHouseChest.getCount(random));
        int pistonMeta = this.getMetadataWithOffset(Blocks.piston, 3);
        this.placeBlockAtCurrentPosition(world, Blocks.piston, pistonMeta, 5, 1, 5, box);

        for (int z = 0; z < 7; z++) {
            for (int x = 0; x < 7; x++) {
                this.clearCurrentPositionBlocksUpwards(world, x, 9, z, box);
                this.func_151554_b(world, Blocks.cobblestone, 0, x, -1, z, box);
            }
        }

        this.spawnVillagers(world, box, 3, 1, 3, 1);
        return true;
    }

    private boolean generateStructureInventoryContents(World world, StructureBoundingBox box, Random random, int x,
        int y, int z, Block block, ChestGenHooks loot) {
        int worldX = this.getXWithOffset(x, z);
        int worldY = this.getYWithOffset(y);
        int worldZ = this.getZWithOffset(x, z);
        if (!box.isVecInside(worldX, worldY, worldZ) || world.getBlock(worldX, worldY, worldZ) == block) return false;

        world.setBlock(worldX, worldY, worldZ, block, 0, 2);
        TileEntity tile = world.getTileEntity(worldX, worldY, worldZ);
        if (tile instanceof IInventory inventory && loot != null) {
            this.generateLootInSlots(random, loot.getItems(random), inventory, loot.getCount(random), 0, 24);
        }
        return true;
    }

    private void generateLootInSlots(Random random, WeightedRandomChestContent[] contents, IInventory inventory,
        int rolls, int minSlot, int maxSolot) {
        this.generateLootInSlots(
            random,
            contents,
            inventory,
            rolls,
            IntStream.range(minSlot, maxSolot)
                .toArray());
    }

    private void generateLootInSlots(Random random, WeightedRandomChestContent[] contents, IInventory inventory,
        int rolls, int... allowedSlots) {
        for (int i = 0; i < rolls; i++) {
            WeightedRandomChestContent selected = (WeightedRandomChestContent) WeightedRandom
                .getRandomItem(random, contents);

            ItemStack[] stacks = ChestGenHooks.generateStacks(
                random,
                selected.theItemId,
                selected.theMinimumChanceToGenerateItem,
                selected.theMaximumChanceToGenerateItem);

            for (ItemStack stack : stacks) {
                int slot = findRandomEmptySlot(random, inventory, allowedSlots);

                if (slot < 0) {
                    inventory.markDirty();
                    return;
                }

                inventory.setInventorySlotContents(slot, stack);
            }
        }

        inventory.markDirty();
    }

    private int findRandomEmptySlot(Random random, IInventory inventory, int[] allowedSlots) {
        int start = random.nextInt(allowedSlots.length);

        for (int i = 0; i < allowedSlots.length; i++) {
            int slot = allowedSlots[(start + i) % allowedSlots.length];

            if (slot >= 0 && slot < inventory.getSizeInventory() && inventory.getStackInSlot(slot) == null) {
                return slot;
            }
        }

        return -1;
    }

    @Override
    protected int getVillagerType(int villagerIndex) {
        // Reborn currently has no dedicated tinkerer profession or trade table. Use the
        // vanilla blacksmith profession so the structure remains self-contained.
        return 3;
    }
}
