package mctbl.tinkersreborn.library.tools;

import java.util.List;

import javax.annotation.Nonnull;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import mctbl.tinkersreborn.library.utils.BlockPos;

public interface IAoeTool {

    /** returns the blocks affected by the tool */
    List<BlockPos> getAOEBlocks(@Nonnull ItemStack stack, World world, EntityPlayer player, BlockPos origin);

}
