package mctbl.tinkersreborn.library.tools.traits;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;

import mctbl.tinkersreborn.library.tools.modifiers.ModifierNBT;
import mctbl.tinkersreborn.util.ToolTagsHelper;

/**
 * Base class for tools that progressively gain/award stats.
 * The modifier persists 2 different stat-data on the tool:
 * - A 'pool' of stats to award
 * - A 'bonus' of already awarded stats
 *
 * The modifier reapplies the 'bonus' stats on application.
 * The pool is not touched inheritly but only provided for the logic of the deriving trait.
 */
public abstract class TraitProgressiveStats extends AbstractTrait {

    protected final static String pool_key = "pool"; // Key to the tag that contains the free unassigned
    protected final static String applied_key = "bonus"; // Key to the tag that contains the already applied bonus stats

    public TraitProgressiveStats(String identifier, EnumChatFormatting color) {
        super(identifier, color);
    }

    public TraitProgressiveStats(String identifier, int color) {
        super(identifier, color);
    }

    /* Modifier management */

    @Override
    public void applyEffect(NBTTagCompound rootCompound, NBTTagCompound modifierTag) {
        super.applyEffect(rootCompound, modifierTag);
        // called on tool loading only
        // we just apply the saved bonus stats
        StatNBT bonus = getBonus(rootCompound);

        ToolTagsHelper
            .setDurabilityStat(rootCompound, ToolTagsHelper.getDurabilityStat(rootCompound) + bonus.durability);
        ToolTagsHelper.setMiningSpeed(rootCompound, ToolTagsHelper.getMiningSpeed(rootCompound) + bonus.speed);
        ToolTagsHelper.setAttackStat(rootCompound, ToolTagsHelper.getAttackStat(rootCompound) + bonus.attack);

    }

    protected boolean hasPool(NBTTagCompound root) {
        NBTTagCompound toolExtraData = ToolTagsHelper.getToolExtraNBTSafe(root);
        return toolExtraData.hasKey(identifier) && toolExtraData.getCompoundTag(identifier)
            .hasKey(pool_key);
    }

    protected StatNBT getPool(NBTTagCompound root) {
        return getStats(root, pool_key);
    }

    protected void setPool(NBTTagCompound root, StatNBT data) {
        setStats(root, data, pool_key);
    }

    protected StatNBT getBonus(NBTTagCompound root) {
        return getStats(root, applied_key);
    }

    protected void setBonus(NBTTagCompound root, StatNBT data) {
        setStats(root, data, applied_key);
    }

    protected StatNBT getStats(NBTTagCompound root, String key) {
        NBTTagCompound toolExtraData = ToolTagsHelper.getToolExtraNBTSafe(root);
        return ModifierNBT.readTag(
            toolExtraData.getCompoundTag(identifier)
                .getCompoundTag(key),
            StatNBT.class);
    }

    protected void setStats(NBTTagCompound root, StatNBT data, String key) {
        NBTTagCompound extra = ToolTagsHelper.getToolExtraNBTSafe(root);
        ToolTagsHelper.setToolExtraNBTSafe(root, extra);

        NBTTagCompound extraTagCompound = null;
        if (extra.hasKey(identifier)) {
            extraTagCompound = extra.getCompoundTag(identifier);
        } else {
            extraTagCompound = new NBTTagCompound();
            extra.setTag(identifier, extraTagCompound);
        }

        NBTTagCompound dataCompound = new NBTTagCompound();
        data.write(dataCompound);

        extraTagCompound.setTag(key, dataCompound);
    }

    protected boolean playerIsBreakingBlock(Entity entity) {
        return false;
    }

    public static class StatNBT extends ModifierNBT {

        // statpool
        public int durability;
        public float attack;
        public float speed;

        @Override
        public void read(NBTTagCompound tag) {
            // super.read(tag);
            durability = tag.getInteger("durability");
            attack = tag.getFloat("attack");
            speed = tag.getFloat("speed");
        }

        @Override
        public void write(NBTTagCompound tag) {
            // super.write(tag);
            tag.setInteger("durability", durability);
            tag.setFloat("attack", attack);
            tag.setFloat("speed", speed);
        }
    }
}
