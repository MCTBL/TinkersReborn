package mctbl.tinkersreborn.library.tools.leveling;

import java.util.Set;

import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

import mctbl.tinkersreborn.TinkersRebornConfig;
import mctbl.tinkersreborn.library.event.Sounds;
import mctbl.tinkersreborn.library.materials.MaterialStatusType;
import mctbl.tinkersreborn.library.tools.BowCore;
import mctbl.tinkersreborn.library.tools.ToolCore;
import mctbl.tinkersreborn.library.utils.MiningLevelHelper;
import mctbl.tinkersreborn.tools.Category;
import mctbl.tinkersreborn.tools.items.tools.BroadSword;
import mctbl.tinkersreborn.tools.items.tools.Excavator;
import mctbl.tinkersreborn.tools.items.tools.Hammer;
import mctbl.tinkersreborn.tools.items.tools.Hatchet;
import mctbl.tinkersreborn.tools.items.tools.LumberAxe;
import mctbl.tinkersreborn.tools.items.tools.Pickaxe;
import mctbl.tinkersreborn.tools.items.tools.Scythe;
import mctbl.tinkersreborn.tools.items.tools.Shovel;
import mctbl.tinkersreborn.util.TinkersStr;
import mctbl.tinkersreborn.util.ToolTags;
import mctbl.tinkersreborn.util.ToolTagsHelper;

public class ToolLevelingHelper {

    private ToolLevelingHelper() {}

    public static int getLevel(NBTTagCompound tags) {
        return tags.getInteger(ToolTags.TAG_LEVEL);
    }

    public static long getXp(NBTTagCompound tags) {
        return tags.getLong(ToolTags.TAG_EXP);
    }

    public static long getBoostXp(NBTTagCompound tags) {
        return tags.getLong(ToolTags.TAG_BOOST_EXP);
    }

    public static boolean hasLevel(NBTTagCompound tags) {
        return tags.hasKey(ToolTags.TAG_LEVEL);
    }

    public static boolean hasXp(NBTTagCompound tags) {
        return tags.hasKey(ToolTags.TAG_EXP);
    }

    public static boolean hasBoostXp(NBTTagCompound tags) {
        return tags.hasKey(ToolTags.TAG_BOOST_EXP);
    }

    public static boolean isBoosted(NBTTagCompound tags) {
        return tags.getBoolean(ToolTags.TAG_IS_BOOSTED);
    }

    public static boolean isMaxLevel(NBTTagCompound tags) {
        return getLevel(tags) >= TinkersRebornConfig.maxToolLevel;
    }

    /**
     * can only be boosted if: - tool was created while pick boosting was active - tool hasn't been boosted yet - tool
     * doesn't have max mining level already
     */
    public static boolean canBoostMiningLevel(ItemStack tool) {
        return !isBoosted(ToolTagsHelper.getToolLevelingNBTSafe(tool))
            && ToolTagsHelper.getHarvestLevelStat(tool) < MiningLevelHelper.getLastMiningLevel().levelIdx;
    }

    public static void getLevelingTags(NBTTagCompound baseTag, ToolCore toolcore) {
        NBTTagCompound nbt = new NBTTagCompound();
        // we start with level 1
        nbt.setInteger(ToolTags.TAG_LEVEL, 1);
        // and no xp :(
        nbt.setLong(ToolTags.TAG_EXP, 0);

        ToolTagsHelper.setToolLevelingNBTSafe(baseTag, nbt);
        // mining level boost
        if (TinkersRebornConfig.pickaxeBoostRequired && (toolcore instanceof Pickaxe || toolcore instanceof Hammer)) {
            resetNewToolHarvestLevelStat(baseTag);
        }
    }

    public static void resetNewToolHarvestLevelStat(NBTTagCompound baseTag) {
        // mining level boost
        int hlvl = ToolTagsHelper.getHarvestLevelStat(baseTag);
        NBTTagCompound nbt = ToolTagsHelper.getToolLevelingNBTSafe(baseTag);
        nbt.setBoolean(ToolTags.TAG_IS_BOOSTED, false);
        nbt.setLong(ToolTags.TAG_BOOST_EXP, 0);
        // reduce harvestlevel by 1
        ToolTagsHelper.setHarvestLevelStat(baseTag, hlvl - 1);
    }

