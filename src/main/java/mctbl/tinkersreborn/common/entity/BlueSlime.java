package mctbl.tinkersreborn.common.entity;

import net.minecraft.world.World;

import mctbl.tinkersreborn.library.entity.SlimeBase;

public class BlueSlime extends SlimeBase {

    public BlueSlime(World world) {
        super(world);
    }

    @Override
    protected String getSlimeParticle() {
        return "tinkers.blueslime";
    }

    @Override
    protected SlimeBase createInstance(World world) {
        return new BlueSlime(world);
    }

}
