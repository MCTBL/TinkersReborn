package mctbl.tinkersreborn.plugins.TConstruct;

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
import mctbl.tinkersreborn.plugins.TConstruct.recipe.TConstructToolPartConvertRecipe;
import mctbl.tinkersreborn.util.TinkersRebornUtils;
import tconstruct.util.config.PHConstruct;

public class TConstructHandler implements ITinkersRebornModule {

    @Override
    public void preInit(FMLPreInitializationEvent e) {
        TConstructHandlerConfig.preInit();

        this.setTConstructConfig();
    }

    @Override
    public void init(FMLInitializationEvent e) {
        GameRegistry.addRecipe(new TConstructToolPartConvertRecipe());

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
        PHConstruct.naturalSlimeSpawn = 0;

        PHConstruct.generateCopper = false;
        PHConstruct.generateTin = false;
        PHConstruct.generateAluminum = false;
        PHConstruct.generateArdite = false;
        PHConstruct.generateCobalt = false;

        PHConstruct.generateIronBush = false;
        PHConstruct.generateGoldBush = false;
        PHConstruct.generateCopperBush = false;
        PHConstruct.generateTinBush = false;
        PHConstruct.generateAluminumBush = false;
        PHConstruct.generateEssenceBush = false;

        PHConstruct.generateIronSurface = false;
        PHConstruct.generateGoldSurface = false;
        PHConstruct.generateCopperSurface = false;
        PHConstruct.generateTinSurface = false;
        PHConstruct.generateAluminumSurface = false;
        PHConstruct.generateCobaltSurface = false;

        PHConstruct.addToVillages = false; // this needs change more early
        PHConstruct.allowVillagerTrading = false; // this needs change more early
        PHConstruct.slimeIslUseWhiteList = true;
        PHConstruct.cfgDimWhiteList = new int[] {};
        PHConstruct.cfgOreBushWhiteList = new int[] {};
        PHConstruct.disableAllRecipes = true;
    }

    public void addTConstructRecipeConverter() {
        for (Entry<String, String> entry : TConstructHandlerConfig.tinkerConstructItemReplaceMap.entrySet()) {
            try {
                String inputName = entry.getKey();
                final String[] inputList = inputName.split(":");
                int inputMeta = 0;
                if (inputList.length == 3) {
                    inputName = inputList[0] + ":" + inputList[1];
                    inputMeta = Integer.parseInt(inputList[2]);
                }

                final ItemStack inputStack = new ItemStack(
                    TinkersRebornUtils.getItemByIdentifier(inputName),
                    1,
                    inputMeta);

                String outputName = entry.getValue();
                final String[] outputList = outputName.split(":");
                int outputMeta = 0;
                if (outputList.length == 3) {
                    outputName = outputList[0] + ":" + outputList[1];
                    outputMeta = Integer.parseInt(outputList[2]);
                }

                final ItemStack outputStack = new ItemStack(
                    TinkersRebornUtils.getItemByIdentifier(outputName),
                    1,
                    outputMeta);

                GameRegistry.addShapelessRecipe(outputStack, inputStack);
            } catch (Exception e) {
                TinkersReborn.LOG.error("Found error when add recipe for {}", entry, e);
            }
        }
    }
}
