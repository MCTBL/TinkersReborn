package mctbl.tinkersreborn.tools.modifiers;

import net.minecraft.block.Block;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

import mctbl.tinkersreborn.library.TinkerGuiException;
import mctbl.tinkersreborn.library.crafting.ToolBuilderHelper;
import mctbl.tinkersreborn.library.tools.IModifier;
import mctbl.tinkersreborn.library.tools.modifiers.ModifierAspect;
import mctbl.tinkersreborn.library.tools.modifiers.ModifierNBT;
import mctbl.tinkersreborn.library.tools.modifiers.ModifierTrait;
import mctbl.tinkersreborn.library.utils.BlockPos;
import mctbl.tinkersreborn.tools.Category;
import mctbl.tinkersreborn.util.TinkersRebornUtils;
import mctbl.tinkersreborn.util.ToolTags;
import mctbl.tinkersreborn.util.ToolTagsHelper;

public class ModLuck extends ModifierTrait {

    protected static final int baseCount = 60;
    protected static final int maxLevel = 3;

    // we have a bit of redundancy going on here with the luckAspect and the trait class
    private final LuckAspect aspect;

    public ModLuck() {
        super("luck", 0x2D51E2, maxLevel, 0);

        aspects.clear();
        aspect = new LuckAspect(this);
        addAspects(
            aspect,
            new ModifierAspect.CategoryAnyAspect(Category.HARVEST, Category.WEAPON, Category.PROJECTILE));
    }

    public int getLuckLevel(ItemStack itemStack) {
        return getLuckLevel(ToolTagsHelper.getModifierTag(itemStack, getIdentifier()));
    }

    public int getLuckLevel(NBTTagCompound modifierTag) {
        ModifierNBT.IntegerNBT data = ModifierNBT.readInteger(modifierTag);
        return aspect.getLevel(data.current);
    }

    @Override
    public boolean canApplyTogether(Enchantment enchantment) {
        return enchantment != Enchantment.silkTouch;
    }

    @Override
    public void applyEffect(NBTTagCompound rootCompound, NBTTagCompound modifierTag) {
        super.applyEffect(rootCompound, modifierTag);
        int lvl = getLuckLevel(modifierTag);

        applyEnchantments(rootCompound, lvl);
    }

    @Override
    public void afterBlockBreak(ItemStack tool, World world, Block block, BlockPos pos, EntityLivingBase player,
        boolean wasEffective) {
        rewardProgress(tool);
    }

    @Override
    public void afterHit(ItemStack tool, EntityLivingBase player, EntityLivingBase target, float damageDealt,
        boolean wasCritical, boolean wasHit) {
        if (player.worldObj.isRemote || !wasHit) {
            return;
        }
        // we reward one chance per full heart damage dealt. No chance for 0.5 heart hits, sorry :(
        for (int i = (int) (Math.min(damageDealt, 50f) / 2f); i > 0; i--) {
            rewardProgress(tool);
        }
    }

    public void rewardProgress(ItemStack tool) {
        // 3% chance
        if (random.nextFloat() > 0.03f) {
            return;
        }

        try {
            if (canApply(tool, tool)) {
                apply(tool);
            }
        } catch (TinkerGuiException e) {
            // no user feedback
        }
    }

    protected void applyEnchantments(NBTTagCompound rootCompound, int lvl) {
        boolean harvest = false;
        boolean weapon = false;

        lvl = Math.min(lvl, Enchantment.looting.getMaxLevel());

        for (Category category : ToolTagsHelper.getCategories(rootCompound)) {
            if (category == Category.HARVEST) {
                harvest = true;
            }
            if (category == Category.WEAPON) {
                weapon = true;
            }
        }

        // weapons get looting
        if (weapon) {
            while (lvl > ToolBuilderHelper.getEnchantmentLevel(rootCompound, Enchantment.looting)) {
                ToolBuilderHelper.addEnchantment(rootCompound, Enchantment.looting);
            }
        }
        // harvest tools get fortune
        if (harvest) {
            while (lvl > ToolBuilderHelper.getEnchantmentLevel(rootCompound, Enchantment.fortune)) {
                ToolBuilderHelper.addEnchantment(rootCompound, Enchantment.fortune);
            }
        }
    }

    @Override
    public String getTooltip(NBTTagCompound modifierTag, boolean detailed) {
        int level = getLuckLevel(modifierTag);

        String tooltip = getLocalizedName();
        if (level > 0) {
            tooltip += " " + TinkersRebornUtils.getRomanNumeral(level);
        }

        if (detailed) {
            ModifierNBT data = ModifierNBT.readInteger(modifierTag);
            tooltip += " " + data.extraInfo;
        }
        return tooltip;
    }

    @Override
    public void convertTConstractNBT(ItemStack tool, NBTTagCompound tcToolNBTCompound) {
        final int[] intArray = tcToolNBTCompound.getIntArray("Lapis");
        if (intArray.length == 2) {
            setLuckProgress(tool, intArray[0]);
        }
    }

    public void setLuckProgress(ItemStack tool, int progress) {
        apply(tool);

        progress = MathHelper.clamp_int(progress, 0, aspect.getMaxForLevel(maxLevel));
        int level = aspect.getLevel(progress);
        if (progress > aspect.getMaxForLevel(level)) {
            level++;
        }
        int max = aspect.getMaxForLevel(level);

        NBTTagCompound root = ToolTagsHelper.getTagSafe(tool);
        NBTTagCompound modifierTag = ToolTagsHelper.getModifierTag(root, this.identifier);

        ModifierNBT.IntegerNBT data = ModifierNBT.readInteger(modifierTag);
        data.identifier = this.identifier;
        data.type = ToolTags.TYPEMODIFIERS;
        data.color = this.color;
        data.max = max;
        data.level = level;
        data.current = progress;
        data.write(modifierTag);

        applyEffect(root, modifierTag);
    }

    public static class LuckAspect extends ModifierAspect.MultiAspect {

        public LuckAspect(IModifier parent) {
            super(parent, 0x5A82E2, maxLevel, baseCount, 1);
            this.freeModifierAspect = new FreeFirstModifierAspect(parent, 1);
        }

        @Override
        protected int getMaxForLevel(int level) {
            return (countPerLevel * level * (level + 1)) / 2; // sum(n)
        }

        public int getLevel(int current) {
            int i = 0;
            while (current >= getMaxForLevel(i + 1)) {
                i++;
            }
            return i;
        }
    }

}
