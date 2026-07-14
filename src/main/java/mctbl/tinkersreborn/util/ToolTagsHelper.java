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
import net.minecraft.network.play.server.S23PacketBlockChange;
import net.minecraft.potion.Potion;
import net.minecraft.stats.StatList;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.event.world.BlockEvent;
import mctbl.tinkersreborn.TinkersReborn;
import mctbl.tinkersreborn.common.network.TinkerNetwork;
import mctbl.tinkersreborn.library.TinkersRebornRegistry;
import mctbl.tinkersreborn.library.materials.TinkersRebornMaterial;
import mctbl.tinkersreborn.library.tools.IModifier;
import mctbl.tinkersreborn.library.tools.ITrait;
import mctbl.tinkersreborn.library.tools.TinkerToolEvent;
import mctbl.tinkersreborn.library.tools.ToolCore;
import mctbl.tinkersreborn.library.tools.ToolNBT;
import mctbl.tinkersreborn.library.utils.BlockPos;
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

    public static ToolNBT getToolOriginStats(ItemStack stack) {
        return new ToolNBT(getToolOriginDataNBTSafe(stack));
    }

    public static ToolNBT getToolOriginStats(NBTTagCompound root) {
        return new ToolNBT(getToolOriginDataNBTSafe(root));
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
     * @return tool -> TinkersRebornTool -> Stats -> ModifierSlots
     */
    public static int getModifierSlots(ItemStack stack) {
        return getToolDataNBTSafe(stack).getInteger(ToolTags.MODIFIERSLOTS);
    }

    /**
     * @param compound
     * @return tool -> TinkersRebornTool -> Stats -> ModifierSlots
     */
    public static int getModifierSlots(NBTTagCompound compound) {
        return getToolDataNBTSafe(compound).getInteger(ToolTags.MODIFIERSLOTS);
    }

    public static void setModifierSlots(ItemStack stack, int slots) {
        NBTTagCompound baseTag = getToolDataNBTSafe(stack);
        baseTag.setInteger(ToolTags.MODIFIERSLOTS, slots);
    }

    public static void setModifierSlots(NBTTagCompound compound, int slots) {
        NBTTagCompound baseTag = getToolDataNBTSafe(compound);
        baseTag.setInteger(ToolTags.MODIFIERSLOTS, slots);
    }

    /**
     * @param stack
     * @return tool -> TinkersRebornTool -> Stats -> ExtraModifiers
     */
    public static int getExtraModifier(ItemStack stack) {
        return getToolDataNBTSafe(stack).getInteger(ToolTags.EXTRAMODIFIERS);
    }

    /**
     * @param compound
     * @return tool -> TinkersRebornTool -> Stats -> ExtraModifiers
     */
    public static int getExtraModifier(NBTTagCompound compound) {
        return getToolDataNBTSafe(compound).getInteger(ToolTags.EXTRAMODIFIERS);
    }

    public static void setExtraModifier(ItemStack stack, int slots) {
        NBTTagCompound baseTag = getToolDataNBTSafe(stack);
        baseTag.setInteger(ToolTags.EXTRAMODIFIERS, slots);
    }

    public static void setExtraModifier(NBTTagCompound compound, int slots) {
        NBTTagCompound baseTag = getToolDataNBTSafe(compound);
        baseTag.setInteger(ToolTags.EXTRAMODIFIERS, slots);
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

    public static List<BlockPos> calcAOEBlocks(ItemStack stack, World world, EntityPlayer player, BlockPos origin,
        int width, int height, int depth, int distance) {
        List<BlockPos> list = new ArrayList<>();
        // only works with toolcore because we need the raytrace call
        if (TinkersRebornUtils.isStackEmpty(stack) || !(stack.getItem() instanceof ToolCore)) {
            return list;
        }

        // find out where the player is hitting the block
        Block targetBlock = world.getBlock(origin.x, origin.y, origin.z);
        int targetBlockMeta = world.getBlockMetadata(origin.x, origin.y, origin.z);
        if (!isToolEffective(stack, targetBlock, targetBlockMeta)) {
            return list;
        }

        // raytrace to get the side, but has to result in the same block
        MovingObjectPosition mop = ((ToolCore) stack.getItem()).getMovingObjectPositionFromPlayer(world, player, true);
        if (mop == null || !origin.equals(mop.blockX, mop.blockY, mop.blockZ)) {
            mop = ((ToolCore) stack.getItem()).getMovingObjectPositionFromPlayer(world, player, false);
        }

        int facing = MathHelper.floor_double((player.rotationYaw / 90D) + 0.5D) & 3;
        ForgeDirection sideHit;
        ForgeDirection playerFacing = switch (facing) {
            case 0 -> ForgeDirection.NORTH;
            case 1 -> ForgeDirection.EAST;
            case 2 -> ForgeDirection.SOUTH;
            case 3 -> ForgeDirection.WEST;
            default -> ForgeDirection.NORTH;
        };

        if (mop != null && origin.equals(mop.blockX, mop.blockY, mop.blockZ)) {
            sideHit = ForgeDirection.getOrientation(mop.sideHit);
        } else {
            // derive facing from player rotation as fallback for edge cases like very fast
            // mining
            sideHit = playerFacing.getOpposite();
        }

        // fire event
        TinkerToolEvent.ExtraBlockBreak event = TinkerToolEvent.ExtraBlockBreak
            .fireEvent(stack, player, targetBlock, width, height, depth, distance);

        if (event.isCanceled()) {
            return list;
        }
        width = event.width;
        height = event.height;
        depth = event.depth;
        distance = event.distance;

        // we know the block and we know which side of the block we're hitting. time to
        // calculate the depth along the different axes
        int x;
        int y;
        int z;
        BlockPos start = origin;
        boolean mopValid = mop != null && origin.equals(mop.blockX, mop.blockY, mop.blockZ);
        switch (sideHit) {
            case DOWN:
            case UP:
                // x y depends on the angle we look?
                x = playerFacing.offsetX * height + playerFacing.offsetZ * width;
                y = sideHit == ForgeDirection.UP ? -depth : depth;
                z = playerFacing.offsetX * width + playerFacing.offsetZ * height;
                start = start.add(-x / 2, 0, -z / 2);
                if (x % 2 == 0) {
                    double hitX = mopValid ? mop.hitVec.xCoord - mop.blockX : 0.5d;
                    if (x > 0 && hitX > 0.5d) start = start.add(1, 0, 0);
                    else if (x < 0 && hitX < 0.5d) start = start.add(-1, 0, 0);
                }
                if (z % 2 == 0) {
                    double hitZ = mopValid ? mop.hitVec.zCoord - mop.blockZ : 0.5d;
                    if (z > 0 && hitZ > 0.5d) start = start.add(0, 0, 1);
                    else if (z < 0 && hitZ < 0.5d) start = start.add(0, 0, -1);
                }
                break;
            case NORTH:
            case SOUTH:
                x = width;
                y = height;
                z = sideHit == ForgeDirection.SOUTH ? -depth : depth;
                start = start.add(-x / 2, -y / 2, 0);
                if (x % 2 == 0 && mopValid && mop.hitVec.xCoord - mop.blockX > 0.5d) {
                    start = start.add(1, 0, 0);
                }
                if (y % 2 == 0 && mopValid && mop.hitVec.yCoord - mop.blockY > 0.5d) {
                    start = start.add(0, 1, 0);
                }
                break;
            case WEST:
            case EAST:
                x = sideHit == ForgeDirection.EAST ? -depth : depth;
                y = height;
                z = width;
                start = start.add(0, -y / 2, -z / 2);
                if (y % 2 == 0 && mopValid && mop.hitVec.yCoord - mop.blockY > 0.5d) {
                    start = start.add(0, 1, 0);
                }
                if (z % 2 == 0 && mopValid && mop.hitVec.zCoord - mop.blockZ > 0.5d) {
                    start = start.add(0, 0, 1);
                }
                break;
            default:
                x = y = z = 0;
        }

        for (int xp = start.getX(); xp != start.getX() + x; xp += x / MathHelper.abs(x)) {
            for (int yp = start.getY(); yp != start.getY() + y; yp += y / MathHelper.abs(y)) {
                for (int zp = start.getZ(); zp != start.getZ() + z; zp += z / MathHelper.abs(z)) {
                    // don't add the origin block
                    if (xp == origin.getX() && yp == origin.getY() && zp == origin.getZ()) {
                        continue;
                    }
                    if (distance > 0 && MathHelper.abs(xp - origin.getX()) + MathHelper.abs(yp - origin.getY())
                        + MathHelper.abs(zp - origin.getZ()) > distance) {
                        continue;
                    }
                    BlockPos pos = new BlockPos(xp, yp, zp);
                    if (isToolEffective(
                        stack,
                        world.getBlock(pos.x, pos.y, pos.z),
                        world.getBlockMetadata(pos.x, pos.y, pos.z))) {
                        list.add(pos);
                    }
                }
            }
        }

        return list;
    }

    public static void breakExtraBlock(ItemStack stack, World world, EntityPlayer player, BlockPos pos,
        BlockPos refPos) {
        if (!canBreakExtraBlock(stack, world, player, pos, refPos)) {
            return;
        }

        Block block = world.getBlock(pos.x, pos.y, pos.z);
        int blockMeta = world.getBlockMetadata(pos.x, pos.y, pos.z);

        // callback to the tool the player uses. Called on both sides. This damages the
        // tool n stuff.
        stack.func_150999_a(world, block, pos.x, pos.y, pos.z, player);

        // server sided handling
        if (!world.isRemote) {
            // send the blockbreak event
            BlockEvent.BreakEvent event = ForgeHooks
                .onBlockBreakEvent(
                    world,
                    ((EntityPlayerMP) player).theItemInWorldManager.getGameType(),
                    (EntityPlayerMP) player,
                    pos.x,
                    pos.y,
                    pos.z);
            if(event.isCanceled()) {
        	return;
            }
            int xp = event.getExpToDrop();

            block.onBlockHarvested(world, pos.x, pos.y, pos.z, blockMeta, player);
            // ItemInWorldManager.removeBlock
            if (block.removedByPlayer(world, player, pos.x, pos.y, pos.z, true)) { // boolean is if block can be
                                                                                   // harvested,
                // checked above
                block.onBlockDestroyedByPlayer(world, pos.x, pos.y, pos.z, blockMeta);
                block.harvestBlock(world, player, pos.x, pos.y, pos.z, blockMeta);
                block.dropXpOnBlockBreak(world, pos.x, pos.y, pos.z, xp);
            }

            // always send block update to client
            TinkerNetwork.sendPacket(player, new S23PacketBlockChange(pos.x, pos.y, pos.z, world));
        }
    }

    /**
     * Preconditions for
     * {@link #breakExtraBlock(ItemStack, World, EntityPlayer, BlockPos, BlockPos)}
     * and
     * {@link #shearExtraBlock(ItemStack, World, EntityPlayer, BlockPos, BlockPos)}
     * 
     * @param stack
     * @param world
     * @param player
     * @param pos
     * @param refPos
     * @return true if the extra block can be broken
     */
    private static boolean canBreakExtraBlock(ItemStack stack, World world, EntityPlayer player, BlockPos pos,
        BlockPos refPos) {
        // prevent calling that stuff for air blocks, could lead to unexpected behaviour
        // since it fires events
        if (world.isAirBlock(pos.x, pos.y, pos.z)) {
            return false;
        }

        // check if the block can be broken, since extra block breaks shouldn't
        // instantly break stuff like obsidian
        // or precious ores you can't harvest while mining stone
        Block block = world.getBlock(pos.x, pos.y, pos.z);
        int blockMeta = world.getBlockMetadata(pos.x, pos.y, pos.z);

        // only effective materials
        if (!isToolEffective(stack, block, blockMeta)) {
            return false;
        }

        Block refBlock = world.getBlock(refPos.x, refPos.y, refPos.z);

        float refStrength = ForgeHooks.blockStrength(refBlock, player, world, refPos.x, refPos.y, refPos.z);
        float strength = ForgeHooks.blockStrength(block, player, world, pos.x, pos.y, pos.z);

        // only harvestable blocks that aren't impossibly slow to harvest
        if (!ForgeHooks.canHarvestBlock(block, player, blockMeta) || refStrength / strength > 10f) {
            return false;
        }

        // From this point on it's clear that the player CAN break the block

        if (player.capabilities.isCreativeMode) {
            block.onBlockHarvested(world, pos.x, pos.y, pos.z, blockMeta, player);
            if (block.removedByPlayer(world, player, pos.x, pos.y, pos.z, false)) {
                block.onBlockDestroyedByPlayer(world, pos.x, pos.y, pos.z, blockMeta);
            }

            // send update to client
            if (!world.isRemote) {
                TinkerNetwork.sendPacket(player, new S23PacketBlockChange(pos.x, pos.y, pos.z, world));
            }
            return false;
        }
        return true;
    }
}
