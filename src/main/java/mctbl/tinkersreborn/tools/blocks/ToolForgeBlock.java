package mctbl.tinkersreborn.tools.blocks;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mctbl.tinkersreborn.common.TinkersRebornGeneral;
import mctbl.tinkersreborn.library.TinkersRebornRegistry;
import mctbl.tinkersreborn.tools.TinkersRebornToolsProxyCommon;
import mctbl.tinkersreborn.tools.entity.TinkersRebornToolForgeLogic;

public class ToolForgeBlock extends ToolStationBlock {

    // TinkersRebornConfig.metalTypes
    public static final String[] materials = new String[] { "iron", "gold", "diamond", "emerald", "cobalt", "ardite",
        "manyullyn", "copper", "bronze", "tin", "aluminum", "alubrass", "alumite", "steel" };

    public ToolForgeBlock() {
        super(Material.iron);
        this.setHardness(2f);
        this.setStepSound(Block.soundTypeMetal);
        this.setBlockName("tinkersreborn.ToolFroge");
        this.setCreativeTab(TinkersRebornRegistry.blockTab);
        this.TEXTURENAMES = new String[] { "tools/toolforge_top", "tools/toolforge_%s" };
    }

    @Override
    public String getUnlocalizedName() {
        return "tinkersreborn.ToolFroge";
    }

    @Override
    public void registerBlockIcons(IIconRegister iconRegister) {
        List<IIcon> l = new ArrayList<>();
        l.add(iconRegister.registerIcon(getTextureDomain(0) + ":" + TEXTURENAMES[0]));

        for (int i = 1; i < this.TEXTURENAMES.length; i++) {
            for (String m : materials) {
                l.add(iconRegister.registerIcon(getTextureDomain(0) + ":" + String.format(TEXTURENAMES[i], m)));
            }
        }
        // top + each side
        this.icons = l.toArray(new IIcon[0]);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta) {
        return switch (ForgeDirection.getOrientation(side)) {
            case UP -> icons[0];
            case DOWN -> switch (meta) {
                    case 0 -> Blocks.iron_block.getIcon(side, 0);
                    case 1 -> Blocks.gold_block.getIcon(side, 0);
                    case 2 -> Blocks.diamond_block.getIcon(side, 0);
                    case 3 -> Blocks.emerald_block.getIcon(side, 0);
                    default -> TinkersRebornGeneral.metalBlock.getIcon(side, meta - 4);
                };
            default -> icons[meta + 1];
        };
    }

    @Override
    public void getSubBlocks(Item itemIn, CreativeTabs tab, List<ItemStack> list) {
        for (int idx = 0; idx < materials.length; idx++) {
            list.add(new ItemStack(itemIn, 1, idx));
        }
    }

    @Override
    public TileEntity createNewTileEntity(World world, int metadata) {
        return new TinkersRebornToolForgeLogic();
    }

    @Override
    public Integer getGui(World world, int x, int y, int z, EntityPlayer entityplayer) {
        return TinkersRebornToolsProxyCommon.toolForgeID;
    }
}
