package mctbl.tinkersreborn.library.tools;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.IIcon;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mctbl.tinkersreborn.TinkersReborn;
import mctbl.tinkersreborn.TinkersRebornConfig;
import mctbl.tinkersreborn.library.TinkersRebornRegistry;
import mctbl.tinkersreborn.library.crafting.ToolBuilderHelper;
import mctbl.tinkersreborn.library.materials.MaterialStatusType;
import mctbl.tinkersreborn.library.materials.TinkersRebornMaterial;
import mctbl.tinkersreborn.tools.entity.FancyEntityItem;
import mctbl.tinkersreborn.tools.items.TinkersRebornToolPart;
import mctbl.tinkersreborn.tools.materials.ExtraMaterialStats;
import mctbl.tinkersreborn.tools.materials.HandleMaterialStats;
import mctbl.tinkersreborn.tools.materials.HeadMaterialStats;
import mctbl.tinkersreborn.util.TextureHelper;
import mctbl.tinkersreborn.util.ToolTags;
import mctbl.tinkersreborn.util.ToolTagsHelper;

/**
 * All the base of a Tinkers style Tool class Author MCTBL Time 2026-05-24
 * 11:46:03
 */
public abstract class ToolCore extends Item implements IModifyable, IToolEvent {

    public Random random = TinkersReborn.random;

    /**
     * first one is main part and has broken icon, but it will render second second
     * will render first then other will render in order
     */
    protected final List<ToolPartRecord> componentsParts = new ArrayList<>(4);
    public final List<Map<Integer, IIcon>> allIcons = new ArrayList<>();
    public final Set<String> categoryTags = new HashSet<>();

    public static IIcon blankSprite;
    public static IIcon emptyIcon;

    public String toolTypeName; // pickaxe
    public final int partAmount;

    public ToolCore(String toolTypeName, int partAmount) {
        super();
        this.partAmount = partAmount;
        this.maxStackSize = 1;
        this.setMaxDamage(100);
        this.setUnlocalizedName("TinkerTools." + toolTypeName);
        this.setCreativeTab(TinkersRebornRegistry.toolsTab);
        this.setNoRepair();
        this.toolTypeName = toolTypeName.toLowerCase();

        // extra 2 map for broken and effect
        for (int i = 0; i < this.partAmount + 2; i++) {
            this.allIcons.add(new HashMap<>());
        }

    }

