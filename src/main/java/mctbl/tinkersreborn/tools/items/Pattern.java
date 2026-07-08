package mctbl.tinkersreborn.tools.items;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mctbl.tinkersreborn.library.TinkersRebornRegistry;
import mctbl.tinkersreborn.library.items.IPattern;
import mctbl.tinkersreborn.library.materials.TinkersRebornMaterial;
import mctbl.tinkersreborn.tools.TinkersRebornTools;
import mctbl.tinkersreborn.util.TinkersRebornUtils;
import mctbl.tinkersreborn.util.ToolTags;
import mctbl.tinkersreborn.util.ToolTagsHelper;

public class Pattern extends Item implements IPattern {

    private final List<String> patternType;

    private final Map<String, IIcon> iconsMap;

    public final static String CAST_BLANK = "cast_blank";
    private IIcon CAST_BLANK_ICON = null;
    public final static String PATTERN_BLANK = "pattern_blank";
    private IIcon PATTERN_BLANK_ICON = null;
    public final static String CAST_INGOT = "cast_ingot";
    private IIcon CAST_INGOT_ICON = null;
    public final static String CAST_GEM = "cast_gem";
    private IIcon CAST_GEM_ICON = null;
    public final static String CAST_NUGGET = "cast_nugget";
    private IIcon CAST_NUGGET_ICON = null;

    public Pattern() {
        super();
        this.setCreativeTab(TinkersRebornRegistry.miscTab);
        // patternName, patternName, "materials/",
        this.setUnlocalizedName("tinkersreborn.Pattern");
        this.hasSubtypes = false;

        this.patternType = new ArrayList<>();
        this.iconsMap = new HashMap<>();

    }

    @Override
    public String getUnlocalizedName() {
        return "tinkersreborn.Pattern";
    }

    @Override
    public String getItemStackDisplayName(ItemStack stack) {
        String identifier = this.getIdentifier(stack);
        switch (identifier) {
            case PATTERN_BLANK:
                return TinkersRebornUtils.translate("tinkersreborn.Pattern");
            case CAST_BLANK:
                return TinkersRebornUtils.translate("tinkersreborn.Cast");
            case CAST_INGOT:
                return TinkersRebornUtils.translate("tinkersreborn.CastIngot");
            case CAST_GEM:
                return TinkersRebornUtils.translate("tinkersreborn.CastGem");
            case CAST_NUGGET:
                return TinkersRebornUtils.translate("tinkersreborn.CastNugget");
            default:
                return this.getToolPart(stack)
                    .getLocalizedPartName() + " "
                    + TinkersRebornUtils.translate("tinkersreborn.Cast");
        }

    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> list, boolean detail) {
        // TODO Auto-generated method stub
        super.addInformation(stack, player, list, detail);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister iconRegister) {
        for (String name : this.patternType) {
            this.iconsMap.put(name, iconRegister.registerIcon("tinkersreborn:materials/cast_" + name));
        }
        CAST_BLANK_ICON = iconRegister.registerIcon("tinkersreborn:materials/" + CAST_BLANK);
        PATTERN_BLANK_ICON = iconRegister.registerIcon("tinkersreborn:materials/" + PATTERN_BLANK);
        CAST_INGOT_ICON = iconRegister.registerIcon("tinkersreborn:materials/" + CAST_INGOT);
        CAST_GEM_ICON = iconRegister.registerIcon("tinkersreborn:materials/" + CAST_GEM);
        CAST_NUGGET_ICON = iconRegister.registerIcon("tinkersreborn:materials/" + CAST_NUGGET);
    }

    @Override
    public IIcon getIcon(ItemStack stack, int pass) {
        String identifier = this.getIdentifier(stack);
        switch (identifier) {
            case PATTERN_BLANK:
                return PATTERN_BLANK_ICON;
            case CAST_BLANK:
                return CAST_BLANK_ICON;
            case CAST_INGOT:
                return CAST_INGOT_ICON;
            case CAST_GEM:
                return CAST_GEM_ICON;
            case CAST_NUGGET:
                return CAST_NUGGET_ICON;
            default:
                return this.iconsMap.getOrDefault(identifier, CAST_BLANK_ICON);
        }
    }

    @Override
    public void getSubItems(Item item, CreativeTabs tab, List<ItemStack> list) {
        list.add(newStackWithIdentifier(PATTERN_BLANK));
        list.add(newStackWithIdentifier(CAST_BLANK));
        list.add(newStackWithIdentifier(CAST_INGOT));
        list.add(newStackWithIdentifier(CAST_GEM));
        list.add(newStackWithIdentifier(CAST_NUGGET));
        for (String p : patternType) {
            list.add(newStackWithIdentifier(p));
        }
    }

    @Override
    public boolean requiresMultipleRenderPasses() {
        return true;
    }

    @Override
    public int getRenderPasses(int metadata) {
        return 1;
    }

    @Override
    public int getPatternCost(ItemStack pattern) {
        TinkersRebornToolPart toolPart = TinkersRebornRegistry.getToolPartByPartName(this.getIdentifier(pattern));
        if (toolPart != null) {
            return toolPart.cost;
        }
        return 0;
    }

    @Override
    public ItemStack getPatternOutput(ItemStack pattern, ItemStack input, TinkersRebornMaterial material) {
        TinkersRebornToolPart toolPart = this.getToolPart(pattern);
        if (toolPart != null) {
            return toolPart.getNewPartWithMaterial(material);
        }
        return null;
    }

    public boolean isBlankPattern(ItemStack stack) {
        return !this.getIdentifier(stack)
            .isEmpty();
    }

    public void addNewPatterntype(TinkersRebornToolPart toolPart) {
        this.addNewPatterntype(toolPart.texture);
    }

    public void addNewPatterntype(String identifier) {
        this.patternType.add(identifier);
    }

    public List<String> getAllPatternType() {
        return this.patternType;
    }

    // NBT
    public TinkersRebornToolPart getToolPart(ItemStack stack) {
        return TinkersRebornRegistry.getToolPartByPartName(this.getIdentifier(stack));
    }

    public String getIdentifier(ItemStack stack) {
        return ToolTagsHelper.getTagSafe(stack)
            .getString(ToolTags.IDENTIFIER);
    }

    public static ItemStack newStackWithToolPart(TinkersRebornToolPart part) {
        return newStackWithIdentifier(part.texture);
    }

    public static ItemStack newStackWithIdentifier(String identifier) {
        ItemStack newStack = new ItemStack(TinkersRebornTools.patternAndCast);
        newStack.setTagCompound(getNewCompoundWithIdentifier(identifier));
        return newStack;
    }

    public static NBTTagCompound getNewCompoundWithToolPart(TinkersRebornToolPart part) {
        return getNewCompoundWithIdentifier(part.texture);
    }

    public static NBTTagCompound getNewCompoundWithIdentifier(String identifier) {
        NBTTagCompound newTag = new NBTTagCompound();
        newTag.setString(ToolTags.IDENTIFIER, identifier);
        return newTag;
    }
}
