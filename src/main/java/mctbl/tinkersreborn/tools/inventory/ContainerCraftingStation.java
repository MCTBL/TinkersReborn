package mctbl.tinkersreborn.tools.inventory;

import java.util.List;
import java.util.stream.Collectors;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCraftResult;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.event.entity.player.PlayerOpenContainerEvent;

import org.apache.commons.lang3.tuple.Pair;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import mctbl.tinkersreborn.library.gui.container.BaseContainer;
import mctbl.tinkersreborn.library.inventory.ContainerSideInventory;
import mctbl.tinkersreborn.library.inventory.InventoryCraftingPersistent;
import mctbl.tinkersreborn.library.utils.BlockPos;
import mctbl.tinkersreborn.tools.entity.CraftingStationLogic;
import mctbl.tinkersreborn.tools.inventory.slots.SlotCraftingStation;

public class ContainerCraftingStation extends ContainerTinkerStation<CraftingStationLogic> {

    private static final int SLOT_RESULT = 0;
    private final EntityPlayer player;
    private final InventoryCraftingPersistent craftMatrix;
    private final InventoryCraftResult craftResult;

    private IRecipe lastRecipe;
    private IRecipe lastLastRecipe;

    @SubscribeEvent
    public static void onCraftingStationGuiOpened(PlayerOpenContainerEvent event) {
        // by default the container does not update after it has been opened.
        // we need it to check its recipe
        if (event.entityPlayer.openContainer instanceof ContainerCraftingStation csc) {
            csc.onCraftMatrixChanged();
        }
    }

    public ContainerCraftingStation(InventoryPlayer playerInventory, CraftingStationLogic tile) {
        super(tile);

        craftResult = new InventoryCraftResult();
        craftMatrix = new InventoryCraftingPersistent(this, tile, 3, 3);
        player = playerInventory.player;

        this.addSlotToContainer(
            new SlotCraftingStation(playerInventory.player, this.craftMatrix, this.craftResult, SLOT_RESULT, 124, 35));
        int i;
        int j;

        for (i = 0; i < 3; ++i) {
            for (j = 0; j < 3; ++j) {
                this.addSlotToContainer(new Slot(this.craftMatrix, j + i * 3, 30 + j * 18, 17 + i * 18));
            }
        }

        // detect te
        TileEntity inventoryTE = null;
        ForgeDirection accessDir = null;
        for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
            if (dir == ForgeDirection.UP || dir == ForgeDirection.DOWN) {
                continue;
            }
            BlockPos neighbor = BlockPos.of(xCoord, yCoord, zCoord)
                .offset(dir);
            boolean stationPart = false;
            for (Pair<BlockPos, Block> tinkerPos : tinkerStationBlocks) {
                if (tinkerPos.getLeft()
                    .equals(neighbor)) {
                    stationPart = true;
                    break;
                }
            }
            if (!stationPart) {
                TileEntity te = world.getTileEntity(neighbor.x, neighbor.y, neighbor.z);
                if (te != null && !(te instanceof CraftingStationLogic)) {

                    if (te instanceof IInventory t && !t.isUseableByPlayer(player)) {
                        continue;
                    }

                    // try internal access first
                    // if (te.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null)) {
                    // if (te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY,
                    // null) instanceof IItemHandlerModifiable) {
                    // inventoryTE = te;
                    // accessDir = null;
                    // break;
                    // }
                    // }
                    // try sided access else
                    // if (te.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, dir.getOpposite())) {
                    // if (te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY,
                    // dir.getOpposite()) instanceof IItemHandlerModifiable) {
                    // inventoryTE = te;
                    // accessDir = dir.getOpposite();
                    // break;
                    // }
                    // }
                }
            }
        }

        if (inventoryTE != null) {
            addSubContainer(new ContainerSideInventory(inventoryTE, accessDir, -6 - 18 * 6, 8, 6), false);
        }

