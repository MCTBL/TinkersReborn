package mctbl.tinkersreborn.smeltery.blocks;

import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
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
import mctbl.tinkersreborn.library.blocks.TinkersRebornInventoryBlock;
import mctbl.tinkersreborn.library.entity.IMasterLogic;
import mctbl.tinkersreborn.library.entity.IServantLogic;
import mctbl.tinkersreborn.smeltery.TinkersRebornSmelteryProxyCommon;
import mctbl.tinkersreborn.smeltery.entity.MultiServantLogic;
import mctbl.tinkersreborn.smeltery.entity.SmelteryDrainLogic;
import mctbl.tinkersreborn.smeltery.entity.SmelteryLogic;
import mctbl.tinkersreborn.smeltery.model.SmelteryRender;

public class SmelteryBlock extends TinkersRebornInventoryBlock {

    enum type {
        cobble, // meta 0
        brick, // meta 1
        smeltery, // meta 2
        furnace, // meta 3
        drain; // meta 4
    }

    public SmelteryBlock() {
        super(Material.rock);
        this.setHardness(3F);
        this.setResistance(20F);
        this.setStepSound(soundTypeMetal);
        this.setCreativeTab(TinkersRebornRegistry.blockTab);
        this.setBlockName("tinkersreborn.Smeltery");
        this.TEXTURENAMES = new String[] { "smeltery/searedcobble", "smeltery/searedbrick",
            "smeltery/smeltery_inactive", "smeltery/smeltery_active", "smeltery/furnace_inactive",
            "smeltery/furnace_active", "smeltery/drain_basin", "smeltery/drain_out" };
    }

    @Override
    public String getUnlocalizedName() {
        return "tinkersreborn.Smeltery";
    }

    @Override
    public int getRenderType() {
        return SmelteryRender.smelteryModel;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta) {
        if (meta <= 1) {
            return this.icons[meta];
        } else {
            if (side == 3) {
                return this.icons[meta * 2 - 2];
            } else {
                return this.icons[1];
            }
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(IBlockAccess world, int x, int y, int z, int side) {
        int meta = world.getBlockMetadata(x, y, z);
        if (meta <= 1) {
            return this.getIcon(side, meta);
        } else {
            TileEntity logic = world.getTileEntity(x, y, z);
            ForgeDirection sideDirection = ForgeDirection.getOrientation(side);
            ForgeDirection faceingDirection = (logic instanceof ITinkersRebornIFacingLogic)
                ? ((ITinkersRebornIFacingLogic) logic).getForgeDirection()
                : ForgeDirection.getOrientation(0);
            if (meta == 4) {
                // drain
                if (sideDirection == faceingDirection) {
                    return this.icons[6];
                } else if (sideDirection == faceingDirection.getOpposite()) {
                    return this.icons[7];
                }
            } else {
                // smeltry or furnace
                if (sideDirection == faceingDirection) {
                    // 2 or 4
                    int idx = meta * 2 - 2 + (isActive(world, x, y, z) ? 1 : 0);
                    return this.icons[idx];
                }
            }
        }
        return this.icons[1];
    }

    @Override
    public Integer getGui(World world, int x, int y, int z, EntityPlayer entityplayer) {
        return TinkersRebornSmelteryProxyCommon.smelteryGuiID;
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
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float clickX,
        float clickY, float clickZ) {
        if (world.getBlockMetadata(x, y, z) <= 1) return false;

        return super.onBlockActivated(world, x, y, z, player, side, clickX, clickY, clickZ);
    }

    @Override
    public TileEntity createNewTileEntity(World world, int metadata) {
        switch (metadata) {
            case 2:
                return new SmelteryLogic();
            // case 3:
            // return new FurnaceLogic();
            case 4:
                return new SmelteryDrainLogic();
        }
        return new MultiServantLogic();
    }

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entityliving, ItemStack stack) {
        super.onBlockPlacedBy(world, x, y, z, entityliving, stack);
        if (world.getBlockMetadata(x, y, z) == 2) onBlockPlacedElsewhere(world, x, y, z, entityliving);
    }

    public void onBlockPlacedElsewhere(World world, int x, int y, int z, EntityLivingBase entityliving) {
        SmelteryLogic logic = (SmelteryLogic) world.getTileEntity(x, y, z);
        logic.checkValidPlacement();
    }

    @Override
    public void getSubBlocks(Item id, CreativeTabs tab, List<ItemStack> list) {
        for (int iter = 0; iter < 5; iter++) {
            list.add(new ItemStack(id, 1, iter));
        }
    }

    @Override
    public void onNeighborBlockChange(World world, int x, int y, int z, Block block) {
        TileEntity logic = world.getTileEntity(x, y, z);
        if (logic instanceof IServantLogic) {
            ((IServantLogic) logic).notifyMasterOfChange();
        } else if (logic instanceof IMasterLogic) {
            ((IMasterLogic) logic).notifyChange(null, x, y, z);
        }
    }

    @Override
    public void breakBlock(World world, int x, int y, int z, Block blockID, int meta) {
        TileEntity logic = world.getTileEntity(x, y, z);
        if (logic instanceof IServantLogic) {
            ((IServantLogic) logic).notifyMasterOfChange();
        }
        super.breakBlock(world, x, y, z, blockID, meta);
    }

    // Comparator
    @Override
    public boolean hasComparatorInputOverride() {
        return true;
    }

    @Override
    public int getComparatorInputOverride(World world, int x, int y, int z, int comparatorSide) {
        // TODO
        int meta = world.getBlockMetadata(x, y, z);
        if (meta == 0) {
            // return Container.calcRedstoneFromInventory(((SmelteryLogic) world.getTileEntity(x, y, z)));
        }
        if (meta == 1) {
            // return ((SmelteryDrainLogic) world.getTileEntity(x, y, z)).comparatorStrength();
        }
        return 0;
    }
}
