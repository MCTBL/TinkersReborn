package mctbl.tinkersreborn.tools.items;

import static mctbl.tinkersreborn.library.materials.TinkersRebornMaterial.VALUE_Ingot;

import java.util.List;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

import mctbl.tinkersreborn.library.materials.MaterialStatusType;

public class BoltCore extends TinkersRebornToolPart {

    public IIcon[] defaultIcon;
    public IIcon[][] icons;

    public BoltCore() {
        super("bolt", "BoltCore", VALUE_Ingot * 2, MaterialStatusType.HANDLE);
    }

    @Override
    public void registerIcons(IIconRegister iconRegister) {
        // TODO Auto-generated method stub
        this.icons = new IIcon[0][0];
        // for bolt is bolt/_bolt and bolt/_bolt_2
        String path = "tinkersreborn:" + folder + "_" + texture;
        this.defaultIcon = new IIcon[] { iconRegister.registerIcon(path), iconRegister.registerIcon(path + "_2") };
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
    public IIcon getIconFromDamageForRenderPass(int meta, int pass) {
        if (meta > icons[pass].length || icons[pass][meta] == null) {
            return defaultIcon[pass];
        } else {
            return icons[pass][meta];
        }
    }

    @Override
    public void getSubItems(Item b, CreativeTabs tab, List<ItemStack> list) {
        // TODO Auto-generated method stub
    }

    @Override
    public int getColorFromItemStack(ItemStack stack, int renderpass) {
        // TODO Auto-generated method stub
        return super.getColorFromItemStack(stack, renderpass);
    }

}
