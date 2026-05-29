package mctbl.tinkersreborn.tools.items;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.StatCollector;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mctbl.tinkersreborn.library.TinkersRebornRegistry;
import mctbl.tinkersreborn.library.items.CraftingItem;
import mctbl.tinkersreborn.library.materials.MaterialStatusType;
import mctbl.tinkersreborn.library.materials.TinkersRebornMaterial;
import mctbl.tinkersreborn.library.tools.IMaterialPart;
import mctbl.tinkersreborn.library.tools.IToolPart;
import mctbl.tinkersreborn.util.TextureHelper;

public class TinkersRebornToolPart extends CraftingItem implements IToolPart, IMaterialPart {

    public static final String LOC_NAME = "tinkersreborn.toolpart.%s";
    public static final String UNLOC_NAME = "tinkersreborn.toolpart.%s.%s";

    public String partName;
    public String texture;
    public int cost;
    public IIcon defaultIcon;
    public Map<Integer, IIcon> icons;
    public MaterialStatusType allowType; // TODO shard is null (maybe sharpen kit is too)
    public String partLocName;

    public TinkersRebornToolPart(String texture, String name, MaterialStatusType allowType) {
        // texture -> pickaxe_head for texture
        // name -> PickaxeHead for localization
        super(null, null, "tools/parts/" + texture + "/", TinkersRebornRegistry.partsTab);
        this.texture = texture;
        this.partName = name;
        this.allowType = allowType;
        this.partLocName = String.format(LOC_NAME, name);
        this.setUnlocalizedName("tinkersreborn." + name); // tinkersreborn.PickaxeHead
    }

    public TinkersRebornToolPart(String texture, String name) {
        this(texture, name, MaterialStatusType.HEAD);
    }

    @Override
    public String getItemStackDisplayName(ItemStack stack) {
        int id = getMaterialId(stack);
        if (id == -1) {
            return super.getItemStackDisplayName(stack);
        } else {
            String partBaseName = StatCollector.translateToLocal(this.partLocName); // "%%material Tool Rod"
            String materialName = StatCollector
                .translateToLocal(TinkersRebornRegistry.getMaterialById(id).localizationIdentifier); // "Wood"
            return partBaseName.replace("%%material", materialName);
        }
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        int id = getMaterialId(stack);
        return id == -1 ? super.getUnlocalizedName()
            : String.format(UNLOC_NAME, partName, TinkersRebornRegistry.getMaterialById(id).identifier);
    }

    @Override
    public void getSubItems(Item b, CreativeTabs tab, List<ItemStack> list) {
        for (Entry<Integer, IIcon> e : this.icons.entrySet()) {
            list.add(new ItemStack(this, 1, e.getKey()));
        }
    }

    @Override
    public void registerIcons(IIconRegister iconRegister) {
        List<TinkersRebornMaterial> statsList = TinkersRebornRegistry.allMaterialsList.stream()
            .filter(m -> m.statsMap.containsKey(this.allowType))
            .collect(Collectors.toList());
        this.icons = new HashMap<>();
        for (TinkersRebornMaterial m : statsList) {
            String path = "tinkersreborn:" + folder + m.identifier + "_" + texture;
            if (TextureHelper.itemTextureExists(path)) {
                this.icons.put(m.materialId, iconRegister.registerIcon(path));
            }
        }
        // default texture
        this.defaultIcon = iconRegister.registerIcon("tinkersreborn:" + folder + "_" + texture);
    }

    @SideOnly(Side.CLIENT)
    public IIcon getIconFromDamage(int meta) {
        if (this.icons.containsKey(meta)) return this.icons.get(meta);
        return this.defaultIcon;
    }

    @Override
    public int getColorFromItemStack(ItemStack stack, int renderpass) {
        int matId = this.getMaterialId(stack);
        if (this.icons.containsKey(matId)) {
            return super.getColorFromItemStack(stack, renderpass);
        } else {
            return TinkersRebornRegistry.getMaterialById(matId).materialTextColor;
        }
        // return super.getColorFromItemStack(stack, renderpass);
    }

    @Override
    public int getMaterialId(ItemStack stack) {
        return stack.getItemDamage();
        // return -1;
    }

}
