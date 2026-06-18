package mctbl.tinkersreborn.tools.items;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.ItemPickaxe;

import mctbl.tinkersreborn.TinkersReborn;
import mctbl.tinkersreborn.library.materials.MaterialStatusType;
import mctbl.tinkersreborn.library.tools.HarvestTool;
import mctbl.tinkersreborn.tools.TinkersRebornTools;
import mctbl.tinkersreborn.tools.gui.ToolBuildGuiInfo;

public class Pickaxe extends HarvestTool {

    public final Set<Material> effectiveMaterials = new HashSet<>();
    public final static Set<Block> pickaxeEffective = new HashSet<>();

    public Pickaxe() {
        super("Pickaxe", 3);

        // set the toolclass, actual harvestlevel is done by the overridden callback
        this.setHarvestLevel("pickaxe", 0);

        this.componentsParts
            .add(new ToolPartRecord(TinkersRebornTools.pickaxeHead, MaterialStatusType.HEAD, "_pickaxe_head"));
        this.componentsParts
            .add(new ToolPartRecord(TinkersRebornTools.rod, MaterialStatusType.HANDLE, "_pickaxe_handle"));
        this.componentsParts
            .add(new ToolPartRecord(TinkersRebornTools.binding, MaterialStatusType.EXTRA, "_pickaxe_accessory"));

        this.initEffectiveMaterial();
        this.getItemPickaxeEffect();
    }

    void initEffectiveMaterial() {
        effectiveMaterials.add(Material.rock);
        effectiveMaterials.add(Material.iron);
        effectiveMaterials.add(Material.ice);
        effectiveMaterials.add(Material.glass);
        effectiveMaterials.add(Material.piston);
        effectiveMaterials.add(Material.anvil);
        effectiveMaterials.add(Material.packedIce);
    }

    void getItemPickaxeEffect() {
        try {
            Field effectSetField = ItemPickaxe.class.getDeclaredField("field_150915_c");
            effectSetField.setAccessible(true);
            @SuppressWarnings("unchecked")
            Set<Block> blockSet = (Set<Block>) effectSetField.get(null);
            pickaxeEffective.addAll(blockSet);
        } catch (Exception e) {
            e.printStackTrace();
            TinkersReborn.LOG.warn("Tinkers Pickaxe get error when try to get vanila pickaxe's effective block list");
        }
    }

    @Override
    public boolean isEffective(Block block) {
        return effectiveMaterials.contains(block.getMaterial()) || pickaxeEffective.contains(block);
    }

    @Override
    public float damagePotential() {
        return 1.0F;
    }

    @Override
    public ToolBuildGuiInfo getToolBuildGuiInfo() {
        if (this.toolBuildGuiInfo == null) {
            this.toolBuildGuiInfo = new ToolBuildGuiInfo(this).addSlotPosition(33 - 18, 42 + 18) // rodg
                .addSlotPosition(33 + 20, 42 - 20) // pick head
                .addSlotPosition(33, 42); // binding
        }
        return this.toolBuildGuiInfo;
    }
}
