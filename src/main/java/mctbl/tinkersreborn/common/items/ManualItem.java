package mctbl.tinkersreborn.common.items;

import java.util.List;
import java.util.Map.Entry;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

import mctbl.tinkersreborn.common.TinkersRebornGeneral;
import mctbl.tinkersreborn.common.manuals.TinkersRebornManualDataBase;
import mctbl.tinkersreborn.library.TinkersRebornRegistry;
import mctbl.tinkersreborn.library.manuals.ManualBookData;
import mctbl.tinkersreborn.library.manuals.ManualBookDefinition;
import mctbl.tinkersreborn.util.TinkersRebornUtils;
import mctbl.tinkersreborn.util.ToolTagsHelper;

public class ManualItem extends Item {

    protected IIcon cover;
    protected IIcon book;

    public ManualItem() {
        super();
        this.setCreativeTab(TinkersRebornRegistry.miscTab);
        this.setUnlocalizedName("tinkerbook");
    }

    @Override
    public ItemStack onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer player) {
        TinkersRebornGeneral.proxy.openManual(itemStackIn);
        return itemStackIn;
    }

    @Override
    public String getItemStackDisplayName(ItemStack stack) {
        String name = ToolTagsHelper.getTagSafe(stack)
            .getString("title");
        if (!name.isEmpty()) {
            if (TinkersRebornUtils.canTranslate(name)) {
                return TinkersRebornUtils.translate(name);
            } else {
                return name;
            }
        } else {
            return TinkersRebornUtils.translate("tinkersreborn.manuals.unname");
        }
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> list, boolean detail) {
        String tooltip = ToolTagsHelper.getTagSafe(stack)
            .getString("tooltip");
        if (!tooltip.isEmpty() && TinkersRebornUtils.canTranslate(tooltip)) {
            list.add(TinkersRebornUtils.translate(tooltip));
        }
    }

    @Override
    public void getSubItems(Item item, CreativeTabs tab, List<ItemStack> list) {
        for (Entry<String, ManualBookData> entry : TinkersRebornManualDataBase.getBooks()
            .entrySet()) {
            ManualBookData bookData = entry.getValue();
            ManualBookDefinition bookDefinition = bookData.getDefinition();
            NBTTagCompound newTag = new NBTTagCompound();
            newTag.setInteger("color", bookDefinition.getColor());
            newTag.setString("title", bookDefinition.getTitle());
            newTag.setString("tooltip", bookDefinition.getTooltip());
            newTag.setString("name", entry.getKey());
            ItemStack temp = new ItemStack(item);
            temp.setTagCompound(newTag);
            list.add(temp);
        }
    }

    @Override
    public void registerIcons(IIconRegister register) {
        this.book = register.registerIcon("tinkersreborn:tinkerbook");
        this.cover = register.registerIcon("tinkersreborn:tinkerbook_cover");
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
    public IIcon getIcon(ItemStack stack, int renderPass) {
        return renderPass == 0 ? book : cover;
    }

    @Override
    public int getColorFromItemStack(ItemStack stack, int renderPass) {
        return renderPass == 0 ? 0xFFFFFF
            : ToolTagsHelper.getTagSafe(stack)
                .getInteger("color");
    }
}
