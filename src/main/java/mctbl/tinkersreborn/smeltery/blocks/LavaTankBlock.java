package mctbl.tinkersreborn.smeltery.blocks;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mctbl.tinkersreborn.library.TinkersRebornRegistry;
import mctbl.tinkersreborn.library.entity.IServantLogic;
import mctbl.tinkersreborn.smeltery.entity.LavaTankLogic;
import mctbl.tinkersreborn.smeltery.itemblocks.LavaTankItemBlock;
import mctbl.tinkersreborn.smeltery.model.TankRender;

public class LavaTankBlock extends BlockContainer {

    public IIcon[] icons;
    public String[] textureNames;

    public LavaTankBlock() {
        super(Material.rock);
        this.setHardness(3F);
        this.setResistance(20F);
        this.setCreativeTab(TinkersRebornRegistry.blockTab);
        this.setBlockName("tinkersreborn.LavaTank");
        this.setStepSound(Block.soundTypeGlass);
        this.textureNames = new String[] { "lavatank_top", "lavatank_side", "lavatank_bottom", "searedgague_top",
            "searedgague_side", "searedgague_bottom", "searedwindow_top", "searedwindow_side", "searedwindow_bottom" };
    }

    @Override
    public String getUnlocalizedName() {
        return "tinkersreborn.LavaTank";
    }

