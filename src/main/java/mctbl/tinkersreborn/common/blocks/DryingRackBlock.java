package mctbl.tinkersreborn.common.blocks;

import java.util.List;

import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mctbl.tinkersreborn.common.TinkersRebornGeneral;
import mctbl.tinkersreborn.common.entity.DryingRackLogic;
import mctbl.tinkersreborn.library.TinkersRebornRegistry;
import mctbl.tinkersreborn.library.blocks.TinkersRebornInventoryBlock;
import mctbl.tinkersreborn.util.ItemHelper;

public class DryingRackBlock extends TinkersRebornInventoryBlock {

    public DryingRackBlock() {
        super(Material.wood);
        this.setCreativeTab(TinkersRebornRegistry.blockTab);
        this.setHardness(2.0f);
        this.setStepSound(soundTypeMetal);
        this.setBlockName("tinkersreborn.DryingRack");
    }

    @Override
    public String getUnlocalizedName() {
        return "tinkersreborn.DryingRack";
    }

    @Override
    public TileEntity createNewTileEntity(World world, int metadata) {
        return new DryingRackLogic();
    }

    @Override
    public int getRenderBlockPass() {
        return 1;
    }

    @Override
    public int onBlockPlaced(World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ, int meta) {
        if (side > 1) return side;
        return meta;
    }

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entityliving, ItemStack stack) {
        int meta = world.getBlockMetadata(x, y, z);
        if (meta == 0) {
            int l = MathHelper.floor_double((entityliving.rotationYaw * 4.0D / 360.0D) + 0.5D) & 3;
            int direction = l % 2;
            if (direction == 1) world.setBlockMetadataWithNotify(x, y, z, 1, 2);
        }
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float clickX,
        float clickY, float clickZ) {
        if (!world.isRemote && world.getTileEntity(x, y, z) instanceof DryingRackLogic logic) {
            if (logic.isStackInSlot(1)) {
                // take out first
                ItemStack decrStack = logic.decrStackSize(1, 1);
                if (decrStack != null) ItemHelper.spawnItemAtPlayer(player, decrStack);
            } else {
                if (!logic.isStackInSlot(0)) {
                    // nothing in slot 0, put it in
                    ItemStack stack = player.getCurrentEquippedItem();
                    if (stack != null) {
                        stack = player.inventory.decrStackSize(player.inventory.currentItem, 1);
                        logic.setInventorySlotContents(0, stack);
                    }
                } else {
                    // take out slot 0
                    ItemStack decrStack = logic.decrStackSize(0, 1);
                    if (decrStack != null) ItemHelper.spawnItemAtPlayer(player, decrStack);
                }
            }

            world.markBlockForUpdate(x, y, z);
        }
        return true;
    }

    @Override
    public AxisAlignedBB getSelectedBoundingBoxFromPool(World world, int x, int y, int z) {
        int metadata = world.getBlockMetadata(x, y, z);
        float xMin = 0F;
        float yMin = 0F;
        float zMin = 0F;
        float xMax = 1F;
        float yMax = 1F;
        float zMax = 1F;
        switch (metadata) {
            case 0:
                zMin = 0.375F;
                yMax = 0.25F;
                zMax = 0.625F;
                break;
            case 1:
                xMin = 0.375F;
                yMax = 0.25F;
                xMax = 0.625F;
                break;
            case 2:
                zMin = 0.75F;
                yMin = 0.75F;
                break;
            case 3:
                zMax = 0.25F;
                yMin = 0.75F;
                break;
            case 4:
                xMin = 0.75F;
                yMin = 0.75F;
                break;
            case 5:
                xMax = 0.25F;
                yMin = 0.75F;
                break;
        }
        return AxisAlignedBB.getBoundingBox(
            (double) x + xMin,
            (double) y + yMin,
            (double) z + zMin,
            (double) x + xMax,
            (double) y + yMax,
            (double) z + zMax);
    }

    @Override
    public void setBlockBoundsBasedOnState(IBlockAccess world, int x, int y, int z) {
        int metadata = world.getBlockMetadata(x, y, z);
        float xMin = 0F;
        float yMin = 0F;
        float zMin = 0F;
        float xMax = 1F;
        float yMax = 1F;
        float zMax = 1F;
        switch (metadata) {
            case 0:
                zMin = 0.375F;
                yMax = 0.25F;
                zMax = 0.625F;
                break;
            case 1:
                xMin = 0.375F;
                yMax = 0.25F;
                xMax = 0.625F;
                break;
            case 2:
                zMin = 0.75F;
                yMin = 0.75F;
                break;
            case 3:
                zMax = 0.25F;
                yMin = 0.75F;
                break;
            case 4:
                xMin = 0.75F;
                yMin = 0.75F;
                break;
            case 5:
                xMax = 0.25F;
                yMin = 0.75F;
                break;
        }
        this.setBlockBounds(xMin, yMin, zMin, xMax, yMax, zMax);
    }

    @Override
    public void addCollisionBoxesToList(World world, int x, int y, int z, AxisAlignedBB aabb, List<AxisAlignedBB> list,
        Entity entity) {
        this.setBlockBoundsBasedOnState(world, x, y, z);
        super.addCollisionBoxesToList(world, x, y, z, aabb, list, entity);
    }

    /* Rendering */
    @Override
    public int getRenderType() {
        return TinkersRebornGeneral.proxy.getDryingRackRenderId();
    }

    @Override
    public void registerBlockIcons(IIconRegister iconRegister) {
        // do nothing
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta) {
        return Blocks.planks.getIcon(side, 0);
    }

    @Override
    public boolean renderAsNormalBlock() {
        return false;
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }
}
