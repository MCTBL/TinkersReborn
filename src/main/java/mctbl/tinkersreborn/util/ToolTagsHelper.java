package mctbl.tinkersreborn.util;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IProjectile;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.potion.Potion;
import net.minecraft.stats.StatList;
import net.minecraft.util.MathHelper;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.Constants;

import mctbl.tinkersreborn.TinkersReborn;
import mctbl.tinkersreborn.library.TinkersRebornRegistry;
import mctbl.tinkersreborn.library.materials.TinkersRebornMaterial;
import mctbl.tinkersreborn.library.tools.IModifier;
import mctbl.tinkersreborn.library.tools.ITrait;
import mctbl.tinkersreborn.library.tools.TinkerToolEvent;
import mctbl.tinkersreborn.library.tools.ToolCore;
import mctbl.tinkersreborn.library.tools.ToolNBT;
import mctbl.tinkersreborn.tools.Category;

public class ToolTagsHelper {

    public static int TAG_TYPE_STRING = Constants.NBT.TAG_STRING;
    public static int TAG_TYPE_COMPOUND = Constants.NBT.TAG_COMPOUND;
    public static Random random = TinkersReborn.random;

    private ToolTagsHelper() {}

    /* Generic Tag Operations */
    public static NBTTagCompound getTagSafe(ItemStack stack) {
        // yes, the null checks aren't needed anymore, but they don't hurt either.
        // After all the whole purpose of this function is safety/processing possibly
        // invalid input ;)
        if (stack == null || stack.getItem() == null || !stack.hasTagCompound()) return new NBTTagCompound();

        return stack.getTagCompound();
    }

    public static NBTTagCompound getTagSafe(NBTTagCompound tag, String key) {
        if (tag == null || !tag.hasKey(key)) {
            return new NBTTagCompound();
        }
        return tag.getCompoundTag(key);
    }

    public static NBTTagList getTagListSafe(NBTTagCompound tag, String key, int type) {
        if (tag == null || !tag.hasKey(key)) {
            return new NBTTagList();
        }
        return tag.getTagList(key, type);
    }

    public static NBTTagList getStringTagListSafe(NBTTagCompound tag, String key) {
        return getTagListSafe(tag, key, TAG_TYPE_STRING);
    }

    public static NBTTagList getCompoundTagListSafe(NBTTagCompound tag, String key) {
        return getTagListSafe(tag, key, TAG_TYPE_COMPOUND);
    }

    /**
     * get the base tagcompound of tool {@link ToolTags}
     *
     * @param stack
     * @return tool -> TinkersRebornTool
     */
    public static NBTTagCompound getToolBaseNBTSafe(ItemStack stack) {
        return getTagSafe(getTagSafe(stack), ToolTags.TOOLBASETAG);
    }

    /**
     * get the base tagcompound of tool {@link ToolTags}
     *
     * @param tags
     * @return tool -> TinkersRebornTool
     */
    public static NBTTagCompound getToolBaseNBTSafe(NBTTagCompound tags) {
        return getTagSafe(tags, ToolTags.TOOLBASETAG);
    }

    /**
     * if root tag is not equal with stack's root tag mean new tag is new tag, set
     * it
     *
     * @param stack
     * @param tags
     */
    public static void setToolBaseNBTSafe(ItemStack stack, NBTTagCompound tags) {

        if (tags != getToolBaseNBTSafe(stack)) getTagSafe(stack).setTag(ToolTags.TOOLBASETAG, tags);
    }

    /**
     * @param stack
     * @return tool -> TinkersRebornTool -> RenderMaterials
     */
    public static NBTTagList getToolRenderMaterialsNBTSafe(ItemStack stack) {
        return getStringTagListSafe(getToolBaseNBTSafe(stack), ToolTags.RENDERMATERIALS);
    }

    public static void setToolRenderMaterialsNBTSafe(ItemStack stack, NBTTagList tagList) {
        NBTTagCompound toolBaseNBTSafe = getToolBaseNBTSafe(stack);
        setToolBaseNBTSafe(stack, toolBaseNBTSafe);
        toolBaseNBTSafe.setTag(ToolTags.RENDERMATERIALS, tagList);
    }

