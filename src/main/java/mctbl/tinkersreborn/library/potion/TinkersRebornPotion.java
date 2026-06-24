package mctbl.tinkersreborn.library.potion;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;

import mctbl.tinkersreborn.TinkersRebornConfig;

public class TinkersRebornPotion extends Potion {

    private final boolean show;

    public TinkersRebornPotion(ResourceLocation location, boolean badEffect, boolean showInInventory) {
        this(location, badEffect, showInInventory, 0xffffff);
    }

    public TinkersRebornPotion(ResourceLocation location, boolean badEffect, boolean showInInventory, int color) {
        super(TinkersRebornConfig.potionIdBias, badEffect, color);
        setPotionName("potion." + location.getResourcePath());

        this.show = showInInventory;
    }

    @Override
    public boolean shouldRenderInvText(PotionEffect effect) {
        return show;
    }

    public PotionEffect apply(EntityLivingBase entity, int duration) {
        return apply(entity, duration, 0);
    }

    public PotionEffect apply(EntityLivingBase entity, int duration, int level) {
        PotionEffect effect = new PotionEffect(this.id, duration, level, false);
        entity.addPotionEffect(effect);
        return effect;
    }

    public int getLevel(EntityLivingBase entity) {
        PotionEffect effect = entity.getActivePotionEffect(this);
        if (effect != null) {
            return effect.getAmplifier();
        }
        return 0;
    }
}
