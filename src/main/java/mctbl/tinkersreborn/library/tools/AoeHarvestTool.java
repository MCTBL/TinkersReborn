package mctbl.tinkersreborn.library.tools;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import mctbl.tinkersreborn.library.utils.BlockPos;
import mctbl.tinkersreborn.tools.Category;
import mctbl.tinkersreborn.util.ToolTagsHelper;

public abstract class AoeHarvestTool extends HarvestTool implements IAoeTool {

    protected AoeHarvestTool(String toolTypeName, int partAmount) {
        super(toolTypeName, partAmount);
        this.categoryTags.add(Category.AOE);
    }

    @Override
    public List<BlockPos> getAOEBlocks(ItemStack stack, World world, EntityPlayer player, BlockPos origin) {
        return ToolTagsHelper.calcAOEBlocks(stack, world, player, origin, 1, 1, 1, -1);
    }
}