    /**
     * @param stack
     * @return tool -> TinkersRebornTool -> Special
     */
    public static NBTTagCompound getToolExtraNBTSafe(ItemStack stack) {
        return getToolBaseNBTSafe(stack).getCompoundTag(ToolTags.TOOLDATAEXTRA);
    }

    /**
     * @param stack
     * @return tool -> TinkersRebornTool -> Special
     */
    public static NBTTagCompound getToolExtraNBTSafe(NBTTagCompound root) {
        return getToolBaseNBTSafe(root).getCompoundTag(ToolTags.TOOLDATAEXTRA);
    }

    public static void setToolExtraNBTSafe(NBTTagCompound root, NBTTagCompound toolExtraNBT) {
        if (getToolExtraNBTSafe(root) != toolExtraNBT) root.setTag(ToolTags.TOOLDATAEXTRA, toolExtraNBT);
    }

    public static List<TinkersRebornMaterial> getToolRenderMaterialsList(ItemStack stack) {
        return fromTagToMaterial(getToolRenderMaterialsNBTSafe(stack));
    }

    /**
     * @param stack
     * @return tool -> TinkersRebornTool -> Materials
     */
    public static NBTTagList getToolBaseMaterialsNBTSafe(ItemStack stack) {
        return getStringTagListSafe(getToolBaseNBTSafe(stack), ToolTags.BASEMATERIALS);
    }

    public static void setToolBaseMaterialsNBTSafe(ItemStack stack, NBTTagList tagList) {
        NBTTagCompound toolBaseNBTSafe = getToolBaseNBTSafe(stack);
        setToolBaseNBTSafe(stack, toolBaseNBTSafe);
        toolBaseNBTSafe.setTag(ToolTags.BASEMATERIALS, tagList);
    }

    /**
     * @param stack
     * @return tool -> TinkersRebornTool -> Materials
     */
    public static List<TinkersRebornMaterial> getToolBaseMaterialsList(ItemStack stack) {
        return fromTagToMaterial(getToolBaseMaterialsNBTSafe(stack));
    }

    public static List<TinkersRebornMaterial> fromTagToMaterial(NBTTagList tagList) {
        List<TinkersRebornMaterial> materialList = new ArrayList<>();
        int tagCount = tagList.tagCount();
        for (int idx = 0; idx < tagCount; idx++) {
            materialList.add(TinkersRebornRegistry.getMaterialByIdentifier(tagList.getStringTagAt(idx)));
        }
        return materialList;
    }

    /**
     * @param stack
     * @return tool -> TinkersRebornTool -> RepairCount
     */
    public static int getRepairCount(ItemStack stack) {
        return getToolBaseNBTSafe(stack).getInteger(ToolTags.REPAIR_COUNT);
    }

    public static void addRepairCount(ItemStack stack) {
        NBTTagCompound toolBase = getToolBaseNBTSafe(stack);
        toolBase.setInteger(ToolTags.REPAIR_COUNT, toolBase.getInteger(ToolTags.REPAIR_COUNT) + 1);
    }

    /**
     * @param stack
     * @return tool -> TinkersRebornTool -> Stats
     */
    public static NBTTagCompound getToolDataNBTSafe(ItemStack stack) {
        return getTagSafe(getToolBaseNBTSafe(stack), ToolTags.TOOLDATA);
    }

    /**
     * @param compound
     * @return tool -> TinkersRebornTool -> Stats
     */
    public static NBTTagCompound getToolDataNBTSafe(NBTTagCompound compound) {
        return getTagSafe(getToolBaseNBTSafe(compound), ToolTags.TOOLDATA);
    }

    public static ToolNBT getToolStats(ItemStack stack) {
        return new ToolNBT(getToolDataNBTSafe(stack));
    }

    public static ToolNBT getToolStats(NBTTagCompound root) {
        return new ToolNBT(getToolDataNBTSafe(root));
    }

    /**
     * @param stack
     * @return tool -> TinkersRebornTool -> StatsOriginal
     */
    public static NBTTagCompound getToolOriginDataNBTSafe(ItemStack stack) {
        return getTagSafe(getToolBaseNBTSafe(stack), ToolTags.TOOLDATAORIG);
    }

    /**
     * @param stack
     * @return tool -> TinkersRebornTool -> StatsOriginal
     */
    public static NBTTagCompound getToolOriginDataNBTSafe(NBTTagCompound compound) {
        return getTagSafe(getToolBaseNBTSafe(compound), ToolTags.TOOLDATAORIG);
    }

