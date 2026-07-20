package mctbl.tinkersreborn.library.tools;

import static mctbl.tinkersreborn.util.TinkersRebornUtils.translate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.annotation.Nonnull;

import net.minecraft.block.Block;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.DamageSource;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mctbl.tinkersreborn.TinkersReborn;
import mctbl.tinkersreborn.TinkersRebornConfig;
import mctbl.tinkersreborn.common.TinkersRebornGeneralProxyClient;
import mctbl.tinkersreborn.library.TinkersRebornRegistry;
import mctbl.tinkersreborn.library.crafting.ToolBuilderHelper;
import mctbl.tinkersreborn.library.event.TinkersRebornEvent;
import mctbl.tinkersreborn.library.materials.MaterialStatusType;
import mctbl.tinkersreborn.library.materials.TinkersRebornMaterial;
import mctbl.tinkersreborn.library.materials.TinkersRebornMaterial.RenderMaterial;
import mctbl.tinkersreborn.library.tools.modifiers.ModifierNBT;
import mctbl.tinkersreborn.library.utils.BlockPos;
import mctbl.tinkersreborn.library.utils.RecipeMatch;
import mctbl.tinkersreborn.tools.Category;
import mctbl.tinkersreborn.tools.TinkersRebornTools;
import mctbl.tinkersreborn.tools.entity.FancyEntityItem;
import mctbl.tinkersreborn.tools.gui.ToolBuildGuiInfo;
import mctbl.tinkersreborn.tools.items.TinkersRebornToolPart;
import mctbl.tinkersreborn.tools.materials.ExtraMaterialStats;
import mctbl.tinkersreborn.tools.materials.HandleMaterialStats;
import mctbl.tinkersreborn.tools.materials.HeadMaterialStats;
import mctbl.tinkersreborn.util.ColorUtil;
import mctbl.tinkersreborn.util.TextureHelper;
import mctbl.tinkersreborn.util.TinkersRebornUtils;
import mctbl.tinkersreborn.util.TinkersStr;
import mctbl.tinkersreborn.util.ToolTags;
import mctbl.tinkersreborn.util.ToolTagsHelper;

/**
 * All the base of a Tinkers style Tool class Author MCTBL Time 2026-05-24
 * 11:46:03
 */
public abstract class ToolCore extends Item implements IModifyable, IToolEvent, IRepairable {

    public Random random = TinkersReborn.random;

    public static final String TOOLNAMEFORMATTER = TinkersStr.tooNamePattern.toString();

    /**
     * first one is main part and has broken icon, but it will render second second
     * will render first then other will render in order
     */
    protected final List<ToolPartRecord> componentsParts = new ArrayList<>(4);
    public final List<Map<String, IIcon>> allIcons = new ArrayList<>();
    public final Map<String, IIcon> effectIcons = new HashMap<>();

    public final Set<Category> categoryTags = new HashSet<>();

    public static IIcon blankSprite;
    public static IIcon emptyIcon;

    public String toolModifierEffect;
    public String toolTypeName; // pickaxe
    public final int partAmount;

    public ItemStack toolForRender;

    // use getToolBuildGuiInfo instanced
    protected ToolBuildGuiInfo toolBuildGuiInfo;