    @Override
    public void registerBlockIcons(IIconRegister reg) {
        this.icons = new IIcon[this.textureNames.length];
        for (int i = 0; i < this.textureNames.length; i++) {
            this.icons[i] = reg.registerIcon("tinkersreborn:smeltery/" + this.textureNames[i]);
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta) {
        int startIdx = meta * 3 + 1;
        ForgeDirection f = ForgeDirection.getOrientation(side);
        if (f == ForgeDirection.DOWN) startIdx += 1;
        else if (f == ForgeDirection.UP) startIdx -= 1;
        return this.icons[startIdx];
    }

    @Override
    public int getRenderBlockPass() {
        return 1;
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
    public boolean shouldSideBeRendered(IBlockAccess world, int x, int y, int z, int side) {
        Block bID = world.getBlock(x, y, z);
        return bID != this && super.shouldSideBeRendered(world, x, y, z, side);
    }

    @Override
    public boolean canRenderInPass(int pass) {
        return true;
    }

    @Override
    public int getLightValue(IBlockAccess world, int x, int y, int z) {
        TileEntity logic = world.getTileEntity(x, y, z);
        if (logic instanceof LavaTankLogic l) return l.getBrightness();
        return 0;
    }

    @Override
    public TileEntity createTileEntity(World world, int metadata) {
        return new LavaTankLogic();
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return this.createTileEntity(worldIn, meta);
    }

    @Override
    public void getSubBlocks(Item id, CreativeTabs tab, List<ItemStack> list) {
        for (int iter = 0; iter < 3; iter++) {
            list.add(new ItemStack(id, 1, iter));
        }
    }

    @Override
    public int getRenderType() {
        return TankRender.tankModelID;
    }

    @Override
    public boolean onBlockActivated(World world, int i, int j, int k, EntityPlayer entityplayer, int par6, float par7,
        float par8, float par9) {
        ItemStack current = entityplayer.inventory.getCurrentItem();
        if (current != null) {
            FluidStack liquid = FluidContainerRegistry.getFluidForFilledItem(current);
            LavaTankLogic logic = (LavaTankLogic) world.getTileEntity(i, j, k);
            // putting liquid into the tank
            if (liquid != null && !world.isRemote) {
                int amount = logic.fill(ForgeDirection.UNKNOWN, liquid, false);
                if (amount == liquid.amount) {
                    logic.fill(ForgeDirection.UNKNOWN, liquid, true);
                    if (!entityplayer.capabilities.isCreativeMode) {
                        replaceHeldItem(entityplayer, FluidContainerRegistry.drainFluidContainer(current));
                    }

                    // update
                    entityplayer.inventoryContainer.detectAndSendChanges();
                    world.markBlockForUpdate(i, j, k);
                }

                return true;
            }
            // taking liquit out of the tank
            else if (FluidContainerRegistry.isContainer(current)) {
                FluidTankInfo[] tanks = logic.getTankInfo(ForgeDirection.UNKNOWN);
                FluidStack fillFluid = tanks[0].fluid; // getFluid();
                if (!world.isRemote) {
                    ItemStack fillStack = FluidContainerRegistry.fillFluidContainer(fillFluid, current);
                    if (fillStack != null) {
                        logic.drain(
                            ForgeDirection.UNKNOWN,
                            FluidContainerRegistry.getFluidForFilledItem(fillStack).amount,
                            true);
                        if (!entityplayer.capabilities.isCreativeMode && !world.isRemote) {
                            replaceHeldItem(entityplayer, fillStack);

                            // update inventory
                            entityplayer.inventoryContainer.detectAndSendChanges();
                            // and block
                        }
                        world.markBlockForUpdate(i, j, k);
                    }
                }

                return true;
            }
        }

        return false;
    }

    /**
     * Replace one currently held item for a given player.
     *
     * @param player      A player
     * @param replacement An ItemStack that will replace one of the items in the player's currently held ItemStack.
     */
    private static void replaceHeldItem(EntityPlayer player, ItemStack replacement) {
        ItemStack current = player.inventory.getCurrentItem();
        if (current.stackSize == 1) {
            player.inventory.setInventorySlotContents(player.inventory.currentItem, replacement);
        } else {
            player.inventory.decrStackSize(player.inventory.currentItem, 1);

            if (!player.inventory.addItemStackToInventory(replacement)) {
                player.dropPlayerItemWithRandomChoice(replacement, false);
            }
        }
    }

    /* Data */
    @Override
    public int damageDropped(int meta) {
        return meta;
    }

    /* Updates */
    @Override
    public void onNeighborBlockChange(World world, int x, int y, int z, Block nBlockID) {
        TileEntity logic = world.getTileEntity(x, y, z);
        if (logic instanceof IServantLogic servant) {
            servant.notifyMasterOfChange();
        }
    }

    @Override
    public boolean removedByPlayer(World world, EntityPlayer player, int x, int y, int z) {
        player.addExhaustion(0.025F);
        int meta = world.getBlockMetadata(x, y, z);
        ItemStack stack = new ItemStack(this, 1, meta);
        LavaTankLogic logic = (LavaTankLogic) world.getTileEntity(x, y, z);
        FluidStack liquid = logic.tank.getFluid();
        if (liquid != null) {
            LavaTankItemBlock lavaTankItemBlock = (LavaTankItemBlock) stack.getItem();
            lavaTankItemBlock.setFluid(stack, liquid);
        }
        if (!player.capabilities.isCreativeMode || player.isSneaking()) dropTankBlock(world, x, y, z, stack);

        return world.setBlockToAir(x, y, z);
    }

    protected void dropTankBlock(World world, int x, int y, int z, ItemStack stack) {
        if (!world.isRemote && world.getGameRules()
            .getGameRuleBooleanValue("doTileDrops")) {
            float f = 0.7F;
            double d0 = (double) (world.rand.nextFloat() * f) + (double) (1.0F - f) * 0.5D;
            double d1 = (double) (world.rand.nextFloat() * f) + (double) (1.0F - f) * 0.5D;
            double d2 = (double) (world.rand.nextFloat() * f) + (double) (1.0F - f) * 0.5D;
            EntityItem entityitem = new EntityItem(world, (double) x + d0, (double) y + d1, (double) z + d2, stack);
            entityitem.delayBeforeCanPickup = 10;
            world.spawnEntityInWorld(entityitem);
        }
    }

    @Override
    public void harvestBlock(World par1World, EntityPlayer par2EntityPlayer, int par3, int par4, int par5, int par6) {}

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase living, ItemStack stack) {
        LavaTankItemBlock lavaTankItemBlock = (LavaTankItemBlock) stack.getItem();
        FluidStack liquid = lavaTankItemBlock.getFluid(stack);
        if (liquid != null) {
            LavaTankLogic logic = (LavaTankLogic) world.getTileEntity(x, y, z);
            logic.tank.setFluid(liquid);
        }
    }

    // Comparator
    @Override
    public boolean hasComparatorInputOverride() {
        return true;
    }

    @Override
    public int getComparatorInputOverride(World world, int x, int y, int z, int comparatorSide) {
        return getTankLogic(world, x, y, z).comparatorStrength();
    }

    public static LavaTankLogic getTankLogic(IBlockAccess blockAccess, int par1, int par2, int par3) {
        return (LavaTankLogic) blockAccess.getTileEntity(par1, par2, par3);
    }
}
