package mctbl.tinkersreborn.tools.items;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import com.google.common.collect.Lists;

import mctbl.tinkersreborn.library.materials.MaterialStatusType;
import mctbl.tinkersreborn.library.materials.TinkersRebornMaterial;
import mctbl.tinkersreborn.tools.materials.HeadMaterialStats;
import mctbl.tinkersreborn.util.TinkersRebornUtils;

public class SharpeningKit extends TinkersRebornToolPart {

    public SharpeningKit(String texture, String name) {
        super(texture, name, TinkersRebornMaterial.VALUE_Ingot * 2, MaterialStatusType.HEAD);
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> tooltip, boolean flagIn) {
        tooltip.addAll(getTooltips(TinkersRebornUtils.translate("item.tconstruct.sharpening_kit.tooltip")));

        TinkersRebornMaterial material = getMaterial(stack);
        HeadMaterialStats stats = material.getStats(MaterialStatusType.HEAD);
        if (stats != null) {
            tooltip.add(HeadMaterialStats.formatHarvestLevel(stats.harvestLevel));
        }

    }

    public static List<String> getTooltips(String text) {
        List<String> list = Lists.newLinkedList();
        if (text == null) return list;
        int j = 0;
        int k;
        while ((k = text.indexOf("\\n", j)) >= 0) {
            list.add(text.substring(j, k));
            j = k + 2;
        }

        list.add(text.substring(j, text.length()));

        return list;
    }
}
