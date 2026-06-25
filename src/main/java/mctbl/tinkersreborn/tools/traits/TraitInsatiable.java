package mctbl.tinkersreborn.tools.traits;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;

import mctbl.tinkersreborn.library.entity.TinkersEntityProperties;
import mctbl.tinkersreborn.library.tools.traits.AbstractTrait;

public class TraitInsatiable extends AbstractTrait {

    public static final String INSATIABLE_KEY = "insatiable";

    public TraitInsatiable() {
        super(INSATIABLE_KEY, EnumChatFormatting.DARK_PURPLE);
    }

    @Override
    public float damage(ItemStack tool, EntityLivingBase player, EntityLivingBase target, float damage, float newDamage,
        boolean isCritical) {

        TinkersEntityProperties props = TinkersEntityProperties.getProps(player);
        // LOG.info("TraitInsatiable cause more damage {} + {}", newDamage, props.getLevel(INSATIABLE_KEY) / 3f);
        newDamage += props.getLevel(INSATIABLE_KEY) / 3f;

        return super.damage(tool, player, target, damage, newDamage, isCritical);
    }

    @Override
    public void afterHit(ItemStack tool, EntityLivingBase player, EntityLivingBase target, float damageDealt,
        boolean wasCritical, boolean wasHit) {
        if (!player.worldObj.isRemote) {
            TinkersEntityProperties props = TinkersEntityProperties.getProps(player);
            int level = 1;
            level += props.getLevel(INSATIABLE_KEY);

            level = Math.min(10, level);

            // LOG.info("Your Insatiable level is {}", level);
            props.apply(INSATIABLE_KEY, 5 * 20, level);
        }

    }

    @Override
    public int onToolDamage(ItemStack tool, int damage, int newDamage, EntityLivingBase entity) {
        TinkersEntityProperties props = TinkersEntityProperties.getProps(entity);
        // LOG.info(
        // "TraitInsatiable cause more damage on your tool {} + {}",
        // newDamage,
        // props.getLevel(INSATIABLE_KEY) / 3);
        newDamage += props.getLevel(INSATIABLE_KEY) / 3;
        return super.onToolDamage(tool, damage, newDamage, entity);
    }
}
