package mctbl.tinkersreborn.library.tools.traits;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.world.BlockEvent;

import mctbl.tinkersreborn.library.tools.ITrait;
import mctbl.tinkersreborn.library.tools.modifiers.AbstractModifier;
import mctbl.tinkersreborn.library.tools.modifiers.ModifierAspect;
import mctbl.tinkersreborn.library.tools.modifiers.ModifierNBT;
import mctbl.tinkersreborn.library.utils.BlockPos;
import mctbl.tinkersreborn.util.ColorUtil;
import mctbl.tinkersreborn.util.TinkersRebornUtils;
import mctbl.tinkersreborn.util.ToolTags;
import mctbl.tinkersreborn.util.ToolTagsHelper;

// Trait and modifier in one! Useful because modifiers are saved as traits
public abstract class AbstractTrait extends AbstractModifier implements ITrait {

    public static final String LOC_Name = AbstractModifier.LOC_Name;
    public static final String LOC_Desc = AbstractModifier.LOC_Desc;
    // private final String identifier;
    protected final int color;

    public AbstractTrait(String identifier, EnumChatFormatting color) {
        this(identifier, ColorUtil.enumChatFormattingToColor(color));
    }

    public AbstractTrait(String identifier, int color) {
        super(TinkersRebornUtils.sanitizeLocalizationString(identifier));
        // this.identifier = Util.sanitizeLocalizationString(identifier);
        this.color = color;

        // we assume traits can only be applied once.
        // If you want stacking traits you'll have to do that stuff yourself :P
        this.addAspects(new ModifierAspect.SingleAspect(this));
    }

    @Override
    public String getIdentifier() {
        return identifier;
    }

    @Override
    public String getLocalizedName() {
        return TinkersRebornUtils.translate(String.format(LOC_Name, getIdentifier()));
    }

    @Override
    public String getLocalizedDesc() {
        return TinkersRebornUtils.translate(String.format(LOC_Desc, getIdentifier()));
    }

    @Override
    public boolean isHidden() {
        return false;
    }

    /* Updating */

    @Override
    public void onUpdate(ItemStack tool, World world, Entity entity, int itemSlot, boolean isSelected) {}

    @Override
    public void onArmorTick(ItemStack tool, World world, EntityPlayer player) {}

    /* Mining & Harvesting */

    @Override
    public void miningSpeed(ItemStack tool, PlayerEvent.BreakSpeed event) {}

    @Override
    public void beforeBlockBreak(ItemStack tool, BlockEvent.BreakEvent event) {}

    @Override
    public void afterBlockBreak(ItemStack tool, World world, Block block, BlockPos pos, EntityLivingBase player,
        boolean wasEffective) {}

    @Override
    public void blockHarvestDrops(ItemStack tool, BlockEvent.HarvestDropsEvent event) {}

    /* Attacking */

    @Override
    public boolean isCriticalHit(ItemStack tool, EntityLivingBase player, EntityLivingBase target) {
        return false;
    }

    @Override
    public float damage(ItemStack tool, EntityLivingBase player, EntityLivingBase target, float damage, float newDamage,
        boolean isCritical) {
        return newDamage;
    }

    @Override
    public void onHit(ItemStack tool, EntityLivingBase player, EntityLivingBase target, float damage,
        boolean isCritical) {}

    @Override
    public void afterHit(ItemStack tool, EntityLivingBase player, EntityLivingBase target, float damageDealt,
        boolean wasCritical, boolean wasHit) {}

    @Override
    public float knockBack(ItemStack tool, EntityLivingBase player, EntityLivingBase target, float damage,
        float knockback, float newKnockback, boolean isCritical) {
        return newKnockback;
    }

    @Override
    public void onBlock(ItemStack tool, EntityPlayer player, LivingHurtEvent event) {}

    /* Durability and repairing */

    @Override
    public int onToolDamage(ItemStack tool, int damage, int newDamage, EntityLivingBase entity) {
        return newDamage;
    }

    @Override
    public int onToolHeal(ItemStack tool, int amount, int newAmount, EntityLivingBase entity) {
        return newAmount;
    }

    @Override
    public void onRepair(ItemStack tool, int amount) {}

    /* Modifier things */
    @Override
    public boolean canApplyCustom(ItemStack stack) {
        // can only apply if the trait isn't present already
        // not present yet
        return !ToolTagsHelper.hasModifier(stack, this.getIdentifier());
    }

    @Override
    public void updateNBT(NBTTagCompound modifierTag) {
        updateNBTforTrait(modifierTag, color);
    }

    public void updateNBTforTrait(NBTTagCompound modifierTag, int newColor) {
        ModifierNBT data = ModifierNBT.readTag(modifierTag);
        data.identifier = this.getIdentifier();
        data.color = newColor;
        // we ensure at least lvl1 for compatibility with the level-aspect
        if (data.level == 0) {
            data.level = 1;
        }
        data.write(modifierTag);
    }

    @Override
    public void applyEffect(NBTTagCompound rootCompound, NBTTagCompound modifierTag) {
        // add the trait to the traitlist so it gets processed
        NBTTagList traits = ToolTagsHelper.getModifiersTagList(rootCompound);
        // if it's not already present
        for (int i = 0; i < traits.tagCount(); i++) {
            NBTTagCompound compoundTagAt = traits.getCompoundTagAt(i);
            if (identifier.equals(compoundTagAt.getString(ToolTags.IDENTIFIER))) {
                return;
            }
        }
    }

    protected boolean isToolWithTrait(ItemStack itemStack) {
        return ToolTagsHelper.hasModifier(itemStack, this.getIdentifier());
    }
}
