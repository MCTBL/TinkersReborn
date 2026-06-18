package mctbl.tinkersreborn.library.crafting;

import static mctbl.tinkersreborn.util.TinkersRebornUtils.isStackEmpty;
import static mctbl.tinkersreborn.util.TinkersRebornUtils.translate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;

import com.google.common.collect.Sets;

import gnu.trove.map.TIntIntMap;
import gnu.trove.map.hash.TIntIntHashMap;
import mctbl.tinkersreborn.TinkersReborn;
import mctbl.tinkersreborn.TinkersRebornConfig;
import mctbl.tinkersreborn.library.TinkerGuiException;
import mctbl.tinkersreborn.library.TinkersRebornRegistry;
import mctbl.tinkersreborn.library.materials.MaterialStatusType;
import mctbl.tinkersreborn.library.materials.TinkersRebornMaterial;
import mctbl.tinkersreborn.library.tools.IModifier;
import mctbl.tinkersreborn.library.tools.IRepairable;
import mctbl.tinkersreborn.library.tools.IToolPart;
import mctbl.tinkersreborn.library.tools.ITrait;
import mctbl.tinkersreborn.library.tools.ToolCore;
import mctbl.tinkersreborn.library.tools.ToolCore.ToolPartRecord;
import mctbl.tinkersreborn.library.tools.traits.AbstractTrait;
import mctbl.tinkersreborn.library.utils.RecipeMatch;
import mctbl.tinkersreborn.util.TinkersRebornUtils;
import mctbl.tinkersreborn.util.ToolTags;
import mctbl.tinkersreborn.util.ToolTagsHelper;

public class ToolBuilderHelper {

    private ToolBuilderHelper() {}

    @Nullable
    public static ItemStack buildTool(String toolName, ItemStack... parts) {
        // check all possible
        return buildTool(toolName, TinkersRebornRegistry.getAllTools(), parts);
    }

    @Nullable
    public static ItemStack buildTool(String toolName, Collection<ToolCore> possibleTools, ItemStack... parts) {
        ToolCore core = findMatchingToolCore(parts, possibleTools);
        if (core == null)
            // build fail because can't find match recipce
            return null;
        List<TinkersRebornMaterial> materials = new ArrayList<>();
        for (ItemStack stack : parts) {
            materials.add(TinkersRebornRegistry.getMaterialById(stack.getItemDamage()));
        }

        ItemStack newTool = new ItemStack(core);
        newTool.setTagCompound(core.buildItemNBT(materials));
        return newTool;
    }

    private static ToolCore findMatchingToolCore(ItemStack[] parts, Collection<ToolCore> possibleTools) {
        for (ToolCore core : possibleTools) if (core.checkRecipeMatch(parts)) return core;
        return null;
    }

    @Nullable
    public static ItemStack tryRepairTool(List<ItemStack> stacks, ItemStack toolStack, boolean removeItems) {
        if (toolStack == null || !(toolStack.getItem() instanceof IRepairable)) {
            return null;
        }

        // obtain a working copy of the items if the originals shouldn't be modified
        if (!removeItems) {
            stacks = TinkersRebornUtils.copyItemStackList(stacks);
        }

        return ((IRepairable) toolStack.getItem()).repair(toolStack, stacks);
    }

