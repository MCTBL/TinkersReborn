package mctbl.tinkersreborn.common.blocks.slime;

import net.minecraft.block.Block;
import net.minecraft.block.BlockVine;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import mctbl.tinkersreborn.TinkersReborn;
import mctbl.tinkersreborn.TinkersRebornConfig;
import mctbl.tinkersreborn.common.TinkersRebornGeneral;
import mctbl.tinkersreborn.common.model.SlimeVineRender;
import mctbl.tinkersreborn.library.TinkersRebornRegistry;

public class SlimeVine extends BlockVine {

    private IIcon[] icons;
    private String type;
    private int color;

    public SlimeVine(int type) {
        super();
        this.setCreativeTab(TinkersRebornRegistry.blockTab);
        this.setStepSound(Block.soundTypeGrass);
        this.type = TinkersRebornConfig.slimeTypes[type];
        this.color = TinkersRebornConfig.slimeColors[type];
    }

    @Override
    public int getRenderType() {
        return SlimeVineRender.model;
    }

    @Override
    public String getUnlocalizedName() {
        return TinkersReborn.MODID + ".slime.vine." + this.type;
    }

    @Override
    public void registerBlockIcons(IIconRegister reg) {
        this.icons = new IIcon[3];
        this.icons[0] = reg.registerIcon("tinkersreborn:slime/slimevine");
        this.icons[1] = reg.registerIcon("tinkersreborn:slime/slimevine_mid");
        this.icons[2] = reg.registerIcon("tinkersreborn:slime/slimevine_end");
    }

    @Override
    public int getRenderColor(int meta) {
        return this.color;
    }

    @Override
    public int colorMultiplier(IBlockAccess worldIn, int x, int y, int z) {
        return this.color;
    }

    @Override
    public IIcon getIcon(int side, int meta) {
        return icons[0];
    }

    @Override
    public IIcon getIcon(IBlockAccess worldIn, int x, int y, int z, int side) {
        if (worldIn.getBlock(x, y + 1, z) != this) {
            return icons[0];
        } else if (worldIn.getBlock(x, y - 1, z) != this) {
            return icons[2];
        } else {
            return icons[1];
        }
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
