package mctbl.tinkersreborn.tools.items.tools;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.block.BlockOre;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.oredict.OreDictionary;

import mctbl.tinkersreborn.TinkersReborn;
import mctbl.tinkersreborn.TinkersRebornConfig;
import mctbl.tinkersreborn.common.particle.Particles;
import mctbl.tinkersreborn.library.materials.MaterialStatusType;
import mctbl.tinkersreborn.library.materials.TinkersRebornMaterial;
import mctbl.tinkersreborn.library.tools.AoeHarvestTool;
import mctbl.tinkersreborn.library.tools.ToolNBT;
import mctbl.tinkersreborn.library.utils.BlockPos;
import mctbl.tinkersreborn.tools.TinkersRebornTools;
import mctbl.tinkersreborn.tools.gui.ToolBuildGuiInfo;

public class Vinehammer extends AoeHarvestTool {

    public static final float DURABILITY_MODIFIER = 3f;

    public Vinehammer() {
        super("Vinehammer", 4);

        // set the toolclass, actual harvestlevel is done by the overridden callback
        this.setHarvestLevel("pickaxe", 0);

        this.componentsParts
            .add(new ToolPartRecord(TinkersRebornTools.pickaxeHead, MaterialStatusType.HEAD, "_pickaxe"));
        this.componentsParts.add(new ToolPartRecord(TinkersRebornTools.hammerHead, MaterialStatusType.HEAD, "_head"));
        this.componentsParts.add(new ToolPartRecord(TinkersRebornTools.toughrod, MaterialStatusType.HANDLE, "_handle"));
        this.componentsParts.add(new ToolPartRecord(TinkersRebornTools.largeplate, MaterialStatusType.EXTRA, "_plate"));
    }

    @Override
    public boolean isEffective(Block block) {
        return pickaxeEffectiveBlocks.contains(block) || pickaxeEffectiveMaterials.contains(block.getMaterial());
    }

    @Override
    public float miningSpeedModifier() {
        return 0.4F;
    }

    @Override
    public float damagePotential() {
        return 1.2f;
    }

    @Override
    public boolean dealDamage(ItemStack stack, EntityLivingBase player, Entity entity, float damage) {
        boolean hit = super.dealDamage(stack, player, entity, damage);

        if (hit) {
            TinkersReborn.proxy.spawnAttackParticle(Particles.HAMMER_ATTACK, player, 0.8d);
        }
        return hit;
    }

    @Override
    public float getRepairModifierForPart(int index) {
        return DURABILITY_MODIFIER;
    }

    @Override
    public ToolNBT buildToolTag(List<TinkersRebornMaterial> materials) {
        ToolNBT toolTag = super.buildToolTag(materials);
        toolTag.durability *= DURABILITY_MODIFIER;
        toolTag.attack += 3;
        return toolTag;
    }

    @Override
    public List<BlockPos> getAOEBlocks(ItemStack stack, World world, EntityPlayer player, BlockPos origin) {
        if (this.isOreBlock(world, origin)) {
            return this.calcOreBlock(world, origin);
        }
        return super.getAOEBlocks(stack, world, player, origin);
    }

    @Override
    public boolean onBlockStartBreak(ItemStack itemstack, int x, int y, int z, EntityPlayer player) {
        // TODO Auto-generated method stub
        return super.onBlockStartBreak(itemstack, x, y, z, player);
    }

    private boolean isOreBlock(World world, BlockPos pos) {
        if (world == null) return false;

        Block block = world.getBlock(pos.x, pos.y, pos.z);
        int meta = world.getBlockMetadata(pos.x, pos.y, pos.z);

        if (block == null) return false;

        ItemStack stack = new ItemStack(block, 1, meta);
        if (stack.getItem() != null) {
            for (int id : OreDictionary.getOreIDs(stack)) {
                String name = OreDictionary.getOreName(id);
                if (name.startsWith("ore")) {
                    return true;
                }
            }
        }

        if (block instanceof BlockOre) return true;

        String tool = block.getHarvestTool(meta);
        int level = block.getHarvestLevel(meta);
        return "pickaxe".equals(tool) && level >= 2;
    }

    private List<BlockPos> calcOreBlock(World world, BlockPos pos) {
        List<BlockPos> waitToMine = new ArrayList<>();
        Set<BlockPos> visited = new HashSet<>();
        Queue<BlockPos> blocks = new LinkedList<>();
        Block block = world.getBlock(pos.x, pos.y, pos.z);
        int meta = world.getBlockMetadata(pos.x, pos.y, pos.z);

        blocks.add(pos);
        while (!blocks.isEmpty() && waitToMine.size() <= TinkersRebornConfig.vineHammerMaxOreMine) {
            BlockPos nextBlockPos = blocks.poll();
            if (visited.contains(nextBlockPos)) {
                continue;
            }
            if (world.getBlock(nextBlockPos.x, nextBlockPos.y, nextBlockPos.z) == block
                && world.getBlockMetadata(nextBlockPos.x, nextBlockPos.y, nextBlockPos.z) == meta) {
                waitToMine.add(nextBlockPos);
                for (ForgeDirection d : ForgeDirection.values()) {
                    BlockPos newPos = nextBlockPos.offset(d);
                    if (!visited.contains(newPos)) {
                        blocks.add(newPos);
                    }
                }
            }
            visited.add(nextBlockPos);

        }

        // remove origin pos
        waitToMine.remove(pos);

        return waitToMine;
    }

    @Override
    public ToolBuildGuiInfo getToolBuildGuiInfo() {
        if (this.toolBuildGuiInfo == null) {
            this.toolBuildGuiInfo = new ToolBuildGuiInfo(this).addSlotPosition(20 + 1, 55 - 30) // pickaxe
                .addSlotPosition(20 + 39, 55 - 10) // hammer
                .addSlotPosition(20, 55) // rod
                .addSlotPosition(20 + 20, 55 - 20); // plate

        }
        return this.toolBuildGuiInfo;
    }

}
