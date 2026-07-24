package mctbl.tinkersreborn.tools.items.tools;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSourceIndirect;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

import mctbl.tinkersreborn.library.TinkersRebornRegistry;
import mctbl.tinkersreborn.library.entity.EntityProjectileBase;
import mctbl.tinkersreborn.library.materials.MaterialStatusType;
import mctbl.tinkersreborn.library.materials.TinkersRebornMaterial;
import mctbl.tinkersreborn.library.tools.AmmoCore;
import mctbl.tinkersreborn.library.tools.IModifier;
import mctbl.tinkersreborn.library.tools.ProjectileNBT;
import mctbl.tinkersreborn.library.tools.ToolNBT;
import mctbl.tinkersreborn.library.tools.modifiers.ModifierNBT;
import mctbl.tinkersreborn.library.utils.RecipeMatch;
import mctbl.tinkersreborn.tools.Category;
import mctbl.tinkersreborn.tools.TinkersRebornTools;
import mctbl.tinkersreborn.tools.TinkersRebornTraits;
import mctbl.tinkersreborn.tools.entity.EntityBolt;
import mctbl.tinkersreborn.tools.gui.ToolBuildGuiInfo;
import mctbl.tinkersreborn.tools.materials.FletchingMaterialStats;
import mctbl.tinkersreborn.tools.materials.HeadMaterialStats;
import mctbl.tinkersreborn.tools.materials.ShaftMaterialStats;
import mctbl.tinkersreborn.tools.traits.TraitEnderference;
import mctbl.tinkersreborn.util.TextureHelper;
import mctbl.tinkersreborn.util.TinkersRebornUtils;
import mctbl.tinkersreborn.util.ToolTags;
import mctbl.tinkersreborn.util.ToolTagsHelper;

public class Bolt extends AmmoCore {

    private final List<ToolPartRecord> boltPart = Arrays.asList(
        new ToolPartRecord(null, MaterialStatusType.SHAFT, "_bolt_shaft"),
        new ToolPartRecord(null, MaterialStatusType.HEAD, "_bolt_head"),
        new ToolPartRecord(null, MaterialStatusType.FLETCHING, "_bolt_fletching"));

    public Bolt() {
        super("Bolt", 2);

        this.componentsParts.add(new ToolPartRecord(TinkersRebornTools.boltCore, MaterialStatusType.HEAD, ""));
        this.componentsParts.add(new ToolPartRecord(TinkersRebornTools.fletching, MaterialStatusType.FLETCHING, ""));

        this.addCategory(Category.NO_MELEE, Category.PROJECTILE);

        // extra one
        this.allIcons.add(new HashMap<>());
    }

    @Override
    public void registerIcons(IIconRegister register) {
        String basePath = "tinkersreborn:tools/" + this.toolTypeName + "/";
        // exclude effect for now
        for (int i = 0; i < 3; i++) {
            String texturePostfix = boltPart.get(i)
                .texturePostfix();
            MaterialStatusType allowType = boltPart.get(i)
                .statusType();

            for (TinkersRebornMaterial material : TinkersRebornRegistry.getAllMaterialList()) {
                if (allowType == MaterialStatusType.HEAD && !material.isCastable()) {
                    continue;
                }
                if (material.hasStats(allowType)) {
                    String path = basePath + material.identifier + texturePostfix;
                    if (TextureHelper.itemTextureExists(path)) {
                        this.allIcons.get(i)
                            .put(material.identifier, register.registerIcon(path));
                    }
                    if (i == this.brokenPartIdx()) {
                        // broken
                        path += "_broken";
                        if (TextureHelper.itemTextureExists(path)) {
                            this.allIcons.get(this.partAmount)
                                .put(material.identifier, register.registerIcon(path));
                        }
                    }
                }
            }
            // standard
            this.allIcons.get(i)
                .put(null, register.registerIcon(basePath + texturePostfix));
            if (i == this.brokenPartIdx()) {
                this.allIcons.get(3)
                    .put(null, register.registerIcon(basePath + texturePostfix + "_broken"));

            }
        }

        for (IModifier m : TinkersRebornRegistry.getAllModifier()) {
            String tempPath = basePath + m.getIdentifier() + this.toolModifierEffect;
            if (TextureHelper.itemTextureExists(tempPath)) {
                this.effectIcons.put(m.getIdentifier(), register.registerIcon(tempPath));
            }
        }

        emptyIcon = register.registerIcon("tinkersreborn:blankface");
        blankSprite = register.registerIcon("tinkersreborn:blanksprite");
    }

    @Override
    public int getPartAmonuntForRender() {
        return 3;
    }

    @Override
    public void getSubItems(Item item, CreativeTabs tab, List<ItemStack> list) {
        // TODO Auto-generated method stub
    }

    @Override
    public ToolNBT buildToolTag(List<TinkersRebornMaterial> materials) {
        ProjectileNBT data = new ProjectileNBT();

        ShaftMaterialStats shaft = materials.get(0)
            .getStats(MaterialStatusType.SHAFT);
        HeadMaterialStats head = materials.get(1)
            .getStats(MaterialStatusType.HEAD);
        FletchingMaterialStats fletching = materials.get(2)
            .getStats(MaterialStatusType.FLETCHING);

        data.head(head);
        data.fletchings(fletching);
        data.shafts(this, shaft);

        data.durability *= 0.8f;

        return data;
    }

