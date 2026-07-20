package mctbl.tinkersreborn.library.event;

import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.MinecraftForge;

import cpw.mods.fml.common.eventhandler.Cancelable;
import cpw.mods.fml.common.eventhandler.Event;
import mctbl.tinkersreborn.library.materials.TinkersRebornMaterial;
import mctbl.tinkersreborn.library.tools.ToolCore;

public abstract class TinkersRebornEvent extends Event {

    /**
     * Fired when a tool is built.
     * This happens every time a tool is loaded as well as when the player actually builds the tool.
     * You can make changes to the tag compound and it'll land on the resulting tool, but its itemstack is not
     * available.
     */
    public static class OnItemBuilding extends TinkersRebornEvent {

        public NBTTagCompound tag;
        public final List<TinkersRebornMaterial> materials;
        public final ToolCore tool;

        public OnItemBuilding(NBTTagCompound tag, List<TinkersRebornMaterial> materials, ToolCore tool) {
            this.tag = tag;
            this.materials = materials;
            this.tool = tool;
        }

        public static OnItemBuilding fireEvent(NBTTagCompound tag, List<TinkersRebornMaterial> materials,
            ToolCore tool) {
            OnItemBuilding event = new OnItemBuilding(tag, materials, tool);
            MinecraftForge.EVENT_BUS.post(event);
            return event;
        }
    }

    /**
     * Fired when the player tries to replace a toolpart.
     * You can modify the input items to achieve different results, this will not modify the actual items in the game.
     * If you're modifying the list itself, make sure to put new items into originally empty indices to prevent the
     * usage of other items in the input. Just append to the list.
     * You can not modify the tool that's getting modified
     */
    @Cancelable
    public static class OnToolPartReplacement extends TinkersRebornEvent {

        /** The items in the tool station. Can be manipulated. */
        public List<ItemStack> replacementParts;
        public ItemStack toolStack;

        public OnToolPartReplacement(List<ItemStack> replacementParts, ItemStack toolStack) {
            this.replacementParts = replacementParts;
            this.toolStack = toolStack.copy();
        }

        public static boolean fireEvent(List<ItemStack> replacementParts, ItemStack toolStack) {
            return !MinecraftForge.EVENT_BUS.post(new OnToolPartReplacement(replacementParts, toolStack));
        }
    }
}
