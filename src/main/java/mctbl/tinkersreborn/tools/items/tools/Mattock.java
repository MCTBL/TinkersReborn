package mctbl.tinkersreborn.tools.items.tools;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StringUtils;
import net.minecraft.world.World;

import mctbl.tinkersreborn.TinkersRebornConfig;
import mctbl.tinkersreborn.library.materials.MaterialStatusType;
import mctbl.tinkersreborn.library.materials.TinkersRebornMaterial;
import mctbl.tinkersreborn.library.tools.HarvestTool;
import mctbl.tinkersreborn.library.tools.ToolNBT;
import mctbl.tinkersreborn.tools.TinkersRebornTools;
import mctbl.tinkersreborn.tools.gui.ToolBuildGuiInfo;
import mctbl.tinkersreborn.tools.materials.HandleMaterialStats;
import mctbl.tinkersreborn.tools.materials.HeadMaterialStats;
import mctbl.tinkersreborn.util.ColorUtil;
import mctbl.tinkersreborn.util.TinkersStr;
import mctbl.tinkersreborn.util.ToolTags;
import mctbl.tinkersreborn.util.ToolTagsHelper;

public class Mattock extends HarvestTool {

    public Mattock() {
        super("Mattock", 3);

        // set the toolclass, actual harvestlevel is done by the overridden callback
        this.setHarvestLevel("axe", 0);
        this.setHarvestLevel("shovel", 0);

        this.componentsParts
            .add(new ToolPartRecord(TinkersRebornTools.axeHead, MaterialStatusType.HEAD, "_mattock_head"));
        this.componentsParts
            .add(new ToolPartRecord(TinkersRebornTools.shovelHead, MaterialStatusType.HEAD, "_mattock_back"));
        this.componentsParts
            .add(new ToolPartRecord(TinkersRebornTools.rod, MaterialStatusType.HANDLE, "_mattock_handle"));
    }

    @Override
    public boolean isEffective(Block block) {
        return axeEffectiveMaterials.contains(block.getMaterial())
            || shovelEffectiveMaterials.contains(block.getMaterial())
            || axeEffectiveBlocks.contains(block)
            || shovelEffectiveBlocks.contains(block);
    }

    @Override
    public int getHarvestLevel(ItemStack stack, String toolClass) {
        if (StringUtils.isNullOrEmpty(toolClass)) {
            return -1;
        }

        // axe harvestlevel
        if (toolClass.equals("axe")) {
            return getAxeLevel(stack);
        }
        // shovel harvestlevel
        else if (toolClass.equals("shovel")) {
            return getShovelLevel(stack);
        }

        // none of them
        return super.getHarvestLevel(stack, toolClass);
    }

    @Override
    public float miningSpeedModifier() {
        return 0.95F;
    }

    @Override
    public float damagePotential() {
        return 0.9F;
    }

    @Override
    public float knockback() {
        return 1.1F;
    }

    @Override
    public boolean onItemUse(ItemStack toolStack, EntityPlayer player, World world, int x, int y, int z, int side,
        float hitX, float hitY, float hitZ) {
        boolean canHoeUse = Items.diamond_hoe.onItemUse(toolStack, player, world, x, y, z, side, hitX, hitY, hitZ);

        // do tinkers damaging
        if (!world.isRemote && canHoeUse) {
            ToolTagsHelper.damageTool(toolStack, 1, player);
        }
        return canHoeUse;
    }

