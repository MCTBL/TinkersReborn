package mctbl.tinkersreborn.tools.items.tools;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.Stack;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import mctbl.tinkersreborn.TinkersReborn;
import mctbl.tinkersreborn.common.particle.Particles;
import mctbl.tinkersreborn.library.materials.MaterialStatusType;
import mctbl.tinkersreborn.library.materials.TinkersRebornMaterial;
import mctbl.tinkersreborn.library.tools.AoeHarvestTool;
import mctbl.tinkersreborn.library.tools.TinkerToolEvent;
import mctbl.tinkersreborn.library.tools.ToolNBT;
import mctbl.tinkersreborn.library.utils.BlockPos;
import mctbl.tinkersreborn.tools.TinkersRebornTools;
import mctbl.tinkersreborn.tools.gui.ToolBuildGuiInfo;
import mctbl.tinkersreborn.util.ToolTagsHelper;

public class LumberAxe extends AoeHarvestTool {

    public static final float DURABILITY_MODIFIER = 2f;

    public LumberAxe() {
        super("LumberAxe", 4);

        // set the toolclass, actual harvestlevel is done by the overridden callback
        this.setHarvestLevel("axe", 0);

        this.componentsParts
            .add(new ToolPartRecord(TinkersRebornTools.lumberaxeHead, MaterialStatusType.HEAD, "_lumberaxe_head"));
        this.componentsParts
            .add(new ToolPartRecord(TinkersRebornTools.largeplate, MaterialStatusType.HEAD, "_lumberaxe_shield"));
        this.componentsParts
            .add(new ToolPartRecord(TinkersRebornTools.toughrod, MaterialStatusType.HANDLE, "_lumberaxe_handle"));
        this.componentsParts
            .add(new ToolPartRecord(TinkersRebornTools.toughbind, MaterialStatusType.EXTRA, "_lumberaxe_binding"));
    }

    @Override
    public boolean isEffective(Block block) {
        return axeEffectiveMaterials.contains(block.getMaterial()) || axeEffectiveBlocks.contains(block);
    }

    @Override
    public float miningSpeedModifier() {
        return 0.35F; // a bit slower because it breaks whole trees
    }

    @Override
    public float damagePotential() {
        return 1.2F;
    }

    @Override
    public float knockback() {
        return 1.5F;
    }

    @Override
    public boolean dealDamage(ItemStack stack, EntityLivingBase player, Entity entity, float damage) {
        boolean hit = super.dealDamage(stack, player, entity, damage);

        if (hit) {
            TinkersReborn.proxy.spawnAttackParticle(Particles.LUMBERAXE_ATTACK, player, 0.8d);
        }

        return hit;
    }

    @Override
    public List<BlockPos> getAOEBlocks(ItemStack stack, World world, EntityPlayer player, BlockPos origin) {
        return ToolTagsHelper.calcAOEBlocks(stack, world, player, origin, 3, 3, 3, -1);
    }

    @Override
    public float getRepairModifierForPart(int index) {
        // 2 or 1.25
        return index == 0 ? DURABILITY_MODIFIER : DURABILITY_MODIFIER * 0.625f;
    }

    @Override
    public boolean onBlockStartBreak(ItemStack itemstack, int x, int y, int z, EntityPlayer player) {
        if (!ToolTagsHelper.isBroken(itemstack) && ToolTagsHelper
            .isToolEffective(itemstack, player.worldObj.getBlock(x, y, z), player.worldObj.getBlockMetadata(x, y, z))
            && detectTree(player.worldObj, BlockPos.of(x, y, z))) {
            return chopTheTree(itemstack, BlockPos.of(x, y, z), player);
        }

        return super.onBlockStartBreak(itemstack, x, y, z, player);
    }

    @Override
    public ToolNBT buildToolTag(List<TinkersRebornMaterial> materials) {
        ToolNBT toolTag = super.buildToolTag(materials);
        toolTag.attack += 2;
        toolTag.durability *= DURABILITY_MODIFIER;
        return toolTag;
    }

    @Override
    public ToolBuildGuiInfo getToolBuildGuiInfo() {
        if (this.toolBuildGuiInfo == null) {
            this.toolBuildGuiInfo = new ToolBuildGuiInfo(this).addSlotPosition(33 + 6 - 6, 42 - 20) // head
                .addSlotPosition(33 + 6 + 14, 42 - 4) // plate
                .addSlotPosition(33 + 6 - 10 + 3, 42 + 4) // handle
                .addSlotPosition(33 + 6 - 10 - 16, 42 + 20); // binding

        }
        return this.toolBuildGuiInfo;
    }

