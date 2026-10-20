package mctbl.tinkersreborn.common.entity;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import mctbl.tinkersreborn.common.TinkersRebornGeneral;
import mctbl.tinkersreborn.library.entity.SlimeBase;

public class BlueSlime extends SlimeBase {

    private static final int BLUE_SLIME_BALL_META = 2;

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

    @Override
    public EntityItem func_145778_a(Item itemIn, int size, float offsetY) {
        return this.entityDropItem(new ItemStack(itemIn, size, BLUE_SLIME_BALL_META), offsetY);
    }

    @Override
    protected void dropFewItems(boolean par1, int par2) {
        int size = this.getSlimeSize();
        Item j = this.getDropItem();

        if (j != null && (rand.nextInt(2) == 0) || size >= 8) {
            int k = rand.nextInt(3) + rand.nextInt(this.getSlimeSize());

            if (par2 > 0) {
                k += this.rand.nextInt(par2 + 1);
            }

            for (int l = 0; l < k; ++l) {
                this.entityDropItem(new ItemStack(j, 1, BLUE_SLIME_BALL_META), 1);
            }
        }
    }
}
