package mctbl.tinkersreborn.tools.traits;

import java.util.List;

import javax.annotation.Nonnull;
import javax.vecmath.Vector3d;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingEvent;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import mctbl.tinkersreborn.library.entity.TinkersEntityProperties;
import mctbl.tinkersreborn.library.tools.modifiers.ModifierNBT;
import mctbl.tinkersreborn.library.tools.traits.AbstractTraitLeveled;
import mctbl.tinkersreborn.library.utils.BlockPos;
import mctbl.tinkersreborn.util.TinkersRebornUtils;
import mctbl.tinkersreborn.util.ToolTagsHelper;

public class TraitMagnetic extends AbstractTraitLeveled {

    public static final String MAGNETIC_KEY = "magnetic";

    public TraitMagnetic(int levels) {
        super(MAGNETIC_KEY, 0xdddddd, 3, levels);
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Override
    public void afterBlockBreak(ItemStack tool, World world, Block block, BlockPos pos, EntityLivingBase player,
        boolean wasEffective) {
        if (!player.worldObj.isRemote) {
            ModifierNBT data = new ModifierNBT(ToolTagsHelper.getModifierTag(tool, identifier));
            TinkersEntityProperties.getProps(player)
                .apply(MAGNETIC_KEY, 60, data.level);
        }
    }

    @Override
    public void onHit(ItemStack tool, EntityLivingBase player, EntityLivingBase target, float damage,
        boolean isCritical) {
        if (!player.worldObj.isRemote) {
            ModifierNBT data = new ModifierNBT(ToolTagsHelper.getModifierTag(tool, identifier));
            TinkersEntityProperties.getProps(player)
                .apply(MAGNETIC_KEY, 60, data.level);
        }
    }

    @SubscribeEvent
    public void updateMagnetic(LivingEvent.LivingUpdateEvent event) {
        if (!event.entityLiving.worldObj.isRemote) {
            TinkersEntityProperties props = TinkersEntityProperties.getProps(event.entityLiving);
            if (props.isActive(MAGNETIC_KEY)) {
                performMagneticAttraction(event.entityLiving, props.getLevel(MAGNETIC_KEY));
            }
        }
    }

    public void performMagneticAttraction(@Nonnull EntityLivingBase entity, int level) {
        // super magnetic - inspired by botanias code
        double x = entity.posX;
        double y = entity.posY;
        double z = entity.posZ;
        double range = 2.0d;

        range += level * 0.5f;

        List<EntityItem> items = entity.worldObj.getEntitiesWithinAABB(
            EntityItem.class,
            AxisAlignedBB.getBoundingBox(x - range, y - range, z - range, x + range, y + range, z + range));
        int pulled = 0;
        for (EntityItem item : items) {
            if (TinkersRebornUtils.isStackEmpty(item.getEntityItem()) || item.isDead) {
                continue;
            }

            if (pulled > 200) {
                break;
            }

            // constant force!
            float strength = 0.07f;

            // calculate direction: item -> player
            Vector3d vec = new Vector3d(x, y, z);
            vec.sub(new Vector3d(item.posX, item.posY, item.posZ));

            if (vec.lengthSquared() <= 0.05) {
                continue;
            }

            vec.normalize();
            vec.scale(strength);

            // we calculated the movement vector and set it to the correct strength.. now we apply it \o/
            item.motionX += vec.x;
            item.motionY += vec.y;
            item.motionZ += vec.z;

            pulled++;
        }
    }
}