    // for tree
    public static boolean detectTree(World world, BlockPos origin) {
        BlockPos pos = null;
        Stack<BlockPos> candidates = new Stack<>();
        candidates.add(origin);

        while (!candidates.isEmpty()) {
            BlockPos candidate = candidates.pop();
            if ((pos == null || candidate.getY() > pos.getY()) && isLog(world, candidate)) {
                pos = candidate.up();
                // go up
                while (isLog(world, pos)) {
                    pos = pos.up();
                }
                // check if we still have a way diagonally up
                candidates.add(pos.north());
                candidates.add(pos.east());
                candidates.add(pos.south());
                candidates.add(pos.west());
            }
        }

        // not even one match, so there were no logs.
        if (pos == null) {
            return false;
        }

        // check if there were enough leaves around the last position
        // pos now contains the block above the topmost log
        // we want at least 5 leaves in the surrounding 26 blocks
        int d = 3;
        int o = -1; // -(d-1)/2
        int leaves = 0;
        for (int x = 0; x < d; x++) {
            for (int y = 0; y < d; y++) {
                for (int z = 0; z < d; z++) {
                    BlockPos leaf = pos.add(o + x, o + y, o + z);
                    if (isLeaf(world, leaf) && ++leaves >= 5) {
                        return true;
                    }
                }
            }
        }

        // not enough leaves. sorreh
        return false;
    }

    private static boolean isLog(World world, BlockPos pos) {
        return world.getBlock(pos.x, pos.y, pos.z)
            .isWood(world, pos.x, pos.y, pos.z);
    }

    private static boolean isLeaf(World world, BlockPos pos) {
        return world.getBlock(pos.x, pos.y, pos.z)
            .isLeaves(world, pos.x, pos.y, pos.z);
    }

    public static boolean chopTheTree(ItemStack itemstack, BlockPos start, EntityPlayer player) {
        if (player.getEntityWorld().isRemote) {
            return true;
        }
        TinkerToolEvent.ExtraBlockBreak event = TinkerToolEvent.ExtraBlockBreak.fireEvent(
            itemstack,
            player,
            player.getEntityWorld()
                .getBlock(start.x, start.y, start.z),
            3,
            3,
            3,
            -1);
        int speed = Math.round((event.width * event.height * event.depth) / 27f);
        if (event.distance > 0) {
            speed = event.distance + 1;
        }

        try {
            FMLCommonHandler.instance()
                .bus()
                .register(new TreeChopTask(itemstack, start, player, speed));
            return true;
        } catch (Exception e) {
            TinkersReborn.LOG.error("Lumber axe chopTheTree get error {}", e.getMessage());
            return false;
        }
    }

    public static class TreeChopTask {

        public final World world;
        public final EntityPlayer player;
        public final ItemStack tool;
        public final int blocksPerTick;

        public Queue<BlockPos> blocks = new LinkedList<>();
        public Set<BlockPos> visited = new HashSet<>();

        public TreeChopTask(ItemStack tool, BlockPos start, EntityPlayer player, int blocksPerTick) {
            this.world = player.getEntityWorld();
            this.player = player;
            this.tool = tool;
            this.blocksPerTick = blocksPerTick;

            this.blocks.add(start);
        }

        @SubscribeEvent
        public void chopChop(TickEvent.WorldTickEvent event) {
            if (event.side.isClient()) {
                finish();
                return;
            }
            // only if same dimension
            if (event.world.provider.dimensionId != world.provider.dimensionId) {
                return;
            }

            // setup
            int left = blocksPerTick;

            // continue running
            BlockPos pos;
            while (left > 0) {
                // completely done or can't do our job anymore?!
                if (blocks.isEmpty() || ToolTagsHelper.isBroken(tool)) {
                    finish();
                    return;
                }

                pos = blocks.remove();
                if (!visited.add(pos)) {
                    continue;
                }

                // can we harvest the block and is effective?
                if (!isLog(world, pos) || !ToolTagsHelper.isToolEffective(
                    tool,
                    world.getBlock(pos.x, pos.y, pos.z),
                    world.getBlockMetadata(pos.x, pos.y, pos.z))) {
                    continue;
                }

                // save its neighbours and layers above
                for (int x = -1; x <= 1; x++) {
                    for (int z = -1; z <= 1; z++) {
                        for (int y = 0; y <= 1; y++) {
                            BlockPos pos2 = pos.add(x, y, z);
                            if (!visited.contains(pos2)) {
                                blocks.add(pos2);
                            }
                        }
                    }
                }

                // break it, wooo!
                ToolTagsHelper.breakExtraBlock(tool, world, player, pos, pos);
                left--;
            }
        }

        private void finish() {
            // goodbye cruel world
            FMLCommonHandler.instance()
                .bus()
                .unregister(this);
        }
    }
}
