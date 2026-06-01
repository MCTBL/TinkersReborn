package mctbl.tinkersreborn.common.items;

import java.util.List;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mctbl.tinkersreborn.library.TinkersRebornRegistry;
import mctbl.tinkersreborn.util.ColorUtil;

public class GoldenHead extends ItemFood {

    public GoldenHead(int healAmount, float saturationModifier, boolean isWolfsFavoriteMeat) {
        super(healAmount, saturationModifier, isWolfsFavoriteMeat);
        this.setCreativeTab(TinkersRebornRegistry.blockTab);
        this.setHasSubtypes(true);
        this.setAlwaysEdible();
        this.setPotionEffect(Potion.regeneration.id, 10, 0, 1.0F);
        this.setUnlocalizedName("tinkersreborn.goldenhead");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean hasEffect(ItemStack par1ItemStack) {
        return par1ItemStack.getItemDamage() > 0;
    }

    @Override
    @SideOnly(Side.CLIENT)
    /**
     * Return an item rarity from EnumRarity
     */
    public EnumRarity getRarity(ItemStack par1ItemStack) {
        return par1ItemStack.getItemDamage() == 0 ? EnumRarity.rare : EnumRarity.epic;
    }

    @Override
    protected void onFoodEaten(ItemStack par1ItemStack, World par2World, EntityPlayer par3EntityPlayer) {
        if (par1ItemStack.getItemDamage() > 0) {
            if (!par2World.isRemote) {
                par3EntityPlayer.addPotionEffect(new PotionEffect(Potion.regeneration.id, 600, 3));
                par3EntityPlayer.addPotionEffect(new PotionEffect(Potion.resistance.id, 6000, 0));
                par3EntityPlayer.addPotionEffect(new PotionEffect(Potion.fireResistance.id, 6000, 0));
            }
        } else {
            super.onFoodEaten(par1ItemStack, par2World, par3EntityPlayer);
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    /**
     * returns a list of items with the same ID, but different meta (eg: dye returns 16 items)
     */
    public void getSubItems(Item b, CreativeTabs par2CreativeTabs, List<ItemStack> list) {
        list.add(new ItemStack(b, 1, 0));
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void registerIcons(IIconRegister iconRegister) {
        this.itemIcon = iconRegister.registerIcon("tinkersreborn:skull_char_gold");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> list, boolean advanced) {
        list.add(
            ColorUtil.addDarkPurple(
                ColorUtil.addItalic(StatCollector.translateToLocal("tinkersreborn.goldenhead1.tooltip"))));
        list.add(
            ColorUtil.addDarkPurple(
                ColorUtil.addItalic(StatCollector.translateToLocal("tinkersreborn.goldenhead2.tooltip"))));
    }
}
