package mctbl.tinkersreborn.smeltery.blocks;

import java.util.Random;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mctbl.tinkersreborn.TinkersReborn;
import mctbl.tinkersreborn.library.blocks.ITinkersRebornIFacingLogic;
import mctbl.tinkersreborn.library.blocks.TinkersRebornMultiBlock;
import mctbl.tinkersreborn.library.entity.IMasterLogic;
import mctbl.tinkersreborn.smeltery.entity.FurnaceLogic;

public class FurnaceController extends TinkersRebornMultiBlock {

    public FurnaceController() {
        super();
        this.setBlockName("tinkersreborn.FurnaceController");
        this.TEXTURENAMES = new String[] { "smeltery/furnace_inactive", "smeltery/furnace_active" };
    }

    @Override
    public String getUnlocalizedName() {
        return "tinkersreborn.FurnaceController";
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(IBlockAccess world, int x, int y, int z, int side) {
        TileEntity logic = world.getTileEntity(x, y, z);
        ForgeDirection sideDirection = ForgeDirection.getOrientation(side);
        ForgeDirection faceingDirection = (logic instanceof ITinkersRebornIFacingLogic)
            ? ((ITinkersRebornIFacingLogic) logic).getForgeDirection()
            : ForgeDirection.getOrientation(0);

        // smeltry or furnace
        if (sideDirection == faceingDirection) {
            return this.icons[isActive(world, x, y, z) ? 1 : 0];
        }

        return super.sideIcon;
    }

    @Override
    public IIcon getIcon(int side, int meta) {
        if (side == 3) {
            return this.icons[0];
        } else {
            return this.sideIcon;
        }
    }

    @Override
    public TileEntity createNewTileEntity(World world, int metadata) {
        return new FurnaceLogic();
    }

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entityliving, ItemStack stack) {
        super.onBlockPlacedBy(world, x, y, z, entityliving, stack);
        ((IMasterLogic) world.getTileEntity(x, y, z)).checkWholeStructureValid();
    }

    @Override
    public void randomDisplayTick(World world, int x, int y, int z, Random random) {
        if (isActive(world, x, y, z)) {
            TileEntity logic = world.getTileEntity(x, y, z);
            ForgeDirection face = ForgeDirection.NORTH;
            if (logic instanceof ITinkersRebornIFacingLogic facingLogic) face = facingLogic.getForgeDirection();

            double wBias = TinkersReborn.random.nextDouble() * 0.8D - 0.4D;
            double hBias = TinkersReborn.random.nextDouble() * 0.8D + 0.1D;
            world.spawnParticle(
                "smoke",
                x + 0.5D + face.offsetX * 0.55D + Math.abs(face.offsetZ) * wBias,
                y + hBias,
                z + 0.5D + face.offsetZ * 0.55D + Math.abs(face.offsetX) * wBias,
                0.0D,
                0.02D,
                0.0D);
            world.spawnParticle(
                "flame",
                x + 0.5D + face.offsetX * 0.55D + Math.abs(face.offsetZ) * wBias,
                y + hBias,
                z + 0.5D + face.offsetZ * 0.55D + Math.abs(face.offsetX) * wBias,
                0.0D,
                0.02D,
                0.0D);
        }
    }

    @Override
    public int getLightValue(IBlockAccess world, int x, int y, int z) {
        return !isActive(world, x, y, z) ? 0 : 9;
    }
}