    protected ToolCore(String toolTypeName, int partAmount) {
        super();
        this.partAmount = partAmount;
        this.maxStackSize = 1;
        this.setUnlocalizedName("TinkerTools." + toolTypeName);
        this.setCreativeTab(TinkersRebornRegistry.toolsTab);
        this.setNoRepair();
        this.toolTypeName = toolTypeName.toLowerCase();

        this.toolModifierEffect = "_effect";

        // extra 2 map for broken
        for (int i = 0; i < this.partAmount + 1; i++) {
            this.allIcons.add(new HashMap<>());
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public FontRenderer getFontRenderer(ItemStack stack) {
        return TinkersRebornGeneralProxyClient.fontRender;
    }

    // Tool and Weapon specific properties
    /**
     * Multiplier applied to the actual mining speed of the tool Internally a hammer
     * and pick have the same speed, but a hammer is 2/3 slower
     */
    public float miningSpeedModifier() {
        return 1f;
    }

    /** Multiplier for damage from materials. Should be fixed per tool. */
    public abstract float damagePotential();

    /**
     * A fixed damage value where the calculations start to apply dimishing returns.
     * Basically if you'd hit more than that damage with this tool, the damage is
     * gradually reduced depending on how much the cutoff is exceeded.
     */
    public float damageCutoff() {
        return 15.0f; // in general this should be sufficient and only needs increasing if it's a
        // stronger weapon
        // fun fact: diamond sword with sharpness V has 15 damage
    }

    /**
     * Knockback modifier. Basically this takes the vanilla knockback on hit and
     * modifies it by this factor.
     */
    public float knockback() {
        return 1.0f;
    }

    @Override
    public void registerIcons(IIconRegister register) {
        String basePath = "tinkersreborn:tools/" + this.toolTypeName + "/";
        // exclude effect for now
        for (int i = 0; i < this.partAmount; i++) {
            if (this.componentsParts.get(i) != null) {
                MaterialStatusType type = this.componentsParts.get(i)
                    .statusType();
                for (TinkersRebornMaterial material : TinkersRebornRegistry.getAllMaterialList()) {
                    if (material.hasStats(type)) {
                        String path = basePath + material.identifier + this.componentsParts.get(i).texturePostfix;
                        if (TextureHelper.itemTextureExists(path)) {
                            this.allIcons.get(i)
                                .put(material.identifier, register.registerIcon(path));
                        }
                        if (i == 0) {
                            // broken
                            path += "_broken";
                            if (TextureHelper.itemTextureExists(path)) {
                                this.allIcons.get(this.partAmount)
                                    .put(material.identifier, register.registerIcon(path));
                            }
                        }
                    }
                }
                // standard
                this.allIcons.get(i)
                    .put(null, register.registerIcon(basePath + this.componentsParts.get(i).texturePostfix));
                if (i == 0) {
                    this.allIcons.get(this.partAmount)
                        .put(
                            null,
                            register.registerIcon(basePath + this.componentsParts.get(i).texturePostfix + "_broken"));

                }
            }
        }

        for (IModifier m : TinkersRebornRegistry.getAllModifier()) {
            String tempPath = basePath + m.getIdentifier() + this.toolModifierEffect;
            if (TextureHelper.itemTextureExists(tempPath)) {
                this.effectIcons.put(m.getIdentifier(), register.registerIcon(tempPath));
            }
        }

        emptyIcon = register.registerIcon("tinkersreborn:blankface");
        blankSprite = register.registerIcon("tinkersreborn:blanksprite");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(ItemStack stack, int renderPass) {
        List<TinkersRebornMaterial> renderMaterials = ToolTagsHelper.getToolRenderMaterialsList(stack);
        if (renderMaterials.size() != 0) {
            if (renderPass < this.partAmount) {
                int iconsIdx = (renderPass == 0 && ToolTagsHelper.isBroken(stack)) ? this.partAmount : renderPass;
                String materialId = renderMaterials.get(renderPass) == null ? null
                    : renderMaterials.get(renderPass).identifier;
                return getCorrectIcon(this.allIcons.get(iconsIdx), materialId);
            }
            // Effects
            else {
                List<NBTTagCompound> modifiersList = ToolTagsHelper.getModifiersList(stack);
                ModifierNBT tag = ModifierNBT.readTag(modifiersList.get(renderPass - this.partAmount));
                return this.effectIcons.get(tag.identifier);
            }

        }
        return emptyIcon;
    }

    protected IIcon getCorrectIcon(Map<String, IIcon> icons, String id) {
        if (icons.containsKey(id)) return icons.get(id);
        // default icon
        return icons.get(null);
    }

    @Override
    public int getColorFromItemStack(ItemStack stack, int renderPass) {
        NBTTagCompound tags = ToolTagsHelper.getToolBaseNBTSafe(stack);
        if (tags != null && !tags.hasNoTags()) {
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

    protected int getCorrectColor(Map<String, IIcon> icons, String materialIdentifier) {
        TinkersRebornMaterial material = null;
        if (materialIdentifier.startsWith("_internal_render")) {
            material = TinkersRebornRegistry.getRenderMaterial(materialIdentifier);
            return material.materialTextColor;
        } else {
            material = TinkersRebornRegistry.getMaterialByIdentifier(materialIdentifier);
            if (material != null && !icons.containsKey(material.identifier)) return material.materialTextColor;
        }

        return TinkersRebornMaterial.UNKNOWN.materialTextColor;
    }

    public void addCategory(Category... tags) {
        Collections.addAll(this.categoryTags, tags);
    }

    public Set<Category> getCategory() {
        return ImmutableSet.copyOf(this.categoryTags);
    }

    public boolean hasCategory(Category tag) {
        return this.categoryTags.contains(tag);
    }

    public List<ToolPartRecord> getToolComponentsParts() {
        return ImmutableList.copyOf(this.componentsParts);
    }

    public String getUnlocalizedToolName() {
        return "tinkersreborn.tool." + this.toolTypeName;
    }

    public String getLocalizedToolName() {
        return translate(this.getUnlocalizedToolName());
    }

    /** Returns info about the Tool. Displayed in the tool stations etc. */
    public String getLocalizedDescription() {
        return translate(this.getUnlocalizedName() + ".desc");
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
        return super.showDurabilityBar(stack) && !ToolTagsHelper.isBroken(stack);
    }

    @Override
    public boolean isDamageable() {
        return true;
    }

    @Override
    public int getMaxDamage(ItemStack stack) {
        return ToolTagsHelper.getDurabilityStat(stack);
    }

    @Override
    public void setDamage(ItemStack stack, int damage) {
        int max = this.getMaxDamage(stack);
        super.setDamage(stack, Math.min(max, damage));

        if (getDamage(stack) == max) {
            ToolTagsHelper.breakTool(stack, null);
        }
    }

    @Override
    public boolean hasCustomEntity(ItemStack stack) {
        return true;
    }

    @Override
    public boolean canHarvestBlock(Block block, ItemStack stack) {
        return isEffective(block) && !ToolTagsHelper.isBroken(stack);
    }

    @Override
    public int getHarvestLevel(ItemStack stack, String toolClass) {
        if (ToolTagsHelper.isBroken(stack)) {
            return -1;
        }
        if (this.getToolClasses(stack)
            .contains(toolClass)) {
            // will return 0 if the tag has no info anyway
            return ToolTagsHelper.getHarvestLevelStat(stack);
        }
        return super.getHarvestLevel(stack, toolClass);
    }

    @Override
    public Set<String> getToolClasses(ItemStack stack) {
        // no classes if broken
        if (ToolTagsHelper.isBroken(stack)) {
            return Collections.emptySet();
        }
        return super.getToolClasses(stack);
    }

    @Override
    public float getDigSpeed(ItemStack itemstack, Block block, int metadata) {
        if (isEffective(block)) {
            return ToolTagsHelper.calcMiningSpeed(itemstack, block, metadata);
        }
        return super.getDigSpeed(itemstack, block, metadata);
    }

    public boolean isEffective(Block block) {
        return false;
    }

    @Override
    public boolean onItemUse(ItemStack toolStack, EntityPlayer player, World world, int x, int y, int z, int side,
        float hitX, float hitY, float hitZ) {
        if (world.isRemote) {
            return true;
        }

        return super.onItemUse(toolStack, player, world, x, y, z, side, hitX, hitY, hitZ);
    }

    /**
     * Actually deal damage to the entity we hit. Can be overridden for special
     * behaviour
     *
     * @return True if the entity was hit. Usually the return value of
     *         {@link Entity#attackEntityFrom(DamageSource, float)}
     */
    public boolean dealDamage(ItemStack stack, EntityLivingBase player, Entity entity, float damage) {
        if (player instanceof EntityPlayer) {
            return entity.attackEntityFrom(DamageSource.causePlayerDamage((EntityPlayer) player), damage);
        }
        return entity.attackEntityFrom(DamageSource.causeMobDamage(player), damage);
    }

    /**
     * Called when an entity is getting damaged with the tool. Reduce the tools
     * durability accordingly player can be null!
     */
    public void reduceDurabilityOnHit(ItemStack stack, EntityPlayer player, float damage) {
        damage = Math.max(1f, damage / 10f);
        if (!hasCategory(Category.WEAPON)) {
            damage *= 2;
        }
        ToolTagsHelper.damageTool(stack, (int) damage, player);
    }

    @Override
    public Entity createEntity(World world, Entity location, ItemStack itemstack) {
        return new FancyEntityItem(world, location, itemstack);
    }

    @Override
    public void getSubItems(Item item, CreativeTabs tab, List<ItemStack> list) {
        for (TinkersRebornMaterial material : TinkersRebornRegistry.getAllMaterialList()) {
            ItemStack tool = buildTool(material, null);
            if (tool != null) list.add(tool);
        }
    }

    private ItemStack buildTool(TinkersRebornMaterial material, String toolName) {
        List<ItemStack> list = new ArrayList<>();
        for (int i = 0; i < this.partAmount; i++) {
            ToolPartRecord toolPartRecord = this.componentsParts.get(i);
            if (toolPartRecord != null && material.hasStats(toolPartRecord.statusType())) {
                list.add(
                    toolPartRecord.toolPart()
                        .getNewPartWithMaterial(material));
            }
        }
        return ToolBuilderHelper.buildTool(null, list.toArray(new ItemStack[0]));
    }

    public boolean checkRecipeMatch(List<ItemStack> parts) {
        if (this.componentsParts.size() != parts.size()) return false;
        List<Item> inputToolPartList = parts.stream()
            .map(stack -> stack.getItem())
            .collect(Collectors.toList());
        List<Item> toolPartList = this.componentsParts.stream()
            .map(record -> record.toolPart())
            .collect(Collectors.toList());
        for (int i = 0; i < inputToolPartList.size(); i++) if (!inputToolPartList.get(i)
            .equals(toolPartList.get(i))) return false;
        return true;
    }

    /**
     * Builds a default tool from: 1. Handle 2. Head 3. Accessoire (if present)
     */
    public ToolNBT buildToolTag(List<TinkersRebornMaterial> materials) {
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
        data.modifierSlots = TinkersRebornConfig.defaultModifiers;

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
     * Builds an Itemstack of this tool with the given materials.
     *
     * @param materials Materials to build with. Have to be in the correct order. No
     *                  nulls!
     * @return The built item or null if invalid input.
     */
    @Nonnull
    public ItemStack buildItem(List<TinkersRebornMaterial> materials) {
        ItemStack tool = new ItemStack(this);
        tool.setTagCompound(buildItemNBT(materials));

        return tool;
    }

    /**
     * Builds the NBT for a new tinker item with the given data.
     *
     * @param materials TinkersRebornMaterial to build with. Have to be in the
     *                  correct order. No nulls!
     * @return The built nbt
     */
    public NBTTagCompound buildItemNBT(List<TinkersRebornMaterial> materials) {
        NBTTagCompound basetag = new NBTTagCompound();
        NBTTagCompound tinkersTag = new NBTTagCompound();
        basetag.setTag(ToolTags.TOOLBASETAG, tinkersTag);

        NBTTagCompound toolTag = this.buildToolTag(materials)
            .get();
        NBTTagList dataTag = this.buildMaterialListData(materials);
        NBTTagList categoryList = this.buildCategoryList();

        tinkersTag.setTag(ToolTags.BASEMATERIALS, dataTag);
        tinkersTag.setTag(ToolTags.RENDERMATERIALS, dataTag.copy());
        tinkersTag.setTag(ToolTags.TOOLDATA, toolTag);
        tinkersTag.setTag(ToolTags.TOOLDATAORIG, toolTag.copy());

        // save categories on the tool
        tinkersTag.setTag(ToolTags.TOOLCATEGORY, categoryList);

        // add traits
        addMaterialTraits(basetag, materials);

        TinkersRebornEvent.OnItemBuilding.fireEvent(toolTag, materials, this);

        return basetag;
    }

    public NBTTagList buildCategoryList() {
        NBTTagList list = new NBTTagList();
        getCategory().stream()
            .forEach(c -> list.appendTag(new NBTTagString(c.name)));
        return list;
    }

    public void addMaterialTraits(NBTTagCompound root, List<TinkersRebornMaterial> materials) {
        int size = this.componentsParts.size();
        // safety
        if (materials.size() < size) {
            size = materials.size();
        }
        // add corresponding traits per material usage
        for (int i = 0; i < size; i++) {
            ToolPartRecord required = this.componentsParts.get(i);
            TinkersRebornMaterial material = materials.get(i);
            for (ITrait trait : required.getApplicableTraitsForMaterial(material)) {
                ToolBuilderHelper.addTrait(root, trait, material.materialTextColor);
            }
        }
    }

    private NBTTagCompound buildRenderNBT(List<TinkersRebornMaterial> materials) {
        NBTTagCompound basetag = new NBTTagCompound();
        NBTTagCompound tinkersTag = new NBTTagCompound();
        NBTTagList dataTag = this.buildMaterialListData(materials);
        tinkersTag.setTag(ToolTags.RENDERMATERIALS, dataTag.copy());
        basetag.setTag(ToolTags.TOOLBASETAG, tinkersTag);
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

    // main logic part
    @Override
    public boolean onLeftClickEntity(ItemStack stack, EntityPlayer player, Entity entity) {
        return ToolTagsHelper.attackEntity(stack, this, player, entity);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public boolean hasEffect(ItemStack stack) {
        return ToolTagsHelper.hasEnchantEffect(stack);
    }

    @Override
    public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        super.onUpdate(stack, worldIn, entityIn, itemSlot, isSelected);

        onUpdateTraits(stack, worldIn, entityIn, itemSlot, isSelected);
    }

    protected void onUpdateTraits(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        ToolTagsHelper.getTraitsOrdered(stack)
            .forEach(trait -> trait.onUpdate(stack, worldIn, entityIn, itemSlot, isSelected));
    }

    // TODO reduce target resistant time
    // @Override
    // public boolean hitEntity(ItemStack stack, EntityLivingBase target,
    // EntityLivingBase attacker) {
    // float speed = ToolTagsHelper.getActualAttackSpeed(stack);
    // int time = Math.round(20f / speed);
    // if(time < target.hurtResistantTime / 2) {
    // target.hurtResistantTime = (target.hurtResistantTime + time) / 2;
    // target.hurtTime = (target.hurtTime + time) / 2;
    // }
    // return super.hitEntity(stack, target, attacker);
    // }

    @Override
    public boolean onBlockStartBreak(ItemStack itemstack, int x, int y, int z, EntityPlayer player) {
        if (!ToolTagsHelper.isBroken(itemstack) && this instanceof IAoeTool) {
            BlockPos blockPos = BlockPos.of(x, y, z);
            for (BlockPos extraPos : ((IAoeTool) this)
                .getAOEBlocks(itemstack, player.getEntityWorld(), player, blockPos)) {
                this.breakExtraBlock(itemstack, player.worldObj, player, extraPos, blockPos);
            }
        }
        return breakBlock(itemstack, x, y, z, player);
    }

    /**
     * Called when an AOE block is broken by the tool. Use to oveerride the block breaking logic
     * 
     * @param tool   Tool ItemStack
     * @param world  World instance
     * @param player Player instance
     * @param pos    Current position
     * @param refPos Base position
     */
    protected void breakExtraBlock(ItemStack tool, World world, EntityPlayer player, BlockPos pos, BlockPos refPos) {
        ToolTagsHelper.breakExtraBlock(tool, world, player, pos, refPos);
    }

    /**
     * Called to break the base block, return false to perform no breaking
     * 
     * @param itemstack Tool ItemStack
     * @param pos       Current position
     * @param player    Player instance
     * @return true if the normal block break code should be skipped
     */
    protected boolean breakBlock(ItemStack itemstack, int x, int y, int z, EntityPlayer player) {
        return super.onBlockStartBreak(itemstack, x, y, z, player);
    }

    @Override
    public boolean onBlockDestroyed(ItemStack stack, World world, Block blockIn, int x, int y, int z,
        EntityLivingBase entityLiving) {
        if (ToolTagsHelper.isBroken(stack)) {
            return false;
        }

        boolean effective = isEffective(blockIn)
            || ToolTagsHelper.isToolEffective(stack, world.getBlock(x, y, z), world.getBlockMetadata(x, y, z));
        int damage = effective ? 1 : 2;

        this.afterBlockBreak(stack, world, blockIn, x, y, z, entityLiving, damage, effective);

        return hasCategory(Category.TOOL);
    }

    public void afterBlockBreak(ItemStack stack, World world, Block block, int x, int y, int z, EntityLivingBase player,
        int damage, boolean wasEffective) {
        ToolTagsHelper.getTraitsOrdered(stack)
            .forEach(trait -> trait.afterBlockBreak(stack, world, block, BlockPos.of(x, y, z), player, wasEffective));
        ToolTagsHelper.damageTool(stack, damage, player);
    }

    /**
     * For tool station display
     * 
     * @param stack
     * @return
     */
    public List<String> getInformation(ItemStack stack) {
        return getInformation(stack, null, false);
    }

    @Override
    public String getItemStackDisplayName(ItemStack stack) {
        if (stack.hasDisplayName()) {
            return stack.getTagCompound()
                .getCompoundTag("display")
                .getString("Name");
        }

        String toolBaseName = getLocalizedToolName();
        String materialName = ToolTagsHelper.getToolBaseMaterialsList(stack)
            .get(0)
            .localizedPrefix();

        return String.format(TOOLNAMEFORMATTER, materialName, toolBaseName);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> list, boolean advanced) {
        boolean shift = TinkersRebornUtils.isShiftKeyDown();
        // modifier first
        this.getTooltipModify(stack, player, list);

        list.add("");
        this.getTooltipDetailed(stack, player, list);

        if (!shift) {
            list.add(TinkersStr.holdShift.toString());
        } else {
            this.getTooltipComponents(stack, player, list);
        }
    }

    protected void getTooltipModify(ItemStack stack, EntityPlayer player, List<String> list) {
        List<NBTTagCompound> modifiersList = ToolTagsHelper.getModifiersList(stack);
        for (NBTTagCompound tag : modifiersList) {
            ModifierNBT data = ModifierNBT.readTag(tag);

            // get matching modifier
            IModifier modifier = TinkersRebornRegistry.getModifierAndTrait(data.identifier);
            if (modifier != null && !modifier.isHidden()) {
                list.add(data.getColorString() + modifier.getTooltip(tag, false));
            }
        }
    }

    /**
     * For tooltip display
     * 
     * @param stack
     * @param list
     */
    protected void getTooltipDetailed(ItemStack stack, EntityPlayer player, List<String> list) {
        list.addAll(this.getInformation(stack, player, true));
    }

    public List<String> getInformation(ItemStack stack, EntityPlayer player, boolean isTooltip) {
        List<String> list = new LinkedList<>();

        // durability
        // is broken and need detail, for tooltip
        if (ToolTagsHelper.isBroken(stack) && isTooltip) {
            list.add(
                String.format(
                    "%s: %s",
                    HeadMaterialStats.LOC_Durability,
                    ColorUtil.addDarkRed(ColorUtil.addUnderLine(TinkersStr.broken.toString()))));
        } else {
            list.add(
                HeadMaterialStats.formatDurability(
                    ToolTagsHelper.getCurrentDurability(stack),
                    ToolTagsHelper.getMaxDurability(stack)));
        }

        if (hasCategory(Category.HARVEST)) {
            list.add(HeadMaterialStats.formatHarvestLevel(ToolTagsHelper.getHarvestLevelStat(stack)));
            list.add(HeadMaterialStats.formatMiningSpeed(ToolTagsHelper.getMiningSpeedStat(stack)));
        }
        float attack = ToolTagsHelper.getActualAttackDamage(stack, player);
        list.add(HeadMaterialStats.formatAttack(attack));

        int freeModifier = ToolTagsHelper.getModifierSlots(stack) + ToolTagsHelper.getExtraModifier(stack)
            - ToolTagsHelper.getUsedModifiers(stack);
        if (freeModifier > 0) {
            list.add(String.format("%s: %d", TinkersStr.modifierToolTip.toString(), freeModifier));
        }

        if (!isTooltip) {
            list.addAll(getModifierInfo(stack));
        }

        return list;
    }

    protected List<String> getModifierInfo(ItemStack tool) {
        List<String> list = new ArrayList<>();

        List<NBTTagCompound> modifiersList = ToolTagsHelper.getModifiersList(tool);
        for (NBTTagCompound compound : modifiersList) {
            ModifierNBT data = ModifierNBT.readTag(compound);
            String modifierIdentifier = compound.getString(ToolTags.IDENTIFIER);

            // get matching modifier
            IModifier modifier = TinkersRebornRegistry.getModifierAndTrait(modifierIdentifier);
            if (modifier != null && !modifier.isHidden()) {
                for (String str : modifier.getExtraInfo(tool, compound)) {
                    if (!str.isEmpty()) list.add(data.getColorString() + str);

                }
            }
        }

        return list;
    }

    protected void getTooltipComponents(ItemStack stack, EntityPlayer player, List<String> list) {

    }

    public ItemStack getToolForRender() {
        // lazy init
        if (this.toolForRender == null) {
            List<TinkersRebornMaterial> materials = IntStream.range(0, getToolComponentsParts().size())
                .mapToObj(this::getMaterialForPartForGuiRendering)
                .collect(Collectors.toList());

            this.toolForRender = new ItemStack(this);
            this.toolForRender.setTagCompound(this.buildRenderNBT(materials));
            this.toolForRender.setStackDisplayName(this.getLocalizedToolName());
        }

        return this.toolForRender;
    }

    @SideOnly(Side.CLIENT)
    private RenderMaterial getMaterialForPartForGuiRendering(int idx) {
        int correctId = idx % TinkersRebornRegistry.getRenderMaterialMap()
            .size() + 1;
        String renderMaterialName = ToolTags.INTERNALPREFIX + correctId;
        return TinkersRebornRegistry.getRenderMaterial(renderMaterialName);
    }

    @Nonnull
    @Override
    public ItemStack repair(ItemStack repairable, List<ItemStack> repairItems) {
        if (repairable.getItemDamage() == 0 && !ToolTagsHelper.isBroken(repairable)) {
            // undamaged and not broken - no need to repair
            return null;
        }

        // we assume the first required part exclusively determines repair material
        List<TinkersRebornMaterial> materials = ToolTagsHelper.getToolBaseMaterialsList(repairable);
        if (materials.isEmpty()) {
            return null;
        }

        // ensure the items only contain valid items
        List<ItemStack> items = TinkersRebornUtils.copyItemStackList(repairItems);
        boolean foundMatch = false;

        for (int index = 0; index < componentsParts.size(); index++) {
            if (componentsParts.get(index)
                .statusType() != MaterialStatusType.HEAD) continue;

            TinkersRebornMaterial material = materials.get(index);

            if (repairCustom(material, items) > 0) {
                foundMatch = true;
            }

            Optional<RecipeMatch.Match> match = material.matches(items);

            // not a single match -> nothing to repair with
            if (!match.isPresent()) {
                continue;
            }
            foundMatch = true;

            while ((match = material.matches(items)).isPresent()) {
                RecipeMatch.removeMatch(items, match.get());
            }
        }

        if (!foundMatch) {
            return null;
        }

        // check if all items were used
        for (int i = 0; i < repairItems.size(); i++) {
            // was non-null and did not get modified (stacksize changed or null now,
            // usually)
            if (!TinkersRebornUtils.isStackEmpty(repairItems.get(i))
                && ItemStack.areItemStacksEqual(repairItems.get(i), items.get(i))) {
                // found an item that was not touched
                return null;
            }
        }

        // now do it all over again with the real items, to actually repair \o/
        ItemStack item = repairable.copy();

        do {
            int amount = calculateRepairAmount(materials, repairItems);

            // nothing to repair with, we're therefore done
            if (amount <= 0) {
                break;
            }

            ToolTagsHelper.repairTool(item, calculateRepair(item, amount));
            // save that we repaired it :I
            ToolTagsHelper.addRepairCount(item);
        } while (item.getItemDamage() > 0);

        return item;
    }

    private int calculateRepairAmount(List<TinkersRebornMaterial> materials, List<ItemStack> repairItems) {
        Set<TinkersRebornMaterial> materialsMatched = Sets.newHashSet();
        float durability = 0f;
        // try to match each material once
        for (int index = 0; index < componentsParts.size(); index++) {
            if (componentsParts.get(index)
                .statusType() != MaterialStatusType.HEAD) continue;
            TinkersRebornMaterial material = materials.get(index);

            if (materialsMatched.contains(material)) {
                continue;
            }

            // custom repairing
            durability += repairCustom(material, repairItems) * getRepairModifierForPart(index);

            Optional<RecipeMatch.Match> matchOptional = material.matches(repairItems);
            if (matchOptional.isPresent()) {
                RecipeMatch.Match match = matchOptional.get();
                HeadMaterialStats stats = material.getStats(MaterialStatusType.HEAD);
                if (stats != null) {
                    materialsMatched.add(material);
                    durability += ((float) stats.durability * (float) match.amount * getRepairModifierForPart(index))
                        / 144f;
                    RecipeMatch.removeMatch(repairItems, match);
                }
            }
        }

        durability *= 1f + ((float) materialsMatched.size() - 1) / 9f;

        return (int) durability;
    }

    public float getRepairModifierForPart(int index) {
        return 1f;
    }

    protected int calculateRepair(ItemStack tool, int amount) {
        float origDur = ToolTagsHelper.getOriginalDurability(tool);
        float actualDur = ToolTagsHelper.getMaxDurability(tool);

        // calculate in modifiers that change the total durability of a tool, like
        // diamond
        // they should not punish the player with higher repair costs
        float durabilityFactor = actualDur / origDur;
        float increase = amount * Math.min(10f, durabilityFactor);

        increase = Math.max(increase, actualDur / 64f);
        // increase = Math.max(50, increase);

        int freeModifier = ToolTagsHelper.getModifierSlots(tool) - ToolTagsHelper.getUsedModifiers(tool);
        float mods = 1.0f;
        if (freeModifier <= 1) {
            mods = 0.85f;
        } else if (freeModifier == 2) {
            mods = 0.9f;
        } else if (freeModifier == 3) {
            mods = 0.95f;
        }

        increase *= mods;

        int repair = ToolTagsHelper.getRepairCount(tool);
        float repairDimishingReturns = (100 - repair / 2f) / 100f;
        if (repairDimishingReturns < 0.5f) {
            repairDimishingReturns = 0.5f;
        }
        increase *= repairDimishingReturns;

        return (int) Math.ceil(increase);
    }

    /**
     * for sharpening kit
     * 
     * @param material
     * @param repairItems
     * @return
     */
    private int repairCustom(TinkersRebornMaterial material, List<ItemStack> repairItems) {
        Optional<RecipeMatch.Match> matchOptional = RecipeMatch.of(TinkersRebornTools.sharpeningKit)
            .matches(repairItems);
        if (!matchOptional.isPresent()) {
            return 0;
        }

        RecipeMatch.Match match = matchOptional.get();
        for (ItemStack stacks : match.stacks) {
            // invalid material?
            if (TinkersRebornTools.sharpeningKit.getMaterial(stacks) != material) {
                return 0;
            }
        }

        RecipeMatch.removeMatch(repairItems, match);
        HeadMaterialStats stats = material.getStats(MaterialStatusType.HEAD);
        float durability = stats.durability * match.amount * TinkersRebornTools.sharpeningKit.getCost();
        durability /= TinkersRebornMaterial.VALUE_Ingot;
        return (int) (durability);
    }

    public abstract ToolBuildGuiInfo getToolBuildGuiInfo();

    @Override
    public MovingObjectPosition getMovingObjectPositionFromPlayer(World worldIn, EntityPlayer player,
        boolean useLiquids) {
        return super.getMovingObjectPositionFromPlayer(worldIn, player, useLiquids);
    }

    @Override
    public Multimap<String, AttributeModifier> getAttributeModifiers(ItemStack stack) {
        Multimap<String, AttributeModifier> multimap = super.getAttributeModifiers(stack);

        if (!ToolTagsHelper.isBroken(stack)) {
            multimap.put(
                SharedMonsterAttributes.attackDamage.getAttributeUnlocalizedName(),
                new AttributeModifier(field_111210_e, "Weapon modifier", ToolTagsHelper.getActualToolAttack(stack), 0));
        }

        ToolTagsHelper.getTraitsOrdered(stack)
            .forEach(trait -> trait.getAttributeModifiers(stack, multimap));

        return multimap;
    }

    public static boolean isEqualTinkersItem(ItemStack item1, ItemStack item2) {
        if (item1 == null || item2 == null
            || item1.getItem() != item2.getItem()
            || !(item1.getItem() instanceof ToolCore)) {
            return false;
        }
        NBTTagCompound tag1 = ToolTagsHelper.getTagSafe(item1);
        NBTTagCompound tag2 = ToolTagsHelper.getTagSafe(item2);

        NBTTagList mods1 = ToolTagsHelper.getModifiersTagList(tag1);
        NBTTagList mods2 = ToolTagsHelper.getModifiersTagList(tag2);

        if (mods1.tagCount() != mods1.tagCount()) {
            return false;
        }
        // check modifiers
        for (int i = 0; i < mods1.tagCount(); i++) {
            NBTTagCompound tag = mods1.getCompoundTagAt(i);
            ModifierNBT data = ModifierNBT.readTag(tag);
            IModifier modifier = TinkersRebornRegistry.getModifierAndTrait(data.identifier);
            if (modifier != null && !modifier.equalModifier(tag, mods2.getCompoundTagAt(i))) {
                return false;
            }
        }
        return ToolTagsHelper.getToolBaseMaterialsNBTSafe(item1)
            .equals(ToolTagsHelper.getToolBaseMaterialsNBTSafe(item2)) && // materials used
        // ToolTagsHelper.getBaseModifiersUsed(tag1) == ToolTagsHelper.getBaseModifiersUsed(tag2) && // number of free
        // modifiers used
            ToolTagsHelper.getToolOriginDataNBTSafe(item1)
                .equals(ToolTagsHelper.getToolOriginDataNBTSafe(item2)); // unmodified base stats
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

        public boolean isValid(ItemStack stack) {
            if (!TinkersRebornUtils.isStackEmpty(stack) && stack.getItem() instanceof IToolPart itp) {
                return isValid(itp, itp.getMaterial(stack));
            }
            return false;
        }

        public boolean isValid(IToolPart part, TinkersRebornMaterial material) {
            return isValidItem(part) && isValidMaterial(material);
        }

        public boolean isValidItem(IToolPart part) {
            return this.toolPart.equals(part);
        }

        public boolean isValidMaterial(TinkersRebornMaterial material) {
            return material.hasStats(this.statusType);
        }

        public List<ITrait> getApplicableTraitsForMaterial(TinkersRebornMaterial material) {
            List<ITrait> list = new ArrayList<>();
            list.addAll(material.getAllTraitsForStats(this.statusType));
            if (list.size() == 0) list.addAll(material.getAllTraitsForStats(null));
            return list;
        }

    }

}
