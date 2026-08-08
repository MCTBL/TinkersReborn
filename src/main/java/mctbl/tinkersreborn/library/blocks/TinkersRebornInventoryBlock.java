package mctbl.tinkersreborn.library.blocks;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import mctbl.tinkersreborn.TinkersReborn;
import mctbl.tinkersreborn.library.entity.TinkersRebornInventoryLogic;
import mctbl.tinkersreborn.library.gui.container.BaseContainer;
import mctbl.tinkersreborn.library.utils.BlockPos;

public abstract class TinkersRebornInventoryBlock extends BlockContainer {

    protected static Random rand = TinkersReborn.random;

    public String[] TEXTURENAMES;

    /* Textures */
    public IIcon[] icons;

    protected TinkersRebornInventoryBlock(Material m) {
        super(m);
    }

    public boolean openGui(EntityPlayer player, World world, BlockPos pos) {
        if (!world.isRemote) {
            player.openGui(TinkersReborn.instance, 0, world, pos.getX(), pos.getY(), pos.getZ());
            if (player.openContainer instanceof BaseContainer bc) {
                bc.syncOnOpen((EntityPlayerMP) player);
            }
        }
        return true;
    }

    public boolean openGui(EntityPlayer player, World world, int x, int y, int z) {
        return this.openGui(player, world, BlockPos.of(x, y, z));
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float clickX,
        float clickY, float clickZ) {
        if (player.isSneaking()) return false;

        if (!world.isRemote) {
            this.openGui(player, world, x, y, z);
            return true;
        }
        return true;
    }

    /* Inventory */
    @Override
    public void breakBlock(World par1World, int x, int y, int z, Block blockID, int meta) {
        TileEntity te = par1World.getTileEntity(x, y, z);

        if (te != null && te instanceof TinkersRebornInventoryLogic) {
            TinkersRebornInventoryLogic logic = (TinkersRebornInventoryLogic) te;
            logic.removeBlock();
            for (int iter = 0; iter < logic.getSizeInventory(); ++iter) {
                ItemStack stack = logic.getStackInSlot(iter);

                if (stack != null && logic.canDropInventorySlot(iter)) {
                    float jumpX = rand.nextFloat() * 0.8F + 0.1F;
                    float jumpY = rand.nextFloat() * 0.8F + 0.1F;
                    float jumpZ = rand.nextFloat() * 0.8F + 0.1F;

                    while (stack.stackSize > 0) {
                        int itemSize = rand.nextInt(21) + 10;

                        if (itemSize > stack.stackSize) {
                            itemSize = stack.stackSize;
                        }

                        stack.stackSize -= itemSize;
                        EntityItem entityitem = new EntityItem(
                            par1World,
                            (double) ((float) x + jumpX),
                            (double) ((float) y + jumpY),
                            (double) ((float) z + jumpZ),
                            new ItemStack(stack.getItem(), itemSize, stack.getItemDamage()));

                        if (stack.hasTagCompound()) {
                            entityitem.getEntityItem()
                                .setTagCompound(
                                    (NBTTagCompound) stack.getTagCompound()
                                        .copy());
                        }

                        float offset = 0.05F;
                        entityitem.motionX = (double) ((float) rand.nextGaussian() * offset);
                        entityitem.motionY = (double) ((float) rand.nextGaussian() * offset + 0.2F);
                        entityitem.motionZ = (double) ((float) rand.nextGaussian() * offset);
                        par1World.spawnEntityInWorld(entityitem);
                    }
                }
            }
        }

        super.breakBlock(par1World, x, y, z, blockID, meta);
    }

    // This class does not have an actual block placed in the world
    @Override
    public int onBlockPlaced(World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ, int meta) {
        return meta;
    }

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entityliving, ItemStack stack) {
        TileEntity logic = world.getTileEntity(x, y, z);

        if (logic instanceof ITinkersRebornIFacingLogic direction) {
            direction.setFacedDirection(entityliving);
        }

        if (logic instanceof TinkersRebornInventoryLogic inv) {
            inv.placeBlock(entityliving, stack);
            if (stack.hasDisplayName()) {
                inv.setInvName(stack.getDisplayName());
            }
        }
    }

    public int damageDropped(int meta) {
        return meta;
    }

    public String getTextureDomain(int textureNameIndex) {
        return "tinkersreborn";
    }

    @Override
    public void registerBlockIcons(IIconRegister iconRegister) {
        this.icons = new IIcon[TEXTURENAMES.length];

        for (int i = 0; i < this.icons.length; ++i) {
            this.icons[i] = iconRegister.registerIcon(getTextureDomain(i) + ":" + TEXTURENAMES[i]);
        }
    }

    public static boolean isActive(IBlockAccess world, int x, int y, int z) {
        TileEntity logic = world.getTileEntity(x, y, z);
        if (logic instanceof IActiveLogic l) {
            return l.getActive();
        }
        return false;
    }
}
