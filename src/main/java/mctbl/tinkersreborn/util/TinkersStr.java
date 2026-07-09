package mctbl.tinkersreborn.util;

import static mctbl.tinkersreborn.util.TinkersRebornUtils.translate;

import mctbl.tinkersreborn.TinkersReborn;

public enum TinkersStr {

    // tooltips
    holdShift("tooltip.holdShift"),
    broken("tooltip.broken"),
    tooNamePattern("tooltip.nameformat"),
    goldenHeadToolToip1("goldenhead.tooltip1"),
    goldenHeadToolToip2("goldenhead.tooltip2"),
    tankToolToip1("tank.tooltip1"),
    tankToolToip2("tank.tooltip2"),
    tankToolToip3("tank.tooltip3"),
    modifierToolTip("tooltip.modifiers"),
    patternToolTip("tooltip.pattern"),

    // stats
    headStatsName("stat.head.name"),
    bowStatsName("stat.bow.name"),
    extraStatsName("stat.extra.name"),
    fletchingStatsName("stat.fletching.name"),
    handleStatsName("stat.handle.name"),
    projectileStatsName("stat.projectile.name"),
    shaftStatsName("stat.shaft.name"),
    stringStatsName("stat.string.name"),

    // gui
    errorTitle("gui.error"),
    warningTitle("gui.warning"),
    partCrafterTitle("gui.partcrafter.title"),
    guiCelsius("gui.general.temperature.celsius"),
    guiKelvin("gui.general.temperature.kelvin"),

    toolStationRepairTitle("gui.toolstation.repair.title"),
    toolStationRepairDesc("gui.toolstation.repair.desc"),
    toolStationComponentTitle("gui.toolstation.components.title"),
    toolStationTraitTitle("gui.toolstation.traits.title"),
    toolStationNoTrait("gui.toolstation.noTraits"),
    partCrafterInfo("gui.partcrafter.desc"),
    partCrafterMaterialValue("gui.partbuilder.material_value"),
    partCrafterMaterialCantMakePart("gui.partbuilder.material_cant_make"),

    smelteryFuelHeat("gui.smeltery.fuel.heat"),
    smelteryFuelEmpty("gui.smeltery.fuel.empty"),

    smtleteryLiquidmB("gui.smeltery.liquid.millibucket"),
    smtleteryLiquidB("gui.smeltery.liquid.bucket"),
    smtleteryLiquidKB("gui.smeltery.liquid.kilobucket"),
    smtleteryLiquidIngot("gui.smeltery.liquid.ingot"),

    smtleteryNoRecipe("gui.smeltery.progress.no_recipe"),
    smtleteryNoFuel("gui.smeltery.progress.no_fuel"),
    smtleteryNoHeat("gui.smeltery.progress.no_heat"),
    smtleteryNoSpace("gui.smeltery.progress.no_space"),

    smtleteryCapacity("gui.smeltery.capacity"),
    smtleteryCapacityAvailable("gui.smeltery.capacity_available"),
    smtleteryCapacityUsed("gui.smeltery.capacity_used"),

    // general
    durability("durability"),
    durabilityDesc("durability.desc"),
    miningSpeed("miningspeed"),
    miningSpeedDesc("miningspeed.desc"),
    attack("attack"),
    attackDesc("attack.desc"),
    harvestLevel("harvestlevel"),
    harvestLevelDesc("harvestlevel.desc"),

    ;

    static String modPrefix = TinkersReborn.MODID;
    String localization;

    TinkersStr(String localization) {
        this.localization = localization;
    }

    public String getUnlocalizationStr() {
        return modPrefix + "." + this.localization;
    }

    public String getLocalizationStr() {
        return translate(this.getUnlocalizationStr());
    }

    @Override
    public String toString() {
        return this.getLocalizationStr();
    }
}
