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
import mctbl.tinkersreborn.library.blocks.ITinkersRebornIFacingLogic;
import mctbl.tinkersreborn.library.blocks.TinkersRebornMultiBlock;
import mctbl.tinkersreborn.library.entity.IMasterLogic;
import mctbl.tinkersreborn.smeltery.entity.SmelteryLogic;
import mctbl.tinkersreborn.smeltery.model.SmelteryRender;

public class SmelteryController extends TinkersRebornMultiBlock {

    public SmelteryController() {
        super();
        this.setBlockName("tinkersreborn.SmelteryController");
        this.TEXTURENAMES = new String[] { "smeltery/smeltery_inactive", "smeltery/smeltery_active" };
    }

    @Override
    public String getUnlocalizedName() {
        return "tinkersreborn.SmelteryController";
    }

    @Override
    public TileEntity createNewTileEntity(World world, int metadata) {
        return new SmelteryLogic();
    }

    @Override
    public int getRenderType() {
        return SmelteryRender.smelteryModel;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(IBlockAccess world, int x, int y, int z, int side) {
        TileEntity logic = world.getTileEntity(x, y, z);
        ForgeDirection faceingDirection = (logic instanceof ITinkersRebornIFacingLogic)
            ? ((ITinkersRebornIFacingLogic) logic).getForgeDirection()
            : ForgeDirection.NORTH;

        // smeltry or furnace
        if (ForgeDirection.getOrientation(side) == faceingDirection) {
            return this.icons[isActive(world, x, y, z) ? 1 : 0];
        }

        return this.sideIcon;
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
    public void randomDisplayTick(World world, int x, int y, int z, Random random) {
        if (isActive(world, x, y, z)) {
            TileEntity logic = world.getTileEntity(x, y, z);
            ForgeDirection face = ForgeDirection.NORTH;
            if (logic instanceof ITinkersRebornIFacingLogic)
                face = ((ITinkersRebornIFacingLogic) logic).getForgeDirection();
            float f = (float) x + 0.5F;
            float f1 = (float) y + 0.5F + (random.nextFloat() * 6F) / 16F;
            float f2 = (float) z + 0.5F;
            float f3 = 0.52F;
            float f4 = random.nextFloat() * 0.6F - 0.3F;
            switch (face) {
                case WEST:
                    world.spawnParticle("smoke", f - f3, f1, f2 + f4, 0.0D, 0.0D, 0.0D);
                    world.spawnParticle("flame", f - f3, f1, f2 + f4, 0.0D, 0.0D, 0.0D);
                    break;

                case EAST:
                    world.spawnParticle("smoke", f + f3, f1, f2 + f4, 0.0D, 0.0D, 0.0D);
                    world.spawnParticle("flame", f + f3, f1, f2 + f4, 0.0D, 0.0D, 0.0D);
                    break;

                case SOUTH:
                    world.spawnParticle("smoke", f + f4, f1, f2 + f3, 0.0D, 0.0D, 0.0D);
                    world.spawnParticle("flame", f + f4, f1, f2 + f3, 0.0D, 0.0D, 0.0D);
                    break;

                case NORTH:
                default:
                    world.spawnParticle("smoke", f + f4, f1, f2 - f3, 0.0D, 0.0D, 0.0D);
                    world.spawnParticle("flame", f + f4, f1, f2 - f3, 0.0D, 0.0D, 0.0D);
                    break;
            }
        }
    }

    @Override
    public int getLightValue(IBlockAccess world, int x, int y, int z) {
        return !isActive(world, x, y, z) ? 0 : 9;
    }

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entityliving, ItemStack stack) {
        // TileEntity logic = world.getTileEntity(x, y, z);
        // if (logic instanceof ITinkersRebornIFacingLogic direction) {
        // direction.setFacedDirection(entityliving);
        // }
        super.onBlockPlacedBy(world, x, y, z, entityliving, stack);
        ((IMasterLogic) world.getTileEntity(x, y, z)).checkWholeStructureValid();
    }
}
