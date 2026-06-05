package mctbl.tinkersreborn.tools.items;

import net.minecraft.block.material.Material;

import mctbl.tinkersreborn.library.materials.MaterialStatusType;
import mctbl.tinkersreborn.library.tools.HarvestTool;
import mctbl.tinkersreborn.tools.TinkersRebornTools;

public class Pickaxe extends HarvestTool {

    public Pickaxe() {
        super("Pickaxe", 3);
        this.categoryTags.add("pickaxe");
        // this.partTypeArray[0] = MaterialStatusType.HEAD;
        // this.toolPartArray[0] = TinkersRebornTools.pickaxeHead;
        // this.iconPostfixArray[0] = "_pickaxe_head";
        // this.partTypeArray[1] = MaterialStatusType.HANDLE;
        // this.toolPartArray[1] = TinkersRebornTools.rod;
        // this.iconPostfixArray[1] = "_pickaxe_handle";
        // this.partTypeArray[2] = MaterialStatusType.EXTRA;
        // this.toolPartArray[2] = TinkersRebornTools.binding;
        // this.iconPostfixArray[2] = "_pickaxe_accessory";

        // this.iconPostfixArray[4] = "_pickaxe_head_broken";
        // this.iconPostfixArray[5] = "_pickaxe_effect";

        this.componentsParts
            .add(new ToolPartRecord(TinkersRebornTools.pickaxeHead, MaterialStatusType.HEAD, "_pickaxe_head"));
        this.componentsParts
            .add(new ToolPartRecord(TinkersRebornTools.rod, MaterialStatusType.HANDLE, "_pickaxe_handle"));
        this.componentsParts
            .add(new ToolPartRecord(TinkersRebornTools.binding, MaterialStatusType.EXTRA, "_pickaxe_accessory"));

        this.initEffectiveMaterial();
    }

    void initEffectiveMaterial() {
        effectiveMaterials.add(Material.rock);
        effectiveMaterials.add(Material.iron);
        effectiveMaterials.add(Material.ice);
        effectiveMaterials.add(Material.glass);
        effectiveMaterials.add(Material.piston);
        effectiveMaterials.add(Material.anvil);
        effectiveMaterials.add(Material.circuits);
    }

    @Override
    protected String getHarvestType() {
        return "pickaxe";
    }
}
