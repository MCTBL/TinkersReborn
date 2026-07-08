package mctbl.tinkersreborn.smeltery.entity;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S2APacketParticles;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidEvent;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;
import net.minecraftforge.fluids.IFluidTank;

import mctbl.tinkersreborn.common.network.TinkerNetwork;
import mctbl.tinkersreborn.library.entity.TinkersRebornInventoryLogic;
import mctbl.tinkersreborn.library.event.Sounds;
import mctbl.tinkersreborn.library.event.TinkerCastingEvent;
import mctbl.tinkersreborn.library.items.IPattern;
import mctbl.tinkersreborn.library.materials.TinkersRebornMaterial;
import mctbl.tinkersreborn.library.smeltery.ICastingRecipe;
import mctbl.tinkersreborn.smeltery.network.FluidUpdatePacket;
import mctbl.tinkersreborn.util.ItemHelper;

public abstract class CastingBlockLogic extends TinkersRebornInventoryLogic
    implements IFluidTank, IFluidHandler, ISidedInventory {

    protected ICastingRecipe recipe; // current recipe
    public FluidStack liquid;
    protected int renderOffset = 0;
    protected int capacity = 0;
    protected int timer;

    public CastingBlockLogic() {
        // input slot and output slot, 1 item in it max
        super(2, 1);
    }

    public int updateCapacity() {
        return this.updateCapacity(recipe != null ? recipe.getFluidAmount() : 0);
    }

    public int updateCapacity(int capacity) {
        ItemStack inv = inventory[0];
        int ret = TinkersRebornMaterial.VALUE_Ingot;

        if (capacity > 0) ret = capacity;
        else {
            if (inv != null && inv.getItem() instanceof IPattern pat) {
                int cost = pat.getPatternCost(inv);
                if (cost > 0) ret *= cost * 0.5;
            }
        }

        return ret;
    }

    /* FluidHandler stuff. Mostly delegated to Tank stuff */
    @Override
    public boolean canFill(ForgeDirection from, Fluid fluid) {
        if (fluid == null) return false;
        return fill(from, new FluidStack(fluid, 1), false) > 0;
    }

    @Override
    public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {
        return fill(resource, doFill);
    }

    @Override
    public int fill(FluidStack resource, boolean doFill) {
        // this is where all the action happens
        if (resource == null || this.isStackInSlot(1)) {
            return 0;
        }
        Fluid fluid = resource.getFluid();

        // if empty, find a new recipe
        if (this.getFluidAmount() == 0) {
            int newCapacity = this.initNewCasting(fluid, doFill);
            if (newCapacity > 0) {
                // no extra checks needed for the tank since it's empty and we have to set the
                // capacity anyway
                if (doFill) {
                    this.capacity = newCapacity;
                    this.liquid = new FluidStack(resource.getFluid(), 0);
                }

                return this.fillInternal(resource, doFill);
            }
        }

        // non-empty tank. just try to fill
        return this.fillInternal(resource, doFill);
    }

    /**
     * copy from 1.12.2 FluidTank#fillInternal
     * 
     * @param resource
     * @param doFill
     * @return
     */
    private int fillInternal(FluidStack resource, boolean doFill) {
        if (!this.liquid.isFluidEqual(resource)) {
            return 0;
        }

        if (resource == null || resource.amount <= 0) {
            return 0;
        }

        if (!doFill) {
            if (this.liquid == null) {
                return Math.min(capacity, resource.amount);
            }

            if (!this.liquid.isFluidEqual(resource)) {
                return 0;
            }

            return Math.min(capacity - this.liquid.amount, resource.amount);
        }

        if (this.liquid == null) {
            this.liquid = new FluidStack(resource, Math.min(capacity, resource.amount));

            if (this != null) {
                FluidEvent.fireEvent(
                    new FluidEvent.FluidFillingEvent(
                        this.liquid,
                        this.worldObj,
                        this.xCoord,
                        this.yCoord,
                        this.zCoord,
                        this,
                        this.liquid.amount));
            }
            return this.liquid.amount;
        }

        int filled = capacity - this.liquid.amount;

        if (resource.amount < filled) {
            this.liquid.amount += resource.amount;
            filled = resource.amount;
        } else {
            this.liquid.amount = capacity;
        }

        if (this != null) {
            FluidEvent.fireEvent(
                new FluidEvent.FluidFillingEvent(
                    this.liquid,
                    this.worldObj,
                    this.xCoord,
                    this.yCoord,
                    this.zCoord,
                    this,
                    filled));
        }
        return filled;
    }

    @Override
    public boolean canDrain(ForgeDirection from, Fluid fluid) {
        if (fluid == null) return false;

        FluidStack drained = drain(from, new FluidStack(fluid, 1), false);
        return drained != null && drained.amount > 0;
    }

    @Override
    public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain) {
        // only same liquid
        if (liquid != null && liquid.getFluid() != resource.getFluid()) return null;

        return drain(resource.amount, doDrain);
    }

    @Override
    public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {
        return drain(maxDrain, doDrain);
    }

    @Override
    public FluidStack drain(int maxDrain, boolean doDrain) {
        FluidStack amount = this.drainInternal(maxDrain, doDrain);
        if (amount != null && doDrain) {
            if (liquid.amount == 0) {
                this.reset();
            }
        }

        return amount;
    }

    /**
     * copy from 1.12.2 FluidTank#drainInternal
     * 
     * @param maxDrain
     * @param doDrain
     * @return
     */
    public FluidStack drainInternal(int maxDrain, boolean doDrain) {
        if (liquid == null || maxDrain <= 0) {
            return null;
        }

        int drained = maxDrain;
        if (liquid.amount < drained) {
            drained = liquid.amount;
        }

        FluidStack stack = new FluidStack(liquid, drained);
        if (doDrain) {
            liquid.amount -= drained;
            if (liquid.amount <= 0) {
                liquid = null;
            }

            if (this != null) {
                FluidEvent.fireEvent(
                    new FluidEvent.FluidDrainingEvent(
                        liquid,
                        this.worldObj,
                        this.xCoord,
                        this.yCoord,
                        this.zCoord,
                        this,
                        drained));
            }
        }
        return stack;
    }

    /* Tank stuff */
    @Override
    public FluidTankInfo[] getTankInfo(ForgeDirection from) {
        return new FluidTankInfo[] { getInfo() };
    }

    @Override
    public FluidStack getFluid() {
        return liquid == null ? null : liquid.copy();
    }

    @Override
    public int getFluidAmount() {
        return liquid != null ? liquid.amount : 0;
    }

    /** Returns the current amount of the liquid FOR RENDERING */
    public int getLiquidAmount() {
        return liquid.amount - renderOffset;
    }

    @Override
    public int getCapacity() {
        return this.capacity;
    }

    @Override
    public FluidTankInfo getInfo() {
        return new FluidTankInfo(this);
    }

    /* Inventory, inserting/extracting */

    public void interact(EntityPlayer player) {
        // only server side
        // if(worldObj.isRemote)
        // return;

        // can't interact with liquid inside
        // todo: maybe let it interact with a bucket or tank!
        if (liquid != null) return;

        // put stuff in?
        if (!isStackInSlot(0) && !isStackInSlot(1)) {
            ItemStack stack = player.inventory.decrStackSize(player.inventory.currentItem, stackSizeLimit);

            setInventorySlotContents(0, stack);
        }
        // take stuff out.
        else {
            // take out of stack 1 if something is in there, 0 otherwise
            int slot = isStackInSlot(1) ? 1 : 0;

            // try to transfer the stack to the player inventory
            ItemStack output = getStackInSlot(slot);
            ItemHelper.spawnItemAtPlayer(player, output);

            // remove inventory contents, since we spilled the full contents of the slot
            inventory[slot] = null;

            // send a block update for the comparator, needs to be done after the stack is
            // removed
            if (slot == 1) {
                this.worldObj.notifyBlockOfNeighborChange(this.xCoord, this.yCoord, this.zCoord, this.getBlockType());
            }
        }
    }

    @Override
    public ItemStack decrStackSize(int slot, int quantity) {
        ItemStack stack = super.decrStackSize(slot, quantity);
        worldObj.func_147479_m(xCoord, yCoord, zCoord);
        return stack;
    }

    @Override
    public int[] getAccessibleSlotsFromSide(int side) {
        return new int[] { 0, 1 };
    }

    @Override
    public boolean canInsertItem(int slot, ItemStack itemstack, int side) {
        // can't insert if there's liquid in it
        if (liquid != null) return false;

        // only into input slot
        return slot == 0 && !isStackInSlot(1);
    }

    @Override
    public boolean canExtractItem(int slot, ItemStack itemstack, int side) {
        // only output slot
        return slot == 1;
    }

    /* We don't have a gui or anything */
    @Override
    public Container getGuiContainer(InventoryPlayer inventoryplayer, World world, int x, int y, int z) {
        return null;
    }

    @Override
    public GuiContainer getGui(InventoryPlayer inventoryplayer, World world, int x, int y, int z) {
        return null;
    }

    @Override
    protected String getDefaultName() {
        return null;
    }

    @Override
    public String getInventoryName() {
        return null;
    }

    @Override
    public String getInvName() {
        return null;
    }

    @Override
    public boolean hasCustomInventoryName() {
        return false;
    }

    @Override
    public void openInventory() {}

    @Override
    public void closeInventory() {}

    /* NBT, Updating */
    @Override
    public void markDirty() {
        super.markDirty();
        worldObj.func_147479_m(xCoord, yCoord, zCoord);
    }

    @Override
    public void updateEntity() {
        // no recipeeeh
        if (recipe == null) {
            return;
        }

        // fully filled
        if (this.liquid.amount == this.capacity) {
            timer++;
            if (!this.worldObj.isRemote) {
                if (timer >= recipe.getTime()) {
                    TinkerCastingEvent.OnCasted event = TinkerCastingEvent.OnCasted.fire(recipe, this);
                    // done, finish!
                    if (event.consumeCast) {
                        // todo: play breaking sound and animation
                        setInventorySlotContents(0, null);

                        for (EntityPlayer player : this.worldObj.playerEntities) {
                            if (player.getDistanceSq(this.xCoord, this.yCoord, this.zCoord) < 1024
                                && player instanceof EntityPlayerMP) {
                                TinkerNetwork.sendPacket(
                                    player,
                                    new S2APacketParticles(
                                        "flame",
                                        this.xCoord + 0.5f,
                                        this.yCoord + 1.1f,
                                        this.zCoord + 0.5f,
                                        0.25f,
                                        0.0125f,
                                        0.25f,
                                        0.005f,
                                        5));
                            }
                        }
                    }

                    // put result into output
                    if (event.switchOutputs) {
                        setInventorySlotContents(1, getStackInSlot(0));
                        setInventorySlotContents(0, event.output);
                    } else {
                        setInventorySlotContents(1, event.output);
                    }
                    Sounds.playSoundAtPos(
                        this.worldObj,
                        this.xCoord,
                        this.yCoord,
                        this.zCoord,
                        Sounds.sizzle,
                        0.5F,
                        4.0F);

                    // reset state
                    reset();

                    // comparator update
                    this.worldObj
                        .notifyBlocksOfNeighborChange(this.xCoord, this.yCoord, this.zCoord, this.getBlockType());

                }
            } else if (this.worldObj.rand.nextFloat() > 0.9f) {
                this.worldObj.spawnParticle(
                    "smoke",
                    this.xCoord + rand.nextDouble(),
                    this.yCoord + 1.1,
                    this.zCoord + rand.nextDouble(),
                    0.0D,
                    0.0D,
                    0.0D);
            }
        }
    }

    /**
     * Sets the state for a new casting recipe, returns the fluid amount needed for
     * casting
     */
    public int initNewCasting(Fluid fluid, boolean setNewRecipe) {
        ICastingRecipe recipeFind = findRecipe(fluid);
        if (recipeFind != null) {
            if (setNewRecipe) {
                this.recipe = recipeFind;
            }
            return recipeFind.getFluidAmount();
        }
        return 0;
    }

    /**
     * Return the recipe for the current state, if one exists. Don't forget to fire
     * the OnCasting event!
     */
    protected abstract ICastingRecipe findRecipe(ItemStack cast, Fluid fluid);

    protected ICastingRecipe findRecipe(Fluid fluid) {
        ICastingRecipe recipeFind = findRecipe(getStackInSlot(0), fluid);
        if (TinkerCastingEvent.OnCasting.fire(recipeFind, this)) {
            return recipeFind;
        }
        // event was cancelled
        return null;
    }

    @Override
    public void readFromNBT(NBTTagCompound tags) {
        super.readFromNBT(tags);
        readCustomNBT(tags);
    }

    public void readCustomNBT(NBTTagCompound tags) {
        if (tags.getBoolean("hasLiquid")) {
            this.liquid = FluidStack.loadFluidStackFromNBT(tags.getCompoundTag("Fluid"));
        } else this.liquid = null;

        if (tags.getBoolean("Initialized")) this.capacity = tags.getInteger("Capacity");
        else this.capacity = updateCapacity();
        this.renderOffset = tags.getInteger("RenderOffset");
        this.timer = tags.getInteger("timer");

        this.updateFluidTo(this.liquid);
    }

    @Override
    public void writeToNBT(NBTTagCompound tags) {
        super.writeToNBT(tags);
        writeCustomNBT(tags);
    }

    public void writeCustomNBT(NBTTagCompound tags) {
        tags.setBoolean("hasLiquid", liquid != null);
        if (liquid != null) {
            NBTTagCompound nbt = new NBTTagCompound();
            liquid.writeToNBT(nbt);
            tags.setTag("Fluid", nbt);
        }
        tags.setInteger("Capacity", capacity);
        tags.setInteger("RenderOffset", renderOffset);
        tags.setInteger("timer", timer);
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
        worldObj.func_147479_m(xCoord, yCoord, zCoord);
    }

    /** Resets the current state completely */
    public void reset() {
        this.timer = 0;
        this.recipe = null;
        this.capacity = 0;
        this.liquid = null;
        this.renderOffset = 0;

        if (this.worldObj != null && !this.worldObj.isRemote && this.worldObj instanceof WorldServer world) {
            TinkerNetwork.sendToClients(world, this.getBlockPos(), new FluidUpdatePacket(this.getBlockPos(), null));
        }
    }

    // called clientside to sync with the server and on load
    public void updateFluidTo(FluidStack fluid) {
        int oldAmount = this.liquid != null ? this.liquid.amount : 0;
        this.liquid = fluid;

        if (fluid == null) {
            reset();
            return;
        } else if (recipe == null) {
            recipe = findRecipe(fluid.getFluid());
            if (recipe != null) {
                this.capacity = recipe.getFluidAmount();
            }
        }

        this.renderOffset += this.liquid.amount - oldAmount;
    }

    public float getProgress() {
        if (recipe == null || this.liquid.amount == 0) {
            return 0f;
        }
        return Math.min(1f, (float) timer / (float) recipe.getTime());
    }

}
