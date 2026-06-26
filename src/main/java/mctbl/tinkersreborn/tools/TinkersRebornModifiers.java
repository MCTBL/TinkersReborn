package mctbl.tinkersreborn.tools;

import java.util.List;

import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import mctbl.tinkersreborn.library.tools.modifiers.AbstractModifier;
import mctbl.tinkersreborn.library.utils.RecipeMatch;
import mctbl.tinkersreborn.tools.modifiers.ModAntiMonsterType;
import mctbl.tinkersreborn.tools.modifiers.ModBeheading;
import mctbl.tinkersreborn.tools.modifiers.ModBlasting;
import mctbl.tinkersreborn.tools.modifiers.ModDiamond;
import mctbl.tinkersreborn.tools.modifiers.ModEmerald;

public class TinkersRebornModifiers {

    public static final TinkersRebornModifiers INSTANCE = new TinkersRebornModifiers();

    public static AbstractModifier modBaneOfArthopods;
    public static AbstractModifier modBeheading;
    public static AbstractModifier modBlasting;
    public static AbstractModifier modDiamond;
    public static AbstractModifier modEmerald;
    public static AbstractModifier modFiery;
    public static AbstractModifier modFins;
    public static AbstractModifier modGlowing;
    public static AbstractModifier modHaste;
    public static AbstractModifier modHarvestWidth;
    public static AbstractModifier modHarvestHeight;
    public static AbstractModifier modKnockback;
    public static AbstractModifier modLuck;
    public static AbstractModifier modMendingMoss;
    public static AbstractModifier modNecrotic;
    public static AbstractModifier modReinforced;
    public static AbstractModifier modSharpness;
    public static AbstractModifier modShulking;
    public static AbstractModifier modSilktouch;
    public static AbstractModifier modAutosmelt;
    public static AbstractModifier modWebbed;
    public static AbstractModifier modSmite;
    public static AbstractModifier modSoulbound;
    public static AbstractModifier modEndearment;
    public static AbstractModifier modIncognito;
    public static AbstractModifier modCreative;

    public static List<AbstractModifier> fortifyMods;
    public static List<AbstractModifier> extraTraitMods;

    public void preInit(FMLPreInitializationEvent e) {}

    public void init(FMLInitializationEvent e) {
        ItemStack tnt = new ItemStack(Blocks.tnt);
        ItemStack glowstoneDust = new ItemStack(Items.glowstone_dust);

        modBaneOfArthopods = new ModAntiMonsterType(
            "bane_of_arthopods",
            0x61ba49,
            5,
            24,
            EnumCreatureAttribute.ARTHROPOD);
        modBaneOfArthopods.addItem(Items.fermented_spider_eye);

        modBeheading = new ModBeheading();
        modBeheading.addRecipeMatch(
            new RecipeMatch.ItemCombination(1, new ItemStack(Items.ender_pearl), new ItemStack(Blocks.obsidian)));

        modBlasting = new ModBlasting();
        modBlasting.addRecipeMatch(new RecipeMatch.ItemCombination(1, tnt, tnt, tnt));

        modDiamond = new ModDiamond();
        modDiamond.addItem("gemDiamond");

        modEmerald = new ModEmerald();
        modEmerald.addItem("gemEmerald");
    }

    public void postInit(FMLPostInitializationEvent e) {
        // registerMobHeadDrops()
    }
}
