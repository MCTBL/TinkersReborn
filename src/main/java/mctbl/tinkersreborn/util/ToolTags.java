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
    public static final String BASEMODIFIERS = "Modifiers";

    /** The tag that contains all the actual calculated runtime-information of the tools */
    public static final String TOOLDATA = "Stats";
    public static final String TOOLDATAORIG = "StatsOriginal";

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
    public static final String FREE_MODIFIERS = "FreeModifiers";
    public static final String BROKEN = "Broken";

    // Extra
    public static final String REPAIR_COUNT = "RepairCount";

    public static final String ENCHANT_EFFECT = "EnchantEffect";
    public static final String RESET_FLAG = "ResetFlag";

    public static final String NO_RENAME = "NoRename";

    // Render
    public static final String RENDERHEAD = "RenderHead";
    public static final String RENDERHANDLE = "RenderHandle";
    public static final String RENDERACCESSORY = "RenderAccessory";
    public static final String RENDEREXTRA = "RenderExtra";

    private ToolTags() {}

}