    /**
     * Takes a tool and an array of itemstacks and tries to modify the tool with
     * those. If removeItems is true, the items used in the process will be removed
     * from the array.
     *
     * @param input       Items to modify the tool with
     * @param toolStack   The tool
     * @param removeItems If true the applied items will be removed from the array
     * @return The modified tool or null if something went wrong or no modifier
     *         applied.
     * @throws TinkerGuiException Thrown when not matching modifiers could be
     *                            applied. Contains extra-information why the
     *                            process failed.
     */
    @Nonnull
    public static ItemStack tryModifyTool(List<ItemStack> input, ItemStack toolStack, boolean removeItems)
        throws TinkerGuiException {
        ItemStack copy = toolStack.copy();

        // obtain a working copy of the items if the originals shouldn't be modified
        List<ItemStack> stacks = TinkersRebornUtils.copyItemStackList(input);
        List<ItemStack> usedStacks = TinkersRebornUtils.copyItemStackList(input);

        Set<IModifier> appliedModifiers = Sets.newHashSet();
        for (IModifier modifier : TinkersRebornRegistry.getAllModifier()) {
            Optional<RecipeMatch.Match> matchOptional;
            do {
                matchOptional = modifier.matches(stacks);
                ItemStack backup = copy.copy();

                // found a modifier that is applicable. Try to apply the match
                if (matchOptional.isPresent()) {
                    RecipeMatch.Match match = matchOptional.get();
                    // we need to apply the whole match
                    while (match.amount > 0) {
                        TinkerGuiException caughtException = null;
                        boolean canApply = false;
                        try {
                            canApply = modifier.canApply(copy, toolStack);
                        } catch (TinkerGuiException e) {
                            caughtException = e;
                        }

                        // but can it be applied?
                        if (canApply) {
                            modifier.apply(copy);

                            appliedModifiers.add(modifier);
                            match.amount--;
                        } else {
                            // materials would allow another application, but modifier doesn't
                            // if we have already applied another modifier we cancel the whole thing to
                            // prevent situations where
                            // only a part of the modifiers gets applied. either all or none.
                            // if we have a reason, rather tell the player that
                            if (caughtException != null && !appliedModifiers.contains(modifier)) {
                                throw caughtException;
                            }

                            copy = backup;
                            RecipeMatch.removeMatch(stacks, match);
                            break;
                        }
                    }

                    if (match.amount == 0) {
                        RecipeMatch.removeMatch(stacks, match);
                        RecipeMatch.removeMatch(usedStacks, match);
                    }
                }
            } while (matchOptional.isPresent());
        }

        // check if all itemstacks were touched - otherwise there's an invalid item in
        // the input
        for (int i = 0; i < input.size(); i++) {
            if (!isStackEmpty(input.get(i)) && ItemStack.areItemStacksEqual(input.get(i), stacks.get(i))) {
                if (!appliedModifiers.isEmpty()) {
                    String error = translate(
                        "gui.error.no_modifier_for_item",
                        input.get(i)
                            .getDisplayName());
                    throw new TinkerGuiException(error);
                }
                return null;
            }
        }

        // update output itemstacks
        if (removeItems) {
            for (int i = 0; i < input.size(); i++) {
                // stacks might be null because stacksize got 0 during processing, we have to
                // reflect that in the input
                // so the caller can identify that
                if (isStackEmpty(usedStacks.get(i))) {
                    input.get(i).stackSize = 0;
                } else {
                    input.get(i).stackSize = usedStacks.get(i).stackSize;
                }
            }
        }

        if (!appliedModifiers.isEmpty()) {
            // always rebuild tinkers items to ensure consistency and find problems earlier
            if (copy.getItem() instanceof ToolCore) {
                rebuildTool(copy);
            }
            return copy;
        }

        return null;
    }

