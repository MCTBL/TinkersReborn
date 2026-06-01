package mctbl.tinkersreborn.smeltery;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.BlockFluidClassic;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidContainerRegistry.FluidContainerData;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.oredict.OreDictionary;

import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import mctbl.tinkersreborn.library.ITinkersRebornModule;
import mctbl.tinkersreborn.smeltery.blocks.GlueBlock;
import mctbl.tinkersreborn.smeltery.blocks.LavaTankBlock;
import mctbl.tinkersreborn.smeltery.blocks.SmelteryBlock;
import mctbl.tinkersreborn.smeltery.blocks.TinkersRebornFluid;
import mctbl.tinkersreborn.smeltery.entity.LavaTankLogic;
import mctbl.tinkersreborn.smeltery.entity.MultiServantLogic;
import mctbl.tinkersreborn.smeltery.entity.SmelteryDrainLogic;
import mctbl.tinkersreborn.smeltery.entity.SmelteryLogic;
import mctbl.tinkersreborn.smeltery.itemblocks.LavaTankItemBlock;
import mctbl.tinkersreborn.smeltery.itemblocks.SmelteryItemBlock;
import mctbl.tinkersreborn.smeltery.items.FilledBucket;

public class TinkersRebornSmeltery implements ITinkersRebornModule {

    public static Item buckets;

    public static Block smeltery;
    public static Block lavaTank;
    public static Block searedBlock;
    public static Block castingChannel;
    public static Block smelteryNether;
    public static Block lavaTankNether;
    public static Block searedBlockNether;
    public static Block searedSlab;
    public static Block glueBlock;
    public static Block clearGlass;
    public static Block stainedGlassClear;
    public static Block glassPane;
    public static Block stainedGlassClearPane;
    public static Block glassMagicSlab;
    public static Block stainedGlassMagicSlab;
    public static Block stainedGlassClearMagicSlab;

    // Glue
    public static Fluid glueFluid;
    public static Block glueFluidBlock;
    // Pigiron
    public static Fluid pigIronFluid;
    public static Block pigIronFluidBlock;
    public static Fluid[] fluids = new Fluid[26];
    public static Block[] fluidBlocks = new Block[25];
    public static FluidStack[] liquids;
    public static Block speedSlab;
    // InfiBlocks
    public static Block speedBlock;
    public static Fluid bloodFluid;
    public static Block blood;
    // TODO
    // private static FluidType metalPatternFluidType;

    @SidedProxy(
        clientSide = "mctbl.tinkersreborn.smeltery.TinkersRebornSmelteryProxyClient",
        serverSide = "mctbl.tinkersreborn.smeltery.TinkersRebornSmelteryProxyCommon")
    public static TinkersRebornSmelteryProxyCommon proxy;

    @Override
    public void preInit(FMLPreInitializationEvent e) {
        buckets = new FilledBucket(Block.getBlockFromItem(buckets));
        GameRegistry.registerItem(buckets, buckets.getUnlocalizedName());

        glueBlock = new GlueBlock();
        GameRegistry.registerBlock(glueBlock, glueBlock.getUnlocalizedName());
        OreDictionary.registerOre("blockRubber", new ItemStack(glueBlock));

        smeltery = new SmelteryBlock();
        GameRegistry.registerBlock(smeltery, SmelteryItemBlock.class, smeltery.getUnlocalizedName());

        GameRegistry.registerTileEntity(SmelteryLogic.class, "tinkersreborn.Smeltery");
        GameRegistry.registerTileEntity(SmelteryDrainLogic.class, "tinkersreborn.SmelteryDrain");
        GameRegistry.registerTileEntity(MultiServantLogic.class, "tinkersreborn.Servants");

        lavaTank = new LavaTankBlock();
        GameRegistry.registerBlock(lavaTank, LavaTankItemBlock.class, lavaTank.getUnlocalizedName());
        GameRegistry.registerTileEntity(LavaTankLogic.class, "tinkersreborn.LavaTank");
    }

    @Override
    public void init(FMLInitializationEvent e) {
        proxy.initialize();
    }

    @Override
    public void postInit(FMLPostInitializationEvent e) {

    }

    public static Fluid registerFluid(String name, int materialId) {
        return registerFluid(name, materialId, "liquid_" + name);
    }

    public static Fluid registerFluid(String name, int materialId, String texture) {
        return registerFluid(
            name,
            materialId,
            name + ".molten",
            "fluid.molten." + name,
            texture,
            3000,
            6000,
            1300,
            Material.lava);
    }

    public static Fluid registerFluid(String name, int materialId, String fluidName, String blockName, String texture,
        int density, int viscosity, int temperature, Material material) {
        // create the new fluid
        Fluid fluid = new TinkersRebornFluid(fluidName, materialId).setDensity(density)
            .setViscosity(viscosity)
            .setTemperature(temperature);
        if (material == Material.lava) fluid.setLuminosity(12);
        // register it if it's not already existing
        boolean isFluidPreRegistered = !FluidRegistry.registerFluid(fluid);

        BlockFluidClassic block = new BlockFluidClassic(fluid, material);
        block.setBlockName(blockName);
        GameRegistry.registerBlock(block, blockName);

        // if the fluid was already registered we use that one instead
        if (isFluidPreRegistered) {
            FluidRegistry.getFluid(fluidName)
                .setBlock(block);
        } else {
            fluid.setBlock(block);
        }

        if (FluidContainerRegistry.fillFluidContainer(new FluidStack(fluid, 1000), new ItemStack(Items.bucket))
            == null) {
            FluidContainerRegistry.registerFluidContainer(
                new FluidContainerData(
                    new FluidStack(fluid, 1000),
                    new ItemStack(buckets, 1, materialId),
                    new ItemStack(Items.bucket)));
        }

        return fluid;
    }
}