    // stats
    /**
     * @param stack
     * @return tool -> TinkersRebornTool -> Broken
     */
    public static boolean isBroken(ItemStack stack) {
        return getToolBaseNBTSafe(stack).getBoolean(ToolTags.BROKEN);
    }

    public static void setBroken(ItemStack stack, boolean isBroken) {
        NBTTagCompound toolBaseNBTSafe = getToolBaseNBTSafe(stack);
        setToolBaseNBTSafe(stack, toolBaseNBTSafe);
        toolBaseNBTSafe.setBoolean(ToolTags.BROKEN, isBroken);
    }

    /**
     * @param stack
     * @return tool -> TinkersRebornTool -> EnchantEffect
     */
    public static boolean hasEnchantEffect(ItemStack stack) {
        return getToolBaseNBTSafe(stack).getBoolean(ToolTags.ENCHANT_EFFECT);
    }

    /**
     * @param root
     * @return tool -> TinkersRebornTool -> CategoryList
     */
    public static NBTTagList getCategoryList(NBTTagCompound root) {
        return getTagListSafe(getToolBaseNBTSafe(root), ToolTags.TOOLCATEGORY, TAG_TYPE_STRING);
    }

    /**
     * @param stack
     * @return tool -> TinkersRebornTool -> Stats -> ModifierSlots
     */
    public static int getModifierSlots(ItemStack stack) {
        return getToolDataNBTSafe(stack).getInteger(ToolTags.MODIFIER_SLOTS);
    }

    /**
     * @param compound
     * @return tool -> TinkersRebornTool -> Stats -> ModifierSlots
     */
    public static int getModifierSlots(NBTTagCompound compound) {
        return getToolDataNBTSafe(compound).getInteger(ToolTags.MODIFIER_SLOTS);
    }

    public static void setModifierSlots(ItemStack stack, int slots) {
        NBTTagCompound baseTag = getToolDataNBTSafe(stack);
        baseTag.setInteger(ToolTags.MODIFIER_SLOTS, slots);
    }

    public static void setModifierSlots(NBTTagCompound compound, int slots) {
        NBTTagCompound baseTag = getToolDataNBTSafe(compound);
        baseTag.setInteger(ToolTags.MODIFIER_SLOTS, slots);
    }

    /**
     * @param compound
     * @return tool -> TinkersRebornTool -> NoRename
     */
    public static boolean isToolNoRenaem(ItemStack stack) {
        return getToolBaseNBTSafe(stack).getBoolean(ToolTags.NO_RENAME);
    }

    /**
     *
     * @param stack
     * @return tool -> TinkersRebornTool -> Modifiers
     */
    public static NBTTagList getModifiersTagList(ItemStack stack) {
        return getCompoundTagListSafe(getToolBaseNBTSafe(stack), ToolTags.MODIFIERS);
    }

    public static List<NBTTagCompound> getModifiersList(ItemStack stack) {
        NBTTagList tagList = getCompoundTagListSafe(getToolBaseNBTSafe(stack), ToolTags.MODIFIERS);
        List<NBTTagCompound> list = new ArrayList<>();
        for (int idx = 0; idx < tagList.tagCount(); idx++) list.add(tagList.getCompoundTagAt(idx));

        return list;
    }

    /**
     *
     * @param stack
     * @return tool -> TinkersRebornTool -> Modifiers
     */
    public static NBTTagList getModifiersTagList(NBTTagCompound compound) {
        return getCompoundTagListSafe(getToolBaseNBTSafe(compound), ToolTags.MODIFIERS);
    }

    public static void setModifiersTagList(ItemStack stack, NBTTagList list) {
        setModifiersTagList(getTagSafe(stack), list);
    }

    public static void setModifiersTagList(NBTTagCompound compound, NBTTagList list) {
        if (getModifiersTagList(compound) != list) getToolBaseNBTSafe(compound).setTag(ToolTags.MODIFIERS, list);
    }

    /**
     * @param stack
     * @return tool -> TinkersRebornTool -> Stats -> HarvestLevel
     */
    public static int getHarvestLevelStat(ItemStack stack) {
        return getToolDataNBTSafe(stack).getInteger(ToolTags.HARVESTLEVEL);
    }

