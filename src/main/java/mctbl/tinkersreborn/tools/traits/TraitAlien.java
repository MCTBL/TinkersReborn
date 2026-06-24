package mctbl.tinkersreborn.tools.traits;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.util.FakePlayer;

import com.google.common.collect.ImmutableList;

import mctbl.tinkersreborn.library.tools.traits.TraitProgressiveStats;
import mctbl.tinkersreborn.tools.materials.HeadMaterialStats;
import mctbl.tinkersreborn.util.ToolTagsHelper;

/**
 * Gives the tool bonus stats on crafting.
 * The bonus stats are distributed over time and are more or less random.
 * The stats that will be rewarded are already designated on the first time the tool is crafted
 */
public class TraitAlien extends TraitProgressiveStats {

    protected static int TICK_PER_STAT = 72;

    protected static int DURABILITY_STEP = 1;
    protected static float SPEED_STEP = 0.007f;
    protected static float ATTACK_STEP = 0.005f;

    public TraitAlien() {
        super("alien", EnumChatFormatting.YELLOW);
    }

    @Override
    public void onUpdate(ItemStack tool, World world, Entity entity, int itemSlot, boolean isSelected) {
        if (entity instanceof FakePlayer || entity.worldObj.isRemote) {
            return;
        }
        // every 3.6 seconds we distribute one stat. This means 1h = 1000 applications
        if (entity.ticksExisted % TICK_PER_STAT > 0) {
            return;
        }

        // we don't update if the player is currently breaking a block because that'd reset it
        if (playerIsBreakingBlock(entity) || (entity instanceof EntityPlayer player && player.getItemInUse() == tool)) {
            return;
        }

        NBTTagCompound root = ToolTagsHelper.getToolBaseNBTSafe(tool);
        StatNBT pool = getPoolLazily(root);
        StatNBT distributed = getBonus(root);

        // attack
        if (entity.ticksExisted % (TICK_PER_STAT * 3) == 0) {
            if (distributed.attack < pool.attack) {
                ToolTagsHelper.setAttackStat(tool, ToolTagsHelper.getAttackStat(tool) + ATTACK_STEP);
                distributed.attack += ATTACK_STEP;
            }
        }
        // speed
        else if (entity.ticksExisted % (TICK_PER_STAT * 2) == 0) {
            if (distributed.speed < pool.speed) {
                ToolTagsHelper.setMiningSpeed(tool, ToolTagsHelper.getMiningSpeed(tool) + SPEED_STEP);
                distributed.speed += SPEED_STEP;
            }
        }
        // durability
        else {
            if (distributed.durability < pool.durability) {
                ToolTagsHelper.setDurabilityStat(tool, ToolTagsHelper.getDurabilityStat(tool) + DURABILITY_STEP);
                distributed.durability += DURABILITY_STEP;
            }
        }

        // update statistics on distributed stats
        setBonus(root, distributed);
    }

    @Override
    public List<String> getExtraInfo(ItemStack tool, NBTTagCompound modifierTag) {
        StatNBT pool = getBonus(ToolTagsHelper.getToolBaseNBTSafe(tool));

        return ImmutableList.of(
            HeadMaterialStats.formatDurability(pool.durability),
            HeadMaterialStats.formatMiningSpeed(pool.speed),
            HeadMaterialStats.formatAttack(pool.attack));
    }

    private StatNBT getPoolLazily(NBTTagCompound rootCompound) {
        if (!hasPool(rootCompound)) {
            // ok, we need new stats. Let the fun begin!
            StatNBT data = new StatNBT();

            int statPoints = 800; // we distribute a whopping X points worth of stats!
            int size = 3;
            int[] tempCount = new int[size];

            for (; statPoints > 0; statPoints--) tempCount[random.nextInt(size)]++;

            data.durability += tempCount[0] * DURABILITY_STEP;
            data.speed += tempCount[1] * SPEED_STEP;
            data.attack += tempCount[2] * ATTACK_STEP;

            setPool(rootCompound, data);
        }
        return getPool(rootCompound);
    }
}
