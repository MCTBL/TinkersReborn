package mctbl.tinkersreborn.tools.items;

import java.util.List;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mctbl.tinkersreborn.library.TinkersRebornRegistry;

public class MaterialItem extends Item {

    String textureName;
    String name;

    public MaterialItem(String name, String textureName) {
        super();
        this.setCreativeTab(TinkersRebornRegistry.miscTab);
        this.setUnlocalizedName("tinkersreborn.material." + name);
        this.name = name;
        this.hasSubtypes = false;
        this.textureName = textureName;
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        return this.getUnlocalizedName();
    }

    @Override
    public String getUnlocalizedName() {
        return "tinkersreborn.material." + this.name;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> list, boolean advanced) {}

    @Override
    public void registerIcons(IIconRegister register) {
        this.itemIcon = register.registerIcon("tinkersreborn:materials/material_" + textureName);
    }

}
