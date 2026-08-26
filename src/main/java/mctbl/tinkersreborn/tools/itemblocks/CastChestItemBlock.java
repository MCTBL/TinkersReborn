package mctbl.tinkersreborn.tools.itemblocks;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

import mctbl.tinkersreborn.util.TinkersRebornUtils;
import mctbl.tinkersreborn.util.TinkersStr;
import mctbl.tinkersreborn.util.ToolTagsHelper;

public class CastChestItemBlock extends ItemBlock {

    public CastChestItemBlock(Block b) {
        super(b);
        this.setMaxDamage(0);
        this.setUnlocalizedName("tinkersreborn.CastChest");
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        return "tinkersreborn.CastChest";
    }

    @Override
    public String getItemStackDisplayName(ItemStack p_77653_1_) {
        return TinkersRebornUtils.translate("tinkersreborn.CastChest.name");
    }

    @Override
    public void addInformation(ItemStack itemStack, EntityPlayer player, List<String> list, boolean isDetail) {
        if (TinkersRebornUtils.isShiftKeyDown()) {
            List<ItemStack> inventoryFromNBT = this.readInventoryFromNBT(ToolTagsHelper.getTagSafe(itemStack));
            for (ItemStack stack : inventoryFromNBT) {
                list.add("- " + stack.getDisplayName() + " x " + stack.stackSize);
            }
        } else {
            list.add(TinkersStr.holdShift.toString());
        }

    }

    public List<ItemStack> readInventoryFromNBT(NBTTagCompound tags) {
        List<ItemStack> itemStackList = new ArrayList<>();
        NBTTagList nbttaglist = tags.getCompoundTag("Inventory")
            .getTagList("Items", 10);

        for (int i = 0; i < nbttaglist.tagCount(); ++i) {
            NBTTagCompound nbttagcompound1 = nbttaglist.getCompoundTagAt(i);
            itemStackList.add(ItemStack.loadItemStackFromNBT(nbttagcompound1));
        }

        return itemStackList;
    }
}
