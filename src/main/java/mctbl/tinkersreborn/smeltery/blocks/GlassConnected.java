package mctbl.tinkersreborn.smeltery.blocks;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.event.TextureStitchEvent;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mctbl.tinkersreborn.TinkersReborn;
import mctbl.tinkersreborn.library.TinkersRebornRegistry;
import mctbl.tinkersreborn.library.blocks.TinkersRebornBlock;
import mctbl.tinkersreborn.library.texture.SubIcon;
import mctbl.tinkersreborn.smeltery.entity.MultiServantLogic;

public class GlassConnected extends TinkersRebornBlock {

    protected String folder;
    private final int renderPass;
    private final boolean canPlayerPass;
    private final boolean hasTileEntity;
    private IIcon bigIcon;

    private boolean isInit = false;

    public GlassConnected(String location, boolean hasAlpha) {
        this(location, hasAlpha, false, false);
    }

    public GlassConnected(String location, boolean hasAlpha, boolean canPlayerPass, boolean hasTileEntity) {
        super(Material.glass);
        this.stepSound = soundTypeGlass;
        this.folder = location;
        this.renderPass = hasAlpha ? 1 : 0;
        this.setHardness(0.3F);
        this.setCreativeTab(TinkersRebornRegistry.blockTab);
        this.icons = new IIcon[47];
        this.unlocalizedName = location + "GlassBlock";
        this.canPlayerPass = canPlayerPass;
        this.hasTileEntity = hasTileEntity;
    }

    @Override
    public boolean hasTileEntity(int metadata) {
        return this.hasTileEntity;
    }

    @Override
    public String getUnlocalizedName() {
        return TinkersReborn.MODID + "." + this.unlocalizedName;
    }

