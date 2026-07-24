package mctbl.tinkersreborn.library.tools;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import mctbl.tinkersreborn.tools.materials.BowMaterialStats;
import mctbl.tinkersreborn.tools.materials.StringMaterialStats;
import mctbl.tinkersreborn.util.ToolTags;
import mctbl.tinkersreborn.util.ToolTagsHelper;

public class ProjectileLauncherNBT extends ToolNBT {

    public float drawSpeed;
    public float range;
    public float bonusDamage;

    public ProjectileLauncherNBT() {
        drawSpeed = 1f;
        range = 1f;
        bonusDamage = 0f;
    }

    public ProjectileLauncherNBT(NBTTagCompound tag) {
        super(tag);
    }

    public ProjectileLauncherNBT limb(BowMaterialStats... bowlimbs) {
        drawSpeed = 0;
        range = 0;
        bonusDamage = 0;

        for (BowMaterialStats limb : bowlimbs) {
            if (limb != null) {
                drawSpeed += limb.drawspeed;
                range += limb.range;
                bonusDamage += limb.bonusDamage;
            }
        }

        drawSpeed /= (float) bowlimbs.length;
        range /= (float) bowlimbs.length;
        bonusDamage /= (float) bowlimbs.length;

        drawSpeed = Math.max(0.001f, drawSpeed);
        range = Math.max(0.001f, range);

        return this;
    }

    public ProjectileLauncherNBT bowstring(StringMaterialStats... bowstrings) {
        float modifier = 0f;

        for (StringMaterialStats bowstring : bowstrings) {
            if (bowstring != null) {
                modifier += bowstring.modifier;
            }
        }

        modifier /= (float) bowstrings.length;
        this.durability = Math.round((float) this.durability * modifier);
        this.durability = Math.max(1, this.durability);

        return this;
    }

    @Override
    public void read(NBTTagCompound tag) {
        super.read(tag);
        this.drawSpeed = tag.getFloat(ToolTags.DRAWSPEED);
        this.range = tag.getFloat(ToolTags.RANGE);
        this.bonusDamage = tag.getFloat(ToolTags.PROJECTILE_BONUS_DAMAGE);
    }

    @Override
    public void write(NBTTagCompound tag) {
        super.write(tag);
        tag.setFloat(ToolTags.DRAWSPEED, drawSpeed);
        tag.setFloat(ToolTags.RANGE, range);
        tag.setFloat(ToolTags.PROJECTILE_BONUS_DAMAGE, bonusDamage);
    }

    public static ProjectileLauncherNBT from(ItemStack itemStack) {
        return new ProjectileLauncherNBT(ToolTagsHelper.getToolDataNBTSafe(itemStack));
    }
}
