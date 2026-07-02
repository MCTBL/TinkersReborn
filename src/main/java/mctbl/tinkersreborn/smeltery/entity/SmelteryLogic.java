package mctbl.tinkersreborn.smeltery.entity;

import java.util.ArrayList;
import java.util.List;

import mctbl.tinkersreborn.library.entity.TinkersRebornMultiBlockInvenotryLogic;
import mctbl.tinkersreborn.library.materials.TinkersRebornMaterial;
import mctbl.tinkersreborn.library.utils.BlockPos;
import mctbl.tinkersreborn.smeltery.TinkersRebornSmeltery;
import mctbl.tinkersreborn.smeltery.gui.GuiSmeltery;
import mctbl.tinkersreborn.smeltery.inventory.ContainerSmeltery;
import net.minecraft.block.Block;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidTank;

public class SmelteryLogic extends TinkersRebornMultiBlockInvenotryLogic implements IFluidTank {

    private static final int MAX_SMELTERY_SIZE = 7;
    public static final int MB_PER_BLOCK_CAPACITY = TinkersRebornMaterial.VALUE_Ingot * 10;

    public BlockPos minPos = new BlockPos(0, 0, 0);
    public BlockPos maxPos = new BlockPos(0, 0, 0);

    protected final List<BlockPos> drains;

    public final List<FluidStack> moltenMetal = new ArrayList<>();
    public int maxLiquid;
    public int currentLiquid;

    public SmelteryLogic() {
	super("Smeltery");
	this.drains = new ArrayList<>();
    }

    @Override
    public void updateEntity() {
	super.updateEntity();

    }

    @Override
    public int getInventoryStackLimit() {
	return 1;
    }

    @Override
    public FluidStack getFluid() {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public int getFluidAmount() {
	// TODO Auto-generated method stub
	return 0;
    }

    @Override
    public int getCapacity() {
	// TODO Auto-generated method stub
	return 0;
    }

    @Override
    public FluidTankInfo getInfo() {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public int fill(FluidStack resource, boolean doFill) {
	// TODO Auto-generated method stub
	return 0;
    }

    @Override
    public FluidStack drain(int maxDrain, boolean doDrain) {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public void checkWholeStructureValid() {
	ForgeDirection opposite = this.getForgeDirection().getOpposite();
	BlockPos masterPos = this.getBlockPos();
	BlockPos checkAir = masterPos.offset(opposite);

    }

    protected boolean isValidWall(Block b) {
	return b == TinkersRebornSmeltery.furnaceController || b == TinkersRebornSmeltery.smelteryDrain
		|| b == TinkersRebornSmeltery.smelteryBlock || b == TinkersRebornSmeltery.lavaTank;
    }
    
    protected boolean isValidTank(Block b) {
	return b == TinkersRebornSmeltery.lavaTank;
    }

    @Override
    protected void consumeFuel() {
	// TODO Auto-generated method stub

    }

    @Override
    protected void updateHeatRequired(int index) {
	// TODO Auto-generated method stub

    }

    @Override
    public Container getGuiContainer(InventoryPlayer inventoryplayer, World world, int x, int y, int z) {
	return new ContainerSmeltery(inventoryplayer, this);
    }

    @Override
    public GuiContainer getGui(InventoryPlayer inventoryplayer, World world, int x, int y, int z) {
	return new GuiSmeltery((ContainerSmeltery) getGuiContainer(inventoryplayer, world, x, y, z), this);
    }

}
