package mctbl.tinkersreborn.smeltery.blocks;

import java.util.Random;

import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mctbl.tinkersreborn.library.TinkersRebornRegistry;
import mctbl.tinkersreborn.library.blocks.ITinkersRebornIFacingLogic;
import mctbl.tinkersreborn.library.blocks.TinkersRebornMultiBlock;
import mctbl.tinkersreborn.smeltery.entity.SmelteryLogic;

public class SmelteryController extends TinkersRebornMultiBlock {

    public SmelteryController() {
        super(Material.rock);
        this.setHardness(3F);
        this.setResistance(20F);
        this.setStepSound(soundTypeMetal);
        this.setCreativeTab(TinkersRebornRegistry.blockTab);
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
    public void randomDisplayTick(World world, int x, int y, int z, Random random) {
        if (isActive(world, x, y, z)) {
            TileEntity logic = world.getTileEntity(x, y, z);
            byte face = 0;
            if (logic instanceof ITinkersRebornIFacingLogic)
                face = ((ITinkersRebornIFacingLogic) logic).getRenderDirection();
            float f = (float) x + 0.5F;
            float f1 = (float) y + 0.5F + (random.nextFloat() * 6F) / 16F;
            float f2 = (float) z + 0.5F;
            float f3 = 0.52F;
            float f4 = random.nextFloat() * 0.6F - 0.3F;
            switch (face) {
                case 4:
                    world.spawnParticle("smoke", f - f3, f1, f2 + f4, 0.0D, 0.0D, 0.0D);
                    world.spawnParticle("flame", f - f3, f1, f2 + f4, 0.0D, 0.0D, 0.0D);
                    break;

                case 5:
                    world.spawnParticle("smoke", f + f3, f1, f2 + f4, 0.0D, 0.0D, 0.0D);
                    world.spawnParticle("flame", f + f3, f1, f2 + f4, 0.0D, 0.0D, 0.0D);
                    break;

                case 2:
                    world.spawnParticle("smoke", f + f4, f1, f2 - f3, 0.0D, 0.0D, 0.0D);
                    world.spawnParticle("flame", f + f4, f1, f2 - f3, 0.0D, 0.0D, 0.0D);
                    break;

                case 3:
                    world.spawnParticle("smoke", f + f4, f1, f2 + f3, 0.0D, 0.0D, 0.0D);
                    world.spawnParticle("flame", f + f4, f1, f2 + f3, 0.0D, 0.0D, 0.0D);
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
        super.onBlockPlacedBy(world, x, y, z, entityliving, stack);
        ((SmelteryLogic) world.getTileEntity(x, y, z)).checkValidPlacement();
    }
}