    @Override
    public void registerIcons(IIconRegister register) {
        String basePath = "tinkersreborn:tools/" + this.toolTypeName + "/";
        // exclude effect for now
        for (int i = 0; i < this.partAmount; i++) {
            if (this.componentsParts.get(i) != null) {
                MaterialStatusType type = this.componentsParts.get(i)
                    .statusType();
                for (TinkersRebornMaterial material : TinkersRebornRegistry.allMaterialsList) {
                    if (material.hasStats(type)) {
                        String path = basePath + material.identifier + this.componentsParts.get(i).texturePostfix;
                        if (TextureHelper.itemTextureExists(path)) {
                            this.allIcons.get(i)
                                .put(material.materialId, register.registerIcon(path));
                        }
                        if (i == 0) {
                            // broken
                            path += "_broken";
                            if (TextureHelper.itemTextureExists(path)) {
                                this.allIcons.get(this.partAmount)
                                    .put(material.materialId, register.registerIcon(path));
                            }
                        }
                    }
                }
                // standard
                this.allIcons.get(i)
                    .put(-1, register.registerIcon(basePath + this.componentsParts.get(i).texturePostfix));
                if (i == 0) {
                    this.allIcons.get(this.partAmount)
                        .put(
                            -1,
                            register.registerIcon(basePath + this.componentsParts.get(i).texturePostfix + "_broken"));

                }
            }
        }

        emptyIcon = register.registerIcon("tinkersreborn:blankface");
        blankSprite = register.registerIcon("tinkersreborn:blanksprite");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(ItemStack stack, int renderPass) {
        NBTTagCompound tags = ToolTagsHelper.getToolBaseNBTSafe(stack);
        if (!tags.hasNoTags()) {
            NBTTagList renderMaterials = ToolTagsHelper.getStringTagListSafe(tags, ToolTags.RENDERMATERIALS);
            if (renderMaterials.tagCount() != 0) {
                if (renderPass < this.partAmount) {
                    int iconsIdx = (renderPass == 0 && tags.getBoolean(ToolTags.BROKEN)) ? this.partAmount : renderPass;
                    return getCorrectIcon(this.allIcons.get(iconsIdx), renderMaterials.getStringTagAt(renderPass));
                }
                // Effects
                // else if (renderPass <= 10) {
                // String effect = "Effect" + (1 + renderPass - this.partAmount);
                // if (tags.hasKey(effect)) return effectIcons.get(tags.getInteger(effect));
                // }
            }
            return blankSprite;
        }
        return emptyIcon;
    }

    protected IIcon getCorrectIcon(Map<Integer, IIcon> icons, int id) {
        if (icons.containsKey(id)) return icons.get(id);
        // default icon
        return icons.get(-1);
    }

    protected IIcon getCorrectIcon(Map<Integer, IIcon> icons, String materialIdentifier) {
        return this.getCorrectIcon(icons, TinkersRebornRegistry.getMaterialByIdentifier(materialIdentifier).materialId);
    }

    @Override
    public int getColorFromItemStack(ItemStack stack, int renderPass) {
        NBTTagCompound tags = ToolTagsHelper.getToolBaseNBTSafe(stack);
        if (!tags.hasNoTags()) {
            NBTTagList renderMaterials = ToolTagsHelper.getStringTagListSafe(tags, ToolTags.RENDERMATERIALS);
            if (renderMaterials.tagCount() != 0) {
                if (renderPass < this.partAmount) {
                    return this
                        .getCorrectColor(this.allIcons.get(renderPass), renderMaterials.getStringTagAt(renderPass));
                }
            }
        }
        return super.getColorFromItemStack(stack, renderPass);
    }

    protected int getCorrectColor(Map<Integer, IIcon> icons, int id) {
        if (icons.containsKey(id))
            // UNKNOWN's color is same as super.getColorFromItemStack
            return TinkersRebornMaterial.UNKNOWN.materialTextColor;

        TinkersRebornMaterial material = TinkersRebornRegistry.getMaterialById(id);
        if (material != null) {
            return material.materialTextColor;
        } else {
            return TinkersRebornMaterial.UNKNOWN.materialTextColor;
        }
    }

    protected int getCorrectColor(Map<Integer, IIcon> icons, String materialIdentifier) {
        TinkersRebornMaterial material = TinkersRebornRegistry.getMaterialByIdentifier(materialIdentifier);
        if (material != null && !icons.containsKey(material.materialId)) return material.materialTextColor;

        return TinkersRebornMaterial.UNKNOWN.materialTextColor;
    }

    @Override
    public String getBaseTagName() {
        return ToolTags.TOOLBASETAG;
    }

    @Override
    public Set<String> getCategoryTags() {
        return this.categoryTags;
    }

    public List<ToolPartRecord> getToolComponentsParts() {
        return this.componentsParts;
    }

    public String getEffectSuffix() {
        return "_" + this.toolTypeName + "_effect";
    }

    public String getDefaultFolder() {
        return this.toolTypeName;
    }

    public String getUnlocalizedToolName() {
        return "tinkersreborn.tool." + this.toolTypeName;
    }

    public String getLocalizedToolName() {
        return StatCollector.translateToLocal(this.getUnlocalizedToolName());
    }

    public boolean isItemTool(ItemStack par1ItemStack) {
        return false;
    }

    @Override
    public boolean isRepairable() {
        return false;
    }

    @Override
    public boolean isFull3D() {
        return true;
    }

    @Override
    public boolean showDurabilityBar(ItemStack stack) {
        // TODO Auto-generated method stub
        return super.showDurabilityBar(stack);
    }

    @Override
    public int getMaxDamage(ItemStack stack) {
        return 100;
    }

    @Override
    public int getDamage(ItemStack stack) {
        // TODO Auto-generated method stub
        return super.getDamage(stack);
    }

    @Override
    public int getDisplayDamage(ItemStack stack) {
        return this.getDamage(stack);
    }

    @Override
    public void setDamage(ItemStack stack, int damage) {
        // TODO Auto-generated method stub
        super.setDamage(stack, damage);
    }

    @Override
    public boolean hasCustomEntity(ItemStack stack) {
        return true;
    }

    @Override
    public Entity createEntity(World world, Entity location, ItemStack itemstack) {
        return new FancyEntityItem(world, location, itemstack);
    }

    @Override
    public void getSubItems(Item item, CreativeTabs tab, List<ItemStack> list) {
        for (TinkersRebornMaterial material : TinkersRebornRegistry.allMaterialsList) {
            ItemStack tool = buildTool(material, null);
            if (tool != null) list.add(tool);
        }
    }

    private ItemStack buildTool(TinkersRebornMaterial material, String toolName) {
        List<ItemStack> list = new ArrayList<>();
        for (int i = 0; i < this.partAmount; i++) {
            ToolPartRecord toolPartRecord = this.componentsParts.get(i);
            if (toolPartRecord != null && material.hasStats(toolPartRecord.statusType())) {
                list.add(new ItemStack(toolPartRecord.toolPart(), 1, material.materialId));
            }
        }
        return ToolBuilderHelper.instance.buildTool(null, list.toArray(new ItemStack[0]));
    }

    public boolean checkRecipeMatch(ItemStack[] parts) {
        if (this.componentsParts.size() != parts.length) return false;
        List<Item> toolPartList = this.componentsParts.stream()
            .map(record -> record.toolPart())
            .collect(Collectors.toList());
        for (int i = 0; i < parts.length; i++) if (!parts[i].getItem()
            .equals(toolPartList.get(i))) return false;
        return true;
    }

    /**
     * Builds a default tool from: 1. Handle 2. Head 3. Accessoire (if present)
     */
    protected ToolNBT buildToolTag(List<TinkersRebornMaterial> materials) {
        ToolNBT data = new ToolNBT();
        List<HeadMaterialStats> heads = new ArrayList<>();
        List<HandleMaterialStats> handles = new ArrayList<>();
        List<ExtraMaterialStats> extras = new ArrayList<>();
        for (int i = 0; i < this.partAmount; i++) {
            switch (this.componentsParts.get(i)
                .statusType()) {
                case HEAD -> heads.add(
                    materials.get(i)
                        .getStats(MaterialStatusType.HEAD));
                case HANDLE -> handles.add(
                    materials.get(i)
                        .getStats(MaterialStatusType.HANDLE));
                case EXTRA -> extras.add(
                    materials.get(i)
                        .getStats(MaterialStatusType.EXTRA));
                default -> {
                    // do nothing by default with other components
                }
            }
        }
        data.head(heads);
        data.extra(extras);
        data.handle(handles);

        // 3 free modifiers
        data.modifiers = TinkersRebornConfig.defaultModifiers;

        return data;
    }

    /**
     * Builds the NBT for new tinkers tools, and this NBT is for render
     * 
     * @param materials
     * @return
     */
    private NBTTagList buildMaterialListData(List<TinkersRebornMaterial> materials) {
        NBTTagList materialList = new NBTTagList();

        for (TinkersRebornMaterial material : materials) {
            materialList.appendTag(new NBTTagString(material.identifier));
        }
        return materialList;
    }

    /**
     * Builds the NBT for a new tinker item with the given data.
     *
     * @param materials Materials to build with. Have to be in the correct order. No
     *                  nulls!
     * @return The built nbt
     */
    public NBTTagCompound buildItemNBT(List<TinkersRebornMaterial> materials) {
        NBTTagCompound basetag = new NBTTagCompound();
        NBTTagCompound tinkersTag = new NBTTagCompound();
        NBTTagCompound toolTag = buildToolTag(materials).get();
        NBTTagList dataTag = buildMaterialListData(materials);

        tinkersTag.setTag(ToolTags.BASEMATERIALS, dataTag);
        tinkersTag.setTag(ToolTags.RENDERMATERIALS, dataTag.copy());
        tinkersTag.setTag(ToolTags.TOOLDATA, toolTag);
        tinkersTag.setTag(ToolTags.TOOLDATAORIG, toolTag.copy());

        // save categories on the tool
        // TagUtil.setCategories(basetag, getCategories());

        // add traits
        // addMaterialTraits(basetag, materials);

        basetag.setTag(getBaseTagName(), tinkersTag);
        return basetag;
    }

    @Override
    public EnumRarity getRarity(ItemStack stack) {
        return EnumRarity.common;
    }

    @Override
    public boolean isBookEnchantable(ItemStack stack, ItemStack book) {
        return false;
    }

    public final class ToolPartRecord {

        private final TinkersRebornToolPart toolPart;
        private final MaterialStatusType statusType;
        private final String texturePostfix;

        public ToolPartRecord(TinkersRebornToolPart toolPart, MaterialStatusType statusType, String texturePostfix) {
            this.toolPart = toolPart;
            this.statusType = statusType;
            this.texturePostfix = texturePostfix;
        }

        public TinkersRebornToolPart toolPart() {
            return this.toolPart;
        }

        public MaterialStatusType statusType() {
            return this.statusType;
        }

        public String texturePostfix() {
            return this.texturePostfix;
        }

    }
}
