package mctbl.tinkersreborn.smeltery.items;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBucket;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mctbl.tinkersreborn.library.TinkersRebornRegistry;
import mctbl.tinkersreborn.library.materials.TinkersRebornMaterial;
import mctbl.tinkersreborn.util.TextureHelper;

public class FilledBucket extends ItemBucket {

    public Map<Integer, IIcon> icons;
    public IIcon bucket;
    public IIcon content;

    public FilledBucket(Block b) {
        super(b);
        this.setUnlocalizedName("tinkersreborn.bucket");
        this.setContainerItem(Items.bucket);
        this.setHasSubtypes(true);
        this.icons = new HashMap<>();
        this.setCreativeTab(TinkersRebornRegistry.miscTab);
    }

    @Override
    public boolean requiresMultipleRenderPasses() {
        return true;
    }

    @Override
    public int getRenderPasses(int metadata) {
        return 2;
    }

    @Override
    public int getColorFromItemStack(ItemStack stack, int renderpass) {
        int itemDamage = stack.getItemDamage();
        if (!icons.containsKey(itemDamage) && renderpass == 1) {
            return TinkersRebornRegistry.getMaterialById(itemDamage).materialTextColor;
        }
        return super.getColorFromItemStack(stack, renderpass);
    }

    @Override
    public IIcon getIcon(ItemStack stack, int renderpass) {
        int itemDamage = stack.getItemDamage();
        if (icons.containsKey(itemDamage)) {
            // has own icon
            return icons.get(itemDamage);
        } else {
            // other wise
            if (renderpass == 0) {
                return bucket;
            } else if (renderpass == 1) {
                return content;
            }
        }
        return super.getIcon(stack, renderpass);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister register) {
        List<TinkersRebornMaterial> castingMaterial = TinkersRebornRegistry.allMaterialsList.stream()
            .filter(m -> m.isCastable())
            .collect(Collectors.toList());
        for (TinkersRebornMaterial m : castingMaterial) {
            // tinkersreborn:bucket/bucket_copper
            String path = "tinkersreborn:bucket/bucket_" + m.identifier;
            if (TextureHelper.itemTextureExists(path)) {
                icons.put(m.materialId, register.registerIcon(path));
            }
        }
        bucket = register.registerIcon("tinkersreborn:bucket/bucket");
        content = register.registerIcon("tinkersreborn:bucket/bucket_content");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void getSubItems(Item item, CreativeTabs tabs, List<ItemStack> list) {
        TinkersRebornRegistry.allMaterialsList.stream()
            .filter(m -> m.isCastable())
            .forEach(m -> list.add(new ItemStack(item, 1, m.materialId)));
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        return super.getUnlocalizedName() + "."
            + TinkersRebornRegistry.getMaterialById(stack.getItemDamage()).identifier;
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        boolean wannabeFull = false;
        MovingObjectPosition position = this.getMovingObjectPositionFromPlayer(world, player, wannabeFull);
        if (position != null) {
            if (position.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
                int clickX = position.blockX;
                int clickY = position.blockY;
                int clickZ = position.blockZ;

                if (!world.canMineBlock(player, clickX, clickY, clickZ)) {
                    return stack;
                }

                if (position.sideHit == 0) {
                    --clickY;
                }

                if (position.sideHit == 1) {
                    ++clickY;
                }

                if (position.sideHit == 2) {
                    --clickZ;
                }

                if (position.sideHit == 3) {
                    ++clickZ;
                }

                if (position.sideHit == 4) {
                    --clickX;
                }

                if (position.sideHit == 5) {
                    ++clickX;
                }

                if (!player.canPlayerEdit(clickX, clickY, clickZ, position.sideHit, stack)) {
                    return stack;
                }

                if (this.tryPlaceContainedLiquid(world, clickX, clickY, clickZ, stack.getItemDamage())
                    && !player.capabilities.isCreativeMode) {
                    return new ItemStack(Items.bucket);
                }
            }
        }
        return stack;
    }

    public boolean tryPlaceContainedLiquid(World world, int clickX, int clickY, int clickZ, int damage) {
        if (!world.isAirBlock(clickX, clickY, clickZ) && world.getBlock(clickX, clickY, clickZ)
            .getMaterial()
            .isSolid()) {
            return false;
        } else {
            world.setBlock(
                clickX,
                clickY,
                clickZ,
                TinkersRebornRegistry.getMaterialById(damage)
                    .getFluid()
                    .getBlock(),
                0,
                3);
            return true;
        }
    }

}
