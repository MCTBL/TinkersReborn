package mctbl.tinkersreborn.common.events;

import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.boss.IBossDisplayData;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSourceIndirect;
import net.minecraftforge.event.entity.living.LivingDropsEvent;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import mctbl.tinkersreborn.TinkersReborn;
import mctbl.tinkersreborn.TinkersRebornConfig;
import mctbl.tinkersreborn.common.TinkersRebornGeneral;
import mctbl.tinkersreborn.tools.TinkersRebornTools;
import mctbl.tinkersreborn.util.ItemHelper;

public class TinkersRebornMobEventHandler {

    @SubscribeEvent
    public void onLivingDrop(LivingDropsEvent event) {
        if (event.entityLiving == null) return;

        if (!event.entityLiving.worldObj.getGameRules()
            .getGameRuleBooleanValue("doMobLoot")) return;

        DamageSource source = event.source;
        if (source.getEntity() instanceof EntityPlayer || (source instanceof EntityDamageSourceIndirect indirect
            && indirect.getEntity() instanceof EntityPlayer)) {
            if (TinkersReborn.random.nextInt(200) == 0 && event.entityLiving instanceof IMob) {
                ItemStack dropStack = new ItemStack(TinkersRebornGeneral.heartCanister, 1, 1);
                ItemHelper.addDrops(event, dropStack);
            }
            if (event.entityLiving instanceof EntitySkeleton skeleton && skeleton.getSkeletonType() == 1
                && TinkersReborn.random.nextInt(Math.max(1, 5 - event.lootingLevel)) == 0) {
                ItemHelper.addDrops(event, new ItemStack(TinkersRebornTools.necroticBone));
            }
        }

        if (event.entityLiving instanceof IBossDisplayData) {
            String entityName = event.entityLiving.getClass()
                .getSimpleName()
                .toLowerCase();
            if (TinkersRebornConfig.heartDropBlacklist.contains(entityName)) {
                return;
            }
            int count = event.entityLiving instanceof EntityDragon ? 5 : 1;
            ItemStack dropStack = new ItemStack(TinkersRebornGeneral.heartCanister, count, 3);
            ItemHelper.addDrops(event, dropStack);
        }

    }

}
