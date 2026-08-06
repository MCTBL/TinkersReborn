package mctbl.tinkersreborn.common.items;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mctbl.tinkersreborn.common.player.PlayerHeartCanisterExtended;
import mctbl.tinkersreborn.common.player.TinkersRebornPlayerStats;
import mctbl.tinkersreborn.library.TinkersRebornRegistry;
import mctbl.tinkersreborn.library.items.CraftingItem;
import mctbl.tinkersreborn.util.TinkersStr;

public class HeartCanister extends CraftingItem {

    public HeartCanister() {
        super(
            new String[] { "empty", "miniheart.red", "red", "miniheart.yellow", "yellow", "miniheart.green", "green" },
            new String[] { "canister_empty", "miniheart_red", "canister_red", "miniheart_yellow", "canister_yellow",
                "miniheart_green", "canister_green" },
            "heart/",
            TinkersRebornRegistry.miscTab);
        this.setMaxStackSize(10);
        this.setUnlocalizedName("tinkersreborn.canister");
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        if (unlocalizedNames != null) {
            int arr = MathHelper.clamp_int(stack.getItemDamage(), 0, unlocalizedNames.length - 1);
            return "tinkersreborn.canister." + unlocalizedNames[arr];
        } else {
            return this.getUnlocalizedName();
        }
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        int meta = stack.getItemDamage();
        if (meta == 1 || meta == 3 || meta == 5) {
            player.setItemInUse(stack, this.getMaxItemUseDuration(stack));
        } else if (!world.isRemote && (meta == 2 || meta == 4 || meta == 6)) {
            TinkersRebornPlayerStats stats = TinkersRebornPlayerStats.get(player);
            if (stats != null && stats.heartCanister != null) {
                PlayerHeartCanisterExtended hearts = stats.heartCanister;
                ItemStack slotStack = hearts.getStackInSlot(meta / 2 - 1);
                if (slotStack == null) {
                    hearts.setInventorySlotContents(meta / 2 - 1, new ItemStack(this, 1, meta));
                    stack.stackSize--;
                } else if (slotStack.getItem() == this && slotStack.stackSize < this.maxStackSize) {
                    slotStack.stackSize++;
                    stack.stackSize--;
                }
                hearts.recalculateHealth(player, stats);
            }
        }
        return stack;
    }

    @Override
    public ItemStack onEaten(ItemStack stack, World world, EntityPlayer player) {
        int meta = stack.getItemDamage();
        --stack.stackSize;
        player.heal((meta + 1) * 10.0F);
        world.playSoundAtEntity(player, "random.burp", 0.5F, world.rand.nextFloat() * 0.1F + 0.9F);
        return stack;
    }

    @Override
    public EnumAction getItemUseAction(ItemStack par1ItemStack) {
        return EnumAction.eat;
    }

    @Override
    public int getMaxItemUseDuration(ItemStack par1ItemStack) {
        return 32;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> list, boolean par4) {
        int meta = stack.getItemDamage();
        if (meta == 0 || meta % 2 == 1) list.add(TinkersStr.crafting.toString());
        else {
            list.add(TinkersStr.accessory.toString());
            list.add(TinkersStr.canister.toString());
        }

        switch (meta) {
            case 1:
                list.add(TinkersStr.canisterRed1.toString());
                list.add(TinkersStr.canisterRed2.toString());
                break;
            case 3:
                list.add(TinkersStr.canisterYellow1.toString());
                list.add(TinkersStr.canisterYellow2.toString());
                break;
            case 5:
                list.add(TinkersStr.canisterGreen1.toString());
                list.add(TinkersStr.canisterGreen2.toString());
                break;
            default:
        }
    }

    public boolean canEquipAccessory(ItemStack item, int slot) {
        int type = item.getItemDamage();
        return ((type == 2 && slot == 6) || (type == 4 && slot == 5) || (type == 6 && slot == 4));
    }

    public int getHealthBoost(ItemStack item) {
        return item.stackSize * 2;
    }
}
