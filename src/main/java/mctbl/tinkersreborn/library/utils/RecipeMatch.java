package mctbl.tinkersreborn.library.utils;

import static mctbl.tinkersreborn.util.TinkersRebornUtils.isStackEmpty;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.IntStream;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

import com.google.common.collect.ImmutableList;

public abstract class RecipeMatch {

    public final int amountNeeded;
    public final int amountMatched;

    /**
     * @param amountNeeded  How many of the itemstack are needed for the match
     * @param amountMatched If amount needed itemstacks are present, as how many
     *                      matches does this count?
     */
    public RecipeMatch(int amountMatched, int amountNeeded) {
        this.amountMatched = amountMatched;
        this.amountNeeded = amountNeeded;
    }

    /**
     * Return all possible inputs that are a single item, if applicable.
     */
    public abstract List<ItemStack> getInputs();

    public abstract Optional<Match> matches(List<ItemStack> stacks);

    public static RecipeMatch of(String oredict) {
        return of(oredict, 1);
    }

    public static RecipeMatch of(String oredict, int matched) {
        return of(oredict, 1, matched);
    }

    public static RecipeMatch of(String oredict, int amount, int matched) {
        return new RecipeMatch.Oredict(oredict, amount, matched);
    }

    public static RecipeMatch of(List<ItemStack> oredict) {
        return of(oredict, 1);
    }

    public static RecipeMatch of(List<ItemStack> oredict, int matched) {
        return of(oredict, 1, matched);
    }

    public static RecipeMatch of(List<ItemStack> oredict, int amount, int matched) {
        return new RecipeMatch.Oredict(oredict, amount, matched);
    }

    public static RecipeMatch of(net.minecraft.item.Item item) {
        return of(item, 1);
    }

    public static RecipeMatch of(net.minecraft.item.Item item, int matched) {
        return of(item, 1, matched);
    }

    public static RecipeMatch of(net.minecraft.item.Item item, int amount, int matched) {
        return new RecipeMatch.Item(new ItemStack(item), amount, matched);
    }

    public static RecipeMatch of(Block block) {
        return of(block, 1);
    }

    public static RecipeMatch of(Block block, int matched) {
        return of(block, 1, matched);
    }

    public static RecipeMatch of(Block block, int amount, int matched) {
        return new RecipeMatch.Item(new ItemStack(block), amount, matched);
    }

    public static RecipeMatch of(ItemStack stack) {
        return of(stack, 1);
    }

    public static RecipeMatch of(ItemStack stack, int matched) {
        return of(stack, 1, matched);
    }

    public static RecipeMatch of(ItemStack stack, int amount, int matched) {
        return new RecipeMatch.Item(stack.copy(), amount, matched);
    }

    public static RecipeMatch ofNBT(ItemStack stack) {
        return ofNBT(stack, 1);
    }

    public static RecipeMatch ofNBT(ItemStack stack, int matched) {
        return new RecipeMatch.ItemCombination(matched, stack);
    }

    /**
     * Removes the match from the stacks. Has to be ensured that the match is
     * contained in the stacks.
     */
    public static void removeMatch(List<ItemStack> stacks, Match match) {
        int[] amountsRemoved = new int[match.stacks.size()];

        removeOrder(stacks, match.stacks, amountsRemoved);
        removeRemaining(stacks, match.stacks, amountsRemoved);
    }

    private static void removeOrder(List<ItemStack> stacks, List<ItemStack> toRemove, int[] amountsRemoved) {
        int i = 0;
        for (int j = 0; j < amountsRemoved.length; j++) {
            ItemStack stack = toRemove.get(j);
            for (; i < stacks.size(); i++) {
                if (isStackEmpty(stack) || isStackEmpty(stacks.get(i))) continue;
                // nbt sensitive since toolparts etc. use nbt
                if (stack.isItemEqual(stacks.get(i)) && ItemStack.areItemStackTagsEqual(stack, stacks.get(i))) {
                    if (stacks.get(i).stackSize >= stack.stackSize) {
                        stacks.get(i).stackSize -= stack.stackSize;
                        if (stacks.get(i).stackSize == 0) {
                            stacks.set(i, null);
                        }
                        amountsRemoved[j] += stack.stackSize;
                    }
                    i++;
                    break;
                }
            }
        }
    }

    private static void removeRemaining(List<ItemStack> stacks, List<ItemStack> toRemove, int[] amountsRemoved) {
        for (int j = 0; j < amountsRemoved.length; j++) {
            ItemStack stack = toRemove.get(j);
            if (isStackEmpty(stack)) continue;
            int needed = stack.stackSize - amountsRemoved[j];
            for (int i = 0; i < stacks.size() && needed > 0; i++) {
                if (isStackEmpty(stacks.get(i))) continue;
                if (stack.isItemEqual(stacks.get(i)) && ItemStack.areItemStackTagsEqual(stack, stacks.get(i))) {
                    int change = Math.min(stacks.get(i).stackSize, needed);
                    stacks.get(i).stackSize -= change;
                    if (stacks.get(i).stackSize == 0) {
                        stacks.set(i, null);
                    }
                    needed -= change;
                }
            }
        }
    }

    /**
     * A specific amount of a certain item is needed. Supports wildcard-metadata.
     * Not NBT sensitive.
     */
    public static class Item extends RecipeMatch {

        private final ItemStack template;

        public Item(ItemStack template, int amountNeeded) {
            this(template, amountNeeded, 1);
        }

