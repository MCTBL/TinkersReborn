package mctbl.tinkersreborn.world.village;

import java.util.List;
import java.util.Random;

import net.minecraft.world.gen.structure.StructureVillagePieces.PieceWeight;
import net.minecraft.world.gen.structure.StructureVillagePieces.Start;

import cpw.mods.fml.common.registry.VillagerRegistry.IVillageCreationHandler;

public class VillageSmelteryHandler implements IVillageCreationHandler {

    @Override
    public PieceWeight getVillagePieceWeight(Random random, int villageSize) {
        return new PieceWeight(ComponentSmeltery.class, 9, 1);
    }

    @Override
    public Class<?> getComponentClass() {
        return ComponentSmeltery.class;
    }

    @Override
    public Object buildComponent(PieceWeight villagePiece, Start startPiece, List pieces, Random random, int x, int y,
        int z, int direction, int componentType) {
        return ComponentSmeltery.buildComponent(startPiece, pieces, random, x, y, z, direction, componentType);
    }
}
