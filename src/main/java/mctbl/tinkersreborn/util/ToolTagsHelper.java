package mctbl.tinkersreborn.util;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants;

import mctbl.tinkersreborn.library.tools.ToolCore;

public class ToolTagsHelper {

    public static int TAG_TYPE_STRING = Constants.NBT.TAG_STRING;
    public static int TAG_TYPE_COMPOUND = Constants.NBT.TAG_COMPOUND;

    private ToolTagsHelper() {}

    /* Generic Tag Operations */
    @Nullable
    public static NBTTagCompound getTagSafe(ItemStack stack) {
        // yes, the null checks aren't needed anymore, but they don't hurt either.
        // After all the whole purpose of this function is safety/processing possibly
        // invalid input ;)
        if (stack == null || stack.getItem() == null || !stack.hasTagCompound()) return null;

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

    /**
     * get the base tagcompound of tool {@link ToolTags}
     * 
     * @param stack
     * @return
     */
    public static NBTTagCompound getToolBaseNBTSafe(ItemStack stack) {
        return getTagSafe(getTagSafe(stack), ToolTags.TOOLBASETAG);
    }

    public static void setToolBaseNBTSafe(ItemStack stack, NBTTagCompound tags) {
        NBTTagCompound basetag = new NBTTagCompound();
        basetag.setTag(ToolTags.TOOLBASETAG, tags);

        stack.setTagCompound(basetag);
    }

    public static NBTTagList getToolRenderMaterialsNBTSafe(ItemStack stack) {
        return getStringTagListSafe(getToolBaseNBTSafe(stack), ToolTags.RENDERMATERIALS);
    }

    public static NBTTagList getToolBaseMaterialsNBTSafe(ItemStack stack) {
        return getStringTagListSafe(getToolBaseNBTSafe(stack), ToolTags.BASEMATERIALS);
    }

    public static NBTTagCompound getToolDataNBTSafe(ItemStack stack) {
        return getTagSafe(getToolBaseNBTSafe(stack), ToolTags.TOOLDATA);
    }

    public static NBTTagCompound getToolOriginDataNBTSafe(ItemStack stack) {
        return getTagSafe(getToolBaseNBTSafe(stack), ToolTags.TOOLDATAORIG);
    }

    // stats
    public static String getCustomName(ItemStack stack) {
        return getToolBaseNBTSafe(stack).getString(ToolTags.CUSTOMNAME);
    }

    public static boolean isBroken(ItemStack stack) {
        return getToolBaseNBTSafe(stack).getBoolean(ToolTags.BROKEN);
    }

    public static int getHarvestLevelStat(ItemStack stack) {
        return getToolDataNBTSafe(stack).getInteger(ToolTags.HARVESTLEVEL);
    }

    public static int getDurabilityStat(ItemStack stack) {
        return getToolDataNBTSafe(stack).getInteger(ToolTags.DURABILITY);
    }

    public static float getMiningSpeed(ItemStack stack) {
        return getToolDataNBTSafe(stack).getFloat(ToolTags.MININGSPEED);
    }

    public static float getAttackStat(ItemStack stack) {
        return getToolDataNBTSafe(stack).getFloat(ToolTags.ATTACK);
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

        if (stack.getItem() instanceof ToolCore) {
            damage = calcCutoffDamage(damage, ((ToolCore) stack.getItem()).damageCutoff());
        }

        return damage;
    }

    public static void breakTool(ItemStack stack, EntityLivingBase entity) {
        NBTTagCompound tag = getToolBaseNBTSafe(stack);
        if (tag == null) {
            setToolBaseNBTSafe(stack, new NBTTagCompound());
        }
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

    public static float calcMiningSpeed(ItemStack stack, Block block, int meta) {
        if (!stack.hasTagCompound()) {
            return 1F;
        } else if (isBroken(stack)) {
            return 0.3F;
        } else if (!canHarvest(stack, block, meta)) {
            return 1.0F;
        }
        float speed = getMiningSpeed(stack);

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
}