    @Override
    public int getColorFromItemStack(ItemStack stack, int renderPass) {
        NBTTagCompound tags = ToolTagsHelper.getToolBaseNBTSafe(stack);
        if (tags != null && !tags.hasNoTags()) {
            NBTTagList renderMaterials = ToolTagsHelper.getStringTagListSafe(tags, ToolTags.RENDERMATERIALS);
            if (renderMaterials.tagCount() != 0 && renderPass < this.getPartAmonuntForRender()) {
                return this.getCorrectColor(this.allIcons.get(renderPass), renderMaterials.getStringTagAt(renderPass));
            }
        }
        return super.getColorFromItemStack(stack, renderPass);
    }

    @Override
    public IIcon getIcon(ItemStack stack, int renderPass) {
        List<TinkersRebornMaterial> renderMaterials = ToolTagsHelper.getToolRenderMaterialsList(stack);
        if (!renderMaterials.isEmpty()) {
            if (renderPass < this.getPartAmonuntForRender()) {
                int iconsIdx = (renderPass == this.brokenPartIdx() && ToolTagsHelper.isBroken(stack))
                    ? this.getPartAmonuntForRender()
                    : renderPass;
                String materialId = renderMaterials.get(renderPass) == null ? null
                    : renderMaterials.get(renderPass).identifier;
                return getCorrectIcon(this.allIcons.get(iconsIdx), materialId);
            } else {
                // Effects
                List<NBTTagCompound> modifiersList = ToolTagsHelper.getModifiersList(stack);
                ModifierNBT tag = ModifierNBT.readTag(modifiersList.get(renderPass - this.getPartAmonuntForRender()));
                return this.effectIcons.get(tag.identifier);
            }
        }
        return emptyIcon;
    }

    @Override
    public boolean dealDamageRanged(ItemStack stack, Entity projectile, EntityLivingBase player, Entity target,
        float damage) {
        // friggin vanilla hardcode 2
        if (target instanceof EntityEnderman enderMan
            && ((TraitEnderference) TinkersRebornTraits.enderference).isActivate(enderMan)) {
            return target.attackEntityFrom(
                new DamageSourceProjectileForEndermen(DAMAGE_TYPE_PROJECTILE, projectile, player),
                damage);
        }

        DamageSource damageSource = new EntityDamageSourceIndirect(DAMAGE_TYPE_PROJECTILE, projectile, player)
            .setProjectile();
        return Rapier.dealHybridDamage(damageSource, target, damage);
    }

    @Override
    public ItemStack repair(ItemStack repairable, List<ItemStack> repairItems) {
        if (repairable.getItemDamage() == 0 && !ToolTagsHelper.isBroken(repairable)) {
            // undamaged and not broken - no need to repair
            return null;
        }

        // we assume the first required part exclusively determines repair material
        TinkersRebornMaterial material = ToolTagsHelper.getToolBaseMaterialsList(repairable)
            .get(1);
        if (material == null) {
            return null;
        }

        // ensure the items only contain valid items
        List<ItemStack> items = TinkersRebornUtils.copyItemStackList(repairItems);

        if (repairCustom(material, items) <= 0) {
            Optional<RecipeMatch.Match> match = material.matches(items);
            // not a single match -> nothing to repair with
            if (!match.isPresent()) {
                return null;
            }

            while ((match = material.matches(items)).isPresent()) {
                RecipeMatch.removeMatch(items, match.get());
            }
        }

        // check if all items were used
        for (int i = 0; i < repairItems.size(); i++) {
            // was non-null and did not get modified (stacksize changed or null now,
            // usually)
            if (!TinkersRebornUtils.isStackEmpty(repairItems.get(i))
                && ItemStack.areItemStacksEqual(repairItems.get(i), items.get(i))) {
                // found an item that was not touched
                return null;
            }
        }

        // now do it all over again with the real items, to actually repair \o/
        ItemStack item = repairable.copy();

        do {
            int amount = calculateRepairAmount(Arrays.asList(material), repairItems);

            // nothing to repair with, we're therefore done
            if (amount <= 0) {
                break;
            }

            ToolTagsHelper.repairTool(item, calculateRepair(item, amount));
            // save that we repaired it :I
            ToolTagsHelper.addRepairCount(item);
        } while (item.getItemDamage() > 0);

        return item;
    }

    @Override
    public EntityProjectileBase getProjectile(ItemStack stack, ItemStack launcher, World world, EntityPlayer player,
        float speed, float inaccuracy, float power, boolean usedAmmo) {
        inaccuracy -= (1f - 1f / ProjectileNBT.from(stack).accuracy) * speed / 2f;
        return new EntityBolt(
            world,
            player,
            speed,
            inaccuracy,
            power,
            getProjectileStack(stack, world, player, usedAmmo),
            launcher);
    }

    @Override
    public float damagePotential() {
        return 1f;
    }

    @Override
    public ToolBuildGuiInfo getToolBuildGuiInfo() {
        if (this.toolBuildGuiInfo == null) {
            this.toolBuildGuiInfo = new ToolBuildGuiInfo(this).addSlotPosition(32 + 8, 41 - 4) // boltcore
                .addSlotPosition(32 - 12, 41 + 12); // fletching
        }
        return this.toolBuildGuiInfo;
    }

}