    /**
     * Takes a tool and toolparts and replaces the parts inside the tool with the
     * given ones. Toolparts have to be applicable to the tool. Toolparts must not
     * be duplicates of currently used parts.
     *
     * @param toolStack   The tool to replace the parts in
     * @param toolPartsIn The toolparts.
     * @param removeItems If true the applied items will be removed from the array
     * @return The tool with the replaced parts or null if the conditions have not
     *         been met.
     */
    @Nullable
    public static ItemStack tryReplaceToolParts(ItemStack toolStack, final List<ItemStack> toolPartsIn,
        final boolean removeItems) throws TinkerGuiException {
        if (toolStack == null || !(toolStack.getItem() instanceof ToolCore)) {
            return null;
        }

        // we never modify the original. Caller can remove all of them if we return a
        // result
        List<ItemStack> inputItems = TinkersRebornUtils.copyItemStackList(toolPartsIn);
        // TODO
        // if(!TinkerEvent.OnToolPartReplacement.fireEvent(inputItems, toolStack)) {
        // // event cancelled
        // return null;
        // }
        // technically we don't need a deep copy here, but meh. less code.
        final List<ItemStack> toolParts = TinkersRebornUtils.copyItemStackList(inputItems);

        TIntIntMap assigned = new TIntIntHashMap();
        ToolCore tool = (ToolCore) toolStack.getItem();
        // materiallist has to be copied because it affects the actual NBT on the tool
        // if it's changed
        final NBTTagList materialList = (NBTTagList) ToolTagsHelper.getToolBaseMaterialsNBTSafe(toolStack)
            .copy();

        TinkersRebornMaterial newHeadMaterial = null;
        // assign each toolpart to a slot in the tool
        for (int i = 0; i < toolParts.size(); i++) {
            ItemStack part = toolParts.get(i);
            if (isStackEmpty(part)) {
                continue;
            }
            if (!(part.getItem() instanceof IToolPart)) {
                // invalid item for toolpart replacement
                return null;
            }

            int candidate = -1;
            // find an applicable slot in the tool structure corresponding to the toolparts
            // position
            List<ToolPartRecord> pms = tool.getToolComponentsParts();
            for (int j = 0; j < pms.size(); j++) {
                ToolPartRecord pmt = pms.get(j);
                String partMat = ((IToolPart) part.getItem()).getMaterial(part).identifier;
                String currentMat = materialList.getStringTagAt(j);
                // is valid and not the same material?
                if (pmt.isValid(part) && !partMat.equals(currentMat)) {
                    // part not taken up by previous part already?
                    if (!assigned.valueCollection()
                        .contains(j)) {
                        candidate = j;
                        // if a tool has multiple of the same parts we may want to replace another one
                        // as the currently selected
                        // for that purpose we only allow to overwrite the current selection if the
                        // input slot is a later one than the current one
                        if (i <= j) {
                            break;
                        }
                    }
                }
            }

            // if this part is a head type, capture its material for later fortify
            // comparison
            if (candidate >= 0) {
                ToolPartRecord pmt = pms.get(candidate);
                if (pmt.statusType() == MaterialStatusType.HEAD) {
                    newHeadMaterial = ((IToolPart) part.getItem()).getMaterial(part);
                }
            }
            // no assignment found for a part. Invalid input.
            else {
                return null;
            }
            assigned.put(i, candidate);
        }

        // did we assign nothing?
        if (assigned.isEmpty()) {
            return null;
        }

        // We now know which parts to replace with which inputs. Yay. Now we only have
        // to do so.
        // to do so we simply switch out the materials used and rebuild the tool
        assigned.forEachEntry((i, j) -> {
            String mat = ((IToolPart) toolParts.get(i)
                .getItem()).getMaterial(toolParts.get(i)).identifier;
            materialList.func_150304_a(j, new NBTTagString(mat));
            if (removeItems) {
                if (i < toolPartsIn.size() && !isStackEmpty(toolPartsIn.get(i))) {
                    toolPartsIn.get(i).stackSize -= 1;
                }
            }
            return true;
        });

        // check that each material is still compatible with each modifier
        ToolCore tinkersItem = (ToolCore) toolStack.getItem();
        ItemStack copyToCheck = tinkersItem.buildItem(ToolTagsHelper.fromTagToMaterial(materialList));
        // this includes traits
        NBTTagList modifiers = ToolTagsHelper.getModifiersTagList(toolStack);
        for (int i = 0; i < modifiers.tagCount(); i++) {
            String id = modifiers.getStringTagAt(i);
            IModifier mod = TinkersRebornRegistry.getModifier(id);

            boolean canApply = false;
            try {
                // will throw an exception if it can't apply
                canApply = mod != null && mod.canApply(copyToCheck, copyToCheck);
            } catch (TinkerGuiException e) {
                // try again with more modifiers, in case something modified them (tinkers tool
                // leveling)
                // ensure that free modifiers are present (
                if (ToolTagsHelper.getFreeModifiers(copyToCheck) < TinkersRebornConfig.defaultModifiers) {
                    ItemStack copyWithModifiers = copyToCheck.copy();
                    ToolTagsHelper.setFreeModifiers(toolStack, TinkersRebornConfig.defaultModifiers);
                    canApply = mod.canApply(copyWithModifiers, copyWithModifiers);
                }
            }
            if (!canApply) {
                throw new TinkerGuiException();
            }
        }

        final NBTTagList modifierList = (NBTTagList) modifiers.copy();
        for (int i = 0; i < modifierList.tagCount(); i++) {
            String id = modifierList.getStringTagAt(i);
            IModifier mod = TinkersRebornRegistry.getModifier(id);
            // if the new head's harvest level equals/exceeds the fortification level, it's
            // no longer beneficial. good riddance!
            // TODO
            // if (newHeadMaterial != null && mod instanceof ModFortify) {
            // HeadMaterialStats newHeadStats = newHeadMaterial.getStats(MaterialTypes.HEAD);
            // HeadMaterialStats fortifyStats = ((ModFortify) mod).material.getStats(MaterialTypes.HEAD);
            // if (newHeadStats != null && fortifyStats != null
            // && newHeadStats.harvestLevel >= fortifyStats.harvestLevel) {
            // modifierList.removeTag(i);
            // }
            // }
        }

        ItemStack output = toolStack.copy();
        ToolTagsHelper.setToolBaseMaterialsNBTSafe(output, materialList);
        ToolTagsHelper.setModifiersTagList(output, modifierList);
        rebuildTool(output);

        // check if the output has enough durability. we only allow it if the result
        // would not be broken
        if (output.getItemDamage() > output.getMaxDamage()) {
            String error = String
                .format(translate("gui.error.not_enough_durability"), output.getItemDamage() - output.getMaxDamage());
            throw new TinkerGuiException(error);
        }

        return output;
    }

