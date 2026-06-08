package mctbl.tinkersreborn.tools.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mctbl.tinkersreborn.library.TinkersRebornRegistry;
import mctbl.tinkersreborn.library.blocks.ITinkersToolStationBlock;
import mctbl.tinkersreborn.library.blocks.TinkersRebornInventoryBlock;
import mctbl.tinkersreborn.tools.TinkersRebornToolsProxyCommon;
import mctbl.tinkersreborn.tools.entity.PartChestLogic;
import mctbl.tinkersreborn.tools.model.ChestRender;

public class PartChestBlock extends TinkersRebornInventoryBlock implements ITinkersToolStationBlock {

    public PartChestBlock() {
        super(Material.wood);
        this.setHardness(2f);
        this.setStepSound(Block.soundTypeWood);
        this.setBlockName("tinkersreborn.PartChest");
        this.setCreativeTab(TinkersRebornRegistry.blockTab);
        this.TEXTURENAMES = new String[] { "tools/partchest_top", "tools/partchest_bottom", "tools/partchest_side" };
    }

    @Override
    public String getUnlocalizedName() {
        return "tinkersreborn.PartChest";
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta) {
        return switch (ForgeDirection.getOrientation(side)) {
            case UP -> icons[0];
            case DOWN -> icons[1];
            default -> icons[2];
        };
    }

    @Override
    public boolean renderAsNormalBlock() {
        return false;
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    @Override
    public boolean isSideSolid(IBlockAccess world, int x, int y, int z, ForgeDirection side) {
        return side == ForgeDirection.UP;
    }

    @Override
    public int getRenderType() {
        return ChestRender.model;
    }

    @Override
    public boolean shouldSideBeRendered(IBlockAccess par1IBlockAccess, int par2, int par3, int par4, int par5) {
        return true;
    }

    @Override
    public AxisAlignedBB getSelectedBoundingBoxFromPool(World world, int x, int y, int z) {
        return AxisAlignedBB.getBoundingBox(
            (double) x + this.minX,
            (double) y + this.minY,
            (double) z + this.minZ,
            (double) x + this.maxX,
            (double) y + this.maxY - 0.125,
            (double) z + this.maxZ);
    }

    @Override
    public TileEntity createNewTileEntity(World world, int metadata) {
        return new PartChestLogic();
    }

    @Override
    public Integer getGui(World world, int x, int y, int z, EntityPlayer entityplayer) {
        return TinkersRebornToolsProxyCommon.partChestID;
    }

    /* Keep pattern chest inventory */
    @Override
    public boolean removedByPlayer(World world, EntityPlayer player, int x, int y, int z, boolean willHarvest) {
        player.addExhaustion(0.025F);
        if (!world.isRemote && world.getGameRules()
            .getGameRuleBooleanValue("doTileDrops")) {

            ItemStack chest = new ItemStack(this, 1);
            PartChestLogic logic = (PartChestLogic) world.getTileEntity(x, y, z);

            NBTTagCompound inventory = new NBTTagCompound();

            logic.writeInventoryToNBT(inventory);
            NBTTagCompound baseTag = new NBTTagCompound();
            baseTag.setTag("Inventory", inventory);
            chest.setTagCompound(baseTag);

            for (int i = 0; i < logic.getSizeInventory(); i++) logic.setInventorySlotContents(i, null);

            if (!player.capabilities.isCreativeMode || player.isSneaking()) {
                float f = 0.7F;
                double d0 = (double) (world.rand.nextFloat() * f) + (double) (1.0F - f) * 0.5D;
                double d1 = (double) (world.rand.nextFloat() * f) + (double) (1.0F - f) * 0.5D;
                double d2 = (double) (world.rand.nextFloat() * f) + (double) (1.0F - f) * 0.5D;
                EntityItem entityitem = new EntityItem(world, (double) x + d0, (double) y + d1, (double) z + d2, chest);
                entityitem.delayBeforeCanPickup = 10;
                world.spawnEntityInWorld(entityitem);
            }

        }
        return world.setBlockToAir(x, y, z);
    }

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase living, ItemStack stack) {
        if (stack.hasTagCompound()) {
            NBTTagCompound inventory = stack.getTagCompound()
                .getCompoundTag("Inventory");
            PartChestLogic logic = (PartChestLogic) world.getTileEntity(x, y, z);
            if (inventory != null) {
                logic.readInventoryFromNBT(inventory);
                logic.xCoord = x;
                logic.yCoord = y;
                logic.zCoord = z;
            }
        }
        super.onBlockPlacedBy(world, x, y, z, living, stack);
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float clickX,
        float clickY, float clickZ) {
        if (world.getTileEntity(x, y, z) instanceof PartChestLogic logic && !player.isSneaking()) {
            // is the pattern/part chest and player is not holding shift key
            ItemStack itemInHand = player.getHeldItem();
            if (itemInHand != null && logic.isItemValid(itemInHand)) {
                // is the player holding a tinker pattern/part
                if (logic.insertItemStackIntoInventory(itemInHand)) {
                    // try insert into chest
                    return true;
                }
            }
        }
        return super.onBlockActivated(world, x, y, z, player, side, clickX, clickY, clickZ);
    }

}
