package mctbl.tinkersreborn.tools.items;

import net.minecraft.item.ItemStack;

import mctbl.tinkersreborn.library.TinkersRebornRegistry;
import mctbl.tinkersreborn.library.items.CraftingItem;

public class Pattern extends CraftingItem {

    private final static String[] patternName = new String[] { "pattern_blank", "cast_blank", "cast_ingot",
        "cast_nugget", "cast_gem", "cast_rod", "cast_binding", "cast_pickaxe", "cast_axe", "cast_shovel",
        "cast_swordblade", "cast_largeguard", "cast_mediumguard", "cast_fullguard", "cast_crossbar", "cast_frypan",
        "cast_sign", "cast_chisel", "cast_knifeblade", "cast_arrowhead", "cast_bobber", "cast_bowstring",
        "cast_fletching",

        "cast_largerod", "cast_toughbinding", "cast_hammerhead", "cast_broadaxe", "cast_excavator", "cast_scythe",
        "cast_largeblade", "cast_largeplate",

        "cast_chestplate", "cast_helmet", "cast_leggings", "cast_boots", };

    public Pattern() {
        super(patternName, patternName, "materials/", TinkersRebornRegistry.miscTab);
        this.setUnlocalizedName("tinkersreborn.Pattern");
        this.hasSubtypes = false;
    }

    @Override
    public String getUnlocalizedName() {
        return "tinkersreborn.Pattern";
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        return this.getUnlocalizedName() + "." + patternName[stack.getItemDamage()];
    }

}
