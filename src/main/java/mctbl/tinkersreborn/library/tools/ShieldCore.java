package mctbl.tinkersreborn.library.tools;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import mctbl.tinkersreborn.library.TinkersRebornRegistry;
import mctbl.tinkersreborn.util.ToolTagsHelper;

public abstract class ShieldCore extends ToolCore {

    protected ShieldCore(String toolTypeName, int partAmount) {
        super(toolTypeName, partAmount);
        this.setCreativeTab(TinkersRebornRegistry.shieldTab);
    }

    @Override
    public float damageCutoff() {
        return 5.0F;
    }

    @Override
    public float damagePotential() {
        return 0.5F;
    }

    @Override
    public int getMaxItemUseDuration(ItemStack stack) {
        return 72000;
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {

        if (ToolTagsHelper.isBroken(stack)) {
            return stack;
        }

        // 两端都做判断；服务端结果为准，客户端冷却状态通过包同步
        // if (ShieldHelper.isDisabled(player)) {
        // return stack;
        // }

        // 防止重复右键重置本轮招架状态
        if (player.isUsingItem()) {
            return stack;
        }

        player.setItemInUse(stack, getMaxItemUseDuration(stack));

        // Forge的物品使用开始事件可能拒绝本次使用
        // if (player.getItemInUse() == stack) {
        // if (!world.isRemote) {
        // ShieldHelper.beginUse(player, stack);
        // }
        // }

        return stack;
    }

    @Override
    public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        // has to be done in onUpdate because onTickUsing is too early and gets
        // overwritten. bleh.
        this.preventSlowDown(entityIn, this.walkingSpeedReduction());

        super.onUpdate(stack, worldIn, entityIn, itemSlot, isSelected);
    }

    public abstract float blockingAngel();

    public abstract float damageReduction();

    public abstract float walkingSpeedReduction();
}