    @Override
    public List<String> getInformation(ItemStack stack, EntityPlayer player, boolean isTooltip) {
        List<String> list = new LinkedList<>();

        // durability
        // is broken and need detail, for tooltip
        if (ToolTagsHelper.isBroken(stack) && isTooltip) {
            list.add(
                String.format(
                    "%s: %s",
                    HeadMaterialStats.LOC_Durability,
                    ColorUtil.addDarkRed(ColorUtil.addUnderLine(TinkersStr.broken.toString()))));
        } else {
            list.add(
                HeadMaterialStats.formatDurability(
                    ToolTagsHelper.getCurrentDurability(stack),
                    ToolTagsHelper.getMaxDurability(stack)));
        }

        list.add(
            String.format(
                "%s: %s",
                TinkersStr.mattockAxeHarvestLevelDesc,
                HeadMaterialStats.harvestLevel(getAxeLevel(stack))) + EnumChatFormatting.RESET);

        list.add(
            String.format(
                "%s: %s",
                TinkersStr.mattockShovelHarvestLevelDesc,
                HeadMaterialStats.harvestLevel(getShovelLevel(stack))) + EnumChatFormatting.RESET);

        list.add(HeadMaterialStats.formatMiningSpeed(ToolTagsHelper.getMiningSpeedStat(stack)));

        float attack = ToolTagsHelper.getActualAttackDamage(stack, player);
        list.add(HeadMaterialStats.formatAttack(attack));

        int freeModifier = ToolTagsHelper.getModifierSlots(stack) + ToolTagsHelper.getExtraModifier(stack)
            - ToolTagsHelper.getUsedModifiers(stack);
        if (freeModifier > 0) {
            list.add(String.format("%s: %d", TinkersStr.modifierToolTip.toString(), freeModifier));
        }

        if (!isTooltip) {
            list.addAll(getModifierInfo(stack));
        }

        return list;
    }

    @Override
    public ToolNBT buildToolTag(List<TinkersRebornMaterial> materials) {
        MattockToolNBT data = new MattockToolNBT();
        List<HeadMaterialStats> heads = new ArrayList<>();
        List<HandleMaterialStats> handles = new ArrayList<>();

        heads.add(
            materials.get(0)
                .getStats(MaterialStatusType.HEAD));// axe
        heads.add(
            materials.get(1)
                .getStats(MaterialStatusType.HEAD));// shovel
        handles.add(
            materials.get(2)
                .getStats(MaterialStatusType.HANDLE));// handle

        data.head(heads);
        data.handle(handles);

        // 3 free modifiers
        data.modifierSlots = TinkersRebornConfig.defaultModifiers;

        // special harvest levels
        data.axeLevel = heads.get(0).harvestLevel;
        data.shovelLevel = heads.get(1).harvestLevel;

        // base damage!
        data.attack += 3;

        return data;
    }

    @Override
    public ToolBuildGuiInfo getToolBuildGuiInfo() {
        if (this.toolBuildGuiInfo == null) {
            this.toolBuildGuiInfo = new ToolBuildGuiInfo(this).addSlotPosition(33 - 2, 42 - 20) // axe head
                .addSlotPosition(33 + 18, 42 - 8) // shovel head
                .addSlotPosition(33 - 11, 42 + 11); // rod
        }
        return this.toolBuildGuiInfo;
    }

    protected int getAxeLevel(ItemStack stack) {
        return new MattockToolNBT(ToolTagsHelper.getToolDataNBTSafe(stack)).axeLevel;
    }

    protected int getShovelLevel(ItemStack stack) {
        return new MattockToolNBT(ToolTagsHelper.getToolDataNBTSafe(stack)).shovelLevel;
    }

    public static class MattockToolNBT extends ToolNBT {

        private static final String TAG_AxeLevel = ToolTags.HARVESTLEVEL + "Axe";
        private static final String TAG_ShovelLevel = ToolTags.HARVESTLEVEL + "Shovel";

        public int axeLevel;
        public int shovelLevel;

        public MattockToolNBT() {}

        public MattockToolNBT(NBTTagCompound tag) {
            super(tag);
        }

        @Override
        public void read(NBTTagCompound tag) {
            super.read(tag);
            axeLevel = tag.getInteger(TAG_AxeLevel);
            shovelLevel = tag.getInteger(TAG_ShovelLevel);
        }

        @Override
        public void write(NBTTagCompound tag) {
            super.write(tag);
            tag.setInteger(TAG_AxeLevel, axeLevel);
            tag.setInteger(TAG_ShovelLevel, shovelLevel);
        }
    }

}
