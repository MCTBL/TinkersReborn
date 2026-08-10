package mctbl.tinkersreborn.world.village;

import java.util.List;
import java.util.Random;

import net.minecraft.world.gen.structure.StructureVillagePieces.PieceWeight;
import net.minecraft.world.gen.structure.StructureVillagePieces.Start;

import cpw.mods.fml.common.registry.VillagerRegistry.IVillageCreationHandler;

public class VillageToolWorkshopHandler implements IVillageCreationHandler {

    @Override
    public PieceWeight getVillagePieceWeight(Random random, int villageSize) {
        return new PieceWeight(ComponentToolWorkshop.class, 30, villageSize + random.nextInt(4));
    }

    @Override
    public Class<?> getComponentClass() {
        return ComponentToolWorkshop.class;
    }

    @Override
    public Object buildComponent(PieceWeight villagePiece, Start startPiece, @SuppressWarnings("rawtypes") List pieces,
        Random random, int x, int y, int z, int direction, int componentType) {
        return ComponentToolWorkshop.buildComponent(startPiece, pieces, random, x, y, z, direction, componentType);
    }
}
