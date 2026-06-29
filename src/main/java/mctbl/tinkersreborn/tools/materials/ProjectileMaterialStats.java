package mctbl.tinkersreborn.tools.materials;

import java.util.ArrayList;
import java.util.List;

import mctbl.tinkersreborn.library.materials.AbstractMaterialStats;
import mctbl.tinkersreborn.library.materials.MaterialStatusType;
import mctbl.tinkersreborn.util.TinkersStr;

public class ProjectileMaterialStats extends AbstractMaterialStats {

    @Override
    public MaterialStatusType getIdentifier() {
        return MaterialStatusType.PROJECTILE;
    }

    @Override
    public String getLocalizedName() {
        return TinkersStr.projectileStatsName.toString();
    }

    @Override
    public List<String> getLocalizedInfo() {
        return new ArrayList<>();
    }

    @Override
    public List<String> getLocalizedDesc() {
        return new ArrayList<>();
    }

}
