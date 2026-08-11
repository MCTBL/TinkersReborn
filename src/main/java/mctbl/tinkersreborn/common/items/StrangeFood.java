package mctbl.tinkersreborn.common.items;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mctbl.tinkersreborn.library.TinkersRebornRegistry;
import mctbl.tinkersreborn.util.ColorUtil;
import mctbl.tinkersreborn.util.TinkersStr;

public class StrangeFood extends SpecialFood {

    public StrangeFood() {
        super(
            new int[] { 2, 2, 4 },
            new float[] { 1f, 1f, 0.6f },
            new String[] { "edibleslime", "edibleblood", "bacon" },
            new String[] { "food/edibleslime", "food/edibleblood", "food/bacon" });
        this.setHasSubtypes(true);
        this.setUnlocalizedName("tinkersreborn.strangefood");
        this.setCreativeTab(TinkersRebornRegistry.miscTab);
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        int arr = MathHelper.clamp_int(stack.getItemDamage(), 0, unlocalizedNames.length);
        return "tinkersreborn.strangefood." + unlocalizedNames[arr];
    }

    @Override
    protected void onFoodEaten(ItemStack stack, World world, EntityPlayer player) {
        if (stack.getItemDamage() == 1) player.addPotionEffect(new PotionEffect(Potion.field_76434_w.id, 20 * 15, 0));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> list, boolean par4) {
        int type = stack.getItemDamage();
        switch (type) {
            case 0:
                list.add(ColorUtil.addAqua(ColorUtil.addItalic(TinkersStr.strangefood1.toString())));
                list.add(ColorUtil.addAqua(ColorUtil.addItalic(TinkersStr.strangefood2.toString())));
                break;
            case 1:
                list.add(ColorUtil.addDarkRed(ColorUtil.addItalic(TinkersStr.strangefood3.toString())));
                list.add(ColorUtil.addDarkRed(ColorUtil.addItalic(TinkersStr.strangefood4.toString())));
                break;
            case 2:
                list.add(ColorUtil.addRed(TinkersStr.strangefood7.toString()));
                break;
            default:
        }
    }
}
