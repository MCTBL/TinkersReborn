package mctbl.tinkersreborn.tools.entity;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class EntityBolt extends EntityArrow {

    public EntityBolt(World world) {
        super(world);
    }

    public EntityBolt(World world, double d, double d1, double d2) {
        super(world, d, d1, d2);
    }

    public EntityBolt(World world, EntityPlayer player, float speed, float inaccuracy, float power, ItemStack stack,
        ItemStack launchingStack) {
        super(world, player, speed, inaccuracy, power, stack, launchingStack);
    }

    @Override
    public double getGravity() {
        return 0.065;
    }

    @Override
    public double getSlowdown() {
        return 0.015;
    }

}
