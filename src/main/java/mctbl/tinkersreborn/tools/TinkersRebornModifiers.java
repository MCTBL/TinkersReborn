package mctbl.tinkersreborn.tools;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import mctbl.tinkersreborn.common.TinkersRebornGeneral;
import mctbl.tinkersreborn.library.TinkersRebornRegistry;
import mctbl.tinkersreborn.library.materials.MaterialStatusType;
import mctbl.tinkersreborn.library.materials.TinkersRebornMaterial;
import mctbl.tinkersreborn.library.tools.modifiers.AbstractModifier;
import mctbl.tinkersreborn.library.utils.RecipeMatch;
import mctbl.tinkersreborn.tools.modifiers.ModAntiMonsterType;
import mctbl.tinkersreborn.tools.modifiers.ModAutosmelt;
import mctbl.tinkersreborn.tools.modifiers.ModBeheading;
import mctbl.tinkersreborn.tools.modifiers.ModBlasting;
import mctbl.tinkersreborn.tools.modifiers.ModCreative;
import mctbl.tinkersreborn.tools.modifiers.ModDiamond;
import mctbl.tinkersreborn.tools.modifiers.ModEmerald;
import mctbl.tinkersreborn.tools.modifiers.ModExtraModifier;
import mctbl.tinkersreborn.tools.modifiers.ModFiery;
import mctbl.tinkersreborn.tools.modifiers.ModFortify;
import mctbl.tinkersreborn.tools.modifiers.ModHaste;
import mctbl.tinkersreborn.tools.modifiers.ModKnockback;
import mctbl.tinkersreborn.tools.modifiers.ModLuck;
import mctbl.tinkersreborn.tools.modifiers.ModMendingMoss;
import mctbl.tinkersreborn.tools.modifiers.ModNecrotic;
import mctbl.tinkersreborn.tools.modifiers.ModReinforced;
import mctbl.tinkersreborn.tools.modifiers.ModSharpness;
import mctbl.tinkersreborn.tools.modifiers.ModSilktouch;
import mctbl.tinkersreborn.tools.modifiers.ModWebbed;

public class TinkersRebornModifiers {

    public static final TinkersRebornModifiers INSTANCE = new TinkersRebornModifiers();

    public static AbstractModifier modBaneOfArthopods;
    public static AbstractModifier modBeheading;
    public static AbstractModifier modBlasting;
    public static AbstractModifier modDiamond;
    public static AbstractModifier modEmerald;
    public static AbstractModifier modFiery;
    // public static AbstractModifier modFins;
    // public static AbstractModifier modGlowing;
    public static AbstractModifier modHaste;
    // public static AbstractModifier modHarvestWidth;
    // public static AbstractModifier modHarvestHeight;
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
            0x61BA49,
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

        modFiery = new ModFiery();
        modFiery.addItem(Items.blaze_powder);

        // modFins = new ModFins();
        // modFins.addItem("fish", 2, 1);

        // modGlowing = registerModifier(new ModGlowing());
        // modGlowing.addRecipeMatch(new RecipeMatch.ItemCombination(1, glowstoneDust, new ItemStack(Items.ENDER_EYE),
        // glowstoneDust));

        modHaste = new ModHaste(50);
        modHaste.addItem("dustRedstone");
        modHaste.addItem("blockRedstone", 1, 9);

        // modHarvestWidth = registerModifier(new ModHarvestSize("width"));
        // modHarvestWidth.addItem(TinkerCommons.matExpanderW, 1, 1);
        //
        // modHarvestHeight = registerModifier(new ModHarvestSize("height"));
        // modHarvestHeight.addItem(TinkerCommons.matExpanderH, 1, 1);

        modKnockback = new ModKnockback();
        modKnockback.addItem(Blocks.piston, 1);
        modKnockback.addItem(Blocks.sticky_piston, 1);

        modLuck = new ModLuck();
        modLuck.addItem("gemLapis");
        modLuck.addItem("blockLapis", 1, 9);

        modMendingMoss = new ModMendingMoss();
        modMendingMoss.addItem(TinkersRebornTools.mossball, 1, 1);

        modNecrotic = new ModNecrotic();
        modNecrotic.addItem("boneWithered");

        modReinforced = new ModReinforced();
        modReinforced.addItem(TinkersRebornTools.reinforcement, 1, 1);

        modSharpness = new ModSharpness(72);
        modSharpness.addItem("gemQuartz");
        modSharpness.addItem("blockQuartz", 1, 4);

        // modShulking = new ModShulking();
        // modShulking.addItem(Items.CHORUS_FRUIT_POPPED);

        modSilktouch = new ModSilktouch();
        modSilktouch.addItem(TinkersRebornTools.silkyJewel, 1, 1);

        modAutosmelt = new ModAutosmelt();
        modAutosmelt.addItem(TinkersRebornTools.lavaCrystal, 1, 1);

        modWebbed = new ModWebbed();
        modWebbed.addItem(Blocks.web, 1);

        modSmite = new ModAntiMonsterType("smite", 0xE8D500, 5, 24, EnumCreatureAttribute.UNDEAD);
        modSmite.addItem(new ItemStack(TinkersRebornGeneral.consecratedSoil), 1, 1);

        modEndearment = new ModExtraModifier();
        modEndearment.addItem(new ItemStack(Items.skull, 1, 5), 1, 1);

        // modIncognito = registerModifier(new ModIncognito());
        // modIncognito.addItem(new ItemStack(Blocks.SPONGE, 1, 1), 1, 1);

        modCreative = new ModCreative();
        modCreative.addItem(TinkersRebornTools.creativeModifier, 1, 1);

    }

    public void postInit(FMLPostInitializationEvent e) {
        // registerMobHeadDrops(); // TODO
        registerFortifyModifiers();
        // registerFortifyModifiers(); // TODO
    }

    private void registerFortifyModifiers() {
        fortifyMods = new ArrayList<>();
        for (TinkersRebornMaterial mat : TinkersRebornRegistry.allMaterialsList) {
            if (mat.hasStats(MaterialStatusType.HEAD)) {
                fortifyMods.add(new ModFortify(mat));
            }
        }
    }
}
