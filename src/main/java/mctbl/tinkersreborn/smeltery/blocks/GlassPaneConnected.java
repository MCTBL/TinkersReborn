package mctbl.tinkersreborn.smeltery.blocks;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mctbl.tinkersreborn.smeltery.model.PaneConnectedRender;

public class GlassPaneConnected extends GlassConnected {

    private IIcon sideIcon;

    public GlassPaneConnected(String location, boolean hasAlpha) {
        super(location, hasAlpha, false);
        this.unlocalizedName = location + "GlassPane";
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(IBlockAccess access, int x, int y, int z, int side) {
        if (side == 0 || side == 1) {
            return access.getBlock(x, y - 1, z) == this && side == 0 ? icons[15]
                : access.getBlock(x, y + 1, z) == this && side == 1 ? icons[15] : getSideTextureIndex();
        } else {
            return super.getIcon(access, x, y, z, side);
        }
    }

    public IIcon getSideTextureIndex() {
        return this.sideIcon;
    }

    @Override
    public void addCollisionBoxesToList(World world, int x, int y, int z, AxisAlignedBB aabb,
        List<AxisAlignedBB> aabbList, Entity entity) {
        boolean flag = this.canPaneConnectTo(world, x, y, z, ForgeDirection.NORTH);
        boolean flag1 = this.canPaneConnectTo(world, x, y, z, ForgeDirection.SOUTH);
        boolean flag2 = this.canPaneConnectTo(world, x, y, z, ForgeDirection.WEST);
        boolean flag3 = this.canPaneConnectTo(world, x, y, z, ForgeDirection.EAST);

        if ((!flag2 || !flag3) && (flag2 || flag3 || flag || flag1)) {
            if (flag2 && !flag3) {
                this.setBlockBounds(0.0F, 0.0F, 0.4375F, 0.5F, 1.0F, 0.5625F);
                super.addCollisionBoxesToList(world, x, y, z, aabb, aabbList, entity);
            } else if (!flag2 && flag3) {
                this.setBlockBounds(0.5F, 0.0F, 0.4375F, 1.0F, 1.0F, 0.5625F);
                super.addCollisionBoxesToList(world, x, y, z, aabb, aabbList, entity);
            }
        } else {
            this.setBlockBounds(0.0F, 0.0F, 0.4375F, 1.0F, 1.0F, 0.5625F);
            super.addCollisionBoxesToList(world, x, y, z, aabb, aabbList, entity);
        }

        if ((!flag || !flag1) && (flag2 || flag3 || flag || flag1)) {
            if (flag && !flag1) {
                this.setBlockBounds(0.4375F, 0.0F, 0.0F, 0.5625F, 1.0F, 0.5F);
                super.addCollisionBoxesToList(world, x, y, z, aabb, aabbList, entity);
            } else if (!flag && flag1) {
                this.setBlockBounds(0.4375F, 0.0F, 0.5F, 0.5625F, 1.0F, 1.0F);
                super.addCollisionBoxesToList(world, x, y, z, aabb, aabbList, entity);
            }
        } else {
            this.setBlockBounds(0.4375F, 0.0F, 0.0F, 0.5625F, 1.0F, 1.0F);
            super.addCollisionBoxesToList(world, x, y, z, aabb, aabbList, entity);
        }
    }

    public boolean canPaneConnectTo(IBlockAccess access, int x, int y, int z, ForgeDirection dir) {
        return canThisPaneConnectToThisBlock(access.getBlock(x + dir.offsetX, y + dir.offsetY, z + dir.offsetZ))
            || access.isSideSolid(x + dir.offsetX, y + dir.offsetY, z + dir.offsetZ, dir.getOpposite(), false);
    }

    public final boolean canThisPaneConnectToThisBlock(Block b) {
        return b.isOpaqueCube() || b == this || b == Blocks.glass;
    }

    @Override
    public void registerBlockIcons(IIconRegister iconRegister) {
        super.registerBlockIcons(iconRegister);
        this.sideIcon = iconRegister.registerIcon("tinkersreborn:glass/" + folder + "/glass_side");
    }

    @Override
    public boolean shouldSideBeRendered(IBlockAccess par1IBlockAccess, int x, int y, int z, int side) {
        return true;
    }

    @Override
    public void setBlockBoundsForItemRender() {
        this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
    }

    @Override
    public IIcon getConnectedBlockTexture(IBlockAccess blockAccess, int x, int y, int z, int side, IIcon[] icons) {
        if (side == 0 || side == 1) {
            return getSideTextureIndex();
        }
        return super.getConnectedBlockTexture(blockAccess, x, y, z, side, icons);
    }

    @Override
    public int getRenderType() {
        return PaneConnectedRender.model;
    }

    @Override
    public void setBlockBoundsBasedOnState(IBlockAccess blockAccess, int x, int y, int z) {
        float f = 0.4375F;
        float f1 = 0.5625F;
        float f2 = 0.4375F;
        float f3 = 0.5625F;
        boolean flag = this.canPaneConnectTo(blockAccess, x, y, z, ForgeDirection.NORTH);
        boolean flag1 = this.canPaneConnectTo(blockAccess, x, y, z, ForgeDirection.SOUTH);
        boolean flag2 = this.canPaneConnectTo(blockAccess, x, y, z, ForgeDirection.WEST);
        boolean flag3 = this.canPaneConnectTo(blockAccess, x, y, z, ForgeDirection.EAST);

        if ((!flag2 || !flag3) && (flag2 || flag3 || flag || flag1)) {
            if (flag2 && !flag3) {
                f = 0.0F;
            } else if (!flag2 && flag3) {
                f1 = 1.0F;
            }
        } else {
            f = 0.0F;
            f1 = 1.0F;
        }

        if ((!flag || !flag1) && (flag2 || flag3 || flag || flag1)) {
            if (flag && !flag1) {
                f2 = 0.0F;
            } else if (!flag && flag1) {
                f3 = 1.0F;
            }
        } else {
            f2 = 0.0F;
            f3 = 1.0F;
        }

        this.setBlockBounds(f, 0.0F, f2, f1, 1.0F, f3);
    }
}
