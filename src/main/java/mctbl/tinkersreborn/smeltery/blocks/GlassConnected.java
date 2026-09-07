package mctbl.tinkersreborn.smeltery.blocks;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
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
import mctbl.tinkersreborn.TinkersReborn;
import mctbl.tinkersreborn.library.TinkersRebornRegistry;
import mctbl.tinkersreborn.library.blocks.TinkersRebornBlock;
import mctbl.tinkersreborn.smeltery.entity.MultiServantLogic;

public class GlassConnected extends TinkersRebornBlock {

    protected String folder;
    private final int renderPass;
    private final boolean canPlayerPass;

    public GlassConnected(String location, boolean hasAlpha) {
        this(location, hasAlpha, false);
    }

    public GlassConnected(String location, boolean hasAlpha, boolean canPlayerPass) {
        super(Material.glass);
        this.stepSound = soundTypeGlass;
        this.folder = location;
        this.renderPass = hasAlpha ? 1 : 0;
        this.setHardness(0.3F);
        this.setCreativeTab(TinkersRebornRegistry.blockTab);
        this.icons = new IIcon[16];
        this.unlocalizedName = location + "GlassBlock";
        this.canPlayerPass = canPlayerPass;
    }

    @Override
    public String getUnlocalizedName() {
        return TinkersReborn.MODID + "." + this.unlocalizedName;
    }

