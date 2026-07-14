package mctbl.tinkersreborn.tools.items.tools;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import mctbl.tinkersreborn.TinkersReborn;
import mctbl.tinkersreborn.common.particle.Particles;
import mctbl.tinkersreborn.library.materials.MaterialStatusType;
import mctbl.tinkersreborn.library.materials.TinkersRebornMaterial;
import mctbl.tinkersreborn.library.tools.HarvestTool;
import mctbl.tinkersreborn.library.tools.ToolNBT;
import mctbl.tinkersreborn.tools.TinkersRebornTools;
import mctbl.tinkersreborn.tools.gui.ToolBuildGuiInfo;
import mctbl.tinkersreborn.util.ToolTagsHelper;

public class Hatchet extends HarvestTool {

    public final Set<Material> effectiveMaterials = new HashSet<>();
    public final Set<Block> hatchetEffective = new HashSet<>();

    public Hatchet() {
        super("Hatchet", 2);

        // set the toolclass, actual harvestlevel is done by the overridden callback
        this.setHarvestLevel("axe", 0);

        this.componentsParts.add(new ToolPartRecord(TinkersRebornTools.axeHead, MaterialStatusType.HEAD, "_axe_head"));
        this.componentsParts.add(new ToolPartRecord(TinkersRebornTools.rod, MaterialStatusType.HANDLE, "_axe_handle"));

        this.initEffectiveMaterial();
        this.getItemAxeEffect();
    }

    void initEffectiveMaterial() {
        effectiveMaterials.add(Material.wood);
        effectiveMaterials.add(Material.vine);
        effectiveMaterials.add(Material.plants);
        effectiveMaterials.add(Material.gourd);
        effectiveMaterials.add(Material.cactus);
    }

    void getItemAxeEffect() {
        try {
            Field effectSetField = ItemAxe.class.getDeclaredField("field_150917_c");
            effectSetField.setAccessible(true);
            @SuppressWarnings("unchecked")
            Set<Block> blockSet = (Set<Block>) effectSetField.get(null);
            hatchetEffective.addAll(blockSet);
        } catch (Exception e) {
            e.printStackTrace();
            TinkersReborn.LOG.warn("Tinkers Hatchet get error when try to get vanila axe's effective block list");
        }
    }

    @Override
    public boolean isEffective(Block block) {
        return effectiveMaterials.contains(block.getMaterial()) || hatchetEffective.contains(block);
    }

    @Override
    public float damagePotential() {
        return 1.1F;
    }

    @Override
    public float knockback() {
        return 1.3F;
    }

    @Override
    public float getDigSpeed(ItemStack itemstack, Block block, int metadata) {
        if (block.getMaterial() == Material.leaves) {
            return ToolTagsHelper.calcMiningSpeed(itemstack, block, metadata);
        }
        return super.getDigSpeed(itemstack, block, metadata);
    }

    @Override
    public void afterBlockBreak(ItemStack stack, World world, Block block, int x, int y, int z, EntityLivingBase player,
        int damage, boolean wasEffective) {
        // breaking leaves does not reduce durability
        if (block.getMaterial() == Material.leaves) {
            damage = 0;
        }
        super.afterBlockBreak(stack, world, block, x, y, z, player, damage, wasEffective);
    }

    @Override
    public boolean dealDamage(ItemStack stack, EntityLivingBase player, Entity entity, float damage) {
        boolean hit = super.dealDamage(stack, player, entity, damage);

        if (hit) {
            TinkersReborn.proxy.spawnAttackParticle(Particles.HATCHET_ATTACK, player, 0.8d);
        }

        return hit;
    }

    @Override
    public ToolNBT buildToolTag(List<TinkersRebornMaterial> materials) {
        ToolNBT data = super.buildToolTag(materials);
        data.attack += 0.5f;
        return data;
    }

    @Override
    public ToolBuildGuiInfo getToolBuildGuiInfo() {
        if (this.toolBuildGuiInfo == null) {
            this.toolBuildGuiInfo = new ToolBuildGuiInfo(this).addSlotPosition(25 + 20, 53 - 20) // hatchet head
                .addSlotPosition(25, 53); // rod
        }
        return this.toolBuildGuiInfo;
    }
}
