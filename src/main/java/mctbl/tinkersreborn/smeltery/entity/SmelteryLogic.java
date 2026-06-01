package mctbl.tinkersreborn.smeltery.entity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;
import net.minecraftforge.fluids.IFluidTank;

import mctbl.tinkersreborn.TinkersReborn;
import mctbl.tinkersreborn.library.blocks.IActiveLogic;
import mctbl.tinkersreborn.library.blocks.ITinkersRebornIFacingLogic;
import mctbl.tinkersreborn.library.crafting.Smeltery;
import mctbl.tinkersreborn.library.entity.IMasterLogic;
import mctbl.tinkersreborn.library.entity.IServantLogic;
import mctbl.tinkersreborn.library.entity.TinkersRebornInventoryLogic;
import mctbl.tinkersreborn.library.materials.TinkersRebornMaterial;
import mctbl.tinkersreborn.library.world.CoordTuple;
import mctbl.tinkersreborn.smeltery.TinkersRebornSmeltery;

public class SmelteryLogic extends TinkersRebornInventoryLogic
    implements IActiveLogic, ITinkersRebornIFacingLogic, IFluidTank, IMasterLogic {

    private static final int MAX_SMELTERY_SIZE = 7;
    public static final int MB_PER_BLOCK_CAPACITY = TinkersRebornMaterial.VALUE_Ingot * 10;

    public boolean validStructure;
    public boolean tempValidStructure;
    protected byte direction;

    public CoordTuple minPos = new CoordTuple(0, 0, 0);
    public CoordTuple maxPos = new CoordTuple(0, 0, 0);
    public int layers;
    public int maxBlockCapacity;

    protected int internalTemp;
    public int useTime;
    public int fuelGague;
    public int fuelAmount;
    public int fuelCapacity;
    protected boolean inUse;

    protected List<CoordTuple> lavaTanks;
    protected ArrayList<CoordTuple> drains;
    protected CoordTuple activeLavaTank;

    public int[] activeTemps; // values are multiplied by 10
    public int[] meltingTemps; // values are multiplied by 10
    private int tick;

    public final List<FluidStack> moltenMetal = Collections.synchronizedList(new ArrayList<>());
    public int maxLiquid;
    public int currentLiquid;

    Random rand = TinkersReborn.random;
    boolean needsUpdate;

    private boolean drainComparatorOutputDirty;

    public SmelteryLogic() {
        super(0);
        lavaTanks = Collections.synchronizedList(new ArrayList<>());
        drains = new ArrayList<>();
        activeTemps = new int[0];
        meltingTemps = new int[0];
    }

    @Override
    public void notifyChange(IServantLogic servant, int x, int y, int z) {
        checkValidPlacement();
    }

    public void checkValidPlacement() {
        switch (getRenderDirection()) {
            case 2: // +z
                alignInitialPlacement(xCoord, yCoord, zCoord + 1);
                break;
            case 3: // -z
                alignInitialPlacement(xCoord, yCoord, zCoord - 1);
                break;
            case 4: // +x
                alignInitialPlacement(xCoord + 1, yCoord, zCoord);
                break;
            case 5: // -x
                alignInitialPlacement(xCoord - 1, yCoord, zCoord);
                break;
        }
    }

    // aligns the position given (inside the smeltery) to be the center of the smeltery
    public void alignInitialPlacement(int x, int y, int z) {
        // x/y/z = the block behind the controller "inside the smeltery"

        // adjust the x-position of the block until the difference between the outer walls is at most 1
        // basically this means we center the block inside the smeltery on the x axis.
        int xd1 = 1, xd2 = 1; // x-difference
        for (int i = 1; i < MAX_SMELTERY_SIZE; i++) // don't check farther than needed
        {
            if (this.worldObj.isAirBlock(x - xd1, y, z)) xd1++;
            else if (this.worldObj.isAirBlock(x + xd2, y, z)) xd2++;

            // if one side hit a wall and the other didn't we might have to center our x-position again
            if (xd1 - xd2 > 1) {
                // move x and offsets to the -x
                xd1--;
                x--;
                xd2++;
            }
            // or the right
            if (xd2 - xd1 > 1) {
                xd2--;
                x++;
                xd1++;
            }
        }
        // same for z-axis
        int zd1 = 1, zd2 = 1;
        for (int i = 1; i < MAX_SMELTERY_SIZE; i++) // don't check farther than needed
        {
            if (this.worldObj.isAirBlock(x, y, z - zd1)) zd1++;
            else if (this.worldObj.isAirBlock(x, y, z + zd2)) zd2++;

            // if one side hit a wall and the other didn't we might have to center our x-position again
            if (zd1 - zd2 > 1) {
                // move x and offsets to the -x
                zd1--;
                z--;
                zd2++;
            }
            // or the right
            if (zd2 - zd1 > 1) {
                zd2--;
                z++;
                zd1++;
            }
        }

        // do the check
        int[] sides = new int[] { xd1, xd2, zd1, zd2 };
        checkValidStructure(x, y, z, sides);
    }

    /**
     *
     * @param x     x-center of the smeltery +-1
     * @param y     y-position of the controller block
     * @param z     z-center of the smeltery +-1
     * @param sides distance between the center point and the wall. [-x,+x,-z,+z]
     */
    public void checkValidStructure(int x, int y, int z, int[] sides) {
        int checkLayers = 0;

        tempValidStructure = false;
        // this piece of code here does the complete validity check.
        if (checkSameLevel(x, y, z, sides)) {
            checkLayers++;
            checkLayers += recurseStructureUp(x, y + 1, z, sides, 0);
            checkLayers += recurseStructureDown(x, y - 1, z, sides, 0);
        }

        if (tempValidStructure != validStructure || checkLayers != this.layers) {
            if (tempValidStructure) {
                // try to derive temperature from fueltank
                activeLavaTank = null;
                synchronized (lavaTanks) {
                    for (CoordTuple tank : lavaTanks) {
                        TileEntity tankContainer = worldObj.getTileEntity(tank.x, tank.y, tank.z);
                        if (!(tankContainer instanceof IFluidHandler)) continue;

                        FluidStack liquid = ((IFluidHandler) tankContainer).getTankInfo(ForgeDirection.DOWN)[0].fluid;
                        if (liquid == null) continue;
                        if (!Smeltery.isSmelteryFuel(liquid.getFluid())) continue;

                        internalTemp = Smeltery.getFuelPower(liquid.getFluid());
                        activeLavaTank = tank;
                        break;
                    }
                }

                // no tank with fuel. we reserve the first found one
                if (activeLavaTank == null) activeLavaTank = lavaTanks.get(0);

                // update other stuff
                adjustLayers(checkLayers, true);
                worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
                validStructure = true;
            } else {
                internalTemp = 20;
                if (validStructure) worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
                validStructure = false;
            }
        }
    }

    public boolean checkBricksOnLevel(int x, int y, int z, int[] sides) {
        int numBricks = 0;
        int xMin = x - sides[0];
        int xMax = x + sides[1];
        int zMin = z - sides[2];
        int zMax = z + sides[3];

        // Check inside
        for (int xPos = xMin + 1; xPos <= xMax - 1; xPos++) {
            for (int zPos = zMin + 1; zPos <= zMax - 1; zPos++) {
                if (!this.worldObj.isAirBlock(xPos, y, zPos)) {
                    return false;
                }
            }
        }

        // Check outer layer
        for (int xPos = xMin + 1; xPos <= xMax - 1; xPos++) {
            numBricks += checkBricks(xPos, y, zMin);
            numBricks += checkBricks(xPos, y, zMax);
        }

        for (int zPos = zMin + 1; zPos <= zMax - 1; zPos++) {
            numBricks += checkBricks(xMin, y, zPos);
            numBricks += checkBricks(xMax, y, zPos);
        }

        int neededBricks = (xMax - xMin) * 2 + (zMax - zMin) * 2 - 4; // -4 because corners are not needed

        return numBricks == neededBricks;
    }

    public boolean checkSameLevel(int x, int y, int z, int[] sides) {
        lavaTanks.clear();
        drains.clear();
        boolean check = checkBricksOnLevel(x, y, z, sides);
        return check && lavaTanks.size() > 0;
    }

    public int recurseStructureUp(int x, int y, int z, int[] sides, int count) {
        boolean check = checkBricksOnLevel(x, y, z, sides);

        if (!check) return count;

        count++;
        return recurseStructureUp(x, y + 1, z, sides, count);
    }

    public int recurseStructureDown(int x, int y, int z, int[] sides, int count) {
        boolean check = checkBricksOnLevel(x, y, z, sides);

        if (!check) {
            // regular check failed, maybe it's the bottom?
            Block block = worldObj.getBlock(x, y, z);
            if (!block.isAir(this.worldObj, x, y, z))
                if (validBlockID(block)) return validateBottom(x, y, z, sides, count);

            return count;
        }

        count++;
        return recurseStructureDown(x, y - 1, z, sides, count);
    }

    public int validateBottom(int x, int y, int z, int[] sides, int count) {
        int bottomBricks = 0;
        int xMin = x - sides[0] + 1;
        int xMax = x + sides[1] - 1;
        int zMin = z - sides[2] + 1;
        int zMax = z + sides[3] - 1;

        // Check inside
        if (y >= 0 && y < 256) {
            for (int xPos = xMin; xPos <= xMax; xPos++) {
                for (int zPos = zMin; zPos <= zMax; zPos++) {
                    Chunk chunk = this.worldObj.getChunkFromBlockCoords(xPos, zPos);
                    if (chunk == null) continue;

                    int xx = xPos & 15;
                    int zz = zPos & 15;
                    if (this.validBlockID(chunk.getBlock(xx, y, zz)) && chunk.getBlockMetadata(xx, y, zz) <= 1) {
                        TileEntity te = worldObj.getTileEntity(xPos, y, zPos);

                        if (te instanceof MultiServantLogic) {
                            MultiServantLogic servant = (MultiServantLogic) te;
                            if (servant.hasValidMaster()) {
                                if (servant.verifyMaster(this, worldObj, this.xCoord, this.yCoord, this.zCoord))
                                    bottomBricks++;
                            } else {
                                servant.overrideMaster(this.xCoord, this.yCoord, this.zCoord);
                                bottomBricks++;
                            }
                        }
                    }
                }
            }
        }

        int neededBricks = (xMax + 1 - xMin) * (zMax + 1 - zMin); // +1 because we want inclusive the upper bound

        if (bottomBricks == neededBricks) {
            tempValidStructure = true;
            minPos = new CoordTuple(xMin, y + 1, zMin);
            maxPos = new CoordTuple(xMax, y + 1, zMax);
        }
        return count;
    }

    /*
     * Returns whether the brick is a lava tank or not. Increments bricks, sets them as part of the structure, and adds
     * tanks to the list.
     */
    int checkBricks(int x, int y, int z) {
        int tempBricks = 0;
        Block blockID = worldObj.getBlock(x, y, z);
        if (validBlockID(blockID) || validTankID(blockID)) {
            TileEntity te = worldObj.getTileEntity(x, y, z);
            if (te == this) {
                tempBricks++;
            } else if (te instanceof MultiServantLogic) {
                MultiServantLogic servant = (MultiServantLogic) te;

                if (servant.hasValidMaster()) {
                    if (servant.verifyMaster(this, worldObj, this.xCoord, this.yCoord, this.zCoord)) tempBricks++;
                } else {
                    servant.overrideMaster(this.xCoord, this.yCoord, this.zCoord);
                    tempBricks++;
                }

                if (te instanceof LavaTankLogic) {
                    lavaTanks.add(new CoordTuple(x, y, z));
                } else if (te instanceof SmelteryDrainLogic) {
                    drains.add(new CoordTuple(x, y, z));
                }
            }
        }
        return tempBricks;
    }

    boolean validBlockID(Block blockID) {
        return blockID == TinkersRebornSmeltery.smeltery;
    }

    boolean validTankID(Block blockID) {
        return blockID == TinkersRebornSmeltery.lavaTank;
    }

    public int getBlocksPerLayer() {
        int xd = maxPos.x - minPos.x + 1;
        int zd = maxPos.z - minPos.z + 1;
        return xd * zd;
    }

    void adjustLayers(int lay, boolean forceAdjust) {
        if (lay != layers || forceAdjust) {
            needsUpdate = true;
            layers = lay;
            maxBlockCapacity = getBlocksPerLayer() * layers;
            maxLiquid = maxBlockCapacity * MB_PER_BLOCK_CAPACITY;

            int[] tempActive = activeTemps;
            activeTemps = new int[maxBlockCapacity];
            int activeLength = Math.min(tempActive.length, activeTemps.length);
            System.arraycopy(tempActive, 0, activeTemps, 0, activeLength);

            int[] tempMelting = meltingTemps;
            meltingTemps = new int[maxBlockCapacity];
            int meltingLength = Math.min(tempMelting.length, meltingTemps.length);
            System.arraycopy(tempMelting, 0, meltingTemps, 0, meltingLength);

            ItemStack[] tempInv = inventory;
            inventory = new ItemStack[maxBlockCapacity];
            int invLength = Math.min(tempInv.length, inventory.length);
            System.arraycopy(tempInv, 0, inventory, 0, invLength);

            if (activeTemps.length > 0 && activeTemps.length > tempActive.length) {
                for (int i = tempActive.length; i < activeTemps.length; i++) {
                    activeTemps[i] = 200;
                    meltingTemps[i] = 200;
                }
            }

            if (tempInv.length > inventory.length) {
                for (int i = inventory.length; i < tempInv.length; i++) {
                    ItemStack stack = tempInv[i];
                    if (stack != null) {
                        float jumpX = rand.nextFloat() * 0.8F + 0.1F;
                        float jumpY = rand.nextFloat() * 0.8F + 0.1F;
                        float jumpZ = rand.nextFloat() * 0.8F + 0.1F;

                        int offsetX = 0;
                        int offsetZ = 0;
                        switch (getRenderDirection()) {
                            case 2: // +z
                                offsetZ = -1;
                                break;
                            case 3: // -z
                                offsetZ = 1;
                                break;
                            case 4: // +x
                                offsetX = -1;
                                break;
                            case 5: // -x
                                offsetX = 1;
                                break;
                        }

                        while (stack.stackSize > 0) {
                            int itemSize = rand.nextInt(21) + 10;

                            if (itemSize > stack.stackSize) {
                                itemSize = stack.stackSize;
                            }

                            stack.stackSize -= itemSize;
                            EntityItem entityitem = new EntityItem(
                                worldObj,
                                (float) xCoord + jumpX + offsetX,
                                (float) yCoord + jumpY,
                                (float) zCoord + jumpZ + offsetZ,
                                new ItemStack(stack.getItem(), itemSize, stack.getItemDamage()));

                            if (stack.hasTagCompound()) {
                                entityitem.getEntityItem()
                                    .setTagCompound(
                                        (NBTTagCompound) stack.getTagCompound()
                                            .copy());
                            }

                            float offset = 0.05F;
                            entityitem.motionX = (float) rand.nextGaussian() * offset;
                            entityitem.motionY = (float) rand.nextGaussian() * offset + 0.2F;
                            entityitem.motionZ = (float) rand.nextGaussian() * offset;
                            worldObj.spawnEntityInWorld(entityitem);
                        }
                    }
                }
            }
        }

        // update current liquid. This is done in case some config or something changed the capacity or other things.
        updateCurrentLiquid();
    }

    private void updateCurrentLiquid() {
        currentLiquid = 0;
        for (FluidStack liquid : moltenMetal) currentLiquid += liquid.amount;
        drainComparatorOutputDirty = true;
    }

    @Override
    public FluidStack getFluid() {
        if (moltenMetal.size() == 0) return null;
        return moltenMetal.get(0);
    }

    @Override
    public int getFluidAmount() {
        return currentLiquid;
    }

    @Override
    public int getCapacity() {
        return maxLiquid;
    }

    @Override
    public FluidTankInfo getInfo() {
        return new FluidTankInfo(this);
    }

    public FluidTankInfo[] getMultiTankInfo() {
        FluidTankInfo[] info = new FluidTankInfo[moltenMetal.size() + 1];
        for (int i = 0; i < moltenMetal.size(); i++) {
            FluidStack fluid = moltenMetal.get(i);
            info[i] = new FluidTankInfo(fluid.copy(), fluid.amount);
        }
        info[moltenMetal.size()] = new FluidTankInfo(null, maxLiquid - currentLiquid);
        return info;
    }

    @Override
    public int fill(FluidStack resource, boolean doFill) {
        // don't fill if we're not complete
        if (!validStructure) return 0;

        if (resource != null && currentLiquid < maxLiquid) // resource.amount +
        // currentLiquid <
        // maxLiquid)
        {
            if (resource.amount + currentLiquid > maxLiquid) resource.amount = maxLiquid - currentLiquid;
            int amount = resource.amount;

            if (amount > 0 && doFill) {
                if (addMoltenMetal(resource, false)) {
                    ArrayList<FluidStack> alloys = Smeltery.mixMetals(moltenMetal);
                    for (FluidStack liquid : alloys) {
                        addMoltenMetal(liquid, true);
                    }
                }
                needsUpdate = true;
                worldObj.func_147479_m(xCoord, yCoord, zCoord);
            }
            return amount;
        } else return 0;
    }

    boolean addMoltenMetal(FluidStack liquid, boolean first) {
        needsUpdate = true;
        if (moltenMetal.size() == 0) {
            // does it fit in?
            if (liquid.amount > this.getCapacity()) return false;

            moltenMetal.add(liquid.copy());
            updateCurrentLiquid();
        } else {
            // update liquid amount..
            updateCurrentLiquid();

            if (liquid.amount + currentLiquid > maxLiquid) return false;

            currentLiquid += liquid.amount;
            drainComparatorOutputDirty = true;
            // TConstruct.logger.info("Current liquid: "+currentLiquid);
            boolean added = false;
            for (int i = 0; i < moltenMetal.size(); i++) {
                FluidStack l = moltenMetal.get(i);
                // if (l.itemID == liquid.itemID && l.itemMeta ==
                // liquid.itemMeta)
                if (l.isFluidEqual(liquid)) {
                    l.amount += liquid.amount;
                    added = true;
                }
                if (l.amount <= 0) {
                    moltenMetal.remove(l);
                    i--;
                }
            }
            if (!added) {
                if (first) moltenMetal.add(0, liquid.copy());
                else moltenMetal.add(liquid.copy());
            }
        }
        return true;
    }

    @Override
    public FluidStack drain(int maxDrain, boolean doDrain) {
        // don't drain if we're not complete
        if (!validStructure) return null;

        if (moltenMetal.size() == 0) return null;

        FluidStack liquid = moltenMetal.get(0);
        if (liquid != null) {
            if (liquid.amount - maxDrain <= 0) {
                FluidStack liq = liquid.copy();
                if (doDrain) {
                    // liquid = null;
                    moltenMetal.remove(liquid);
                    worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
                    needsUpdate = true;
                    updateCurrentLiquid();
                }
                return liq;
            } else {
                if (doDrain && maxDrain > 0) {
                    liquid.amount -= maxDrain;
                    worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
                    currentLiquid -= maxDrain;
                    drainComparatorOutputDirty = true;
                    needsUpdate = true;
                }
                return new FluidStack(liquid.getFluid(), maxDrain, liquid.tag);
            }
        } else {
            return new FluidStack(FluidRegistry.getFluid(0), 0);
        }
    }

    @Override
    public byte getRenderDirection() {
        return direction;
    }

    @Override
    public ForgeDirection getForgeDirection() {
        return ForgeDirection.getOrientation(direction);
    }

    @Override
    public void setRenderDirection(float yaw, float pitch, EntityLivingBase player) {
        int facing = MathHelper.floor_double((double) (yaw / 360) + 0.5D) & 3;
        switch (facing) {
            case 0:
                direction = 2;
                break;

            case 1:
                direction = 5;
                break;

            case 2:
                direction = 3;
                break;

            case 3:
                direction = 4;
                break;
        }
    }

    @Override
    public boolean getActive() {
        return validStructure;
    }

    @Override
    public void setActive(boolean flag) {
        worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
    }

    @Override
    public Container getGuiContainer(InventoryPlayer inventoryplayer, World world, int x, int y, int z) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getDefaultName() {
        return "tinkersreborn.crafters.Smeltery";
    }

    /* NBT */
    @Override
    public void readFromNBT(NBTTagCompound tags) {
        layers = tags.getInteger("Layers");
        int[] pos = tags.getIntArray("MinPos");
        if (pos.length > 2) minPos = new CoordTuple(pos[0], pos[1], pos[2]);
        else minPos = new CoordTuple(xCoord, yCoord, zCoord);

        pos = tags.getIntArray("MaxPos");
        if (pos.length > 2) maxPos = new CoordTuple(pos[0], pos[1], pos[2]);
        else maxPos = new CoordTuple(xCoord, yCoord, zCoord);

        maxBlockCapacity = getBlocksPerLayer() * layers;
        inventory = new ItemStack[maxBlockCapacity];
        super.readFromNBT(tags);

        internalTemp = tags.getInteger("InternalTemp");
        inUse = tags.getBoolean("InUse");

        direction = tags.getByte("Direction");
        useTime = tags.getInteger("UseTime");
        currentLiquid = tags.getInteger("CurrentLiquid");
        drainComparatorOutputDirty = true;
        maxLiquid = tags.getInteger("MaxLiquid");
        meltingTemps = tags.getIntArray("MeltingTemps");
        activeTemps = tags.getIntArray("ActiveTemps");

        NBTTagList liquidTag = tags.getTagList("Liquids", 10);
        moltenMetal.clear();

        for (int iter = 0; iter < liquidTag.tagCount(); iter++) {
            NBTTagCompound nbt = liquidTag.getCompoundTagAt(iter);
            FluidStack fluid = FluidStack.loadFluidStackFromNBT(nbt);
            if (fluid != null) moltenMetal.add(fluid);
        }

        // if(maxBlockCapacity != meltingTemps.length)
        // adjustLayers(layers, true);

        if (!tags.getBoolean("ValidStructure")) validStructure = false; // only negative update because we want to do a
                                                                        // clientside structure check too
        else if (!validStructure && worldObj != null) // if the worldobj is null it happens on loading of a world. check
                                                      // shouldn't be done
            // there
            checkValidPlacement();

        // adjustLayers(layers, true);
        // checkValidPlacement();
    }

    @Override
    public void writeToNBT(NBTTagCompound tags) {
        super.writeToNBT(tags);

        tags.setBoolean("ValidStructure", validStructure);
        tags.setInteger("InternalTemp", internalTemp);
        tags.setBoolean("InUse", inUse);

        int[] pos;
        if (minPos == null) pos = new int[] { xCoord, yCoord, zCoord };
        else pos = new int[] { minPos.x, minPos.y, minPos.z };
        tags.setIntArray("MinPos", pos);

        if (maxPos == null) pos = new int[] { xCoord, yCoord, zCoord };
        else pos = new int[] { maxPos.x, maxPos.y, maxPos.z };
        tags.setIntArray("MaxPos", pos);

        tags.setByte("Direction", direction);
        tags.setInteger("UseTime", useTime);
        tags.setInteger("CurrentLiquid", currentLiquid);
        tags.setInteger("MaxLiquid", maxLiquid);
        tags.setInteger("Layers", layers);
        tags.setIntArray("MeltingTemps", meltingTemps);
        tags.setIntArray("ActiveTemps", activeTemps);

        NBTTagList taglist = new NBTTagList();
        for (FluidStack liquid : moltenMetal) {
            NBTTagCompound nbt = new NBTTagCompound();
            liquid.writeToNBT(nbt);
            taglist.appendTag(nbt);
        }

        tags.setTag("Liquids", taglist);
    }

    /* Packets */
    @Override
    public Packet getDescriptionPacket() {
        NBTTagCompound tag = new NBTTagCompound();
        writeToNBT(tag);
        return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 1, tag);
    }

    @Override
    public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity packet) {
        readFromNBT(packet.func_148857_g());
        markDirty();
        worldObj.func_147479_m(xCoord, yCoord, zCoord);
        this.needsUpdate = true;
    }

    @Override
    public String getInventoryName() {
        return getDefaultName();
    }

    @Override
    public boolean hasCustomInventoryName() {
        return true;
    }

    public int getCapacityPerLayer() {
        return getBlocksPerLayer() * MB_PER_BLOCK_CAPACITY;
    }

    public int getTempForSlot(int slot) {
        return activeTemps[slot] / 10;
    }

    @Override
    public void setFrogeDirection(ForgeDirection direction) {}

    @Override
    public void setRenderDirection(int side) {}

}