    @Override
    public void getSubBlocks(Item block, CreativeTabs tab, List<ItemStack> list) {
        list.add(new ItemStack(block, 1, 0));
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    @Override
    public boolean renderAsNormalBlock() {
        return false;
    }

    @Override
    public int getRenderBlockPass() {
        return renderPass;
    }

    /**
     * This is checked to see if the texture should connect to this block
     * 
     * @param x
     * @param y
     * @param z
     * @param block ID this block is asking to connect to (may be 0 if there is no block)
     * @param meta  Metadata of the block this block is trying to connect to
     * @return true if should connect
     */
    public boolean shouldConnectToBlock(IBlockAccess blockAccess, int x, int y, int z, Block block, int meta) {
        return block == this;
    }

    @Override
    public boolean shouldSideBeRendered(IBlockAccess blockAccess, int x, int y, int z, int meta) {
        return blockAccess.getBlock(x, y, z) != this && super.shouldSideBeRendered(blockAccess, x, y, z, meta);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta) {
        return icons[0];
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(IBlockAccess blockAccess, int x, int y, int z, int meta) {
        return blockAccess.getBlockMetadata(x, y, z) == 15 ? icons[0]
            : getConnectedBlockTexture(blockAccess, x, y, z, meta, icons);
    }

    public IIcon getConnectedBlockTexture(IBlockAccess blockAccess, int x, int y, int z, int meta, IIcon[] icons) {
        boolean isOpenUp = false, isOpenDown = false, isOpenLeft = false, isOpenRight = false;

        switch (meta) {
            case 0:
                if (shouldConnectToBlock(
                    blockAccess,
                    x,
                    y,
                    z,
                    blockAccess.getBlock(x - 1, y, z),
                    blockAccess.getBlockMetadata(x - 1, y, z))) {
                    isOpenDown = true;
                }

                if (shouldConnectToBlock(
                    blockAccess,
                    x,
                    y,
                    z,
                    blockAccess.getBlock(x + 1, y, z),
                    blockAccess.getBlockMetadata(x + 1, y, z))) {
                    isOpenUp = true;
                }

                if (shouldConnectToBlock(
                    blockAccess,
                    x,
                    y,
                    z,
                    blockAccess.getBlock(x, y, z - 1),
                    blockAccess.getBlockMetadata(x, y, z - 1))) {
                    isOpenLeft = true;
                }

                if (shouldConnectToBlock(
                    blockAccess,
                    x,
                    y,
                    z,
                    blockAccess.getBlock(x, y, z + 1),
                    blockAccess.getBlockMetadata(x, y, z + 1))) {
                    isOpenRight = true;
                }

                if (isOpenUp && isOpenDown && isOpenLeft && isOpenRight) {
                    return icons[15];
                } else if (isOpenUp && isOpenDown && isOpenLeft) {
                    return icons[11];
                } else if (isOpenUp && isOpenDown && isOpenRight) {
                    return icons[12];
                } else if (isOpenUp && isOpenLeft && isOpenRight) {
                    return icons[13];
                } else if (isOpenDown && isOpenLeft && isOpenRight) {
                    return icons[14];
                } else if (isOpenDown && isOpenUp) {
                    return icons[5];
                } else if (isOpenLeft && isOpenRight) {
                    return icons[6];
                } else if (isOpenDown && isOpenLeft) {
                    return icons[8];
                } else if (isOpenDown && isOpenRight) {
                    return icons[10];
                } else if (isOpenUp && isOpenLeft) {
                    return icons[7];
                } else if (isOpenUp && isOpenRight) {
                    return icons[9];
                } else if (isOpenDown) {
                    return icons[3];
                } else if (isOpenUp) {
                    return icons[4];
                } else if (isOpenLeft) {
                    return icons[2];
                } else if (isOpenRight) {
                    return icons[1];
                }
                break;
            case 1:
                if (shouldConnectToBlock(
                    blockAccess,
                    x,
                    y,
                    z,
                    blockAccess.getBlock(x - 1, y, z),
                    blockAccess.getBlockMetadata(x - 1, y, z))) {
                    isOpenDown = true;
                }

                if (shouldConnectToBlock(
                    blockAccess,
                    x,
                    y,
                    z,
                    blockAccess.getBlock(x + 1, y, z),
                    blockAccess.getBlockMetadata(x + 1, y, z))) {
                    isOpenUp = true;
                }

                if (shouldConnectToBlock(
                    blockAccess,
                    x,
                    y,
                    z,
                    blockAccess.getBlock(x, y, z - 1),
                    blockAccess.getBlockMetadata(x, y, z - 1))) {
                    isOpenLeft = true;
                }

                if (shouldConnectToBlock(
                    blockAccess,
                    x,
                    y,
                    z,
                    blockAccess.getBlock(x, y, z + 1),
                    blockAccess.getBlockMetadata(x, y, z + 1))) {
                    isOpenRight = true;
                }

                if (isOpenUp && isOpenDown && isOpenLeft && isOpenRight) {
                    return icons[15];
                } else if (isOpenUp && isOpenDown && isOpenLeft) {
                    return icons[11];
                } else if (isOpenUp && isOpenDown && isOpenRight) {
                    return icons[12];
                } else if (isOpenUp && isOpenLeft && isOpenRight) {
                    return icons[13];
                } else if (isOpenDown && isOpenLeft && isOpenRight) {
                    return icons[14];
                } else if (isOpenDown && isOpenUp) {
                    return icons[5];
                } else if (isOpenLeft && isOpenRight) {
                    return icons[6];
                } else if (isOpenDown && isOpenLeft) {
                    return icons[8];
                } else if (isOpenDown && isOpenRight) {
                    return icons[10];
                } else if (isOpenUp && isOpenLeft) {
                    return icons[7];
                } else if (isOpenUp && isOpenRight) {
                    return icons[9];
                } else if (isOpenDown) {
                    return icons[3];
                } else if (isOpenUp) {
                    return icons[4];
                } else if (isOpenLeft) {
                    return icons[2];
                } else if (isOpenRight) {
                    return icons[1];
                }
                break;
            case 2:
                if (shouldConnectToBlock(
                    blockAccess,
                    x,
                    y,
                    z,
                    blockAccess.getBlock(x, y - 1, z),
                    blockAccess.getBlockMetadata(x, y - 1, z))) {
                    isOpenDown = true;
                }

                if (shouldConnectToBlock(
                    blockAccess,
                    x,
                    y,
                    z,
                    blockAccess.getBlock(x, y + 1, z),
                    blockAccess.getBlockMetadata(x, y + 1, z))) {
                    isOpenUp = true;
                }

                if (shouldConnectToBlock(
                    blockAccess,
                    x,
                    y,
                    z,
                    blockAccess.getBlock(x - 1, y, z),
                    blockAccess.getBlockMetadata(x - 1, y, z))) {
                    isOpenLeft = true;
                }

                if (shouldConnectToBlock(
                    blockAccess,
                    x,
                    y,
                    z,
                    blockAccess.getBlock(x + 1, y, z),
                    blockAccess.getBlockMetadata(x + 1, y, z))) {
                    isOpenRight = true;
                }

                if (isOpenUp && isOpenDown && isOpenLeft && isOpenRight) {
                    return icons[15];
                } else if (isOpenUp && isOpenDown && isOpenLeft) {
                    return icons[13];
                } else if (isOpenUp && isOpenDown && isOpenRight) {
                    return icons[14];
                } else if (isOpenUp && isOpenLeft && isOpenRight) {
                    return icons[11];
                } else if (isOpenDown && isOpenLeft && isOpenRight) {
                    return icons[12];
                } else if (isOpenDown && isOpenUp) {
                    return icons[6];
                } else if (isOpenLeft && isOpenRight) {
                    return icons[5];
                } else if (isOpenDown && isOpenLeft) {
                    return icons[9];
                } else if (isOpenDown && isOpenRight) {
                    return icons[10];
                } else if (isOpenUp && isOpenLeft) {
                    return icons[7];
                } else if (isOpenUp && isOpenRight) {
                    return icons[8];
                } else if (isOpenDown) {
                    return icons[1];
                } else if (isOpenUp) {
                    return icons[2];
                } else if (isOpenLeft) {
                    return icons[4];
                } else if (isOpenRight) {
                    return icons[3];
                }
                break;
            case 3:
                if (shouldConnectToBlock(
                    blockAccess,
                    x,
                    y,
                    z,
                    blockAccess.getBlock(x, y - 1, z),
                    blockAccess.getBlockMetadata(x, y - 1, z))) {
                    isOpenDown = true;
                }

                if (shouldConnectToBlock(
                    blockAccess,
                    x,
                    y,
                    z,
                    blockAccess.getBlock(x, y + 1, z),
                    blockAccess.getBlockMetadata(x, y + 1, z))) {
                    isOpenUp = true;
                }

                if (shouldConnectToBlock(
                    blockAccess,
                    x,
                    y,
                    z,
                    blockAccess.getBlock(x - 1, y, z),
                    blockAccess.getBlockMetadata(x - 1, y, z))) {
                    isOpenLeft = true;
                }

                if (shouldConnectToBlock(
                    blockAccess,
                    x,
                    y,
                    z,
                    blockAccess.getBlock(x + 1, y, z),
                    blockAccess.getBlockMetadata(x + 1, y, z))) {
                    isOpenRight = true;
                }

                if (isOpenUp && isOpenDown && isOpenLeft && isOpenRight) {
                    return icons[15];
                } else if (isOpenUp && isOpenDown && isOpenLeft) {
                    return icons[14];
                } else if (isOpenUp && isOpenDown && isOpenRight) {
                    return icons[13];
                } else if (isOpenUp && isOpenLeft && isOpenRight) {
                    return icons[11];
                } else if (isOpenDown && isOpenLeft && isOpenRight) {
                    return icons[12];
                } else if (isOpenDown && isOpenUp) {
                    return icons[6];
                } else if (isOpenLeft && isOpenRight) {
                    return icons[5];
                } else if (isOpenDown && isOpenLeft) {
                    return icons[10];
                } else if (isOpenDown && isOpenRight) {
                    return icons[9];
                } else if (isOpenUp && isOpenLeft) {
                    return icons[8];
                } else if (isOpenUp && isOpenRight) {
                    return icons[7];
                } else if (isOpenDown) {
                    return icons[1];
                } else if (isOpenUp) {
                    return icons[2];
                } else if (isOpenLeft) {
                    return icons[3];
                } else if (isOpenRight) {
                    return icons[4];
                }
                break;
            case 4:
                if (shouldConnectToBlock(
                    blockAccess,
                    x,
                    y,
                    z,
                    blockAccess.getBlock(x, y - 1, z),
                    blockAccess.getBlockMetadata(x, y - 1, z))) {
                    isOpenDown = true;
                }

                if (shouldConnectToBlock(
                    blockAccess,
                    x,
                    y,
                    z,
                    blockAccess.getBlock(x, y + 1, z),
                    blockAccess.getBlockMetadata(x, y + 1, z))) {
                    isOpenUp = true;
                }

                if (shouldConnectToBlock(
                    blockAccess,
                    x,
                    y,
                    z,
                    blockAccess.getBlock(x, y, z - 1),
                    blockAccess.getBlockMetadata(x, y, z - 1))) {
                    isOpenLeft = true;
                }

                if (shouldConnectToBlock(
                    blockAccess,
                    x,
                    y,
                    z,
                    blockAccess.getBlock(x, y, z + 1),
                    blockAccess.getBlockMetadata(x, y, z + 1))) {
                    isOpenRight = true;
                }

                if (isOpenUp && isOpenDown && isOpenLeft && isOpenRight) {
                    return icons[15];
                } else if (isOpenUp && isOpenDown && isOpenLeft) {
                    return icons[14];
                } else if (isOpenUp && isOpenDown && isOpenRight) {
                    return icons[13];
                } else if (isOpenUp && isOpenLeft && isOpenRight) {
                    return icons[11];
                } else if (isOpenDown && isOpenLeft && isOpenRight) {
                    return icons[12];
                } else if (isOpenDown && isOpenUp) {
                    return icons[6];
                } else if (isOpenLeft && isOpenRight) {
                    return icons[5];
                } else if (isOpenDown && isOpenLeft) {
                    return icons[10];
                } else if (isOpenDown && isOpenRight) {
                    return icons[9];
                } else if (isOpenUp && isOpenLeft) {
                    return icons[8];
                } else if (isOpenUp && isOpenRight) {
                    return icons[7];
                } else if (isOpenDown) {
                    return icons[1];
                } else if (isOpenUp) {
                    return icons[2];
                } else if (isOpenLeft) {
                    return icons[3];
                } else if (isOpenRight) {
                    return icons[4];
                }
                break;
            case 5:
                if (shouldConnectToBlock(
                    blockAccess,
                    x,
                    y,
                    z,
                    blockAccess.getBlock(x, y - 1, z),
                    blockAccess.getBlockMetadata(x, y - 1, z))) {
                    isOpenDown = true;
                }

                if (shouldConnectToBlock(
                    blockAccess,
                    x,
                    y,
                    z,
                    blockAccess.getBlock(x, y + 1, z),
                    blockAccess.getBlockMetadata(x, y + 1, z))) {
                    isOpenUp = true;
                }

                if (shouldConnectToBlock(
                    blockAccess,
                    x,
                    y,
                    z,
                    blockAccess.getBlock(x, y, z - 1),
                    blockAccess.getBlockMetadata(x, y, z - 1))) {
                    isOpenLeft = true;
                }

                if (shouldConnectToBlock(
                    blockAccess,
                    x,
                    y,
                    z,
                    blockAccess.getBlock(x, y, z + 1),
                    blockAccess.getBlockMetadata(x, y, z + 1))) {
                    isOpenRight = true;
                }

                if (isOpenUp && isOpenDown && isOpenLeft && isOpenRight) {
                    return icons[15];
                } else if (isOpenUp && isOpenDown && isOpenLeft) {
                    return icons[13];
                } else if (isOpenUp && isOpenDown && isOpenRight) {
                    return icons[14];
                } else if (isOpenUp && isOpenLeft && isOpenRight) {
                    return icons[11];
                } else if (isOpenDown && isOpenLeft && isOpenRight) {
                    return icons[12];
                } else if (isOpenDown && isOpenUp) {
                    return icons[6];
                } else if (isOpenLeft && isOpenRight) {
                    return icons[5];
                } else if (isOpenDown && isOpenLeft) {
                    return icons[9];
                } else if (isOpenDown && isOpenRight) {
                    return icons[10];
                } else if (isOpenUp && isOpenLeft) {
                    return icons[7];
                } else if (isOpenUp && isOpenRight) {
                    return icons[8];
                } else if (isOpenDown) {
                    return icons[1];
                } else if (isOpenUp) {
                    return icons[2];
                } else if (isOpenLeft) {
                    return icons[4];
                } else if (isOpenRight) {
                    return icons[3];
                }
                break;
        }

        return icons[0];
    }

    @Override
    public TileEntity createTileEntity(World world, int metadata) {
        if (this.canPlayerPass) {
            return new MultiServantLogic();
        }
        return null;
    }

    @Override
    public void registerBlockIcons(IIconRegister iconRegister) {
        icons[0] = iconRegister.registerIcon("tinkersreborn:glass/" + folder + "/glass");
        icons[1] = iconRegister.registerIcon("tinkersreborn:glass/" + folder + "/glass_1_d");
        icons[2] = iconRegister.registerIcon("tinkersreborn:glass/" + folder + "/glass_1_u");
        icons[3] = iconRegister.registerIcon("tinkersreborn:glass/" + folder + "/glass_1_l");
        icons[4] = iconRegister.registerIcon("tinkersreborn:glass/" + folder + "/glass_1_r");
        icons[5] = iconRegister.registerIcon("tinkersreborn:glass/" + folder + "/glass_2_h");
        icons[6] = iconRegister.registerIcon("tinkersreborn:glass/" + folder + "/glass_2_v");
        icons[7] = iconRegister.registerIcon("tinkersreborn:glass/" + folder + "/glass_2_dl");
        icons[8] = iconRegister.registerIcon("tinkersreborn:glass/" + folder + "/glass_2_dr");
        icons[9] = iconRegister.registerIcon("tinkersreborn:glass/" + folder + "/glass_2_ul");
        icons[10] = iconRegister.registerIcon("tinkersreborn:glass/" + folder + "/glass_2_ur");
        icons[11] = iconRegister.registerIcon("tinkersreborn:glass/" + folder + "/glass_3_d");
        icons[12] = iconRegister.registerIcon("tinkersreborn:glass/" + folder + "/glass_3_u");
        icons[13] = iconRegister.registerIcon("tinkersreborn:glass/" + folder + "/glass_3_l");
        icons[14] = iconRegister.registerIcon("tinkersreborn:glass/" + folder + "/glass_3_r");
        icons[15] = iconRegister.registerIcon("tinkersreborn:glass/" + folder + "/glass_4");
    }

    @Override
    public boolean canPlaceTorchOnTop(World world, int x, int y, int z) {
        return true;
    }

    @Override
    public void addCollisionBoxesToList(World worldIn, int x, int y, int z, AxisAlignedBB mask,
        List<AxisAlignedBB> list, Entity collider) {
        if (this.canPlayerPass && collider instanceof EntityPlayer) {
            return;
        }

        super.addCollisionBoxesToList(worldIn, x, y, z, mask, list, collider);
    }
}
