package mctbl.tinkersreborn.tools.items;

import static mctbl.tinkersreborn.library.materials.TinkersRebornMaterial.VALUE_Ingot;

import java.util.Arrays;
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
import net.minecraft.util.MathHelper;

import mctbl.tinkersreborn.library.TinkersRebornRegistry;
import mctbl.tinkersreborn.library.materials.MaterialStatusType;
import mctbl.tinkersreborn.library.materials.TinkersRebornMaterial;
import mctbl.tinkersreborn.util.TextureHelper;
import mctbl.tinkersreborn.util.ToolTags;
import mctbl.tinkersreborn.util.ToolTagsHelper;

public class BoltCore extends TinkersRebornToolPart {

    public List<IIcon> defaultIcon;
    public List<Map<String, IIcon>> iconMap;

    public BoltCore() {
        super("bolt", "BoltCore", VALUE_Ingot * 2, MaterialStatusType.SHAFT);
    }

    @Override
    public void registerIcons(IIconRegister iconRegister) {
        String basePath = "tinkersreborn:" + folder;

        this.iconMap = Arrays.asList(new HashMap<>(), new HashMap<>());

        List<TinkersRebornMaterial> shaftStatsList = TinkersRebornRegistry.getAllMaterialList()
            .stream()
            .filter(m -> m.statsMap.containsKey(MaterialStatusType.SHAFT))
            .collect(Collectors.toList());
        for (TinkersRebornMaterial m : shaftStatsList) {
            String path = basePath + m.identifier + "_" + texture;
            if (TextureHelper.itemTextureExists(path)) {
                this.iconMap.get(0)
                    .put(m.identifier, iconRegister.registerIcon(path));
            }
        }

        List<TinkersRebornMaterial> castableHeadStatsList = TinkersRebornRegistry.getAllMaterialList()
            .stream()
            .filter(m -> m.statsMap.containsKey(MaterialStatusType.HEAD) && m.isCastable())
            .collect(Collectors.toList());
        for (TinkersRebornMaterial m : castableHeadStatsList) {
            String path = basePath + m.identifier + "_" + texture + "_2";
            if (TextureHelper.itemTextureExists(path)) {
                this.iconMap.get(0)
                    .put(m.identifier, iconRegister.registerIcon(path));
            }
        }

        // for bolt is bolt/_bolt and bolt/_bolt_2
        String path = basePath + "_" + texture;
        this.defaultIcon = Arrays.asList(iconRegister.registerIcon(path), iconRegister.registerIcon(path + "_2"));
        this.outlineIcon = iconRegister.registerIcon(basePath + "outline_" + texture);
    }

    @Override
    public int getRenderPasses(int metadata) {
        return 2;
    }

    @Override
    public boolean requiresMultipleRenderPasses() {
        return true;
    }

    @Override
    public IIcon getIcon(ItemStack stack, int pass) {
        pass = MathHelper.clamp_int(pass, 0, 1);
        Map<String, IIcon> icon = this.iconMap.get(pass);
        TinkersRebornMaterial m = pass == 0 ? this.getMaterial(stack) : this.getHeadMaterial(stack);
        return icon.getOrDefault(m.identifier, this.defaultIcon.get(pass));
    }

    @Override
    public int getColorFromItemStack(ItemStack stack, int pass) {
        pass = MathHelper.clamp_int(pass, 0, 1);
        Map<String, IIcon> icon = this.iconMap.get(pass);
        TinkersRebornMaterial m = pass == 0 ? this.getMaterial(stack) : this.getHeadMaterial(stack);
        return icon.containsKey(m.identifier) ? 0xFFFFFF : m.materialTextColor;
    }

    @Override
    public String getItemStackDisplayName(ItemStack stack) {
        TinkersRebornMaterial material = this.getHeadMaterial(stack);
        if (material == TinkersRebornMaterial.UNKNOWN) {
            return this.getLocalizedPartName();
        } else {
            return String.format(TOOLNAMEFORMATTER, material.localizedPrefix(), this.getLocalizedPartName());
        }
    }

    @Override
    public void getSubItems(Item b, CreativeTabs tab, List<ItemStack> list) {
        TinkersRebornRegistry.getAllMaterialList()
            .stream()
            .filter(m -> m.statsMap.containsKey(MaterialStatusType.HEAD) && m.isCastable())
            .forEach(m -> list.add(getNewPartWithMaterial(m.identifier)));
    }

    public TinkersRebornMaterial getHeadMaterial(ItemStack stack) {
        return TinkersRebornRegistry.getMaterialByIdentifier(readHeadNBT(stack));
    }

    @Override
    public ItemStack getNewPartWithMaterial(String identifier) {
        TinkersRebornMaterial m = TinkersRebornRegistry.getMaterialByIdentifier(identifier);
        return this.getNewPartWithMaterial(
            m,
            m.hasStats(MaterialStatusType.SHAFT) ? m : TinkersRebornRegistry.getMaterialByIdentifier("wood"));
    }

    public ItemStack getNewPartWithMaterial(TinkersRebornMaterial head, TinkersRebornMaterial shaft) {
        ItemStack stack = new ItemStack(this);
        stack.setTagCompound(this.getNewPartNBT(head, shaft));
        return stack;
    }

    private NBTTagCompound getNewPartNBT(TinkersRebornMaterial head, TinkersRebornMaterial shaft) {
        NBTTagCompound nbt = new NBTTagCompound();
        nbt.setString(ToolTags.IDENTIFIER, shaft.identifier);
        nbt.setString(ToolTags.HEADIDENTIFIER, head.identifier);
        return nbt;
    }

    public static String readHeadNBT(ItemStack stack) {
        return ToolTagsHelper.getTagSafe(stack)
            .getString(ToolTags.HEADIDENTIFIER);
    }
}
