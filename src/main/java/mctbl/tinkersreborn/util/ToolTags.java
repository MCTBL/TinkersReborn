package mctbl.tinkersreborn.util;

/**
 * Holds all the NBT Tag keys used by the standard tinkers stuff.
 */
public final class ToolTags {

    public static final String TOOLBASETAG = "TinkersRebornTool";

    /** Contains the materials of the parts the tool was built from */
    public static final String BASEMATERIALS = "Materials";
    public static final String RENDERMATERIALS = "RenderMaterials";
    /** Contains all the applied modifiers */
    public static final String MODIFIERS = "Modifiers";
    public static final String FREEMODIFIERS = "FreeModifiers";
    public static final String USEDMODIFIERS = "UsedModifiers";

    /** The tag that contains all the actual calculated runtime-information of the tools */
    public static final String TOOLDATA = "Stats";
    public static final String TOOLDATAORIG = "StatsOriginal";
    /**
     * Extra-data that is specific to this Itemstack and is used to build the item. An example would be if a special
     * pickaxe had 100 more durability, it'd be stored in here.
     */
    public static final String TOOLDATAEXTRA = "Special";

    public static final String TYPEMODIFIERS = "modifier";
    public static final String TYPETRAITS = "trait";

    // tools
    public static final String DURABILITY = "Durability";
    public static final String ATTACK = "Attack";
    public static final String ATTACKSPEEDMULTIPLIER = "AttackSpeedMultiplier";
    public static final String MININGSPEED = "MiningSpeed";
    public static final String HARVESTLEVEL = "HarvestLevel";

    // bows
    public static final String DRAWSPEED = "DrawSpeed";
    public static final String RANGE = "Range";
    public static final String PROJECTILE_BONUS_DAMAGE = "ProjectileBonusDamage";

    // projectile
    public static final String ACCURACY = "Accuracy";

    // misc. tool info
    public static final String BROKEN = "Broken";
    public static final String UNBREAKABLE = "Unbreakable";
    public static final String CUSTOMNAME = "CustomName";

    // Extra
    public static final String REPAIR_COUNT = "RepairCount";

    public static final String ENCHANT_EFFECT = "EnchantEffect";
    public static final String RESET_FLAG = "ResetFlag";

    public static final String NO_RENAME = "NoRename";

    // modifier detail
    public static final String IDENTIFIER = "identifier";
    public static final String TYPE = "type";
    public static final String COLOR = "color";
    public static final String LEVEL = "level";
    public static final String EXTRAINFO = "extraInfo";
    public static final String STATUS = "status";
    public static final String CURRENT = "current";
    public static final String MAX = "max";
    public static final String MODIFIERUSED = "modifierUsed";

    public static final String INTERNALPREFIX = "_internal_render";

    private ToolTags() {}

}
