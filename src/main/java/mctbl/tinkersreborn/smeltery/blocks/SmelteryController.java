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
import org.joml.Vector3f;

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
            Vector3f center = new Vector3f(x + 0.5f, y + 0.5f, z + 0.5f);
            Vector3f rota = new Vector3f(0.52F, (random.nextFloat() * 6F) / 16F, random.nextFloat() * 0.6F - 0.3F);
            rota.rotateY((float) Math.toRadians(getRotationYaw(face)));
            rota.add(center);

            world.spawnParticle("smoke", rota.x, rota.y, rota.z, 0.0D, 0.0D, 0.0D);
                world.spawnParticle("flame", rota.x, rota.y, rota.z, 0.0D, 0.0D, 0.0D);
        }
    }

    public static float getRotationYaw(ForgeDirection facing) {
        return switch (facing) {
            case NORTH -> 90;
            case WEST -> 180;
            case EAST -> 0;
            case SOUTH -> 270;
            default -> 0;
        };
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