    /**
     * @param stack
     * @return tool -> TinkersRebornTool -> Stats -> HarvestLevel
     */
    public static int getHarvestLevelStat(NBTTagCompound root) {
        return getToolDataNBTSafe(root).getInteger(ToolTags.HARVESTLEVEL);
    }

    /**
     * @param stack
     * @return tool -> TinkersRebornTool -> Stats -> HarvestLevel
     */
    public static void setHarvestLevelStat(NBTTagCompound root, int newHarvertLevel) {
        getToolDataNBTSafe(root).setInteger(ToolTags.HARVESTLEVEL, newHarvertLevel);
    }

    /**
     * @param stack
     * @return tool -> TinkersRebornTool -> Stats -> Durability
     */
    public static int getDurabilityStat(ItemStack stack) {
        return getToolDataNBTSafe(stack).getInteger(ToolTags.DURABILITY);
    }

    /**
     * @param stack
     * @return tool -> TinkersRebornTool -> Stats -> Durability
     */
    public static int getDurabilityStat(NBTTagCompound root) {
        return getToolDataNBTSafe(root).getInteger(ToolTags.DURABILITY);
    }

    public static void setDurabilityStat(ItemStack stack, int newDurability) {
        getToolDataNBTSafe(stack).setInteger(ToolTags.DURABILITY, newDurability);
    }

    public static void setDurabilityStat(NBTTagCompound root, int newDurability) {
        getToolDataNBTSafe(root).setInteger(ToolTags.DURABILITY, newDurability);
    }

    /**
     * @param stack
     * @return tool -> TinkersRebornTool -> Stats -> UsedModifiers
     */
    public static int getUsedModifiers(ItemStack stack) {
        return getToolDataNBTSafe(stack).getInteger(ToolTags.USEDMODIFIERS);
    }

    /**
     * @param compound
     * @return tool -> TinkersRebornTool -> Stats -> UsedModifiers
     */
    public static int getUsedModifiers(NBTTagCompound compound) {
        return getToolDataNBTSafe(compound).getInteger(ToolTags.USEDMODIFIERS);
    }

    public static void setUsedModifiers(ItemStack stack, int used) {
        getToolDataNBTSafe(stack).setInteger(ToolTags.USEDMODIFIERS, used);
    }

    public static void setUsedModifiers(NBTTagCompound compound, int used) {
        getToolDataNBTSafe(compound).setInteger(ToolTags.USEDMODIFIERS, used);
    }

    /**
     * @param stack
     * @return tool -> TinkersRebornTool -> Stats -> MiningSpeed
     */
    public static float getMiningSpeedStat(ItemStack stack) {
        return getToolDataNBTSafe(stack).getFloat(ToolTags.MININGSPEED);
    }

    /**
     * @param stack
     * @return tool -> TinkersRebornTool -> Stats -> MiningSpeed
     */
    public static float getMiningSpeedStat(NBTTagCompound root) {
        return getToolDataNBTSafe(root).getFloat(ToolTags.MININGSPEED);
    }

    public static void setMiningSpeed(ItemStack stack, float newMiningSpeed) {
        getToolDataNBTSafe(stack).setFloat(ToolTags.MININGSPEED, newMiningSpeed);
    }

    public static void setMiningSpeed(NBTTagCompound root, float newMiningSpeed) {
        getToolDataNBTSafe(root).setFloat(ToolTags.MININGSPEED, newMiningSpeed);
    }

    /**
     * @param stack
     * @return tool -> TinkersRebornTool -> Stats -> Attack
     */
    public static float getAttackStat(ItemStack stack) {
        return getToolDataNBTSafe(stack).getFloat(ToolTags.ATTACK);
    }

    /**
     * @param stack
     * @return tool -> TinkersRebornTool -> Stats -> Attack
     */
    public static float getAttackStat(NBTTagCompound root) {
        return getToolDataNBTSafe(root).getFloat(ToolTags.ATTACK);
    }

    public static void setAttackStat(ItemStack stack, float newDamamge) {
        getToolDataNBTSafe(stack).setFloat(ToolTags.ATTACK, newDamamge);
    }

    public static void setAttackStat(NBTTagCompound root, float newDamamge) {
        getToolDataNBTSafe(root).setFloat(ToolTags.ATTACK, newDamamge);
    }

