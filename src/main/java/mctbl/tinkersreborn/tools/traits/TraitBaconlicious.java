package mctbl.tinkersreborn.tools.traits;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import mctbl.tinkersreborn.common.TinkersRebornGeneral;
import mctbl.tinkersreborn.library.tools.traits.AbstractTrait;
import mctbl.tinkersreborn.library.utils.BlockPos;

public class TraitBaconlicious extends AbstractTrait {

    public TraitBaconlicious() {
        super("baconlicious", 0xffaaaa);
    }

    @Override
    public void afterBlockBreak(ItemStack tool, World world, Block block, BlockPos pos, EntityLivingBase player,
        boolean wasEffective) {
        dropBacon(player.worldObj, pos.getX(), pos.getY(), pos.getZ(), 0.005f);
    }

    @Override
    public void afterHit(ItemStack tool, EntityLivingBase player, EntityLivingBase target, float damageDealt,
        boolean wasCritical, boolean wasHit) {
        if (!target.isEntityAlive() && wasHit) {
            dropBacon(target.worldObj, target.posX, target.posY, target.posZ, 0.05f);
        }
    }

    protected void dropBacon(World world, double x, double y, double z, float chance) {
        if (!world.isRemote && random.nextFloat() < chance) {
            EntityItem entity = new EntityItem(world, x, y, z, new ItemStack(TinkersRebornGeneral.strangeFood, 1, 0));
            world.spawnEntityInWorld(entity);
        }
    }
}
