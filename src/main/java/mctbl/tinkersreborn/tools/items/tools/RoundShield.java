package mctbl.tinkersreborn.tools.items.tools;

import java.util.List;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

import mctbl.tinkersreborn.TinkersReborn;
import mctbl.tinkersreborn.library.event.Sounds;
import mctbl.tinkersreborn.library.materials.MaterialStatusType;
import mctbl.tinkersreborn.library.materials.TinkersRebornMaterial;
import mctbl.tinkersreborn.library.tools.ShieldCore;
import mctbl.tinkersreborn.library.tools.ToolNBT;
import mctbl.tinkersreborn.tools.TinkersRebornTools;
import mctbl.tinkersreborn.tools.gui.ToolBuildGuiInfo;
import mctbl.tinkersreborn.util.ShieldHelper;
import mctbl.tinkersreborn.util.ToolTagsHelper;

public class RoundShield extends ShieldCore {

    public static final float DURABILITY_MODIFIER = 1.25f;

    public RoundShield() {
        super("RoundShield", 3);

        this.componentsParts
            .add(new ToolPartRecord(TinkersRebornTools.shieldCore, MaterialStatusType.HEAD, "_roundshield_head"));
        this.componentsParts
            .add(new ToolPartRecord(TinkersRebornTools.rod, MaterialStatusType.HANDLE, "_roundshield_handle"));
        this.componentsParts
            .add(new ToolPartRecord(TinkersRebornTools.binding, MaterialStatusType.EXTRA, "_roundshield_bind"));
    }

    @Override
    public ToolBuildGuiInfo getToolBuildGuiInfo() {
        if (this.toolBuildGuiInfo == null) {
            this.toolBuildGuiInfo = new ToolBuildGuiInfo(this).addSlotPosition(29, 30) // core
                .addSlotPosition(20, 25) // rod
                .addSlotPosition(9, 35); // binding

        }
        return this.toolBuildGuiInfo;
    }

    @Override
    public void handleBlocking(ItemStack shieldStack, EntityPlayer player, LivingHurtEvent event) {
        // 2 ~ 5 tick
        // precise blocking
        if (player.getItemInUseDuration() - this.blockPrepareTime() <= 4) {
            int damage = event.ammount < 2f ? 1 : Math.round(event.ammount / 2f);

            // precise block reduce 90% damage
            event.ammount = (event.ammount * 0.1F);

            // reflect damage
            if (event.source.getSourceOfDamage() != null) {
                event.source.getSourceOfDamage()
                    .attackEntityFrom(DamageSource.causeThornsDamage(player), event.ammount / 2f);
                damage = damage * 3 / 2;
            }

            // blocking success
            ToolTagsHelper.getTraitsOrdered(shieldStack)
                .forEach(trait -> trait.onBlock(shieldStack, player, event));

            ShieldHelper.preventNextKnockback(player);
            Sounds.playSoundForAll(player, Sounds.blocking, 2.0f, 1.6f + 0.8f * TinkersReborn.random.nextFloat());

            // use more tool durability
            ToolTagsHelper.damageTool(shieldStack, damage + 2, player);

            // give player speed I and strength I for 5s
            player.addPotionEffect(new PotionEffect(Potion.moveSpeed.id, 100, 0));
            player.addPotionEffect(new PotionEffect(Potion.damageBoost.id, 100, 0));

            // if is living attacker, give it 2s slowdown III and weakness III
            if (event.source.getEntity() instanceof EntityLiving living) {
                living.addPotionEffect(new PotionEffect(Potion.moveSlowdown.id, 40, 2));
                living.addPotionEffect(new PotionEffect(Potion.weakness.id, 40, 2));
            }
        } else {
            super.handleBlocking(shieldStack, player, event);
        }
    }

    @Override
    public float getRepairModifierForPart(int index) {
        return DURABILITY_MODIFIER;
    }

    @Override
    public float blockingAngel() {
        return 110.0F;
    }

    @Override
    public float damageReduction() {
        return 0.55F;
    }

    @Override
    public float walkingSpeedReduction() {
        return 0.7F;
    }

    @Override
    public float breakLimit() {
        return 12.0F;
    }

    @Override
    public int breakingRecoverTime() {
        return 25;
    }

    @Override
    public int blockPrepareTime() {
        // 0.1s
        return 2;
    }

    @Override
    public ToolNBT buildToolTag(List<TinkersRebornMaterial> materials) {
        ToolNBT toolTag = super.buildToolTag(materials);
        toolTag.durability *= DURABILITY_MODIFIER;
        return toolTag;
    }

}