    @Override
    public void getSubBlocks(Item block, CreativeTabs tab, List<ItemStack> list) {
        list.add(new ItemStack(block, 1, 0));
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
    public int getRenderBlockPass() {
        return renderPass;
    }

    /**
     * This is checked to see if the texture should connect to this block
     * 
     * @param x
     * @param y
     * @param z
     * @param block ID this block is asking to connect to (may be 0 if there is no block)
     * @param meta  Metadata of the block this block is trying to connect to
     * @return true if should connect
     */
    public boolean shouldConnectToBlock(IBlockAccess blockAccess, int x, int y, int z, Block block, int meta) {
        return block == this;
    }

    @Override
    public boolean shouldSideBeRendered(IBlockAccess blockAccess, int x, int y, int z, int meta) {
        return blockAccess.getBlock(x, y, z) != this && super.shouldSideBeRendered(blockAccess, x, y, z, meta);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta) {
        if (!isInit) {
            this.buildSubIcons();
        }
        return icons[0];
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(IBlockAccess blockAccess, int x, int y, int z, int side) {
        if (!isInit) {
            this.buildSubIcons();
        }
        return blockAccess.getBlockMetadata(x, y, z) == 15 ? icons[0]
            : getConnectedBlockTexture(blockAccess, x, y, z, side, icons);
    }

    public IIcon getConnectedBlockTexture(IBlockAccess blockAccess, int x, int y, int z, int side, IIcon[] icons) {
        if (side < 0 || side >= 6) return icons[0];

        int[][] offsets = NEIGHBOR8[side];
        int mask = 0;

        for (int i = 0; i < 8; i++) {
            int nx = x + offsets[i][0];
            int ny = y + offsets[i][1];
            int nz = z + offsets[i][2];
            boolean connected = shouldConnectToBlock(
                blockAccess,
                x,
                y,
                z,
                blockAccess.getBlock(nx, ny, nz),
                blockAccess.getBlockMetadata(nx, ny, nz));
            if (connected) mask |= (1 << i);
        }

        // bit 0=Bottom, 1=Top, 2=Left, 3=Right, 4=BL, 5=BR, 6=TL, 7=TR
        if ((mask & 0x01) == 0 || (mask & 0x04) == 0) mask &= ~(1 << 4); // BL needs Bottom && Left
        if ((mask & 0x01) == 0 || (mask & 0x08) == 0) mask &= ~(1 << 5); // BR needs Bottom && Right
        if ((mask & 0x02) == 0 || (mask & 0x04) == 0) mask &= ~(1 << 6); // TL needs Top && Left
        if ((mask & 0x02) == 0 || (mask & 0x08) == 0) mask &= ~(1 << 7); // TR needs Top && Right

        int ctmIndex = CTM_INDEX[mask];

        int col = ctmIndex % 12;
        int row = ctmIndex / 12;
        return icons[row * 12 + col];
    }

    @Override
    public TileEntity createTileEntity(World world, int metadata) {
        if (this.hasTileEntity) {
            return new MultiServantLogic();
        }
        return null;
    }

    @Override
    public void registerBlockIcons(IIconRegister iconRegister) {
        bigIcon = iconRegister.registerIcon("tinkersreborn:glass/" + folder);
    }

    @SubscribeEvent
    public void onTextureStitch(TextureStitchEvent.Post event) {
        if (event.map.getTextureType() == 1) {
            this.buildSubIcons();
        }
    }

    public void buildSubIcons() {
        for (int i = 0; i < 47; i++) {
            icons[i] = new SubIcon(bigIcon, i % 12, i / 12);
        }
        this.isInit = true;
    }

    @Override
    public boolean canPlaceTorchOnTop(World world, int x, int y, int z) {
        return true;
    }

    @Override
    public void addCollisionBoxesToList(World worldIn, int x, int y, int z, AxisAlignedBB mask,
        List<AxisAlignedBB> list, Entity collider) {
        if (this.canPlayerPass && collider instanceof EntityPlayer) {
            return;
        }

        super.addCollisionBoxesToList(worldIn, x, y, z, mask, list, collider);
    }

    // for CTM texture
    private static final int[][][] NEIGHBOR8 = {
        { { 0, 0, 1 }, { 0, 0, -1 }, { 1, 0, 0 }, { -1, 0, 0 }, { 1, 0, 1 }, { -1, 0, 1 }, { 1, 0, -1 },
            { -1, 0, -1 } },
        { { 0, 0, 1 }, { 0, 0, -1 }, { -1, 0, 0 }, { 1, 0, 0 }, { -1, 0, 1 }, { 1, 0, 1 }, { -1, 0, -1 },
            { 1, 0, -1 } },
        { { 0, -1, 0 }, { 0, 1, 0 }, { 1, 0, 0 }, { -1, 0, 0 }, { 1, -1, 0 }, { -1, -1, 0 }, { 1, 1, 0 },
            { -1, 1, 0 } },
        { { 0, -1, 0 }, { 0, 1, 0 }, { -1, 0, 0 }, { 1, 0, 0 }, { -1, -1, 0 }, { 1, -1, 0 }, { -1, 1, 0 },
            { 1, 1, 0 } },
        { { 0, -1, 0 }, { 0, 1, 0 }, { 0, 0, -1 }, { 0, 0, 1 }, { 0, -1, -1 }, { 0, -1, 1 }, { 0, 1, -1 },
            { 0, 1, 1 } },
        { { 0, -1, 0 }, { 0, 1, 0 }, { 0, 0, 1 }, { 0, 0, -1 }, { 0, -1, 1 }, { 0, -1, -1 }, { 0, 1, 1 },
            { 0, 1, -1 } }, };

    private static final int[] CTM_INDEX = new int[256];
    static {
        java.util.Arrays.fill(CTM_INDEX, 0);

        int bottom = 1 << 0;
        int top = 1 << 1;
        int left = 1 << 2;
        int right = 1 << 3;
        int bl = 1 << 4;
        int br = 1 << 5;
        int tl = 1 << 6;
        int tr = 1 << 7;

        // https://github.com/sp614x/optifine/blob/master/OptiFineDoc/doc/images/ctm_template.png
        put(1, right);
        put(3, left);
        put(12, bottom);
        put(36, top);

        put(2, right | left);
        put(4, bottom | right);
        put(5, bottom | left);
        put(16, top | right);
        put(17, top | left);
        put(24, bottom | top);

        put(6, top | bottom | right);
        put(7, bottom | left | right);
        put(13, br | right | bottom);
        put(15, bl | bottom | left);
        put(18, top | left | right);
        put(19, top | left | bottom);
        put(37, tr | top | right);
        put(39, tl | top | left);

        put(28, br | top | right | bottom);
        put(29, bl | right | bottom | left);
        put(30, tr | top | right | bottom);
        put(31, br | right | bottom | left);
        put(40, tr | top | right | left);
        put(41, tl | top | bottom | left);
        put(42, tl | top | right | left);
        put(43, bl | top | bottom | left);
        put(46, top | right | bottom | left);

        put(8, tr | top | right | bottom | left);
        put(9, br | top | right | bottom | left);
        put(14, br | bl | right | bottom | left);
        put(20, tl | top | right | bottom | left);
        put(21, bl | top | right | bottom | left);
        put(25, br | tr | top | right | bottom);
        put(27, bl | tl | top | bottom | left);
        put(38, tr | tl | top | right | left);

        put(10, bl | tl | top | right | bottom | left);
        put(11, tr | tl | top | right | bottom | left);
        put(22, br | bl | top | right | bottom | left);
        put(23, br | tr | top | right | bottom | left);
        put(34, bl | tr | top | right | bottom | left);
        put(35, br | tl | top | right | bottom | left);

        put(32, tl | bl | tr | top | right | bottom | left);
        put(33, br | tr | tl | top | right | bottom | left);
        put(44, br | bl | tl | top | right | bottom | left);
        put(45, br | bl | tr | top | right | bottom | left);

        put(26, br | bl | tr | tl | top | right | bottom | left);
    }

    private static void put(int index, int mask) {
        CTM_INDEX[mask] = index;
    }

}
