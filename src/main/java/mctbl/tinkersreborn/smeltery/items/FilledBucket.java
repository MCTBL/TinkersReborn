package mctbl.tinkersreborn.smeltery.items;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBucket;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mctbl.tinkersreborn.library.TinkersRebornRegistry;
import mctbl.tinkersreborn.smeltery.blocks.TinkersRebornFluid;
import mctbl.tinkersreborn.util.TextureHelper;
import mctbl.tinkersreborn.util.TinkersRebornUtils;
import mctbl.tinkersreborn.util.ToolTags;
import mctbl.tinkersreborn.util.ToolTagsHelper;

public class FilledBucket extends ItemBucket {

    public Map<String, IIcon> icons;
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
    public String getUnlocalizedName() {
        return "tinkersreborn.bucket";
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
        TinkersRebornFluid fluidByIdentifier = TinkersRebornRegistry.getFluidByIdentifier(this.readNBT(stack));
        if (!icons.containsKey(fluidByIdentifier.identifier) && renderpass == 1) {
            return fluidByIdentifier.color;
        }
        return super.getColorFromItemStack(stack, renderpass);
    }

    @Override
    public IIcon getIcon(ItemStack stack, int renderpass) {
        String identifier = this.readNBT(stack);
        if (icons.containsKey(identifier)) {
            // has own icon
            return icons.get(identifier);
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
        for (Entry<String, TinkersRebornFluid> entry : TinkersRebornRegistry.getAllFluidMap()
            .entrySet()) {
            String path = "tinkersreborn:bucket/bucket_" + entry.getKey();
            if (TextureHelper.itemTextureExists(path)) {
                icons.put(entry.getKey(), register.registerIcon(path));
            }
        }
        bucket = register.registerIcon("tinkersreborn:bucket/bucket");
        content = register.registerIcon("tinkersreborn:bucket/bucket_content");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void getSubItems(Item item, CreativeTabs tabs, List<ItemStack> list) {
        for (Entry<String, TinkersRebornFluid> entry : TinkersRebornRegistry.getAllFluidMap()
            .entrySet()) {
            list.add(this.getNewFluidBucketWithMaterial(entry.getKey()));
        }
    }

    @Override
    public String getItemStackDisplayName(ItemStack stack) {
        String bucketName;
        String fluidUnName = TinkersRebornRegistry.getFluidByIdentifier(this.readNBT(stack))
            .getUnlocalizedName();
        if (fluidUnName.startsWith("molten_")) {
            bucketName = TinkersRebornUtils.translate("tinkersreborn.moltenBucket");
            fluidUnName = TinkersRebornRegistry.getMaterialByIdentifier(fluidUnName.replace("molten_", ""))
                .localizedName();
        } else {
            bucketName = TinkersRebornUtils.translate("tinkersreborn.bucket");
            fluidUnName = TinkersRebornUtils.translate("fluid." + fluidUnName);
        }

        return bucketName.replace("%%material", fluidUnName);
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

                if (this.tryPlaceContainedLiquid(world, clickX, clickY, clickZ, stack)
                    && !player.capabilities.isCreativeMode) {
                    return new ItemStack(Items.bucket);
                }
            }
        }
        return stack;
    }

    public boolean tryPlaceContainedLiquid(World world, int clickX, int clickY, int clickZ, ItemStack stack) {

        if (!world.isAirBlock(clickX, clickY, clickZ) && world.getBlock(clickX, clickY, clickZ)
            .getMaterial()
            .isSolid()) {
            return false;
        } else {
            world.setBlock(
                clickX,
                clickY,
                clickZ,
                TinkersRebornRegistry.getFluidByIdentifier(this.readNBT(stack))
                    .getBlock(),
                0,
                3);
            return true;
        }
    }

    public ItemStack getNewFluidBucketWithMaterial(String identifier) {
        ItemStack stack = new ItemStack(this);
        stack.setTagCompound(this.getNewPartNBT(identifier));
        return stack;
    }

    public NBTTagCompound getNewPartNBT(String identifier) {
        NBTTagCompound nbt = new NBTTagCompound();
        nbt.setString(ToolTags.IDENTIFIER, identifier);
        return nbt;
    }

    public String readNBT(ItemStack stack) {
        return ToolTagsHelper.getTagSafe(stack)
            .getString(ToolTags.IDENTIFIER);
    }
}