    public static void onHurt(LivingHurtEvent event) {
        // only player caused damage
        if (!(event.source.damageType.equals("player") || event.source.damageType.equals("arrow"))) return;

        // only players
        if (!(event.source.getEntity() instanceof EntityPlayer)) return;
        EntityPlayer player = (EntityPlayer) event.source.getEntity();
        if (player instanceof FakePlayer && !TinkersRebornConfig.allowFakePlayerLeveling) return;

        ItemStack stack = player.getCurrentEquippedItem();

        if (stack == null || !stack.hasTagCompound()) return;

        if (stack.getItem() == null || !(stack.getItem() instanceof ToolCore toolCore)) return;

        int damageDealt = Math.round(event.ammount);
        int mobHealth = Math.round(event.entityLiving.prevHealth);
        int xp = Math.min(damageDealt, mobHealth);
        // non-weapons get half xp
        if (!toolCore.getCategory()
            .contains(Category.WEAPON)) {
            xp = Math.max(1, Math.round((float) xp / 2));
        }
        // reduce xp for hitting poor animals
        if (event.entityLiving instanceof EntityAnimal) {
            xp = Math.max(1, Math.round((float) xp / 2));
        }

        if (!event.entityLiving.isEntityAlive()) {
            return;
        }

        ItemStack ammo = null;
        // projectile weapons also get xp on their ammo!
        if (stack.getItem() instanceof BowCore bowCore && event.source.damageType.equals("arrow")) {
            ammo = bowCore.findAmmo(stack, player);
            if (ammo != null && !(ammo.getItem() instanceof ToolCore)) ammo = null;
        }

        // projectile weapons and ammo only get xp when they're shot
        if (!event.source.damageType.equals("arrow") && toolCore.getCategory()
            .contains(Category.PROJECTILE)) {
            return;
        }

        if (xp > 0) {
            for (ItemStack itemstack : new ItemStack[] { stack, ammo }) {
                if (itemstack == null) continue;
                addXP(itemstack, player, xp);
            }
        }
    }

    public static void addXP(ItemStack tool, EntityPlayer player, long xp) {
        if (player.capabilities.isCreativeMode) return;

        NBTTagCompound tags = ToolTagsHelper.getToolLevelingNBTSafe(tool);

        // only if we have a level or xp
        if (!hasLevel(tags) || !hasXp(tags)) return;

        // tool EXP
        Long toolXp = -1L;
        if (hasXp(tags)) toolXp = getXp(tags) + xp;

        // mininglevel boost EXP
        Long boostXp = -1L;
        if (hasBoostXp(tags)) boostXp = getBoostXp(tags) + xp;

        // update the tool information
        updateXP(tool, player, toolXp, boostXp);
    }

    /**
     * Updates the tool information with the given tool and boost xp. This SETS the xp!
     * 
     * @param player  Required for awesome *ding* sound
     * @param toolXP  Value the tool XP shall be set to. -1 for no change.
     * @param boostXP Value the mining-boost XP shall be set to. -1 for no change.
     */
    public static void updateXP(ItemStack tool, EntityPlayer player, long toolXP, long boostXP) {
        NBTTagCompound tags = ToolTagsHelper.getToolLevelingNBTSafe(tool);
        if (!hasLevel(tags)) return;

        int level = getLevel(tags);

        boolean leveled = false;
        boolean pickLeveled = false;

        // Update Tool XP
        if (toolXP >= 0 && hasXp(tags) && level > 0 && !isMaxLevel(tags) && TinkersRebornConfig.toolLevelingEnable) {
            // set new xp value
            tags.setLong(ToolTags.TAG_EXP, toolXP);

            // check for levelup
            if (toolXP >= getRequiredXp(tool, tags)) {
                levelUpTool(tool, player);
                leveled = true;
            }
        }

        // handle mining boost XP
        if (TinkersRebornConfig.pickaxeBoostRequired) {
            // we can only if we have a proper material (>stone) and are not max mining level already
            if (canBoostMiningLevel(tool)) {
                tags.setLong(ToolTags.TAG_BOOST_EXP, boostXP);

                // check for mining boost levelup!
                if (boostXP >= getRequiredBoostXp(tool)) {
                    levelUpMiningLevel(tool, player, leveled);

                    pickLeveled = true;
                }
            }
        }

        // if we got a levelup, play a sound!
        if ((leveled || pickLeveled) && !player.worldObj.isRemote)
            Sounds.playSoundForAll(player, Sounds.chime, 0.9f, 1.0f);
    }