        /**
         * @param template      The itemstack to match
         * @param amountNeeded  How many of the itemstack are needed for the match
         * @param amountMatched If amountneeded itemstacks are present, as how many
         *                      matches does this count?
         */
        public Item(ItemStack template, int amountNeeded, int amountMatched) {
            super(amountMatched, amountNeeded);
            this.template = template;
        }

        @Override
        public List<ItemStack> getInputs() {
            return ImmutableList.of(template);
        }

        @Override
        public Optional<Match> matches(List<ItemStack> stacks) {
            List<ItemStack> found = new LinkedList<>();
            int stillNeeded = amountNeeded;

            for (ItemStack stack : stacks) {
                if (OreDictionary.itemMatches(template, stack, false)) {
                    // add the amount found to the list
                    ItemStack copy = stack.copy();
                    copy.stackSize = Math.min(copy.stackSize, stillNeeded);
                    found.add(copy);
                    stillNeeded -= copy.stackSize;

                    // we found enough
                    if (stillNeeded <= 0) {
                        return Optional.of(new Match(found, amountMatched));
                    }
                }
            }

            return Optional.empty();
        }
    }

    /** A combination of multiple items. NBT SENSITIVE! */
    public static class ItemCombination extends RecipeMatch {

        protected final List<ItemStack> itemStacks;

        /**
         * @param amountMatched If amount needed itemstacks are present, as how many
         *                      matches does this count?
         */
        public ItemCombination(int amountMatched, ItemStack... stacks) {
            super(amountMatched, 0);

            List<ItemStack> nonNullStacks = new ArrayList<>(Collections.nCopies(stacks.length, null));
            for (int i = 0; i < stacks.length; i++) {
                if (stacks[i].stackSize != 0) {
                    nonNullStacks.set(i, stacks[i].copy());
                }
            }

            this.itemStacks = nonNullStacks;
        }

        @Override
        public List<ItemStack> getInputs() {
            return ImmutableList.copyOf(itemStacks);
        }

        @Override
        public Optional<Match> matches(List<ItemStack> stacks) {
            List<ItemStack> found = new LinkedList<>();
            Set<Integer> needed = new HashSet<>();

            for (int i = 0; i < itemStacks.size(); i++) {
                if (itemStacks.get(i).stackSize != 0) {
                    needed.add(i);
                }
            }

            for (ItemStack stack : stacks) {
                Iterator<Integer> iter = needed.iterator();
                while (iter.hasNext()) {
                    int index = iter.next();
                    ItemStack template = itemStacks.get(index);
                    if (isStackEmpty(template) || isStackEmpty(stack)) continue;
                    if (template.isItemEqual(stack) && ItemStack.areItemStackTagsEqual(template, stack)) {
                        // add the amount found to the list
                        ItemStack copy = stack.copy();
                        copy.stackSize = 1;
                        found.add(copy);
                        iter.remove();
                        break;
                    }
                }
            }

            if (needed.isEmpty()) {
                return Optional.of(new Match(found, amountMatched));
            }
            return Optional.empty();
        }
    }

    /** A specific amount of an oredicted material is needed to match. */
    public static class Oredict extends RecipeMatch {

        private final List<ItemStack> oredictEntry; // todo: change this to the actual list in the oredict

        public Oredict(List<ItemStack> oredictEntry, int amountNeeded) {
            this(oredictEntry, amountNeeded, 1);
        }

        public Oredict(List<ItemStack> oredictEntry, int amountNeeded, int amountMatched) {
            super(amountMatched, amountNeeded);
            this.oredictEntry = oredictEntry;
        }

        public Oredict(String oredictEntry, int amountNeeded) {
            this(oredictEntry, amountNeeded, 1);
        }

        public Oredict(String oredictEntry, int amountNeeded, int amountMatched) {
            super(amountMatched, amountNeeded);
            this.oredictEntry = OreDictionary.getOres(oredictEntry);
        }

        @Override
        public List<ItemStack> getInputs() {
            // transform "Requires 2 Cobblestone" into "2x require 1 Cobblestone" since the
            // oredictEntry only contains stacksize 1 usually
            ImmutableList.Builder<ItemStack> builder = ImmutableList.builder();
            oredictEntry.forEach(
                stack -> IntStream.range(0, amountNeeded)
                    .forEach(i -> builder.add(stack)));
            return builder.build();
        }

        @Override
        public Optional<Match> matches(List<ItemStack> stacks) {
            List<ItemStack> found = new LinkedList<>();
            int stillNeeded = amountNeeded;

            for (ItemStack ore : oredictEntry) {
                for (ItemStack stack : stacks) {
                    if (OreDictionary.itemMatches(ore, stack, false)) {
                        // add the amount found to the list
                        ItemStack copy = stack.copy();
                        copy.stackSize = Math.min(copy.stackSize, stillNeeded);
                        found.add(copy);
                        stillNeeded -= copy.stackSize;

                        // we found enough
                        if (stillNeeded <= 0) {
                            return Optional.of(new Match(found, amountMatched));
                        }
                    }
                }
            }

            return Optional.empty();
        }
    }

    /** Represents a collection of items that match the recipies */
    public static class Match {

        /** The stacks that have to be removed to apply this match */
        public List<ItemStack> stacks;

        /** How often the recipe is found within this match */
        public int amount;

        public Match(List<ItemStack> stacks, int amount) {
            this.stacks = stacks;
            this.amount = amount;
        }
    }
}