        this.addPlayerInventory(playerInventory, 8, 84);
    }

    public void onCraftMatrixChanged() {
        this.onCraftMatrixChanged(this.craftMatrix);
    }

    @Override
    public void onCraftMatrixChanged(IInventory inventoryIn) {
        this.slotChangedCraftingGrid(this.world, this.player, this.craftMatrix, this.craftResult);

    }

    public IRecipe findMatchingRecipe(InventoryCrafting inv, World world) {
        for (IRecipe irecipe : CraftingManager.getInstance()
            .getRecipeList()) if (irecipe.matches(inv, world)) return irecipe;

        return null;
    }

    protected void slotChangedCraftingGrid(World world, EntityPlayer player, InventoryCrafting inv,
        InventoryCraftResult result) {
        ItemStack itemstack = null;

        // if the recipe is no longer valid, update it
        if (lastRecipe == null || !lastRecipe.matches(inv, world)) {
            lastRecipe = findMatchingRecipe(inv, world);
        }

        // if we have a recipe, fetch its result
        if (lastRecipe != null) {
            itemstack = lastRecipe.getCraftingResult(inv);
        }
        // set the slot on both sides, client is for display/so the client knows about
        // the recipe
        result.setInventorySlotContents(SLOT_RESULT, itemstack);

        // update recipe on server
        if (!world.isRemote) {
            EntityPlayerMP entityplayermp = (EntityPlayerMP) player;

            // we need to sync to all players currently in the inventory
            List<EntityPlayerMP> relevantPlayers = getAllPlayersWithThisContainerOpen(
                this,
                entityplayermp.getServerForPlayer());

            // sync result to all serverside inventories to prevent duplications/recipes
            // being blocked
            // need to do this every time as otherwise taking items of the result causes
            // desync
            syncResultToAllOpenWindows(itemstack, relevantPlayers);

            // if the recipe changed, update clients last recipe
            // this also updates the client side display when the recipe is added
            if (lastLastRecipe != lastRecipe) {
                syncRecipeToAllOpenWindows(lastRecipe, relevantPlayers);
                lastLastRecipe = lastRecipe;
            }
        }
    }

    private void syncResultToAllOpenWindows(final ItemStack stack, List<EntityPlayerMP> players) {
        players.forEach(otherPlayer -> {
            otherPlayer.openContainer.putStackInSlot(SLOT_RESULT, stack);
            // otherPlayer.connection.sendPacket(new
            // SPacketSetSlot(otherPlayer.openContainer.windowId, SLOT_RESULT, stack));
        });
    }

    private void syncRecipeToAllOpenWindows(final IRecipe lastRecipe, List<EntityPlayerMP> players) {
        players.forEach(otherPlayer -> {
            // safe cast since hasSameContainerOpen does class checks
            ((ContainerCraftingStation) otherPlayer.openContainer).lastRecipe = lastRecipe;
            // TinkerNetwork.sendTo(new LastRecipeMessage(lastRecipe), otherPlayer);
        });
    }

    // todo: move this to Mantle
    // server can be gotten from EntityPlayerMP
    private <T extends TileEntity> List<EntityPlayerMP> getAllPlayersWithThisContainerOpen(BaseContainer<T> container,
        WorldServer server) {
        return server.playerEntities.stream()
            .filter(player -> hasSameContainerOpen(container, player))
            .map(player -> (EntityPlayerMP) player)
            .collect(Collectors.toList());
    }

    private <T extends TileEntity> boolean hasSameContainerOpen(BaseContainer<T> container,
        EntityPlayer playerToCheck) {
        return playerToCheck instanceof EntityPlayerMP && playerToCheck.openContainer.getClass()
            .isAssignableFrom(container.getClass()) && this.sameGui((BaseContainer) playerToCheck.openContainer);
    }

    @Override
    public boolean canMergeSlot(ItemStack p_94530_1_, Slot p_94530_2_) {
        return p_94530_2_.inventory != this.craftResult && super.canMergeSlot(p_94530_1_, p_94530_2_);
    }

    protected TileEntity detectInventory() {

        for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
            if (dir == ForgeDirection.UP || dir == ForgeDirection.DOWN) {
                continue;
            }
            BlockPos neighbor = BlockPos.of(xCoord, yCoord, zCoord)
                .offset(dir);
            boolean stationPart = false;
            for (Pair<BlockPos, Block> tinkerPos : tinkerStationBlocks) {
                if (tinkerPos.getLeft()
                    .equals(neighbor)) {
                    stationPart = true;
                    break;
                }
            }
            if (!stationPart) {
                TileEntity te = world.getTileEntity(neighbor.x, neighbor.y, neighbor.z);
                // if (te != null && te.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, dir.getOpposite()))
                // {
                // if (te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY,
                // dir.getOpposite()) instanceof IItemHandlerModifiable) {
                // return te;
                // }
                // }
            }
        }

        return null;
    }

    /**
     * @return the starting slot for the player inventory. Present for usage in the
     *         NEI crafting station support
     */
    public int getPlayerInventoryStart() {
        return playerInventoryStart;
    }

    public InventoryCrafting getCraftMatrix() {
        return craftMatrix;
    }

    public void updateLastRecipeFromServer(IRecipe recipe) {
        lastRecipe = recipe;
        // if no recipe, set to empty to prevent ghost outputs when another player grabs
        // the result
        this.craftResult
            .setInventorySlotContents(SLOT_RESULT, recipe != null ? recipe.getCraftingResult(craftMatrix) : null);
    }

    public List<ItemStack> getRemainingItems() {
        if (lastRecipe != null && lastRecipe.matches(craftMatrix, world)) {
            return craftMatrix.getRemainingItems();
        }

        return craftMatrix.getStackList();
    }
}
