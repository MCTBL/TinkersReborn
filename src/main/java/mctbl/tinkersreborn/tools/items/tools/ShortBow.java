package mctbl.tinkersreborn.tools.items.tools;

import java.util.Arrays;
import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mctbl.tinkersreborn.library.client.Crosshairs;
import mctbl.tinkersreborn.library.client.ICrosshair;
import mctbl.tinkersreborn.library.client.ICustomCrosshairUser;
import mctbl.tinkersreborn.library.materials.MaterialStatusType;
import mctbl.tinkersreborn.library.materials.TinkersRebornMaterial;
import mctbl.tinkersreborn.library.tools.BowCore;
import mctbl.tinkersreborn.library.tools.ProjectileLauncherNBT;
import mctbl.tinkersreborn.tools.TinkersRebornTools;
import mctbl.tinkersreborn.tools.gui.ToolBuildGuiInfo;
import mctbl.tinkersreborn.tools.materials.BowMaterialStats;
import mctbl.tinkersreborn.tools.materials.HeadMaterialStats;
import mctbl.tinkersreborn.tools.materials.StringMaterialStats;
import mctbl.tinkersreborn.util.ToolTagsHelper;

public class ShortBow extends BowCore implements ICustomCrosshairUser {

    public ShortBow() {
        super("ShortBow", 3);

        this.componentsParts
            .add(new ToolPartRecord(TinkersRebornTools.bowString, MaterialStatusType.STRING, "_bowstring"));
        this.componentsParts.add(new ToolPartRecord(TinkersRebornTools.bowLimb, MaterialStatusType.BOW, "_bow_top"));
        this.componentsParts.add(new ToolPartRecord(TinkersRebornTools.bowLimb, MaterialStatusType.BOW, "_bow_bottom"));
    }

    @Override
    protected boolean animateLayer(int renderPass) {
        return renderPass < 3;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public ICrosshair getCrosshair(ItemStack itemStack, EntityPlayer player) {
        return Crosshairs.SQUARE;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public float getCrosshairState(ItemStack itemStack, EntityPlayer player) {
        return getDrawbackProgress(itemStack, player);
    }

    @Override
    public ProjectileLauncherNBT buildToolTag(List<TinkersRebornMaterial> materials) {
        ProjectileLauncherNBT data = new ProjectileLauncherNBT();
        HeadMaterialStats head1 = materials.get(1)
            .getStats(MaterialStatusType.HEAD);
        HeadMaterialStats head2 = materials.get(2)
            .getStats(MaterialStatusType.HEAD);
        BowMaterialStats limb1 = materials.get(1)
            .getStats(MaterialStatusType.BOW);
        BowMaterialStats limb2 = materials.get(2)
            .getStats(MaterialStatusType.BOW);
        StringMaterialStats bowstring = materials.get(0)
            .getStats(MaterialStatusType.STRING);

        data.head(head1, head2);
        data.limb(limb1, limb2);
        data.bowstring(bowstring);

        return data;
    }

    @Override
    protected List<Item> getAmmoItems() {
        return Arrays.asList(TinkersRebornTools.arrow, Items.arrow);
    }

    @Override
    public float baseProjectileDamage() {
        return 0f;
    }

    @Override
    public float projectileDamageModifier() {
        return 0.8F;
    }

    @Override
    public float damagePotential() {
        return 0.7F;
    }

    @Override
    protected float baseInaccuracy() {
        return 1.0F;
    }

    @Override
    public int getDrawTime() {
        return 12;
    }

    @Override
    public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        // has to be done in onUpdate because onTickUsing is too early and gets overwritten. bleh.
        // shortbows are more mobile than other bows
        preventSlowDown(entityIn, 0.5f);

        super.onUpdate(stack, worldIn, entityIn, itemSlot, isSelected);
    }

    @Override
    public ToolBuildGuiInfo getToolBuildGuiInfo() {
        if (this.toolBuildGuiInfo == null) {
            this.toolBuildGuiInfo = new ToolBuildGuiInfo(this).addSlotPosition(32 + 6, 41 + 6) // bowstring
                .addSlotPosition(32 + 4, 41 - 18) // top limb
                .addSlotPosition(32 - 18, 41 + 4); // left limb
        }
        return this.toolBuildGuiInfo;
    }

    @Override
    public String getItemStackDisplayName(ItemStack stack) {
        if (stack.hasDisplayName()) {
            return stack.getTagCompound()
                .getCompoundTag("display")
                .getString("Name");
        }

        String toolBaseName = getLocalizedToolName();
        String firstLimbMaterialName = ToolTagsHelper.getToolBaseMaterialsList(stack)
            .get(1)
            .localizedPrefix();
        String secondLimbMaterialName = ToolTagsHelper.getToolBaseMaterialsList(stack)
            .get(2)
            .localizedPrefix();
        String materialPrefix = firstLimbMaterialName;
        if (!firstLimbMaterialName.equals(secondLimbMaterialName)) {
            materialPrefix += (" - " + secondLimbMaterialName);
        }

        return String.format(TOOLNAMEFORMATTER, materialPrefix, toolBaseName);
    }
}
