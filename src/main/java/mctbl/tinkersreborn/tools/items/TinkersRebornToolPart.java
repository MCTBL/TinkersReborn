package mctbl.tinkersreborn.tools.items;

import java.util.List;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mctbl.tinkersreborn.library.TinkersRebornRegistry;
import mctbl.tinkersreborn.library.items.CraftingItem;
import mctbl.tinkersreborn.library.tools.IToolPart;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

public class TinkersRebornToolPart extends CraftingItem implements IToolPart {

	public String partName;
	public String texture;
	public int cost;
	public IIcon defaultIcon;
	public String allowMaterialListName;

	public TinkersRebornToolPart(String texture, String name, String allowListName) {
		// texture -> pickaxe_head for texture
		// name -> PickaxeHead for localization
		super(null, null, "tools/parts/" + texture, TinkersRebornRegistry.parts);
		this.setUnlocalizedName("tinkersreborn." + name); // tinkersreborn.PickaxeHead
		this.partName = name;
		this.texture = texture;
		this.allowMaterialListName = allowListName;
	}

	public TinkersRebornToolPart(String texture, String name) {
		this(texture, name, "all");
	}

	@Override
	public String getItemStackDisplayName(ItemStack p_77653_1_) {
		// TODO Auto-generated method stub
		return super.getItemStackDisplayName(p_77653_1_);
	}

	@Override
	public String getUnlocalizedName() {
		// TODO Auto-generated method stub
		return super.getUnlocalizedName();
	}

	@Override
	public void getSubItems(Item b, CreativeTabs tab, List<ItemStack> list) {
		// TODO Auto-generated method stub
	}

	@Override
	public void registerIcons(IIconRegister iconRegister) {
		// TODO Auto-generated method stub
		this.icons = new IIcon[0];
		// default texture
		this.defaultIcon = iconRegister.registerIcon("tinkersreborn:" + folder + "_" + texture);
	}

	@SideOnly(Side.CLIENT)
	public IIcon getIconFromDamage(int meta) {
		if (meta > icons.length)
			return defaultIcon;

		if (icons[meta] == null)
			return defaultIcon;

		return icons[meta];
	}

	@Override
	public int getColorFromItemStack(ItemStack stack, int renderpass) {
		// TODO Auto-generated method stub
		return super.getColorFromItemStack(stack, renderpass);
	}

}
