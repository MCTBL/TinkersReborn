package mctbl.tinkersreborn.tools.traits;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.event.entity.player.PlayerEvent;

import mctbl.tinkersreborn.library.tools.traits.AbstractTrait;
import mctbl.tinkersreborn.library.utils.BlockPos;

/**
 * Increases mining speed and damage in hot/dry areas, decreases a bit in wet areas
 */
public class TraitAridiculous extends AbstractTrait {

    public TraitAridiculous() {
        super("aridiculous", EnumChatFormatting.DARK_RED);
    }

    @Override
    public void miningSpeed(ItemStack tool, PlayerEvent.BreakSpeed event) {
        // speedup or slowdown depending on biome temperature. hotter areas are much faster
        float coeff = calcAridiculousness(event.entityPlayer.getEntityWorld(), BlockPos.of(event.x, event.y, event.z))
            / 10f; // /10 = 10% for a coeff of 1. But can be bigger.
        event.newSpeed += event.originalSpeed * coeff;
    }

    @Override
    public float damage(ItemStack tool, EntityLivingBase player, EntityLivingBase target, float damage, float newDamage,
        boolean isCritical) {
        float extraDamage = 2f
            * calcAridiculousness(player.worldObj, BlockPos.of(player.posX, player.posY, player.posZ));
        return extraDamage + super.damage(tool, player, target, damage, newDamage, isCritical);
    }

    protected float calcAridiculousness(World world, BlockPos pos) {
        BiomeGenBase biome = world.getBiomeGenForCoordsBody(pos.x, pos.z);

        float rain = world.isRaining() ? biome.getFloatRainfall() / 2f : 0f;
        return (float) (Math.pow(1.25, 3d * (0.5f + biome.temperature - biome.getFloatRainfall())) - 1.25d) - rain;
    }
}