    public static int getRequiredBoostXp(ItemStack tool) {
        return getRequiredXp(tool, null, true);
    }

    protected static int getRequiredXp(ItemStack tool, NBTTagCompound tags) {
        return getRequiredXp(tool, tags, false);
    }

    protected static int getRequiredXp(ItemStack tool, NBTTagCompound tags, boolean miningBoost) {
        if (tags == null) tags = ToolTagsHelper.getToolLevelingNBTSafe(tool);

        float base = 100f;

        if (tool.getItem() instanceof ToolCore core) {
            Set<Category> category = core.getCategory();
            boolean weapon = category.contains(Category.WEAPON);
            boolean bow = category.contains(Category.LAUNCHER);

            if (bow) {
                base *= 2.0F;
                base *= TinkersRebornConfig.xpRequiredWeaponsPercentage / 100f;
            } else if (weapon) {
                base *= 1.4F;
                base *= core.damagePotential();
                base *= Math.max(1, ToolTagsHelper.getActualToolAttack(tool)) * 1.2f;

                if (core instanceof Scythe || core instanceof BroadSword) base *= 1.5f;
                base *= TinkersRebornConfig.xpRequiredWeaponsPercentage / 100f;
            } else {
                int harvestLevel = ToolTagsHelper.getHarvestLevelStat(tool);
                if (harvestLevel < 1) base -= 20;
                if (harvestLevel < 2) base -= 15;

                float baseMiningSpeed = ToolTagsHelper.getToolOriginDataNBTSafe(tool)
                    .getFloat(ToolTags.MININGSPEED);
                float miningSpeed = ToolTagsHelper.getMiningSpeedStat(tool);

                float divider = 1.4f + core.getToolComponentsParts()
                    .stream()
                    .filter(p -> p.statusType() == MaterialStatusType.HEAD)
                    .count();

                base += (baseMiningSpeed + (miningSpeed - baseMiningSpeed) / 5f) / divider;

                // shovels need a bit more xp because their blocks break much faster
                if (tool.getItem() instanceof Hammer) base *= 5.1f;
                if (tool.getItem() instanceof Excavator) base *= 6.2f;
                if (tool.getItem() instanceof LumberAxe) base *= 1.38f;
                if (tool.getItem() instanceof Shovel) base *= 1.2f; // shovels break their blocks faster than picks
                if (tool.getItem() instanceof Hatchet) base *= 0.66f; // not much wood to chop, but usable as weapon

                base *= TinkersRebornConfig.xpRequiredToolsPercentage / 100f;
            }
        }

        if (miningBoost) {
            int harvestLevelCopper = 2;
            int harvestLevel = ToolTagsHelper.getHarvestLevelStat(tool);
            if (harvestLevel >= harvestLevelCopper)
                base *= Math.pow(TinkersRebornConfig.xpPerBoostLevelMultiplier, harvestLevel - harvestLevelCopper);
            if (harvestLevel == 0)
                base /= TinkersRebornConfig.xpPerBoostLevelMultiplier * TinkersRebornConfig.xpPerBoostLevelMultiplier;

            base *= TinkersRebornConfig.levelingPickaxeBoostXpPercentage / 100f;
        } else {
            int level = tags.getInteger("ToolLevel");
            if (level >= 1) base *= Math.pow(TinkersRebornConfig.xpPerLevelMultiplier, level - 1.0D);
            if (ToolTagsHelper.getHarvestLevelStat(tool) == 0)
                base /= TinkersRebornConfig.xpPerLevelMultiplier * TinkersRebornConfig.xpPerLevelMultiplier;
        }

        return Math.round(base);
    }

