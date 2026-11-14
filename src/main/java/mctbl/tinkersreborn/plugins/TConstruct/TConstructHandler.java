package mctbl.tinkersreborn.plugins.TConstruct;

import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import mctbl.tinkersreborn.TinkersReborn;
import mctbl.tinkersreborn.library.ITinkersRebornModule;
import mctbl.tinkersreborn.plugins.TConstruct.recipe.TConstructNBTBlockConvertRecipe;
import mctbl.tinkersreborn.plugins.TConstruct.recipe.TConstructToolConvertRecipe;
import mctbl.tinkersreborn.plugins.TConstruct.recipe.TConstructToolPartConvertRecipe;
import mctbl.tinkersreborn.util.TinkersRebornUtils;

public class TConstructHandler implements ITinkersRebornModule {

    @Override
    public void preInit(FMLPreInitializationEvent e) {
        TConstructHandlerConfig.preInit();

        this.setTConstructConfig();
    }

    @Override
    public void init(FMLInitializationEvent e) {
        GameRegistry.addRecipe(new TConstructToolPartConvertRecipe());
        GameRegistry.addRecipe(new TConstructNBTBlockConvertRecipe());
        GameRegistry.addRecipe(new TConstructToolConvertRecipe());

    }

    @Override
    public void postInit(FMLPostInitializationEvent e) {
        final TConstructEventHandler target = new TConstructEventHandler();
        MinecraftForge.EVENT_BUS.register(target);
        FMLCommonHandler.instance()
            .bus()
            .register(target);

        addTConstructRecipeConverter();
    }

    private void setTConstructConfig() {
        Map<String, Object> overrides = new LinkedHashMap<>();
        overrides.put("naturalSlimeSpawn", 0);
        overrides.put("generateCopper", false);
        overrides.put("generateTin", false);
        overrides.put("generateAluminum", false);
        overrides.put("generateArdite", false);
        overrides.put("generateCobalt", false);
        overrides.put("generateIronBush", false);
        overrides.put("generateGoldBush", false);
        overrides.put("generateCopperBush", false);
        overrides.put("generateTinBush", false);
        overrides.put("generateAluminumBush", false);
        overrides.put("generateEssenceBush", false);
        overrides.put("generateIronSurface", false);
        overrides.put("generateGoldSurface", false);
        overrides.put("generateCopperSurface", false);
        overrides.put("generateTinSurface", false);
        overrides.put("generateAluminumSurface", false);
        overrides.put("generateCobaltSurface", false);
        overrides.put("addToVillages", false);
        overrides.put("allowVillagerTrading", false);
        overrides.put("slimeIslUseWhiteList", true);
        overrides.put("cfgDimWhiteList", new int[] {});
        overrides.put("cfgOreBushWhiteList", new int[] {});
        overrides.put("disableAllRecipes", true);

        setStaticFieldsSafely("tconstruct.util.config.PHConstruct", overrides);
    }

    private static void setStaticFieldsSafely(String className, Map<String, Object> values) {
        Class<?> clazz;
        try {
            clazz = Class.forName(className);
        } catch (ClassNotFoundException e) {
            TinkersReborn.LOG.warn("TConstruct config class not found, skip overrides");
            return;
        }

        int failed = 0;
        for (Map.Entry<String, Object> entry : values.entrySet()) {
            try {
                Field field = clazz.getField(entry.getKey());
                field.setAccessible(true);
                field.set(null, entry.getValue());
            } catch (NoSuchFieldException e) {
                TinkersReborn.LOG.warn("PHConstruct.{} not found, version mismatch?", entry.getKey());
                failed++;
            } catch (IllegalAccessException e) {
                TinkersReborn.LOG.warn("Cannot set PHConstruct.{}", entry.getKey(), e);
                failed++;
            }
        }
        if (failed > 0) {
            TinkersReborn.LOG.warn("TConstruct config override finished with {} failures", failed);
        }
    }

    public void addTConstructRecipeConverter() {
        for (Entry<String, String> entry : TConstructHandlerConfig.tinkerConstructItemReplaceMap.entrySet()) {
            try {
                String inputName = entry.getKey();
                String outputName = entry.getValue();

                final ItemStack inputStack = TinkersRebornUtils.getItemStackByIdentifier(inputName);
                final ItemStack outputStack = TinkersRebornUtils.getItemStackByIdentifier(outputName);

                if (inputStack != null && outputStack != null) {
                    if (inputStack.getItem() != null && inputStack.getItem()
                        .getContainerItem() != null) {
                        continue;
                    }
                    GameRegistry.addShapelessRecipe(outputStack, inputStack);
                }
            } catch (Exception e) {
                TinkersReborn.LOG.error("Found error when add recipe for {}", entry, e);
            }
        }
    }
}
