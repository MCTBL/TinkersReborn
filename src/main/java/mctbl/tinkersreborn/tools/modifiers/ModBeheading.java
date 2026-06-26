package mctbl.tinkersreborn.tools.modifiers;

import java.util.Collection;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;

import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import mctbl.tinkersreborn.library.TinkersRebornRegistry;
import mctbl.tinkersreborn.library.tools.modifiers.ModifierAspect;
import mctbl.tinkersreborn.library.tools.modifiers.ModifierNBT;
import mctbl.tinkersreborn.library.tools.modifiers.ToolModifier;
import mctbl.tinkersreborn.util.TinkersRebornUtils;
import mctbl.tinkersreborn.util.ToolTagsHelper;

public class ModBeheading extends ToolModifier {

    private static String BEHEADING_ID = "beheading";
    private static String CLEAVER_MODIFIER_ID = BEHEADING_ID + "_cleaver";
    private static int BEHEADING_COLOR = 0x10574b;

    public static ModBeheading CLEAVER_BEHEADING_MOD = new ModBeheadingCleaver();

    public ModBeheading() {
        this("beheading");

        addAspects(ModifierAspect.freeModifier);

        MinecraftForge.EVENT_BUS.register(this);
    }

    ModBeheading(String traitBeheading) {
        super(traitBeheading, BEHEADING_COLOR);

        addAspects(new ModifierAspect.LevelAspect(this, 10), new ModifierAspect.DataAspect(this));
    }

    @Override
    public void applyEffect(NBTTagCompound rootCompound, NBTTagCompound modifierTag) {
        // remove the cleaver beheading if present and add it to the beheading modifier
        NBTTagCompound tag = ToolTagsHelper.getModifierTag(rootCompound, CLEAVER_MODIFIER_ID);
        if (!tag.hasNoTags()) {
            // update level if it hasn't been done before
            if (!modifierTag.getBoolean("absorbedCleaver")) {
                ModifierNBT data = ModifierNBT.readTag(modifierTag);
                data.level += ModifierNBT.readTag(tag).level;
                data.write(modifierTag);
                modifierTag.setBoolean("absorbedCleaver", true);
            }

            // remove other tag
            ToolTagsHelper.removeModifiersTag(rootCompound, CLEAVER_MODIFIER_ID);

            // ToolTagsHelper.setModifiersTagList(rootCompound, tagList);
        }
    }

    private int getBeheadingLevel(DamageSource source) {
        // TODO getTrueSource => getSourceOfDamage?
        // Projectile or item in hand
        // ItemStack item = CapabilityTinkerProjectile.getTinkerProjectile(source)
        // .map(ITinkerProjectile::getItemStack)
        // .orElse(((EntityLivingBase)source.getSourceOfDamage()).getHeldItem(EnumHand.MAIN_HAND));

        // source.isProjectile()
        ItemStack item = null;
        if (source.getSourceOfDamage() instanceof EntityPlayer player) {
            item = player.getHeldItem();
        }

        if (TinkersRebornUtils.isStackEmpty(item)) {
            return 0;
        }

        NBTTagCompound tag = ToolTagsHelper.getModifierTag(item, getIdentifier());
        int level = ModifierNBT.readTag(tag).level;

        if (level == 0) {
            tag = ToolTagsHelper.getModifierTag(item, CLEAVER_MODIFIER_ID);
            level = ModifierNBT.readTag(tag).level;
        }

        return level;
    }

    @SubscribeEvent
    public void onLivingDrops(LivingDropsEvent event) {
        // has beheading
        int level = getBeheadingLevel(event.source);
        if (shouldDropHead(level)) {
            Collection<ItemStack> heads = TinkersRebornRegistry.getHeadDrops(event.entityLiving);
            if (!heads.isEmpty()) {
                // Pick one random ItemStack from the collection
                ItemStack head = heads.toArray(new ItemStack[0])[random.nextInt(heads.size())];
                if (head.stackSize > 1) {
                    head.stackSize = (random.nextInt(head.stackSize) + 1);
                }
                if (!TinkersRebornUtils.isStackEmpty(head) && !alreadyContainsDrop(event, head)) {
                    EntityItem entityitem = new EntityItem(
                        event.entityLiving.worldObj,
                        event.entityLiving.posX,
                        event.entityLiving.posY,
                        event.entityLiving.posZ,
                        head.copy());
                    entityitem.delayBeforeCanPickup = 10;
                    event.drops.add(entityitem);
                    LOG.debug(
                        "Dropped random head for {}: {}",
                        event.entityLiving.getClass()
                            .getSimpleName(),
                        head);
                }
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void playerDrop(LivingDeathEvent event) {
        // if keepInventory is true, players do not fire the living drops event
        EntityLivingBase entity = event.entityLiving;
        if (entity.worldObj.getGameRules()
            .getGameRuleBooleanValue("keepInventory") && entity instanceof EntityPlayerMP) {
            int level = getBeheadingLevel(event.source);

            if (shouldDropHead(level)) {
                Collection<ItemStack> heads = TinkersRebornRegistry.getHeadDrops(entity);
                if (!heads.isEmpty()) {
                    // Pick one random ItemStack from the collection
                    ItemStack head = heads.toArray(new ItemStack[0])[random.nextInt(heads.size())];
                    if (head.stackSize > 1) {
                        head.stackSize = random.nextInt(head.stackSize) + 1;
                    }
                    if (!TinkersRebornUtils.isStackEmpty(head)) {
                        ((EntityPlayerMP) entity).dropPlayerItemWithRandomChoice(head.copy(), true);
                        LOG.debug("Dropped random head for player: {}", head);
                    }
                }
            }
        }
    }

    private boolean shouldDropHead(int level) {
        return level > 0 && level > random.nextInt(10);
    }

    private boolean alreadyContainsDrop(LivingDropsEvent event, ItemStack head) {
        // special case players: we want to add a new head drop even if they have their own head in their inventory
        if (event.entityLiving instanceof EntityPlayerMP) {
            return false;
        }
        return event.drops.stream()
            .map(EntityItem::getEntityItem)
            .anyMatch(drop -> ItemStack.areItemStacksEqual(drop, head));
    }

    private static class ModBeheadingCleaver extends ModBeheading {

        public ModBeheadingCleaver() {
            super(CLEAVER_MODIFIER_ID);
        }

        @Override
        public void applyEffect(NBTTagCompound rootCompound, NBTTagCompound modifierTag) {
            // do nothing
        }

        @Override
        public String getLocalizedDesc() {
            return TinkersRebornUtils.translate(LOC_Desc, BEHEADING_ID);
        }

        @Override
        public String getLocalizedName() {
            return TinkersRebornUtils.translate(LOC_Name, BEHEADING_ID);
        }
    }
}
