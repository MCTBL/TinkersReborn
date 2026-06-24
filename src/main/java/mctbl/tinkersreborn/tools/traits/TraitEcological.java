package mctbl.tinkersreborn.tools.traits;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;

import mctbl.tinkersreborn.library.tools.traits.AbstractTrait;
import mctbl.tinkersreborn.util.ToolTagsHelper;

public class TraitEcological extends AbstractTrait {

    private static int chance = 60; // 1/X chance of getting the effect

    public TraitEcological() {
        super("ecological", EnumChatFormatting.GREEN);
    }

    @Override
    public void onUpdate(ItemStack tool, World world, Entity entity, int itemSlot, boolean isSelected) {
        // *20 because 20 ticks in a second
        if (!world.isRemote && entity instanceof EntityPlayer player && random.nextInt(chance * 20) == 0) {
            if (player.getItemInUse() != tool) {
                ToolTagsHelper.healTool(tool, 1, (EntityLivingBase) entity);
            }
        }
    }
}
