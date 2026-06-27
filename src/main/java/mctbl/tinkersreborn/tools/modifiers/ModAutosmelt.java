package mctbl.tinkersreborn.tools.modifiers;

import java.util.ListIterator;

import net.minecraft.block.Block;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.world.World;
import net.minecraftforge.event.world.BlockEvent;

import mctbl.tinkersreborn.TinkersRebornConfig;
import mctbl.tinkersreborn.library.tools.IToolMod;
import mctbl.tinkersreborn.library.tools.modifiers.ModifierAspect;
import mctbl.tinkersreborn.library.tools.modifiers.ModifierTrait;
import mctbl.tinkersreborn.library.utils.BlockPos;
import mctbl.tinkersreborn.tools.TinkersRebornModifiers;
import mctbl.tinkersreborn.util.TinkersRebornUtils;
import mctbl.tinkersreborn.util.ToolTagsHelper;

// Identical to the trait version of Autosmelt, these are separate because both modifiers and traits are registered
// differently
public class ModAutosmelt extends ModifierTrait {

    public ModAutosmelt() {
        super("mod_autosmelt", 0xFC0000);

        addAspects(
            new ModifierAspect.SingleAspect(this),
            new ModifierAspect.DataAspect(this),
            ModifierAspect.harvestOnly,
            ModifierAspect.freeModifier);
    }

    @Override
    public boolean canApplyTogether(Enchantment enchantment) {
        return enchantment != Enchantment.silkTouch;
    }

    @Override
    public boolean canApplyTogether(IToolMod otherModifier) {
        return !otherModifier.getIdentifier()
            .equals(TinkersRebornModifiers.modSilktouch.getIdentifier());
    }

    @Override
    public void blockHarvestDrops(ItemStack tool, BlockEvent.HarvestDropsEvent event) {
        if (ToolTagsHelper.isToolEffective(tool, event.block, event.blockMetadata)) {
            // go through the drops and replace them with their furnace'd variant if applicable
            ListIterator<ItemStack> iter = event.drops.listIterator();
            while (iter.hasNext()) {
                ItemStack drop = iter.next();
                ItemStack smelted = FurnaceRecipes.smelting()
                    .getSmeltingResult(drop);
                if (!TinkersRebornUtils.isStackEmpty(smelted)) {
                    smelted = smelted.copy();
                    smelted.stackSize = drop.stackSize;

                    int fortune = EnchantmentHelper.getEnchantmentLevel(Enchantment.fortune.effectId, tool);
                    if (TinkersRebornConfig.autoSmeltWithLapis && fortune > 0) {
                        smelted.stackSize = smelted.stackSize * random.nextInt(fortune + 1) + 1;
                    }

                    iter.set(smelted);

                    // drop XP for it
                    float xp = FurnaceRecipes.smelting()
                        .func_151398_b(smelted);
                    if (xp < 1 && Math.random() < xp) {
                        xp += 1f;
                    }
                    if (xp >= 1f) {
                        event.block.dropXpOnBlockBreak(event.world, event.x, event.y, event.z, (int) xp);
                    }
                }
            }
        }
    }

    @Override
    public void afterBlockBreak(ItemStack tool, World world, Block block, BlockPos pos, EntityLivingBase player,
        boolean wasEffective) {
        if (world.isRemote && wasEffective) {
            for (int i = 0; i < 3; i++) {
                world.spawnParticle(
                    "flame",
                    pos.getX() + random.nextDouble(),
                    pos.getY() + random.nextDouble(),
                    pos.getZ() + random.nextDouble(),
                    0.0D,
                    0.0D,
                    0.0D);
            }
        }
    }
}
