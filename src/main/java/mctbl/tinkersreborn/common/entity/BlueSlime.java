package mctbl.tinkersreborn.common.entity;

import net.minecraft.item.Item;
import net.minecraft.world.World;

import mctbl.tinkersreborn.common.TinkersRebornGeneral;
import mctbl.tinkersreborn.library.entity.SlimeBase;

public class BlueSlime extends SlimeBase {

    public BlueSlime(World world) {
        super(world);
    }

    @Override
    protected SlimeBase createInstance(World world) {
        return new BlueSlime(world);
    }

    @Override
    protected Item getDropItem() {
        return TinkersRebornGeneral.strangeFood;
    }
}
