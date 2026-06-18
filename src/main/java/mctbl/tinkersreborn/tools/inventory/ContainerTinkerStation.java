package mctbl.tinkersreborn.tools.inventory;

import java.util.Comparator;
import java.util.List;
import java.util.Queue;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.inventory.IInventory;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import org.apache.commons.lang3.tuple.Pair;

import com.google.common.collect.Lists;
import com.google.common.collect.Queues;
import com.google.common.collect.Sets;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mctbl.tinkersreborn.library.blocks.ITinkersToolStationBlock;
import mctbl.tinkersreborn.library.gui.container.ContainerMultiModule;
import mctbl.tinkersreborn.library.utils.BlockPos;
import mctbl.tinkersreborn.tools.TinkersRebornTools;
import mctbl.tinkersreborn.tools.gui.GuiTinkerStation;

public class ContainerTinkerStation<T extends TileEntity & IInventory> extends ContainerMultiModule<T> {

    public final boolean hasCraftingStation;
    public final List<Pair<BlockPos, Block>> tinkerStationBlocks;

    public final static TinkerBlockComp tinkerBlockComp = new TinkerBlockComp();

    public ContainerTinkerStation(T tile) {
        super(tile);

        tinkerStationBlocks = Lists.newLinkedList();
        BlockPos pos = new BlockPos(tile.xCoord, tile.yCoord, tile.zCoord);
        hasCraftingStation = detectedTinkerStationParts(tile.getWorldObj(), pos);
    }

    @SuppressWarnings("unchecked")
    public <TE extends TileEntity> TE getTinkerTE(Class<TE> clazz) {
        for (Pair<BlockPos, Block> pair : tinkerStationBlocks) {
            BlockPos pos = pair.getLeft();
            TileEntity te = this.world.getTileEntity(pos.x, pos.y, pos.z);
            if (te != null && clazz.isAssignableFrom(te.getClass())) {
                return (TE) te;
            }
        }
        return null;
    }

    public boolean detectedTinkerStationParts(World world, BlockPos start) {
        Set<Integer> found = Sets.newHashSet();
        Set<BlockPos> visited = Sets.newHashSet();
        Set<Block> ret = Sets.newHashSet();
        boolean hasMaster = false;

        // BFS for related blocks
        Queue<BlockPos> queue = Queues.newPriorityQueue();
        queue.add(start);

        while (!queue.isEmpty()) {
            BlockPos pos = queue.poll();
            // already visited between adding and call
            if (visited.contains(pos)) {
                continue;
            }

            Block b = world.getBlock(pos.x, pos.y, pos.z);
            if (!(b instanceof ITinkersToolStationBlock)) {
                // not a valid block for us
                continue;
            }

            // found a part, add surrounding blocks that haven't been visited yet
            if (!visited.contains(pos.north())) {
                queue.add(pos.north());
            }
            if (!visited.contains(pos.east())) {
                queue.add(pos.east());
            }
            if (!visited.contains(pos.south())) {
                queue.add(pos.south());
            }
            if (!visited.contains(pos.west())) {
                queue.add(pos.west());
            }
            // add to visited
            visited.add(pos);

            // save the thing
            if (b instanceof ITinkersToolStationBlock tinker) {
                Integer number = tinker.getGuiNumber(b);
                if (!found.contains(number)) {
                    found.add(number);
                    tinkerStationBlocks.add(Pair.of(pos, b));
                    ret.add(b);
                    if (Block.isEqualTo(b, TinkersRebornTools.craftingStation)) {
                        hasMaster = true;
                    }
                }
            }

        }

        // sort the found blocks by priority
        tinkerStationBlocks.sort(tinkerBlockComp);

        return hasMaster;
    }

    /** Tells the client to take the current state and update its info displays */
    public void updateGUI() {
        if (tile.getWorldObj().isRemote) {
            Minecraft.getMinecraft()
                .func_152344_a(new Runnable() {

                    @Override
                    public void run() {
                        ContainerTinkerStation.clientGuiUpdate();
                    }
                });
        }
    }

    /** Tells the client to display the LOCALIZED error message */
    public void error(final String message) {
        if (tile.getWorldObj().isRemote) {
            Minecraft.getMinecraft()
                .func_152344_a(new Runnable() {

                    @Override
                    public void run() {
                        ContainerTinkerStation.clientError(message);
                    }
                });
        }
    }

    /** Tells the client to display the LOCALIZED warning message */
    public void warning(final String message) {
        if (tile.getWorldObj().isRemote) {
            Minecraft.getMinecraft()
                .func_152344_a(new Runnable() {

                    @Override
                    public void run() {
                        ContainerTinkerStation.clientWarning(message);
                    }
                });
        }
    }

    @SideOnly(Side.CLIENT)
    private static void clientGuiUpdate() {
        GuiScreen screen = Minecraft.getMinecraft().currentScreen;
        if (screen instanceof GuiTinkerStation) {
            ((GuiTinkerStation) screen).updateDisplay();
        }
    }

    @SideOnly(Side.CLIENT)
    private static void clientError(String message) {
        GuiScreen screen = Minecraft.getMinecraft().currentScreen;
        if (screen instanceof GuiTinkerStation) {
            ((GuiTinkerStation) screen).error(message);
        }
    }

    @SideOnly(Side.CLIENT)
    private static void clientWarning(String message) {
        GuiScreen screen = Minecraft.getMinecraft().currentScreen;
        if (screen instanceof GuiTinkerStation) {
            ((GuiTinkerStation) screen).warning(message);
        }
    }

    private static class TinkerBlockComp implements Comparator<Pair<BlockPos, Block>> {

        @Override
        public int compare(Pair<BlockPos, Block> o1, Pair<BlockPos, Block> o2) {
            Block s1 = o1.getRight();
            Block s2 = o2.getRight();

            return ((ITinkersToolStationBlock) s2).getGuiNumber(s2) - ((ITinkersToolStationBlock) s1).getGuiNumber(s1);
        }
    }
}
