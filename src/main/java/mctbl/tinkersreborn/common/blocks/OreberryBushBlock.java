package mctbl.tinkersreborn.common.blocks;

import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.EnumPlantType;
import net.minecraftforge.common.IPlantable;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mctbl.tinkersreborn.TinkersReborn;
import mctbl.tinkersreborn.TinkersRebornConfig;
import mctbl.tinkersreborn.common.TinkersRebornGeneral;
import mctbl.tinkersreborn.common.entity.OreberryTileEntity;
import mctbl.tinkersreborn.common.model.OreberryBushRender;
import mctbl.tinkersreborn.library.TinkersRebornRegistry;
import mctbl.tinkersreborn.library.blocks.TinkersRebornBlock;
import mctbl.tinkersreborn.util.TinkersRebornUtils;

public class OreberryBushBlock extends TinkersRebornBlock implements IPlantable, ITileEntityProvider {

    public OreberryBushBlock() {
        super(Material.leaves, "tinkersreborn.OreberryBush", 0.3F, TinkersRebornConfig.oreberryBushTypes);
        this.setStepSound(Block.soundTypeMetal);
        this.setCreativeTab(TinkersRebornRegistry.blockTab);
        this.setTickRandomly(true);
    }

    @Override
    public void updateTick(World world, int x, int y, int z, Random random) {
        if (!world.isRemote && random.nextInt(20) == 0
            && world.getFullBlockLightValue(x, y, z) < 10
            && world.getTileEntity(x, y, z) instanceof OreberryTileEntity ob) {
            ob.bushGrow();
        }
    }

    /* Left-click harvests berries */
    @Override
    public void onBlockClicked(World world, int x, int y, int z, EntityPlayer player) {
        harvest(world, x, y, z, player);
    }

