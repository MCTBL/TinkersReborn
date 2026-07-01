package mctbl.tinkersreborn.smeltery.blocks;

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

public class FurnaceController extends TinkersRebornMultiBlock {

    public FurnaceController() {
        super(Material.rock);
        this.setHardness(3F);
        this.setResistance(20F);
        this.setStepSound(soundTypeMetal);
        this.setCreativeTab(TinkersRebornRegistry.blockTab);
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
        // TODO
        return null;
    }

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entityliving, ItemStack stack) {
        // TODO
        super.onBlockPlacedBy(world, x, y, z, entityliving, stack);
        // ((SmelteryLogic) world.getTileEntity(x, y, z)).checkValidPlacement();
    }
}
