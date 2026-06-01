package mctbl.tinkersreborn.smeltery.items;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.init.Items;
import net.minecraft.item.ItemBucket;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

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
    public String getUnlocalizedName(ItemStack stack) {
        return super.getUnlocalizedName() + "."
            + TinkersRebornRegistry.getMaterialById(stack.getItemDamage()).identifier;
    }
}
