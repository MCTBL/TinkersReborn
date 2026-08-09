package mctbl.tinkersreborn.util;

import static mctbl.tinkersreborn.util.TinkersRebornUtils.translate;

import mctbl.tinkersreborn.TinkersReborn;

public enum TinkersStr {

    // tooltips
    holdShift("tooltip.holdShift"),
    broken("tooltip.broken"),
    empty("tooltip.empty"),
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
    ammoName("stat.ammo.name"),

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
    mattockAxeHarvestLevelDesc("mattock.axelevel"),
    mattockShovelHarvestLevelDesc("mattock.shovellevel"),

    tooltipMiningxp("tooltip.level.miningxp"),
    tooltipBoosted("tooltip.level.boosted"),
    tooltipSkillLevel("tooltip.level.skilllevel"),
    tooltipSkillxp("tooltip.level.skillxp"),

    accessory("item.accessory"),
    crafting("item.crafting"),
    canister("canister"),
    canisterRed1("canister.red1"),
    canisterRed2("canister.red2"),
    canisterYellow1("canister.yellow1"),
    canisterYellow2("canister.yellow2"),
    canisterGreen1("canister.green1"),
    canisterGreen2("canister.green2"),

    // nerf vanilla
    uselessTool1("tooltip.uselessTool1"),
    uselessTool2("tooltip.uselessTool2"),
    uselessBow1("tooltip.uselessBow1"),
    uselessHoe1("tooltip.uselessHoe1"),
    uselessWeapon1("tooltip.uselessWeapon1"),

    // foods
    strangefood1("tooltip.strangefood1"),
    strangefood2("tooltip.strangefood2"),
    strangefood3("tooltip.strangefood3"),
    strangefood4("tooltip.strangefood4"),
    strangefood5("tooltip.strangefood5"),
    strangefood6("tooltip.strangefood6"),
    strangefood7("tooltip.strangefood7"),

    // NEI
    neiDryingrack("nei.dryingrack"),
    neiDryingrackDuration("nei.dryingrack.duration"),
    neiMelting("nei.melting"),
    neiAlloying("nei.alloying"),
    neiCastingtable("nei.castingtable"),
    neiCastingbasin("nei.castingbasin"),
    neiCoolDownDuration("nei.casting.cooldown.duration"),

    // Waila
    wailaEmpty("waila.empty"),
    wailaLiquidtag("waila.liquidtag"),
    wailaAmounttag("waila.amounttag"),
    wailaProgress("waila.progress"),
    wailaContains("waila.contains"),
    wailaSubtanks("waila.subtanks"),
    wailaInvalidstructure("waila.invalidstructure"),
    wailaDrying("waila.drying"),
    wailaDryingProgress("waila.dryingProgress"),;

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
