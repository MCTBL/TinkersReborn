package mctbl.tinkersreborn.tools.items.tools;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.ItemSpade;

import mctbl.tinkersreborn.TinkersReborn;
import mctbl.tinkersreborn.library.materials.MaterialStatusType;
import mctbl.tinkersreborn.library.tools.HarvestTool;
import mctbl.tinkersreborn.tools.TinkersRebornTools;
import mctbl.tinkersreborn.tools.gui.ToolBuildGuiInfo;

public class Shovel extends HarvestTool {

    public final Set<Material> effectiveMaterials = new HashSet<>();
    public final Set<Block> shovelEffective = new HashSet<>();

    public Shovel() {
        super("Shovel", 2);

        // set the toolclass, actual harvestlevel is done by the overridden callback
        this.setHarvestLevel("shovel", 0);

        this.componentsParts
            .add(new ToolPartRecord(TinkersRebornTools.shovelHead, MaterialStatusType.HEAD, "_shovel_head"));
        this.componentsParts
            .add(new ToolPartRecord(TinkersRebornTools.rod, MaterialStatusType.HANDLE, "_shovel_handle"));

        this.initEffectiveMaterial();
        this.getItemShovelEffect();
    }

    void initEffectiveMaterial() {
        effectiveMaterials.add(Material.grass);
        effectiveMaterials.add(Material.ground);
        effectiveMaterials.add(Material.sand);
        effectiveMaterials.add(Material.snow);
        effectiveMaterials.add(Material.craftedSnow);
        effectiveMaterials.add(Material.clay);
        effectiveMaterials.add(Material.cake);
    }

    void getItemShovelEffect() {
        try {
            Field effectSetField = ItemSpade.class.getDeclaredField("field_150916_c");
            effectSetField.setAccessible(true);
            @SuppressWarnings("unchecked")
            Set<Block> blockSet = (Set<Block>) effectSetField.get(null);
            shovelEffective.addAll(blockSet);
        } catch (Exception e) {
            e.printStackTrace();
            TinkersReborn.LOG.warn("Tinkers Shovel get error when try to get vanila shovel's effective block list");
        }
    }

    @Override
    public boolean isEffective(Block block) {
        return effectiveMaterials.contains(block.getMaterial()) || shovelEffective.contains(block);
    }

    @Override
    public float damagePotential() {
        return 0.9F;
    }

    @Override
    public ToolBuildGuiInfo getToolBuildGuiInfo() {
        if (this.toolBuildGuiInfo == null) {
            this.toolBuildGuiInfo = new ToolBuildGuiInfo(this).addSlotPosition(25 + 20, 53 - 20) // shovel head
                .addSlotPosition(25, 53); // rod
        }
        return this.toolBuildGuiInfo;
    }
}