    /**
     * Rebuilds a tool from its raw data, material info and applied modifiers
     *
     * @param rootNBT The root NBT tag compound of the tool to to rebuild. The NBT
     *                will be modified, overwriting old data.
     */
    public static void rebuildTool(ItemStack tool) throws TinkerGuiException {
        ToolCore tinkersItem = (ToolCore) tool.getItem();

        NBTTagCompound tinkersTag = ToolTagsHelper.getToolBaseNBTSafe(tool);
        ToolTagsHelper.setToolBaseNBTSafe(tool, tinkersTag);

        boolean broken = ToolTagsHelper.isBroken(tool);
        // Recalculate tool base stats from material stats
        List<TinkersRebornMaterial> materials = ToolTagsHelper.getToolBaseMaterialsList(tool);
        List<ToolPartRecord> pms = tinkersItem.getToolComponentsParts();

        // ensure all needed Stats are present
        while (materials.size() < pms.size()) {
            materials.add(TinkersRebornMaterial.UNKNOWN);
        }
        for (int i = 0; i < pms.size(); i++) {
            if (!pms.get(i)
                .isValidMaterial(materials.get(i))) {
                materials.set(i, TinkersRebornMaterial.UNKNOWN);
            }
        }

        // the base stats of the tool
        NBTTagCompound toolTag = tinkersItem.buildToolTag(materials)
            .get();
        tinkersTag.setTag(ToolTags.TOOLDATA, toolTag);
        // and its copy for reference
        tinkersTag.setTag(ToolTags.TOOLDATAORIG, toolTag.copy());

        // save the old modifiers list and clean up all tags that get set by
        // save old modifiers and traits
        List<NBTTagCompound> modifiersAndTraitTagOld = ToolTagsHelper.getModifiersList(tool);

        // clear old
        tinkersTag.removeTag(ToolTags.MODIFIERS); // the active-modifiers tag
        tinkersTag.setTag(ToolTags.MODIFIERS, new NBTTagList());
        tinkersTag.removeTag("ench"); // and the enchantments tag
        tinkersTag.removeTag(ToolTags.ENCHANT_EFFECT); // enchant effect too, will be readded by a trait either way

        // readd traits
        tinkersItem.addMaterialTraits(tinkersTag, materials);

        // fire event TODO
        // TinkerEvent.OnItemBuilding.fireEvent(rootNBT, ImmutableList.copyOf(materials), tinkersItem);

        // reapply modifiers
        List<NBTTagCompound> oldModifiersTag = modifiersAndTraitTagOld.stream()
            .filter(
                c -> c.getString(ToolTags.TYPE)
                    .equals(ToolTags.TYPEMODIFIERS))
            .collect(Collectors.toList());
        // ModifierNBT.readTag(toolTag)

        // copy over and reapply all relevant modifiers
        for (NBTTagCompound modifiers : oldModifiersTag) {
            String identifier = modifiers.getString(ToolTags.IDENTIFIER);
            IModifier modifier = TinkersRebornRegistry.getModifier(identifier);
            if (modifier == null) {
                TinkersReborn.LOG.debug("Missing modifier: {}", identifier);
                continue;
            }

            modifier.applyEffect(tinkersTag, modifiers);
        }

        // remaining info, get updated toolTag
        // adjust free modifiers
        int freeModifiers = ToolTagsHelper.getFreeModifiers(tool);
        freeModifiers -= oldModifiersTag.size();
        ToolTagsHelper.setFreeModifiers(tool, freeModifiers);

        // broken?
        ToolTagsHelper.setBroken(tool, broken);

        if (freeModifiers < 0) {
            throw new TinkerGuiException(
                String.format(TinkersRebornUtils.translate("gui.error.not_enough_modifiers"), -freeModifiers));
        }
    }

    /**
     * Adds the trait to the tag, taking max-count and already existing traits into
     * account.
     *
     * @param rootCompound The root compound of the item
     * @param trait        The trait to add.
     * @param color        The color used on the tooltip. Will not be used if the
     *                     trait already exists on the tool.
     */
    public static void addTrait(NBTTagCompound rootCompound, ITrait trait, int color) {
        // only registered traits allowed
        if (TinkersRebornRegistry.getTrait(trait.getIdentifier()) == null) {
            TinkersReborn.LOG.error("addTrait: Trying to apply unregistered Trait {}", trait.getIdentifier());
            return;
        }

        ITrait newTrait = TinkersRebornRegistry.getTrait(trait.getIdentifier());

        if (newTrait == null || !(newTrait instanceof AbstractTrait)) {
            TinkersReborn.LOG.error("addTrait: No matching modifier for the Trait {} present", trait.getIdentifier());
            return;
        }

        AbstractTrait traitModifier = (AbstractTrait) newTrait;

        NBTTagList tagList = ToolTagsHelper.getModifiersTagList(rootCompound);
        ToolTagsHelper.setModifiersTagList(rootCompound, tagList);

        NBTTagCompound tag = new NBTTagCompound();
        traitModifier.updateNBT(tag);
        tagList.appendTag(tag);

        traitModifier.applyEffect(rootCompound, tag);
    }
}
