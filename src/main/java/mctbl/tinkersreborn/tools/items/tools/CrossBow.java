package mctbl.tinkersreborn.tools.items.tools;

import java.util.Arrays;
import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mctbl.tinkersreborn.library.client.Crosshairs;
import mctbl.tinkersreborn.library.client.ICrosshair;
import mctbl.tinkersreborn.library.client.ICustomCrosshairUser;
import mctbl.tinkersreborn.library.event.Sounds;
import mctbl.tinkersreborn.library.materials.MaterialStatusType;
import mctbl.tinkersreborn.library.materials.TinkersRebornMaterial;
import mctbl.tinkersreborn.library.tools.BowCore;
import mctbl.tinkersreborn.library.tools.ProjectileLauncherNBT;
import mctbl.tinkersreborn.tools.TinkersRebornTools;
import mctbl.tinkersreborn.tools.gui.ToolBuildGuiInfo;
import mctbl.tinkersreborn.tools.materials.BowMaterialStats;
import mctbl.tinkersreborn.tools.materials.ExtraMaterialStats;
import mctbl.tinkersreborn.tools.materials.HandleMaterialStats;
import mctbl.tinkersreborn.tools.materials.HeadMaterialStats;
import mctbl.tinkersreborn.tools.materials.StringMaterialStats;
import mctbl.tinkersreborn.util.ToolTagsHelper;

public class CrossBow extends BowCore implements ICustomCrosshairUser {

    private List<Item> allowAmmo;

    private static final String TAG_Loaded = "Loaded";

    public CrossBow() {
        super("CrossBow", 4);

        this.componentsParts
            .add(new ToolPartRecord(TinkersRebornTools.crossbowBody, MaterialStatusType.BOW, "_crossbow_body"));
        this.componentsParts
            .add(new ToolPartRecord(TinkersRebornTools.bowString, MaterialStatusType.STRING, "_crossbow_string"));
        this.componentsParts
            .add(new ToolPartRecord(TinkersRebornTools.crossbowLimb, MaterialStatusType.HANDLE, "_crossbow_bow"));
        this.componentsParts
            .add(new ToolPartRecord(TinkersRebornTools.toughbind, MaterialStatusType.EXTRA, "_crossbow_binding"));
    }

    public CrossBow(String toolTypeName, int partAmount) {
        super(toolTypeName, partAmount);
    }

    @Override
    protected boolean animateLayer(int renderPass) {
        return renderPass < 2;
    }

    @Override
    protected int brokenPartIdx() {
        return 1;
    }

    @Override
    protected List<Item> getAmmoItems() {
        if (allowAmmo == null) {
            allowAmmo = Arrays.asList(TinkersRebornTools.bolt);
        }
        return allowAmmo;
    }

    @Override
    public float baseProjectileDamage() {
        return 3F;
    }

    @Override
    public float damagePotential() {
        return 0.8F;
    }

    @Override
    protected float baseProjectileSpeed() {
        return 7F;
    }

    @Override
    public float projectileDamageModifier() {
        return 1.3F;
    }

    @Override
    public int getDrawTime() {
        return 45;
    }

    public boolean isLoaded(ItemStack stack) {
        return ToolTagsHelper.getToolBaseNBTSafe(stack)
            .getBoolean(TAG_Loaded);
    }

    public void setLoaded(ItemStack stack, boolean isLoaded) {
        NBTTagCompound tag = ToolTagsHelper.getToolBaseNBTSafe(stack);
        tag.setBoolean(TAG_Loaded, isLoaded);
        ToolTagsHelper.setToolBaseNBTSafe(stack, tag);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public ICrosshair getCrosshair(ItemStack itemStack, EntityPlayer player) {
        return Crosshairs.T;
    }

    @Override
    public IIcon getIcon(ItemStack stack, int renderPass) {
        if (isLoaded(stack) && this.animateLayer(renderPass) && !ToolTagsHelper.isBroken(stack)) {
            List<TinkersRebornMaterial> renderMaterials = ToolTagsHelper.getToolRenderMaterialsList(stack);
            String materialId = renderMaterials.get(renderPass) == null ? null
                : renderMaterials.get(renderPass).identifier;
            return this.getCorrectAnimationIcon(this.allIcons.get(renderPass), materialId, 1.0F);
        }
        return super.getIcon(stack, renderPass);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public float getCrosshairState(ItemStack itemStack, EntityPlayer player) {
        if (isLoaded(itemStack)) {
            return 1f;
        } else if (player.getItemInUse() != itemStack) {
            return 0f;
        }
        return getDrawbackProgress(itemStack, player);
    }

    @Override
    public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        // has to be done in onUpdate because onTickUsing is too early and gets
        // overwritten. bleh.
        preventSlowDown(entityIn, 0.195f);

        super.onUpdate(stack, worldIn, entityIn, itemSlot, isSelected);
    }

    @Override
    public ItemStack onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer player) {
        if (isLoaded(itemStackIn) && !ToolTagsHelper.isBroken(itemStackIn)) {
            super.onPlayerStoppedUsing(itemStackIn, worldIn, player, 0);
            setLoaded(itemStackIn, false);
        } else {
            return super.onItemRightClick(itemStackIn, worldIn, player);
        }

        return itemStackIn;
    }

    @Override
    public void onPlayerStoppedUsing(ItemStack stack, World worldIn, EntityPlayer player, int timeLeft) {
        if (!ToolTagsHelper.isBroken(stack)) {
            int useTime = this.getMaxItemUseDuration(stack) - timeLeft;
            if (getDrawbackProgress(stack, useTime) >= 1f) {
                Sounds.playSoundForPlayer(player, Sounds.crossbow_reload, 1.5f, 0.9f + itemRand.nextFloat() * 0.1f);

                setLoaded(stack, true);
            }
        }
    }

    @Override
    public void playShootSound(float power, EntityPlayer entityPlayer) {
        Sounds.playSoundForAll(entityPlayer, "random.bow", 1.0F, 0.5f + itemRand.nextFloat() * 0.1f);
    }

    @Override
    public ProjectileLauncherNBT buildToolTag(List<TinkersRebornMaterial> materials) {
        ProjectileLauncherNBT data = new ProjectileLauncherNBT();

        HandleMaterialStats body = materials.get(0)
            .getStats(MaterialStatusType.HANDLE);
        ExtraMaterialStats bodyExtra = materials.get(0)
            .getStats(MaterialStatusType.EXTRA);
        StringMaterialStats bowstring = materials.get(1)
            .getStats(MaterialStatusType.STRING);
        HeadMaterialStats head = materials.get(2)
            .getStats(MaterialStatusType.HEAD);
        BowMaterialStats limb = materials.get(2)
            .getStats(MaterialStatusType.BOW);
        ExtraMaterialStats binding = materials.get(3)
            .getStats(MaterialStatusType.EXTRA);

        data.head(head);
        data.limb(limb);
        data.extra(binding, bodyExtra);
        data.handle(body);
        data.bowstring(bowstring);

        data.bonusDamage *= 1.5f;

        return data;
    }

    @Override
    public ToolBuildGuiInfo getToolBuildGuiInfo() {
        if (this.toolBuildGuiInfo == null) {
            this.toolBuildGuiInfo = new ToolBuildGuiInfo(this).addSlotPosition(32 + 6, 41 + 6) // body
                .addSlotPosition(32 - 14, 41 + 10) // bowstring
                .addSlotPosition(32 + 12, 41 - 22) // limb
                .addSlotPosition(32 - 18, 41 - 18); // grip
        }
        return this.toolBuildGuiInfo;
    }
}
