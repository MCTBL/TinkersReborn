package mctbl.tinkersreborn.tools.items;

import static mctbl.tinkersreborn.util.TinkersRebornUtils.translate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mctbl.tinkersreborn.library.TinkersRebornRegistry;
import mctbl.tinkersreborn.library.items.CraftingItem;
import mctbl.tinkersreborn.library.materials.MaterialStatusType;
import mctbl.tinkersreborn.library.materials.TinkersRebornMaterial;
import mctbl.tinkersreborn.library.tools.IToolPart;
import mctbl.tinkersreborn.util.TextureHelper;
import mctbl.tinkersreborn.util.TinkersStr;
import mctbl.tinkersreborn.util.ToolTags;
import mctbl.tinkersreborn.util.ToolTagsHelper;

public class TinkersRebornToolPart extends CraftingItem implements IToolPart {

    public static final String toolNameFormatter = TinkersStr.tooNamePattern.toString();

    public String partName;
    public String texture;
    public int cost;
    public IIcon defaultIcon;
    public IIcon outlineIcon;
    public Map<String, IIcon> iconMap;
    public MaterialStatusType allowType; // TODO shard is null (maybe sharpen kit is too)

    public TinkersRebornToolPart(String texture, String partName, int cost, MaterialStatusType allowType) {
        // texture -> pickaxe_head for texture
        // name -> PickaxeHead for localization
        super(null, null, "tools/parts/" + texture + "/", TinkersRebornRegistry.partsTab);
        this.texture = texture;
        this.partName = partName;
        this.allowType = allowType;
        this.setUnlocalizedName("tinkersreborn." + partName); // tinkersreborn.PickaxeHead
        this.cost = cost;

        TinkersRebornRegistry.registerToolPart(this);
    }

    public TinkersRebornToolPart(String texture, String partName, int cost) {
        this(texture, partName, cost, MaterialStatusType.HEAD);
    }

    public String getUnlocalizedToolName() {
        return "tinkersreborn.toolpart." + this.partName;
    }

    public String getLocalizedPartName() {
        return translate(this.getUnlocalizedToolName());
    }

    @Override
    public String getItemStackDisplayName(ItemStack stack) {
        TinkersRebornMaterial material = this.getMaterial(stack);
        if (material == TinkersRebornMaterial.UNKNOWN) {
            return this.getLocalizedPartName();
        } else {
            return String.format(toolNameFormatter, material.localizedPrefix(), this.getLocalizedPartName());
        }
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        TinkersRebornMaterial material = this.getMaterial(stack);
        return material == TinkersRebornMaterial.UNKNOWN ? super.getUnlocalizedName()
            : String.format(toolNameFormatter, material.localizedPrefix(), this.getLocalizedPartName());
    }

    @Override
    public void getSubItems(Item b, CreativeTabs tab, List<ItemStack> list) {
        List<TinkersRebornMaterial> statsList = TinkersRebornRegistry.allMaterialsList.stream()
            .filter(m -> m.statsMap.containsKey(this.allowType))
            .collect(Collectors.toList());
        for (TinkersRebornMaterial m : statsList) {
            list.add(this.getNewPartWithMaterial(m));
        }
    }

    @Override
    public void registerIcons(IIconRegister iconRegister) {
        List<TinkersRebornMaterial> statsList = TinkersRebornRegistry.allMaterialsList.stream()
            .filter(m -> m.statsMap.containsKey(this.allowType))
            .collect(Collectors.toList());
        this.iconMap = new HashMap<>();
        for (TinkersRebornMaterial m : statsList) {
            String path = "tinkersreborn:" + folder + m.identifier + "_" + texture;
            if (TextureHelper.itemTextureExists(path)) {
                this.iconMap.put(m.identifier, iconRegister.registerIcon(path));
            }
        }
        // default texture
        this.defaultIcon = iconRegister.registerIcon("tinkersreborn:" + folder + "_" + texture);
        if (TextureHelper.itemTextureExists("tinkersreborn:" + folder + "outline_" + texture)) {
            this.outlineIcon = iconRegister.registerIcon("tinkersreborn:" + folder + "outline_" + texture);
        } else {
            this.outlineIcon = this.defaultIcon;
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
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(ItemStack stack, int pass) {
        TinkersRebornMaterial material = this.getMaterial(stack);
        if (this.iconMap != null) return this.iconMap.getOrDefault(material.identifier, this.defaultIcon);
        return this.defaultIcon;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public int getColorFromItemStack(ItemStack stack, int renderpass) {
        TinkersRebornMaterial material = this.getMaterial(stack);
        if (this.iconMap != null && this.iconMap.containsKey(material.identifier)) {
            return super.getColorFromItemStack(stack, renderpass);
        } else {
            return material.materialTextColor;
        }
    }

    @Override
    public TinkersRebornMaterial getMaterial(ItemStack stack) {
        return TinkersRebornRegistry.getMaterialByIdentifier(readNBT(stack));
    }

    public int getCost() {
        return this.cost;
    }

    public ItemStack getNewPartWithMaterial(TinkersRebornMaterial material) {
        if (this.allowType != null && !material.hasStats(this.allowType)) {
            return null;
        }
        return this.getNewPartWithMaterial(material.identifier);
    }

    public ItemStack getNewPartWithMaterial(String identifier) {
        ItemStack stack = new ItemStack(this);
        stack.setTagCompound(this.getNewPartNBT(identifier));
        return stack;
    }

    public NBTTagCompound getNewPartNBT(String identifier) {
        NBTTagCompound nbt = new NBTTagCompound();
        nbt.setString(ToolTags.IDENTIFIER, identifier);
        return nbt;
    }

    public static String readNBT(ItemStack stack) {
        return ToolTagsHelper.getTagSafe(stack)
            .getString(ToolTags.IDENTIFIER);
    }
}