    public static float getActualToolAttack(ItemStack stack) {
        float damage = getAttackStat(stack);
        if (stack != null && stack.getItem() instanceof ToolCore core) {
            damage *= core.damagePotential();
        }
        return damage;
    }

    public static float calcCutoffDamage(float damage, float cutoff) {
        float p = 1f;
        float d = damage;
        damage = 0f;
        while (d > cutoff) {
            damage += p * cutoff;
            // safety for ridiculous values
            if (p > 0.001f) {
                p *= 0.9f;
            } else {
                damage += p * cutoff * ((d / cutoff) - 1f);
                return damage;
            }
            d -= cutoff;
        }

        damage += p * d;

        return damage;
    }

    public static float getActualAttackDamage(ItemStack stack, EntityLivingBase player) {
        float damage = (float) SharedMonsterAttributes.attackDamage.getDefaultValue();
        if (player != null) {
            damage = (float) player.getEntityAttribute(SharedMonsterAttributes.attackDamage)
                .getAttributeValue();
        }

        damage += getActualToolAttack(stack);

        if (stack.getItem() instanceof ToolCore core) {
            damage = calcCutoffDamage(damage, core.damageCutoff());
        }

        return damage;
    }

    public static float getActualMiningSpeed(ItemStack stack) {
        float speed = getMiningSpeedStat(stack);
        if (!TinkersRebornUtils.isStackEmpty(stack) && stack.getItem() instanceof ToolCore) {
            speed *= ((ToolCore) stack.getItem()).miningSpeedModifier();
        }
        return speed;
    }

    public static void breakTool(ItemStack stack, EntityLivingBase entity) {
        NBTTagCompound tag = getToolBaseNBTSafe(stack);
        setToolBaseNBTSafe(stack, tag);

        tag.setBoolean(ToolTags.BROKEN, true);

        if (entity instanceof EntityPlayerMP player) {
            // this
            player.playSound("entity.item.break", 0.8F, 0.8F + entity.worldObj.rand.nextFloat() * 0.4F);

            // or this
            // player.worldObj.playSound(player.posX, player.posY, player.posZ,
            // "entity.item.break", 0.8F,
            // 0.8F + entity.worldObj.rand.nextFloat() * 0.4F, false);

            player.renderBrokenItemStack(stack);
        }
    }

    public static void unbreakTool(ItemStack stack) {
        if (isBroken(stack)) {
            // ensure correct damage value
            stack.setItemDamage(stack.getMaxDamage());

            // setItemDamage might break the tool again, so we do this afterwards
            NBTTagCompound tag = getToolBaseNBTSafe(stack);
            setToolBaseNBTSafe(stack, tag);

            tag.setBoolean(ToolTags.BROKEN, false);
        }
    }

    public static float calcMiningSpeed(ItemStack stack, Block block, int meta) {
        if (!stack.hasTagCompound()) {
            return 1F;
        } else if (isBroken(stack)) {
            return 0.3F;
        } else if (!canHarvest(stack, block, meta)) {
            return 1.0F;
        }
        float speed = getMiningSpeedStat(stack);

        if (stack.getItem() instanceof ToolCore core) speed *= core.miningSpeedModifier();

        return speed;
    }

    /**
     * Checks if an item has the right harvest level of the correct type for the
     * block.
     */
    public static boolean canHarvest(ItemStack stack, Block block, int meta) {
        // doesn't require a tool
        if (block.getMaterial()
            .isToolNotRequired()) {
            return true;
        }

        return stack.getItem()
            .getHarvestLevel(stack, block.getHarvestTool(meta)) >= block.getHarvestLevel(meta);
    }

    /* Tool Durability */
    public static int getCurrentDurability(ItemStack stack) {
        return stack.getMaxDamage() - stack.getItemDamage();
    }

    public static int getMaxDurability(ItemStack stack) {
        return stack.getMaxDamage();
    }

    /**
     * @param stack
     * @return tool -> TinkersRebornTool -> StatsOriginal -> Durability
     */
    public static int getOriginalDurability(ItemStack stack) {
        return getToolOriginDataNBTSafe(stack).getInteger(ToolTags.DURABILITY);
    }

