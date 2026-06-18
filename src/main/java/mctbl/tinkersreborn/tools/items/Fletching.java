package mctbl.tinkersreborn.tools.items;

import static mctbl.tinkersreborn.library.materials.TinkersRebornMaterial.VALUE_Ingot;

import mctbl.tinkersreborn.library.materials.MaterialStatusType;

public class Fletching extends TinkersRebornToolPart {

    public Fletching() {
        super("fletching", "Fletching", VALUE_Ingot * 2, MaterialStatusType.FLETCHING);
    }

}
