package mctbl.tinkersreborn.library.tools.modifiers;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;

import mctbl.tinkersreborn.TinkersReborn;
import mctbl.tinkersreborn.library.tools.IModifier;
import mctbl.tinkersreborn.library.tools.ITrait;
import mctbl.tinkersreborn.util.ColorUtil;
import mctbl.tinkersreborn.util.ToolTags;

/**
 * Represents the NBT data saved for a modifier.
 */
public class ModifierNBT {

    public String identifier;
    public String type;
    public int color;
    public int level;
    public String extraInfo;

    public ModifierNBT() {
        identifier = "";
        color = 0xffffff;
        level = 0;
    }

    public ModifierNBT(IModifier modifier) {
        this.identifier = modifier.getIdentifier();
        this.level = 0;
        this.color = ColorUtil.enumChatFormattingToColor(EnumChatFormatting.GRAY);
        this.type = "modifier";
    }

    public ModifierNBT(ITrait trait) {
        this.identifier = trait.getIdentifier();
        this.level = 0;
        this.color = ColorUtil.enumChatFormattingToColor(EnumChatFormatting.GRAY);
        this.type = "trait";
    }

    public ModifierNBT(NBTTagCompound tag) {
        this();
        read(tag);
    }

    public static ModifierNBT readTag(NBTTagCompound tag) {
        ModifierNBT data = new ModifierNBT();
        if (tag != null) {
            data.read(tag);
        }

        return data;
    }

    public void read(NBTTagCompound tag) {
        identifier = tag.getString(ToolTags.IDENTIFIER);
        type = tag.getString(ToolTags.TYPE);
        color = tag.getInteger(ToolTags.COLOR);
        level = tag.getInteger(ToolTags.LEVEL);
        extraInfo = tag.getString(ToolTags.EXTRAINFO);
    }

    public void write(NBTTagCompound tag) {
        tag.setString(ToolTags.IDENTIFIER, identifier);
        tag.setString(ToolTags.TYPE, type);
        tag.setInteger(ToolTags.COLOR, color);
        if (level > 0) {
            tag.setInteger(ToolTags.LEVEL, level);
        } else {
            tag.removeTag(ToolTags.LEVEL);
        }
        if (extraInfo != null && !extraInfo.isEmpty()) {
            tag.setString(ToolTags.EXTRAINFO, extraInfo);
        }
    }

    public String getColorString() {
        return ColorUtil.encodeColor(color);
    }

    public static <T extends ModifierNBT> T readTag(NBTTagCompound tag, Class<T> clazz) {
        try {
            T data = clazz.newInstance();
            data.read(tag);
            return data;
        } catch (ReflectiveOperationException e) {
            TinkersReborn.LOG.error(e);
            return null;
        }
    }

    public static IntegerNBT readInteger(NBTTagCompound tag) {
        return ModifierNBT.readTag(tag, IntegerNBT.class);
    }

    public static BooleanNBT readBoolean(NBTTagCompound tag) {
        return ModifierNBT.readTag(tag, BooleanNBT.class);
    }

    /**
     * Single boolean value
     */
    public static class BooleanNBT extends ModifierNBT {

        public boolean status;

        public BooleanNBT() {}

        public BooleanNBT(IModifier modifier, boolean status) {
            super(modifier);
            this.status = status;
        }

        public BooleanNBT(ITrait trait, boolean status) {
            super(trait);
            this.status = status;
        }

        @Override
        public void write(NBTTagCompound tag) {
            super.write(tag);
            tag.setBoolean(ToolTags.STATUS, status);
        }

        @Override
        public void read(NBTTagCompound tag) {
            super.read(tag);
            status = tag.getBoolean(ToolTags.STATUS);
        }
    }

    /**
     * Data can be applied multiple times up to a maximum.
     */
    public static class IntegerNBT extends ModifierNBT {

        public int current;
        public int max;

        public IntegerNBT() {}

        public IntegerNBT(IModifier modifier, int current, int max) {
            super(modifier);
            this.current = current;
            this.max = max;

            this.extraInfo = calcInfo();
        }

        public IntegerNBT(ITrait trait, int current, int max) {
            super(trait);
            this.current = current;
            this.max = max;

            this.extraInfo = calcInfo();
        }

        @Override
        public void write(NBTTagCompound tag) {
            super.write(tag);
            tag.setInteger(ToolTags.CURRENT, current);
            tag.setInteger(ToolTags.MAX, max);
            
            extraInfo = calcInfo();
        }

        @Override
        public void read(NBTTagCompound tag) {
            super.read(tag);
            current = tag.getInteger(ToolTags.CURRENT);
            max = tag.getInteger(ToolTags.MAX);

            extraInfo = calcInfo();
        }

        public String calcInfo() {
            if (max > 0) {
                return String.format("%d / %d", current, max);
            }

            return current > 0 ? String.valueOf(current) : "";
        }
    }
}
