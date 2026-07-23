package mctbl.tinkersreborn.tools.traits;

import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import mctbl.tinkersreborn.library.event.ProjectileEvent;
import mctbl.tinkersreborn.library.tools.traits.AbstractTrait;
import mctbl.tinkersreborn.util.ToolTagsHelper;

public class TraitBreakable extends AbstractTrait {

    private static final float BREAKCHANCE = 0.5f;

    public TraitBreakable() {
        super("breakable", 0xffffff);

        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onHitBlock(ProjectileEvent.OnHitBlock event) {
        if (event.projectile != null && !event.projectile.worldObj.isRemote) {

            ItemStack itemStack = event.projectile.getEntityItem();
            if (ToolTagsHelper.hasModifier(itemStack, this.identifier) && random.nextFloat() < BREAKCHANCE) {
                event.projectile.setDead();
            }
        }
    }
}
