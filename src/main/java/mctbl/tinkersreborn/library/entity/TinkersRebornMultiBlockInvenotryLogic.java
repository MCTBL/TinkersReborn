package mctbl.tinkersreborn.library.entity;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import mctbl.tinkersreborn.library.blocks.IActiveLogic;
import mctbl.tinkersreborn.library.utils.BlockPos;
import mctbl.tinkersreborn.smeltery.entity.ItemIOHatchLogic;
import mctbl.tinkersreborn.util.TinkersRebornUtils;

public abstract class TinkersRebornMultiBlockInvenotryLogic extends TinkersRebornInventoryLogic
    implements IMasterLogic, IActiveLogic {

    // NBT Tags
    public static final String TAG_TANKS = "tanks";
    public static final String TAG_FUEL_QUALITY = "fuelQuality";
    public static final String TAG_CURRENT_FUEL = "currentFuel";
    public static final String TAG_CURRENT_TANK = "currentTank";
    public static final String TAG_ACTIVE = "active";
    public static final String TAG_MINPOS = "minPos";
    public static final String TAG_MAXPOS = "maxPos";
    protected static final int TIME_FACTOR = 8;
    public static final BlockPos DEFAULT_POS = BlockPos.of(0, 0, 0);
    /** smallest coordinate INSIDE the multiblock */
    public BlockPos minPos = DEFAULT_POS;
    /** biggest coordinate INSIDE the multiblock */
    public BlockPos maxPos = DEFAULT_POS;
    public boolean validStructure;
    protected int tickCounter = 0;
    protected int secondCounter = 0;
    protected boolean needsUpdate;
    /**
     * used by {@link #checkSteppingingValid()} and {@link #stepNextInnerPos()}
     */
    protected BlockPos nextCheckInner;
    /**
     * @see #getDefaultName
     */
    protected String name;

    protected TinkersRebornMultiBlockInvenotryLogic(String name) {
        super(0);
        this.name = "tinkersreborn.gui." + name;
    }

    @Override
    public void notifyChange(IServantLogic servant, int x, int y, int z) {
        this.checkWholeStructureValid();
    }

    @Override
    public boolean getActive() {
        return this.validStructure;
    }

    @Override
    public void setActive(boolean flag) {
        this.validStructure = flag;
        worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
    }

    @Override
    protected String getDefaultName() {
        return this.name;
    }

    @Override
    public abstract void updateEntity();

    @Override
    public void writeToNBT(NBTTagCompound tags) {
        super.writeToNBT(tags);

        tags.setBoolean(TAG_ACTIVE, validStructure);
        tags.setTag(TAG_MINPOS, writePos(minPos));
        tags.setTag(TAG_MAXPOS, writePos(maxPos));
    }

    @Override
    public void readFromNBT(NBTTagCompound tags) {
        super.readFromNBT(tags);

        validStructure = tags.getBoolean(TAG_ACTIVE);
        minPos = readPos(tags.getCompoundTag(TAG_MINPOS));
        maxPos = readPos(tags.getCompoundTag(TAG_MAXPOS));

        needsUpdate = true;
    }

    public static NBTTagCompound writePos(BlockPos pos) {
        NBTTagCompound tag = new NBTTagCompound();
        if (pos != null) {
            tag.setInteger("x", pos.getX());
            tag.setInteger("y", pos.getY());
            tag.setInteger("z", pos.getZ());
        }
        return tag;
    }

    public static BlockPos readPos(NBTTagCompound tag) {
        if (tag != null) {
            return new BlockPos(tag.getInteger("x"), tag.getInteger("y"), tag.getInteger("z"));
        }
        return null;
    }

    protected void resizeInventory(int newSize) {
        ItemStack[] oldInv = this.inventory;
        this.inventory = new ItemStack[newSize];
        int loopIdx = Math.min(oldInv.length, this.inventory.length);
        System.arraycopy(oldInv, 0, this.inventory, 0, loopIdx);
        for (int idx = loopIdx; idx < oldInv.length; idx++) {
            if (oldInv[idx] != null && oldInv[idx].stackSize != 0) TinkersRebornUtils.dropItemAtPos(
                this.worldObj,
                this.getBlockPos()
                    .offset(this.faceDirection),
                oldInv[idx]);
        }
    }

    /**
     * special for {@link ItemIOHatchLogic#canExtractItem(int, ItemStack, int)}
     * 
     * @param slot
     * @param stack
     * @return
     */
    public abstract boolean canExtractItem(int slot, ItemStack stack);
}
