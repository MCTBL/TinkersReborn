package mctbl.tinkersreborn.library.entity;

import java.util.Random;

import mctbl.tinkersreborn.TinkersReborn;

public abstract class TinkersRebornMultiBlockInvenotryLogic extends TinkersRebornInventoryLogic {

    public boolean validStructure;
    public boolean tempValidStructure;

    public Random rand = TinkersReborn.random;

    public TinkersRebornMultiBlockInvenotryLogic(int invSize) {
        super(invSize);
    }

}