    /**
     * @param stack
     * @return tool -> TinkersRebornTool -> StatsOriginal -> Durability
     */
    public static int getOriginalDurability(NBTTagCompound root) {
        return getToolOriginDataNBTSafe(root).getInteger(ToolTags.DURABILITY);
    }

    /**
     * Returns true if the tool is effective for harvesting the given block.
     */
    public static boolean isToolEffective(ItemStack stack, Block block, int meta) {
        // check material
        for (String type : stack.getItem()
            .getToolClasses(stack)) {
            if (block.isToolEffective(type, meta)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Damages the tool. Entity is only needed in case the tool breaks for rendering
     * the break effect.
     */
    public static void damageTool(ItemStack stack, int amount, EntityLivingBase entity) {
        if (amount == 0 || isBroken(stack)) {
            return;
        }

        int actualAmount = amount;

        for (ITrait trait : getTraitsOrdered(stack)) {
            if (amount > 0) {
                actualAmount = trait.onToolDamage(stack, amount, actualAmount, entity);
            } else {
                actualAmount = trait.onToolHeal(stack, amount, actualAmount, entity);
            }
        }

        // extra compatibility for unbreaking.. because things just love to mess it up..
        // like 3rd party stuff
        if (actualAmount > 0 && getToolBaseNBTSafe(stack).getBoolean(ToolTags.UNBREAKABLE)) {
            actualAmount = 0;
        }

        // ensure we never deal more damage than durability
        actualAmount = Math.min(actualAmount, getCurrentDurability(stack));
        stack.setItemDamage(stack.getItemDamage() + actualAmount);

        if (getCurrentDurability(stack) == 0) {
            breakTool(stack, entity);
        }
    }

    public static void healTool(ItemStack stack, int amount, EntityLivingBase entity) {
        damageTool(stack, -amount, entity);
    }

    public static void repairTool(ItemStack stack, int amount) {
        // entity is optional, only needed for rendering break effect, never needed when
        // repairing
        repairTool(stack, amount, null);
    }

    public static void repairTool(ItemStack stack, int amount, EntityLivingBase entity) {
        unbreakTool(stack);

        TinkerToolEvent.OnRepair.fireEvent(stack, amount);

        healTool(stack, amount, entity);
    }

    public static List<ITrait> getTraitsOrdered(ItemStack tool) {
        List<ITrait> traits = new ArrayList<>();

        List<NBTTagCompound> traitList = getModifiersList(tool);
        for (NBTTagCompound compound : traitList) {
            IModifier trait = TinkersRebornRegistry.getModifierAndTrait(compound.getString(ToolTags.IDENTIFIER));
            if (trait != null && trait instanceof ITrait t) {
                traits.add(t);
            }
        }

        traits.sort(
            Comparator.comparingInt(ITrait::getPriority)
                .reversed());

        return traits;
    }

    /* Dealing tons of damage */
    /**
     * General version of attackEntity. Applies cooldowns but has no projectile
     * entity
     */
    public static boolean attackEntity(ItemStack stack, ToolCore tool, EntityLivingBase attacker, Entity targetEntity) {
        return attackEntity(stack, tool, attacker, targetEntity, null);
    }

    /**
     * Makes all the calls to attack an entity. Takes enchantments and potions and
     * traits into account. Basically call this when a tool deals damage. Most of
     * this function is the same as
     * {@link EntityPlayer#attackTargetEntityWithCurrentItem(Entity targetEntity)}
     */
    public static boolean attackEntity(ItemStack stack, ToolCore tool, EntityLivingBase attacker, Entity targetEntity,
        Entity projectileEntity) {
        // nothing to do, no target?
        if (targetEntity == null || !targetEntity.canAttackWithItem()
            || targetEntity.hitByEntity(attacker)
            || !stack.hasTagCompound()) {
            return false;
        }
        if (isBroken(stack)) {
            return false;
        }
        if (attacker == null) {
            return false;
        }
        boolean isProjectile = projectileEntity != null;
        EntityLivingBase target = null;
        EntityPlayer player = null;
        if (targetEntity instanceof EntityLivingBase) {
            target = (EntityLivingBase) targetEntity;
        }
        if (attacker instanceof EntityPlayer p) {
            player = p;
            if (target instanceof EntityPlayer t) {
                if (!player.canAttackPlayer(t)) {
                    return false;
                }
            }
        }

        // traits on the tool
        List<ITrait> traits = getTraitsOrdered(stack);

        // players base damage (includes tools damage stat)
        float baseDamage = (float) attacker.getEntityAttribute(SharedMonsterAttributes.attackDamage)
            .getAttributeValue();

        // missing because not supported by tcon tools: vanilla damage enchantments, we
        // have our own modifiers
        // missing because not supported by tcon tools: vanilla knockback enchantments,
        // we have our own modifiers
        float baseKnockback = attacker.isSprinting() ? 1 : 0;

        // calculate if it's a critical hit
        boolean isCritical = attacker.fallDistance > 0.0F && !attacker.onGround
            && !attacker.isOnLadder()
            && !attacker.isInWater()
            && !attacker.isPotionActive(Potion.blindness)
            && !attacker.isRiding();
        for (ITrait trait : traits) {
            if (trait.isCriticalHit(stack, attacker, target)) {
                isCritical = true;
            }
        }

        // calculate actual damage
        float damage = baseDamage;
        if (target != null) {
            for (ITrait trait : traits) {
                damage = trait.damage(stack, attacker, target, baseDamage, damage, isCritical);
            }
        }

        // apply critical damage
        if (isCritical) {
            damage *= 1.5f;
        }

        // calculate cutoff
        damage = calcCutoffDamage(damage, tool.damageCutoff());

        // calculate actual knockback
        float knockback = baseKnockback;
        if (target != null) {
            for (ITrait trait : traits) {
                knockback = trait.knockBack(stack, attacker, target, damage, baseKnockback, knockback, isCritical);
            }
        }

        // missing because not supported by tcon tools: vanilla fire aspect
        // enchantments, we have our own modifiers

        float oldHP = 0;

        double oldVelX = targetEntity.motionX;
        double oldVelY = targetEntity.motionY;
        double oldVelZ = targetEntity.motionZ;

        if (target != null) {
            oldHP = target.getHealth();
        }

        // apply cooldown damage decrease
        String sound = null;

        // deal the damage
        if (target != null) {
            int hurtResistantTime = target.hurtResistantTime;
            for (ITrait trait : traits) {
                trait.onHit(stack, attacker, target, damage, isCritical);
                // reset hurt reristant time
                target.hurtResistantTime = hurtResistantTime;
            }
        }

        boolean hit = false;
        if (isProjectile && tool instanceof IProjectile) {
            // hit = ((IProjectile) tool).dealDamageRanged(stack, projectileEntity,
            // attacker, targetEntity, damage);
        } else {
            hit = tool.dealDamage(stack, attacker, targetEntity, damage);
        }

        // did we hit?
        if (hit && target != null) {
            // actual damage dealt
            float damageDealt = oldHP - target.getHealth();

            // apply knockback modifier
            oldVelX = target.motionX = oldVelX + (target.motionX - oldVelX) * tool.knockback();
            oldVelY = target.motionY = oldVelY + (target.motionY - oldVelY) * tool.knockback() / 3f;
            oldVelZ = target.motionZ = oldVelZ + (target.motionZ - oldVelZ) * tool.knockback();

            // apply knockback
            if (knockback > 0f) {
                double velX = -MathHelper.sin(attacker.rotationYaw * (float) Math.PI / 180.0F) * knockback * 0.5F;
                double velZ = MathHelper.cos(attacker.rotationYaw * (float) Math.PI / 180.0F) * knockback * 0.5F;
                targetEntity.addVelocity(velX, 0.1d, velZ);

                // slow down player
                attacker.motionX *= 0.6f;
                attacker.motionZ *= 0.6f;
                attacker.setSprinting(false);
            }

            // Send movement changes caused by attacking directly to hit players.
            // I guess this is to allow better handling at the hit players side? No idea why
            // it resets the motion though.
            if (targetEntity instanceof EntityPlayerMP && targetEntity.velocityChanged) {
                // TinkerNetwork.sendPacket(targetEntity, new
                // SPacketEntityVelocity(targetEntity));
                // targetEntity.velocityChanged = false;
                // targetEntity.motionX = oldVelX;
                // targetEntity.motionY = oldVelY;
                // targetEntity.motionZ = oldVelZ;
            }

            if (player != null) {
                // vanilla critical callback
                if (isCritical) {
                    player.onCriticalHit(target);
                    // not sure
                    sound = "entity.player.attack.crit";
                }

                // "magical" critical damage? (aka caused by modifiers)
                if (damage > baseDamage) {
                    // this usually only displays some particles :)
                    player.onEnchantmentCritical(targetEntity);
                }

                // vanilla achievement support :D
                // TODO: READD
                // if(damage >= 18f) {
                // player.addStat(AchievementList.OVERKILL);
                // }
            }
            // attacker.setLastAttacker(target);
            target.setLastAttacker(attacker);

            // Damage indicator particles

            // call post-hit callbacks before reducing the durability
            for (ITrait trait : traits) {
                trait.afterHit(stack, attacker, target, damageDealt, isCritical, true); // hit is always true
            }

            // damage the tool
            if (player != null) {
                stack.hitEntity(target, player);
                if (!player.capabilities.isCreativeMode && !isProjectile) {
                    tool.reduceDurabilityOnHit(stack, player, damage);
                }

                player.addStat(StatList.damageDealtStat, Math.round(damageDealt * 10f));
                player.addExhaustion(0.3f);

                if (player.getEntityWorld() instanceof WorldServer world && damageDealt > 2f) {
                    int k = (int) (damageDealt * 0.5);
                    for (int i = 0; i < k; i++) {
                        world.spawnParticle(
                            "damageIndicator",
                            targetEntity.posX + 0.5D - random.nextDouble(),
                            targetEntity.posY + targetEntity.height * 0.5D * random.nextDouble(),
                            targetEntity.posZ + 0.5D - random.nextDouble(),
                            0.2D,
                            0.2D,
                            0.2D);
                    }
                }

            } else if (!isProjectile) {
                tool.reduceDurabilityOnHit(stack, null, damage);
            }
        } else {
            sound = "entity.player.attack.nodamage";
        }

        if (player != null && sound != null && !player.worldObj.isRemote) {
            player.playSound(sound, 0.8F, 0.8F + player.worldObj.rand.nextFloat() * 0.4F);
        }

        return true;
    }

    public static boolean hasModifier(ItemStack stack, String identifier) {
        return !getModifierTag(stack, identifier).hasNoTags();
    }

    public static NBTTagCompound getModifierTag(ItemStack stack, String identifier) {
        return getModifierTag(getTagSafe(stack), identifier);
    }

    public static boolean hasModifier(NBTTagCompound root, String identifier) {
        return !getModifierTag(root, identifier).hasNoTags();
    }

    public static NBTTagCompound getModifierTag(NBTTagCompound root, String identifier) {
        NBTTagList modifiersList = getModifiersTagList(root);
        int tagLength = modifiersList.tagCount();
        if (tagLength == 0) {
            return new NBTTagCompound();
        }

        for (int idx = 0; idx < tagLength; idx++) {
            NBTTagCompound modifierCompound = modifiersList.getCompoundTagAt(idx);
            if (identifier.equals(modifierCompound.getString(ToolTags.IDENTIFIER))) return modifierCompound;
        }

        return new NBTTagCompound();
    }

    public static void removeModifiersTag(NBTTagCompound root, String identifier) {
        NBTTagList modifiersList = getModifiersTagList(root);
        int tagLength = modifiersList.tagCount();
        for (int idx = 0; idx < tagLength; idx++) {
            NBTTagCompound modifierCompound = modifiersList.getCompoundTagAt(idx);
            if (identifier.equals(modifierCompound.getString(ToolTags.IDENTIFIER))) {
                modifiersList.removeTag(idx);
                return;
            }
        }
    }

    public static List<Category> getCategories(NBTTagCompound root) {
        List<Category> cateList = new ArrayList<>();
        NBTTagList categoryList = getCategoryList(root);
        int tagCount = categoryList.tagCount();
        for (int idx = 0; idx < tagCount; idx++) {
            cateList.add(Category.categories.get(categoryList.getStringTagAt(idx)));
        }
        return cateList;
    }

    public static boolean hasCategory(ItemStack stack, Category category) {
        if (stack == null || stack.stackSize == 0) {
            return false;
        } else if (stack.getItem() instanceof ToolCore core) {
            return core.hasCategory(category);
        }
        return false;
    }
}
