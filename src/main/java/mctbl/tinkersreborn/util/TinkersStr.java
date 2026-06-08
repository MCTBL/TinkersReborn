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

    // gui
    partCrafterTitle("gui.partcrafter.title"),
    toolForgeTitle("gui.toolforge.title"),
    toolForgeDesc("gui.toolforge.desc"),

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
