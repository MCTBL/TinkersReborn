package mctbl.tinkersreborn.library.tools;

import java.util.HashSet;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public abstract class HarvestTool extends ToolCore {

    public static final Set<Material> effectiveMaterials = new HashSet<>();

    public HarvestTool(String toolTypeName, int partAmount) {
        super(toolTypeName, partAmount);
        this.categoryTags.add("harvest");
    }

    protected abstract String getHarvestType();

    public boolean isEffective(Block block, int meta) {
        if (this.getHarvestType()
            .equals(block.getHarvestTool(meta))) return true;
        else return isEffective(block.getMaterial());
    }

    @Override
    public boolean func_150897_b(Block block) {
        return isEffective(block.getMaterial());
    }

    public boolean isEffective(Material material) {
        return getEffectiveMaterials().contains(material);
    }

    protected Set<Material> getEffectiveMaterials() {
        return effectiveMaterials;
    }

    @Override
    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side,
        float clickX, float clickY, float clickZ) {
        // when right click these tools will place next slots block
        boolean isUsed = false;

        int toolSlotId = player.inventory.currentItem;
        // TODO which one is better?
        int nextTargetSlotId = (toolSlotId + 1) % 9;
        int prevTargetSlotId = (toolSlotId + 8) % 9;

        ItemStack targetStack = player.inventory.getStackInSlot(nextTargetSlotId);
        if (targetStack != null) {
            Item item = targetStack.getItem();

            if (item instanceof ItemBlock) {
                ForgeDirection forgeSide = ForgeDirection.getOrientation(side);
                int posX = x + forgeSide.offsetX;
                int posY = y + forgeSide.offsetY;
                int posZ = z + forgeSide.offsetZ;

                AxisAlignedBB blockBounds = AxisAlignedBB
                    .getBoundingBox(posX, posY, posZ, posX + 1, posY + 1, posZ + 1);
                AxisAlignedBB playerBounds = player.boundingBox;

                if (item instanceof ItemBlock i) {
                    // prevent player place a sand then fall with it
                    if (i.field_150939_a.getMaterial()
                        .blocksMovement() && playerBounds.intersectsWith(blockBounds)) {
                        return false;
                    }
                }

                int dmg = targetStack.getItemDamage();
                int count = targetStack.stackSize;
                isUsed = item.onItemUse(targetStack, player, world, x, y, z, side, clickX, clickY, clickZ);

                // handle creative mode
                if (player.capabilities.isCreativeMode) {
                    // fun fact: vanilla minecraft does it exactly the same way
                    targetStack.setItemDamage(dmg);
                    targetStack.stackSize = count;
                }

                if (targetStack.stackSize < 1) {
                    targetStack = null;
                    player.inventory.setInventorySlotContents(nextTargetSlotId, null);
                }
            }
        }

        return isUsed;
    }
}
