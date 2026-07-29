package mctbl.tinkersreborn.tools;

import java.io.File;

import net.minecraftforge.common.config.Configuration;

import mctbl.tinkersreborn.TinkersReborn;
import mctbl.tinkersreborn.library.TinkersRebornRegistry;
import mctbl.tinkersreborn.library.materials.IMaterialStats;
import mctbl.tinkersreborn.library.materials.MaterialStatusType;
import mctbl.tinkersreborn.library.materials.TinkersRebornMaterial;
import mctbl.tinkersreborn.util.ColorUtil;

public class TinkersRebornMaterialConfig {

    private TinkersRebornMaterialConfig() {}

    public static void initMaterialConfig() {

        saveDeafult(
            new Configuration(
                new File(TinkersReborn.cfgDirectory + "/Tinkersreborn/TinkersRebornMaterialDefault.cfg")));

        readOverride(
            new Configuration(
                new File(TinkersReborn.cfgDirectory + "/Tinkersreborn/TinkersRebornMaterialOverride.cfg")));
    }

    private static void saveDeafult(Configuration cfg) {
        cfg.addCustomCategoryComment("materials", "");
        for (TinkersRebornMaterial m : TinkersRebornRegistry.getAllMaterialList()) {
            String key = "materials." + m.identifier;
            cfg.get(key, "color", ColorUtil.toHexString(m.materialTextColor))
                .getString();
            cfg.get(key, "craftable", m.isCraftable())
                .getBoolean();
            cfg.get(key, "castable", m.isCastable())
                .getBoolean();
            m.getAllStats()
                .forEach(t -> t.writeToCfg(cfg, key));
        }
        cfg.save();
    }

    private static void readOverride(Configuration cfg) {
        cfg.load();
        cfg.setCategoryComment("materials", "rewrite any thing you want under here");
        cfg.save();
        for (TinkersRebornMaterial m : TinkersRebornRegistry.getAllMaterialList()) {
            String key = "materials." + m.identifier;
            if (cfg.hasCategory(key)) {
                m.materialTextColor = ColorUtil.fromHexString(
                    cfg.get(key, "color", ColorUtil.toHexString(m.materialTextColor))
                        .getString());
                m.setCraftable(
                    cfg.get(key, "craftable", m.isCraftable())
                        .getBoolean());
                m.setCastable(
                    cfg.get(key, "castable", m.isCastable())
                        .getBoolean());
                for (MaterialStatusType type : MaterialStatusType.values()) {
                    IMaterialStats temp = m.getStats(type);
                    String typeName = type.toString()
                        .toLowerCase();
                    if (cfg.hasCategory(key + "." + typeName)) {
                        if (temp != null) {
                            temp.readFromCfg(cfg, key);
                        } else {
                            m.addStats(
                                TinkersRebornMaterial.UNKNOWN.getStats(type)
                                    .getNewStatsFromCfg(cfg, key));
                        }
                    }
                }
            }
        }
    }
}
