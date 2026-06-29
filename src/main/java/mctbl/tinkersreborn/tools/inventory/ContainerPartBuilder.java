package mctbl.tinkersreborn.tools.inventory;

import java.util.Arrays;
import java.util.Optional;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCraftResult;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.world.WorldServer;

import cpw.mods.fml.common.FMLCommonHandler;
import mctbl.tinkersreborn.library.TinkersRebornRegistry;
import mctbl.tinkersreborn.library.items.IPattern;
import mctbl.tinkersreborn.library.materials.TinkersRebornMaterial;
import mctbl.tinkersreborn.library.utils.RecipeMatch.Match;
import mctbl.tinkersreborn.tools.TinkersRebornTools;
import mctbl.tinkersreborn.tools.entity.PartBuilderLogic;
import mctbl.tinkersreborn.tools.items.TinkersRebornToolPart;

public class ContainerPartBuilder extends ContainerTinkerStation<PartBuilderLogic> {

    private final EntityPlayer player;
    protected SlotPartBuilderOut out;
    protected TinkersRebornToolPart part = TinkersRebornTools.rod;
    public TinkersRebornMaterial material;
    public int materialCount = 0;

    public ContainerPartBuilder(InventoryPlayer inventoryplayer, PartBuilderLogic tile) {
        super(tile);

        this.player = inventoryplayer.player;

        // pattern in
        addSlotToContainer(new SlotPartBuilderIn(tile, 0, 8, 35, this, IPattern.class));
        // material in
        addSlotToContainer(new SlotPartBuilderIn(tile, 1, 29, 35, this, Object.class));

        out = new SlotPartBuilderOut(0, 148, 35, this);
        addSlotToContainer(out);

        this.addPlayerInventory(inventoryplayer, 8, 84);

        onCraftMatrixChanged(inventoryplayer);
    }

    // Called when the crafting result is taken out of its slot
    public void onResultTaken(EntityPlayer playerIn, ItemStack stack) {

    }

    public ItemStack getResult() {
        return out.getStack();
    }

    public void toolPartSelected(TinkersRebornToolPart part) {
        this.part = part;
    }

    @Override
    public boolean canMergeSlot(ItemStack stack, Slot slot) {
        if (slot == out && slot instanceof SlotPartBuilderIn) {
            return false;
        }

        return super.canMergeSlot(stack, slot);
    }

    @Override
    public void onCraftMatrixChanged(IInventory inventoryIn) {
        updateGUI();

        updateMaterialAndCount();

        if (part != null && material != null && materialCount >= 1) {
            out.inventory.setInventorySlotContents(0, part.getNewPartWithMaterial(material));
        } else {
            out.inventory.setInventorySlotContents(0, null);
        }

        // sync output with other open containers on the server
        if (!this.world.isRemote) {
            WorldServer server = (WorldServer) this.world;
            for (EntityPlayer player : server.playerEntities) {
                if (player.openContainer != this && player.openContainer instanceof ContainerPartBuilder
                    && this.sameGui((ContainerPartBuilder) player.openContainer)) {
                    ((ContainerPartBuilder) player.openContainer).out.inventory
                        .setInventorySlotContents(0, out.getStack());
                }
            }
        }
    }

    public void updateMaterialAndCount() {
        ItemStack materialStack = tile.getStackInSlot(1);
        if (materialStack != null) {
            for (TinkersRebornMaterial m : TinkersRebornRegistry.allMaterialsList) {
                Optional<Match> matches = m.matchesRecursively(Arrays.asList(materialStack));
                if (matches.isPresent() && matches.get().amount > 0) {
                    material = m;
                    materialCount = matches.get().amount / TinkersRebornMaterial.VALUE_Ingot;
                }
            }
        } else {
            material = null;
            materialCount = 0;
        }
    }

    public static class SlotPartBuilderIn extends Slot {

        public ContainerPartBuilder parent;
        private Class<?> restrict;

        public SlotPartBuilderIn(IInventory inventoryIn, int index, int xPosition, int yPosition,
            ContainerPartBuilder container, Class<?> restrict) {
            super(inventoryIn, index, xPosition, yPosition);
            this.restrict = restrict;
            this.parent = container;
        }

        @Override
        public boolean isItemValid(ItemStack stack) {
            return stack != null && restrict.isInstance(stack.getItem());
        }

        @Override
        public void onSlotChanged() {
            // notify container to update craft result
            parent.onCraftMatrixChanged(inventory);
        }
    }

    public static class SlotPartBuilderOut extends Slot {

        public ContainerPartBuilder parent;

        public SlotPartBuilderOut(int index, int xPosition, int yPosition, ContainerPartBuilder container) {
            super(new InventoryCraftResult(), index, xPosition, yPosition);

            this.parent = container;
        }

        @Override
        public void onPickupFromSlot(EntityPlayer playerIn, ItemStack stack) {
            FMLCommonHandler.instance()
                .firePlayerCraftingEvent(playerIn, stack, parent.getTile());
            parent.onResultTaken(playerIn, stack);
            stack.onCrafting(playerIn.getEntityWorld(), playerIn, 1);
            super.onPickupFromSlot(playerIn, stack);
        }
    }
}
