package mctbl.tinkersreborn.smeltery.blocks;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mctbl.tinkersreborn.library.TinkersRebornRegistry;
import mctbl.tinkersreborn.library.blocks.TinkersRebornInventoryBlock;
import mctbl.tinkersreborn.smeltery.entity.CastingBasinLogic;
import mctbl.tinkersreborn.smeltery.entity.CastingBlockLogic;
import mctbl.tinkersreborn.smeltery.entity.CastingTableLogic;
import mctbl.tinkersreborn.smeltery.entity.FaucetLogic;
import mctbl.tinkersreborn.smeltery.model.CastingBlockRender;

public class SearedBlock extends TinkersRebornInventoryBlock {

    public SearedBlock() {
        super(Material.rock);
        this.setCreativeTab(TinkersRebornRegistry.blockTab);
        this.setHardness(3F);
        this.setResistance(20F);
        this.setStepSound(soundTypeMetal);
        this.setBlockName("tinkersreborn.SearedBlock");
        this.TEXTURENAMES = new String[] { "smeltery/castingtable_top", "smeltery/castingtable_side",
            "smeltery/castingtable_bottom", "smeltery/faucet", "smeltery/blockcast_top", "smeltery/blockcast_side",
            "smeltery/blockcast_bottom" };
    }

    @Override
    public TileEntity createTileEntity(World world, int metadata) {
        return switch (metadata) {
            case 0 -> new CastingTableLogic();
            case 1 -> new FaucetLogic();
            case 2 -> new CastingBasinLogic();
            default -> null;
        };
    }

    @Override
    public int getRenderBlockPass() {
        return 0;
    }

    /* Activation */
    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float clickX,
        float clickY, float clickZ) {
        int md = world.getBlockMetadata(x, y, z);
        // casting table or basin
        if (md == 0 || md == 2) {
            return activateCastingBlock(world, x, y, z, player);
        } else if (md == 1) {
            // faucet
            if (player.isSneaking()) return false;

            if (!world.isRemote) {
                FaucetLogic logic = (FaucetLogic) world.getTileEntity(x, y, z);
                logic.setActive(true);
            }
            return true;
        } else return super.onBlockActivated(world, x, y, z, player, side, clickX, clickY, clickZ);
    }

    boolean activateCastingBlock(World world, int x, int y, int z, EntityPlayer player) {
        if (!world.isRemote) {
            CastingBlockLogic logic = (CastingBlockLogic) world.getTileEntity(x, y, z);
            logic.interact(player);
            world.markBlockForUpdate(x, y, z);
        }
        return true;
    }

    /* Rendering */
    @Override
    public int getRenderType() {
        return CastingBlockRender.searedModel;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta) {
        if (meta == 0) return icons[getTextureIndex(side)];
        else if (meta == 2) return icons[getTextureIndex(side) + 4];
        else return icons[3];
    }

    public int getTextureIndex(int side) {
        if (side == 0) return 2;
        if (side == 1) return 0;

        return 1;
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
    public boolean shouldSideBeRendered(IBlockAccess par1IBlockAccess, int par2, int par3, int par4, int par5) {
        return true;
    }

    @Override
    public void getSubBlocks(Item id, CreativeTabs tab, List<ItemStack> list) {
        for (int iter = 0; iter < 3; iter++) {
            list.add(new ItemStack(id, 1, iter));
        }
    }

    @Override
    public void setBlockBoundsBasedOnState(IBlockAccess world, int x, int y, int z) {
        int meta = world.getBlockMetadata(x, y, z);
        if (meta != 1) {
            this.setBlockBounds(0, 0, 0, 1, 1, 1);
        } else {
            TileEntity te = world.getTileEntity(x, y, z);
            float xMin = 0.25F;
            float xMax = 0.75F;
            float zMin = 0.25F;
            float zMax = 0.75F;

            if (te instanceof FaucetLogic logic) {
                switch (logic.getForgeDirection()) {
                    case SOUTH:
                        zMax = 0.375F;
                        zMin = 0F;
                        break;
                    case WEST:
                        xMin = 0.625F;
                        xMax = 1.0F;
                        break;
                    case EAST:
                        xMax = 0.375F;
                        xMin = 0F;
                        break;
                    case NORTH:
                    default:
                        zMin = 0.625F;
                        zMax = 1.0F;
                        break;
                }
            }

            this.setBlockBounds(xMin, 0.25F, zMin, xMax, 0.625F, zMax);
        }
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
        int meta = world.getBlockMetadata(x, y, z);
        if (meta != 1) {
            return AxisAlignedBB.getBoundingBox(x, y, z, x + 1, y + 1, z + 1);
        } else {
            TileEntity tile = world.getTileEntity(x, y, z);
            if (tile instanceof FaucetLogic logic) {
                float xMin = 0.25F;
                float xMax = 0.75F;
                float zMin = 0.25F;
                float zMax = 0.75F;

                switch (logic.getForgeDirection()) {
                    case SOUTH:
                        zMax = 0.375F;
                        zMin = 0F;
                        break;
                    case WEST:
                        xMin = 0.625F;
                        xMax = 1.0F;
                        break;
                    case EAST:
                        xMax = 0.375F;
                        xMin = 0F;
                        break;
                    case NORTH:
                    default:
                        zMin = 0.625F;
                        zMax = 1.0F;
                        break;
                }

                return AxisAlignedBB.getBoundingBox(
                    (float) x + xMin,
                    (double) y + 0.25,
                    (float) z + zMin,
                    (float) x + xMax,
                    (double) y + 0.625,
                    (float) z + zMax);
            }
        }

        return super.getCollisionBoundingBoxFromPool(world, x, y, z);
    }

    @Override
    public void onNeighborBlockChange(World world, int x, int y, int z, Block neighborBlockID) {
        if (world.getBlockMetadata(x, y, z) == 1) {
            boolean isPowered = world.isBlockIndirectlyGettingPowered(x, y, z);
            TileEntity te = world.getTileEntity(x, y, z);
            if (!(te instanceof FaucetLogic logic)) return;
            // was it a low->high pulse
            if (!logic.hasRedstonePower && isPowered) {
                logic.hasRedstonePower = true;
                logic.setActive(true);
            }

            // update its state
            logic.hasRedstonePower = isPowered;
        }
    }

    @Override
    public TileEntity createNewTileEntity(World world, int metadata) {
        return switch (metadata) {
            case 0 -> new CastingTableLogic();
            case 1 -> new FaucetLogic();
            case 2 -> new CastingBasinLogic();
            default -> null;
        };
    }

}
