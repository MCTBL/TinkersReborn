package mctbl.tinkersreborn.tools.traits;

import net.minecraftforge.common.MinecraftForge;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import mctbl.tinkersreborn.library.event.TinkerToolEvent;
import mctbl.tinkersreborn.library.tools.traits.AbstractTrait;
import mctbl.tinkersreborn.util.ToolTagsHelper;

public class TraitSplitting extends AbstractTrait {

    private static final float DOUBLESHOT_CHANCE = 0.5f;

    public TraitSplitting() {
        super("splitting", 0xEDEBCA);

        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onBowShooting(TinkerToolEvent.OnBowShoot event) {

        if (ToolTagsHelper.hasModifier(event.ammo, this.identifier) && random.nextFloat() < DOUBLESHOT_CHANCE) {
            event.setProjectileCount(2);
            event.setConsumeAmmoPerProjectile(false);
            event.setConsumeDurabilityPerProjectile(false);
            event.setBonusInaccuracy(3f);
        }
    }
}
