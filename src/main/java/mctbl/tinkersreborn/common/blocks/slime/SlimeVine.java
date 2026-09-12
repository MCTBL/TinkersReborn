package mctbl.tinkersreborn.common.blocks.slime;

import net.minecraft.block.Block;
import net.minecraft.block.BlockVine;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import mctbl.tinkersreborn.TinkersReborn;
import mctbl.tinkersreborn.common.TinkersRebornGeneral;
import mctbl.tinkersreborn.library.TinkersRebornRegistry;

public class SlimeVine extends BlockVine {

    public SlimeVine() {
        super();
        this.setCreativeTab(TinkersRebornRegistry.blockTab);
        this.setStepSound(Block.soundTypeGrass);
    }

    @Override
    public void registerBlockIcons(IIconRegister reg) {
        this.blockIcon = reg.registerIcon("tinkersreborn:slime/slime_vine");
    }

    @Override
    public String getUnlocalizedName() {
        return TinkersReborn.MODID + ".slime.vine";
    }

    @Override
    public int colorMultiplier(IBlockAccess worldIn, int x, int y, int z) {
        return 0xFFFFFF;
    }

    @Override
    public boolean canPlaceBlockOnSide(World world, int x, int y, int z, int side) {
        int supportX = x;
        int supportY = y;
        int supportZ = z;
        switch (side) {
            case 1:
                supportY++;
                break;
            case 2:
                supportZ++;
                break;
            case 3:
                supportZ--;
                break;
            case 4:
                supportX++;
                break;
            case 5:
                supportX--;
                break;
            default:
                return false;
        }
        if (world.getBlock(supportX, supportY, supportZ) == TinkersRebornGeneral.slimeLeaves) {
            return true;
        }
        return super.canPlaceBlockOnSide(world, x, y, z, side);
    }

}