    /* Right-click harvests berries */
    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int par6, float par7,
        float par8, float par9) {
        return harvest(world, x, y, z, player);
    }

    public boolean harvest(World world, int x, int y, int z, EntityPlayer player) {
        if (world.isRemote) return true;

        final int meta = world.getBlockMetadata(x, y, z);

        if (world.getTileEntity(x, y, z) instanceof OreberryTileEntity ob && ob.harvest()) {
            TinkersRebornUtils.drropItemAtPlayer(
                player,
                new ItemStack(TinkersRebornGeneral.oreberries, TinkersReborn.random.nextInt(3) + 1, meta));
            return true;
        }

        return false;
    }

    /**
     * Is this block (a) opaque and (b) a full 1m cube? This determines whether or not to render the shared face of two
     * adjacent blocks and also whether the player can attach torches, redstone wire, etc to this block.
     */
    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    @Override
    public boolean renderAsNormalBlock() {
        return false;
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new OreberryTileEntity();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean shouldSideBeRendered(IBlockAccess worldIn, int x, int y, int z, int side) {
        Block block = worldIn.getBlock(x, y, z);
        // If the block touching the side is same type of bush and not fully grown then render side.
        if (block == this && worldIn.getTileEntity(x, y, z) instanceof OreberryTileEntity ob && ob.state < 3) {
            return true;
            // If this block is fully grown and is touching a bush (fast mode) or solid block then don't render side.
        } else if ((Blocks.leaves.isOpaqueCube() && block == this) || block.isOpaqueCube()) {
            if (side == 0) {
                return false;
            }
            return maxY < 1f;
        }
        // If none of the above then render side.
        return true;
    }

    @Override
    public void registerBlockIcons(IIconRegister iconRegister) {
        this.icons = new IIcon[this.textureNames.length * 2];

        for (int i = 0; i < this.textureNames.length; ++i) {
            this.icons[i * 2] = iconRegister.registerIcon("tinkersreborn:crops/berry_" + textureNames[i]);
            this.icons[i * 2 + 1] = iconRegister.registerIcon("tinkersreborn:crops/berry_" + textureNames[i] + "_ripe");
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta) {
        return this.icons[meta * 2];
    }

    @Override
    public IIcon getIcon(IBlockAccess worldIn, int x, int y, int z, int side) {
        int meta = worldIn.getBlockMetadata(x, y, z) * 2;
        if (worldIn.getTileEntity(x, y, z) instanceof OreberryTileEntity ob && ob.state == 3) {
            meta++;
        }
        return this.icons[meta];
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
        if (world.getTileEntity(x, y, z) instanceof OreberryTileEntity ob) {
            switch (ob.state) {
                case 0:
                    return AxisAlignedBB.getBoundingBox(x + 0.25D, y, z + 0.25D, x + 0.75D, y + 0.5D, z + 0.75D);
                case 1:
                    return AxisAlignedBB.getBoundingBox(x + 0.125D, y, z + 0.125D, x + 0.875D, y + 0.75D, z + 0.875D);
                default:
                    return AxisAlignedBB
                        .getBoundingBox(x + 0.0625, y, z + 0.0625, x + 0.9375D, y + 0.9375D, z + 0.9375D);
            }
        } else {
            return super.getCollisionBoundingBoxFromPool(world, x, y, z);
        }
    }

    @Override
    public AxisAlignedBB getSelectedBoundingBoxFromPool(World world, int x, int y, int z) {
        if (world.getTileEntity(x, y, z) instanceof OreberryTileEntity ob) {
            switch (ob.state) {
                case 0:
                    return AxisAlignedBB.getBoundingBox(x + 0.25D, y, z + 0.25D, x + 0.75D, y + 0.5D, z + 0.75D);
                case 1:
                    return AxisAlignedBB.getBoundingBox(x + 0.125D, y, z + 0.125D, x + 0.875D, y + 0.75D, z + 0.875D);
                default:
                    return AxisAlignedBB.getBoundingBox(x, y, z, x + 1.0D, y + 1.0D, z + 1.0D);
            }

        }
        return super.getSelectedBoundingBoxFromPool(world, x, y, z);
    }

    @Override
    public void setBlockBoundsBasedOnState(IBlockAccess iblockaccess, int x, int y, int z) {
        if (iblockaccess.getTileEntity(x, y, z) instanceof OreberryTileEntity ob) {
            float minX;
            float minY = 0F;
            float minZ;
            float maxX;
            float maxY;
            float maxZ;

            if (ob.state == 0) {
                minX = minZ = 0.25F;
                maxX = maxZ = 0.75F;
                maxY = 0.5F;
            } else if (ob.state == 1) {
                minX = minZ = 0.125F;
                maxX = maxZ = 0.875F;
                maxY = 0.75F;
            } else {
                minX = minZ = 0.0F;
                maxX = maxZ = 1.0F;
                maxY = 1.0F;
            }
            this.setBlockBounds(minX, minY, minZ, maxX, maxY, maxZ);
        }

    }

    @Override
    public int getRenderType() {
        return OreberryBushRender.model;
    }

    @Override
    public boolean canPlaceBlockAt(World world, int x, int y, int z) {
        if (world.getFullBlockLightValue(x, y, z) < 13) return super.canPlaceBlockAt(world, x, y, z);
        return false;
    }

    @Override
    public void getSubBlocks(Item block, CreativeTabs tab, List<ItemStack> list) {
        if (this.textureNames != null) {
            for (int iter = 0; iter < this.textureNames.length; iter++) {
                list.add(new ItemStack(block, 1, iter));
            }
        }
    }

    @Override
    public EnumPlantType getPlantType(IBlockAccess world, int x, int y, int z) {
        return EnumPlantType.Cave;
    }

    @Override
    public Block getPlant(IBlockAccess world, int x, int y, int z) {
        return this;
    }

    @Override
    public int getPlantMetadata(IBlockAccess world, int x, int y, int z) {
        return world.getBlockMetadata(x, y, z);
    }

    @Override
    public void onEntityCollidedWithBlock(World world, int x, int y, int z, Entity entity) {
        if (!(entity instanceof EntityItem)) entity.attackEntityFrom(DamageSource.cactus, 1);
    }
}
