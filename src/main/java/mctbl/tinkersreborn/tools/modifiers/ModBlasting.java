package mctbl.tinkersreborn.tools.modifiers;

import java.util.List;
import java.util.ListIterator;

import net.minecraft.block.Block;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.world.BlockEvent;

import com.google.common.collect.ImmutableList;

import mctbl.tinkersreborn.library.tools.IToolMod;
import mctbl.tinkersreborn.library.tools.modifiers.ModifierAspect;
import mctbl.tinkersreborn.library.tools.modifiers.ModifierNBT;
import mctbl.tinkersreborn.library.tools.modifiers.ModifierTrait;
import mctbl.tinkersreborn.library.utils.BlockPos;
import mctbl.tinkersreborn.tools.TinkersRebornModifiers;
import mctbl.tinkersreborn.util.TinkersRebornUtils;
import mctbl.tinkersreborn.util.ToolTagsHelper;

public class ModBlasting extends ModifierTrait {

    public ModBlasting() {
        super("blasting", 0xffaa23, 3, 0);

        ListIterator<ModifierAspect> iter = aspects.listIterator();
        while (iter.hasNext()) {
            if (iter.next() == ModifierAspect.freeModifier) {
                iter.set(new ModifierAspect.FreeFirstModifierAspect(this, 1));
            }
        }

        addAspects(ModifierAspect.harvestOnly);

        MinecraftForge.EVENT_BUS.register(this);
    }

    @Override
    public boolean canApplyTogether(Enchantment enchantment) {
        return enchantment != Enchantment.silkTouch && enchantment != Enchantment.looting
            && enchantment != Enchantment.fortune;
    }

    @Override
    public boolean canApplyTogether(IToolMod toolmod) {
        String id = toolmod.getIdentifier();
        return !id.equals(TinkersRebornModifiers.modLuck.getIdentifier())
            && !id.equals(TinkersRebornModifiers.modSilktouch.getIdentifier());
    }

    private int getLevel(ItemStack tool) {
        return ModifierNBT.readInteger(ToolTagsHelper.getModifierTag(tool, getIdentifier())).level;
    }

    @Override
    public void miningSpeed(ItemStack tool, PlayerEvent.BreakSpeed event) {
        World world = event.entityPlayer.getEntityWorld();
        // target speed
        float speed = ToolTagsHelper.getActualMiningSpeed(tool);
        int level = getLevel(tool);

        // mitigate block hardness
        float hardness = event.block.getBlockHardness(world, event.x, event.y, event.z);
        if (hardness <= 0f) {
            // hardness 0 -> already instabreaks. otherwise we set speed to 0
            return;
        }

        speed *= hardness;

        if (level > 2) {
            speed /= 1.1f;
        } else if (level > 1) {
            speed /= 5f;
        } else {
            speed /= 10f;
        }

        float weight1 = (float) level / (float) maxLevel;
        float weight2 = 1f - (float) level / (float) maxLevel;

        // we weight the speed depending on how much the current level is. So 0 = full old speed, 10 = full new speed, 5
        // = in the middle
        float totalSpeed = speed * weight1 + event.originalSpeed * weight2;

        event.newSpeed = totalSpeed;
    }

    private float getBlockDestroyChange(ItemStack tool) {
        float level = getLevel(tool);
        float chancePerLevel = 1f / maxLevel;
        return level * chancePerLevel;
    }

    @Override
    public void afterBlockBreak(ItemStack tool, World world, Block block, BlockPos pos, EntityLivingBase player,
        boolean wasEffective) {
        String particleType = random.nextInt(20) == 0 ? "largeexplode" : "explode";
        world.spawnParticle(particleType, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 0, 0, 0);
    }

    @Override
    public void blockHarvestDrops(ItemStack tool, BlockEvent.HarvestDropsEvent event) {
        float chance = 1f - getBlockDestroyChange(tool);
        event.dropChance = event.dropChance * chance;
    }

    @Override
    public List<String> getExtraInfo(ItemStack tool, NBTTagCompound modifierTag) {
        String loc = TinkersRebornUtils.translate(String.format(LOC_Extra, getIdentifier()));
        float chance = getBlockDestroyChange(tool);

        return ImmutableList.of(String.format(loc, TinkersRebornUtils.dfPercent.format(chance)));
    }

    @Override
    public int getPriority() {
        // blasting destroys all the things, higher priority
        return 200;
    }
}