    /**
     * Applies all the logic for increasing the tool level. This is only specific to the *tool* level, and has no
     * relation to the mining-level-boost!
     */
    public static void levelUpTool(ItemStack stack, EntityPlayer player) {
        NBTTagCompound tags = ToolTagsHelper.getToolLevelingNBTSafe(stack);
        World world = player.worldObj;

        // *ding* levelup!
        int level = getLevel(tags);
        if (level >= TinkersRebornConfig.maxToolLevel) {
            return;
        }

        level++;

        // tell the player how awesome he is
        if (!world.isRemote) {
            // special message
            IChatComponent levelMsg;
            if (StatCollector.canTranslate("tinkersreborn.message.levelup." + level)) {
                levelMsg = new ChatComponentTranslation(
                    "tinkersreborn.message.levelup." + level,
                    stack.getItem()
                        .getItemStackDisplayName(stack));
            } else {
                levelMsg = new ChatComponentTranslation(
                    "tinkersreborn.message.levelup.generic",
                    stack.getItem()
                        .getItemStackDisplayName(stack),
                    LevelingTooltips.getLevelComponent(level));
            }
            levelMsg.getChatStyle()
                .setColor(EnumChatFormatting.DARK_AQUA);
            player.addChatMessage(levelMsg);
        }

        // and NOW save the change
        tags.setInteger(ToolTags.TAG_LEVEL, level);
        // reset tool xp to 0, since we're at a new level now
        tags.setLong(ToolTags.TAG_EXP, 0L);

        int currentModifiers = ToolTagsHelper.getModifierSlots(stack);

        // Add Modifier for leveling up?
        int modifiersToAdd = 0;
        // check if we are supposed to add a modifier at this levelup
        for (int lvl : TinkersRebornConfig.toolModifiersAtLevels) if (level == lvl) modifiersToAdd++;
        // yes, no break. this means if a level is in the list multiple times, you get multiple modifiers

        if (modifiersToAdd > 0) {
            currentModifiers += modifiersToAdd;
            ToolTagsHelper.setModifierSlots(stack, currentModifiers);

            // fancy message on clientside
            if (!world.isRemote) {
                String modLabelKey = world.rand.nextInt(10) < modifiersToAdd
                    ? "tinkersreborn.message.levelup.newmodifier.2"
                    : "tinkersreborn.message.levelup.newmodifier.1";
                IChatComponent modMsg = new ChatComponentText(EnumChatFormatting.DARK_AQUA.toString())
                    .appendSibling(new ChatComponentTranslation(modLabelKey))
                    .appendSibling(new ChatComponentText(EnumChatFormatting.DARK_AQUA + " ("))
                    .appendSibling(new ChatComponentText(EnumChatFormatting.GOLD + "+" + modifiersToAdd + " "))
                    .appendSibling(new ChatComponentText(TinkersStr.modifierToolTip.toString()))
                    .appendSibling(new ChatComponentText(EnumChatFormatting.DARK_AQUA + ")"));
                player.addChatMessage(modMsg);
            }
        }
    }

    public static void levelUpMiningLevel(ItemStack stack, EntityPlayer player, boolean leveled) {
        NBTTagCompound tags = ToolTagsHelper.getToolLevelingNBTSafe(stack);

        // we only apply that once
        if (isBoosted(tags)) return;

        // reset miningboost xp to 0
        if (hasBoostXp(tags)) tags.setLong(ToolTags.TAG_BOOST_EXP, 0L);

        // fancy message
        if (player != null) {
            if (!player.worldObj.isRemote) {
                IChatComponent miningMsg = new ChatComponentText(EnumChatFormatting.DARK_AQUA.toString())
                    .appendSibling(
                        new ChatComponentTranslation(
                            "tinkersreborn.message.levelup.miningboost",
                            stack.getItem()
                                .getItemStackDisplayName(stack)))
                    .appendSibling(new ChatComponentText(EnumChatFormatting.DARK_AQUA + " ("))
                    .appendSibling(new ChatComponentText(EnumChatFormatting.GOLD + "+1 "))
                    .appendSibling(new ChatComponentText(TinkersStr.harvestLevel.toString()))
                    .appendSibling(new ChatComponentText(EnumChatFormatting.DARK_AQUA + ")"));
                player.addChatMessage(miningMsg);
            }
        }

        tags.setBoolean(ToolTags.TAG_IS_BOOSTED, true);
        // increase harvest level by 1
        ToolTagsHelper
            .setHarvestLevelStat(ToolTagsHelper.getTagSafe(stack), ToolTagsHelper.getHarvestLevelStat(stack) + 1);
    }
}
