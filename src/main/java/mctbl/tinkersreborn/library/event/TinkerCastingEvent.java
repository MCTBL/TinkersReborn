package mctbl.tinkersreborn.library.event;

import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fluids.Fluid;

import cpw.mods.fml.common.eventhandler.Cancelable;
import cpw.mods.fml.common.eventhandler.Event;
import mctbl.tinkersreborn.library.smeltery.ICastingRecipe;
import mctbl.tinkersreborn.smeltery.entity.CastingBlockLogic;

public abstract class TinkerCastingEvent extends Event {

    public final ICastingRecipe recipe;
    public final CastingBlockLogic tile;

    public TinkerCastingEvent(ICastingRecipe recipe, CastingBlockLogic tile) {
        this.recipe = recipe;
        this.tile = tile;
    }

    /**
     * Fired when a casting block wants to begins casting
     * Can be cancelled to prevent the casting
     */
    @Cancelable
    public static class OnCasting extends TinkerCastingEvent {

        public OnCasting(ICastingRecipe recipe, CastingBlockLogic tile) {
            super(recipe, tile);
        }

        public static boolean fire(ICastingRecipe recipe, CastingBlockLogic tile) {
            OnCasting event = new OnCasting(recipe, tile);
            MinecraftForge.EVENT_BUS.post(event);
            return !event.isCanceled();
        }
    }

    public static class OnCasted extends TinkerCastingEvent {

        public ItemStack output;
        public boolean consumeCast;
        public boolean switchOutputs;

        public OnCasted(ICastingRecipe recipe, CastingBlockLogic tile) {
            super(recipe, tile);
            ItemStack cast = tile.getStackInSlot(0);
            Fluid fluid = tile.getFluid()
                .getFluid();
            this.output = recipe.getResult(cast, fluid)
                .copy();
            this.consumeCast = recipe.consumesCast();
            this.switchOutputs = recipe.switchOutputs();
        }

        public static OnCasted fire(ICastingRecipe recipe, CastingBlockLogic tile) {
            OnCasted event = new OnCasted(recipe, tile);
            MinecraftForge.EVENT_BUS.post(event);
            return event;
        }
    }
}
