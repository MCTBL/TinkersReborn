package mctbl.tinkersreborn.library.crafting;

import static mctbl.tinkersreborn.util.TinkersRebornUtils.isStackEmpty;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;

import com.google.common.collect.Sets;

import mctbl.tinkersreborn.TinkersReborn;
import mctbl.tinkersreborn.TinkersRebornConfig;
import mctbl.tinkersreborn.library.TinkerGuiException;
import mctbl.tinkersreborn.library.TinkersRebornRegistry;
import mctbl.tinkersreborn.library.event.TinkersRebornEvent;
import mctbl.tinkersreborn.library.materials.MaterialStatusType;
import mctbl.tinkersreborn.library.materials.TinkersRebornMaterial;
import mctbl.tinkersreborn.library.tools.IModifier;
import mctbl.tinkersreborn.library.tools.IRepairable;
import mctbl.tinkersreborn.library.tools.IToolPart;
import mctbl.tinkersreborn.library.tools.ITrait;
import mctbl.tinkersreborn.library.tools.ToolCore;
import mctbl.tinkersreborn.library.tools.ToolCore.MaterialReplacement;
import mctbl.tinkersreborn.library.tools.ToolCore.ToolPartRecord;
import mctbl.tinkersreborn.library.tools.ToolNBT;
import mctbl.tinkersreborn.library.tools.leveling.ToolLevelingHelper;
import mctbl.tinkersreborn.library.tools.traits.AbstractTrait;
import mctbl.tinkersreborn.library.utils.RecipeMatch;
import mctbl.tinkersreborn.tools.items.BoltCore;
import mctbl.tinkersreborn.tools.items.TinkersRebornToolPart;
import mctbl.tinkersreborn.tools.items.tools.Hammer;
import mctbl.tinkersreborn.tools.items.tools.Pickaxe;
import mctbl.tinkersreborn.tools.materials.HeadMaterialStats;
import mctbl.tinkersreborn.tools.modifiers.ModFortify;
import mctbl.tinkersreborn.util.TinkersRebornUtils;
import mctbl.tinkersreborn.util.TinkersStr;
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
        List<ItemStack> inputToolPartList = Arrays.asList(parts)
            .stream()
            .filter(i -> i != null)
            .collect(Collectors.toList());
        ToolCore core = findMatchingToolCore(inputToolPartList, possibleTools);
        if (core == null)
            // build fail because can't find match recipce
            return null;
        List<TinkersRebornMaterial> materials = new ArrayList<>();
        for (ItemStack stack : inputToolPartList) {
            if (stack.getItem() instanceof TinkersRebornToolPart trtp) {
                TinkersRebornMaterial materialByIdentifier = trtp.getMaterial(stack);
                if (materialByIdentifier != TinkersRebornMaterial.UNKNOWN) materials.add(materialByIdentifier);
                if (trtp instanceof BoltCore bolt) materials.add(bolt.getHeadMaterial(stack));
            }
        }

        ItemStack newTool = new ItemStack(core);
        newTool.setTagCompound(core.buildItemNBT(materials));
        return newTool;
    }

    private static ToolCore findMatchingToolCore(List<ItemStack> parts, Collection<ToolCore> possibleTools) {
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
    @Nullable
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

                    String error = String.format(
                        TinkersStr.errorNoModifierForItem.toString(),
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
                ItemStack original = input.get(i);
                if (isStackEmpty(original)) {
                    continue;
                }
                ItemStack used = usedStacks.get(i);
                if (!isStackEmpty(used)) {
                    original.stackSize = used.stackSize;
                } else {
                    original.stackSize = 0;
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
        if (!TinkersRebornEvent.OnToolPartReplacement.fireEvent(inputItems, toolStack)) {
            // event cancelled
            return null;
        }
        // technically we don't need a deep copy here, but meh. less code.
        final List<ItemStack> toolParts = TinkersRebornUtils.copyItemStackList(inputItems);

        Map<Integer, MaterialReplacement> assigned = new LinkedHashMap<>();
        Set<Integer> assignedComponents = new HashSet<>();
        Set<Integer> assignedMaterialSlots = new HashSet<>();
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

            MaterialReplacement candidate = null;
            for (MaterialReplacement replacement : tool.getMaterialReplacements(part)) {
                if (assignedComponents.contains(replacement.componentIndex())
                    || !canUseReplacement(replacement, materialList, assignedMaterialSlots)) {
                    continue;
                }

                candidate = replacement;
                // Preserve the old selection behaviour for tools with duplicate parts: an
                // input in an earlier station slot prefers the matching earlier component.
                if (i <= replacement.componentIndex()) {
                    break;
                }
            }

            // if this part is a head type, capture its material for later fortify
            // comparison
            if (candidate != null) {
                if (candidate.headMaterial() != null) {
                    newHeadMaterial = candidate.headMaterial();
                }
                assignedComponents.add(candidate.componentIndex());
                for (int j = 0; j < candidate.size(); j++) {
                    assignedMaterialSlots.add(candidate.materialIndex(j));
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
        for (Map.Entry<Integer, MaterialReplacement> entry : assigned.entrySet()) {
            int inputIndex = entry.getKey();
            MaterialReplacement replacement = entry.getValue();
            for (int j = 0; j < replacement.size(); j++) {
                materialList
                    .func_150304_a(replacement.materialIndex(j), new NBTTagString(replacement.material(j).identifier));
            }
            if (removeItems) {
                if (inputIndex < toolPartsIn.size() && !isStackEmpty(toolPartsIn.get(inputIndex))) {
                    toolPartsIn.get(inputIndex).stackSize -= 1;
                }
            }
        }

        // check that each material is still compatible with each modifier
        // ToolCore tinkersItem = (ToolCore) toolStack.getItem();
        // ItemStack copyToCheck = tinkersItem.buildItem(ToolTagsHelper.fromTagToMaterial(materialList));

        // this includes traits
        NBTTagList modifiers = ToolTagsHelper.getModifiersTagList(toolStack);

        final NBTTagList modifierList = (NBTTagList) modifiers.copy();
        for (int i = 0; i < modifierList.tagCount(); i++) {
            String id = modifierList.getStringTagAt(i);
            IModifier mod = TinkersRebornRegistry.getModifierAndTrait(id);
            // if the new head's harvest level equals/exceeds the fortification level, it's
            // no longer beneficial. good riddance!
            if (newHeadMaterial != null && mod instanceof ModFortify fortiry) {
                HeadMaterialStats newHeadStats = newHeadMaterial.getStats(MaterialStatusType.HEAD);
                HeadMaterialStats fortifyStats = fortiry.material.getStats(MaterialStatusType.HEAD);
                if (newHeadStats != null && fortifyStats != null
                    && newHeadStats.getHarvestLevel() >= fortifyStats.getHarvestLevel()) {
                    modifierList.removeTag(i);
                }
            }
        }

        ItemStack output = toolStack.copy();
        ToolTagsHelper.setToolBaseMaterialsNBTSafe(output, materialList);
        ToolTagsHelper.setToolRenderMaterialsNBTSafe(output, (NBTTagList) materialList.copy());
        ToolTagsHelper.setModifiersTagList(output, modifierList);
        rebuildTool(output);

        // check if the output has enough durability. we only allow it if the result
        // would not be broken
        if (output.getItemDamage() > output.getMaxDamage()) {
            String error = String
                .format(TinkersStr.errorNotEnoughDurability.toString(), output.getItemDamage() - output.getMaxDamage());
            throw new TinkerGuiException(error);
        }

        return output;
    }

    private static boolean canUseReplacement(MaterialReplacement replacement, NBTTagList materialList,
        Set<Integer> assignedMaterialSlots) {
        boolean changesMaterial = false;
        for (int i = 0; i < replacement.size(); i++) {
            int materialIndex = replacement.materialIndex(i);
            if (materialIndex < 0 || materialIndex >= materialList.tagCount()
                || assignedMaterialSlots.contains(materialIndex)) {
                return false;
            }
            if (!replacement.material(i).identifier.equals(materialList.getStringTagAt(materialIndex))) {
                changesMaterial = true;
            }
        }
        return changesMaterial;
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
        List<ToolPartRecord> pms = tinkersItem.getToolMaterialParts();

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

        // save UsedModifiers before Stats gets overwritten
        int oldUsedModifiers = ToolTagsHelper.getUsedModifiers(tool);
        int oldModifiersSlots = ToolTagsHelper.getModifierSlots(tool);

        // the base stats of the tool
        ToolNBT toolNbt = tinkersItem.buildToolTag(materials);
        // remaining info, restore UsedModifiers (ModifierSlots is the cap, unchanged by modifiers)
        toolNbt.modifierSlots = oldModifiersSlots;
        toolNbt.usedModifiers = oldUsedModifiers;

        NBTTagCompound toolTag = toolNbt.get();
        tinkersTag.setTag(ToolTags.TOOLDATA, toolTag);
        // and its copy for reference
        tinkersTag.setTag(ToolTags.TOOLDATAORIG, toolTag.copy());

        // save the old modifiers list and clean up all tags that get set by
        // save old modifiers and traits
        List<NBTTagCompound> modifiersAndTraitTagOld = ToolTagsHelper.getModifiersList(tool);

        // clear old
        tinkersTag.removeTag(ToolTags.MODIFIERS); // the active-modifiers tag
        tinkersTag.setTag(ToolTags.MODIFIERS, new NBTTagList());
        tinkersTag.removeTag(ToolTags.ENCHANT_EFFECT); // enchant effect too, will be readded by a trait either way

        tool.getTagCompound()
            .removeTag("ench"); // and the enchantments tag

        // readd traits
        NBTTagCompound baseTag = ToolTagsHelper.getTagSafe(tool);
        tinkersItem.addMaterialTraits(baseTag, materials);

        // reset harvest level
        if (TinkersRebornConfig.toolLevelingEnable && TinkersRebornConfig.pickaxeBoostRequired
            && (tinkersItem instanceof Pickaxe || tinkersItem instanceof Hammer)) {
            ToolLevelingHelper.resetNewToolHarvestLevelStat(baseTag);
        }

        // fire event
        TinkersRebornEvent.OnItemBuilding.fireEvent(tinkersTag, materials, tinkersItem);

        // reapply modifiers
        List<NBTTagCompound> oldModifiersTag = modifiersAndTraitTagOld.stream()
            .filter(
                c -> c.getString(ToolTags.TYPE)
                    .equals(ToolTags.TYPEMODIFIERS))
            .collect(Collectors.toList());

        // copy over and reapply all relevant modifiers
        for (NBTTagCompound modifiers : oldModifiersTag) {
            String identifier = modifiers.getString(ToolTags.IDENTIFIER);
            IModifier modifier = TinkersRebornRegistry.getModifierAndTrait(identifier);
            if (modifier == null) {
                continue;
            }
            ToolTagsHelper.getModifiersTagList(baseTag)
                .appendTag(modifiers);

            modifier.applyEffect(baseTag, modifiers);

        }

        // broken?
        ToolTagsHelper.setBroken(tool, broken);

        // validate: cap must be >= used
        int extraModifier = ToolTagsHelper.getExtraModifier(tool);
        int modifierSlots = ToolTagsHelper.getModifierSlots(tool);
        int usedModifiers = ToolTagsHelper.getUsedModifiers(tool);
        if (modifierSlots + extraModifier < usedModifiers) {
            throw new TinkerGuiException(
                String.format(
                    TinkersStr.errorNotEnoughModifier.toString(),
                    usedModifiers - (modifierSlots + extraModifier)));
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
        if (TinkersRebornRegistry.getModifierAndTrait(trait.getIdentifier()) == null) {
            TinkersReborn.LOG.error("addTrait: Trying to apply unregistered Trait {}", trait.getIdentifier());
            return;
        }

        IModifier newTrait = TinkersRebornRegistry.getModifierAndTrait(trait.getIdentifier());

        if (!(newTrait instanceof AbstractTrait)) {
            TinkersReborn.LOG.error("addTrait: No matching modifier for the Trait {} present", trait.getIdentifier());
            return;
        }

        AbstractTrait traitModifier = (AbstractTrait) newTrait;

        NBTTagList tagList = ToolTagsHelper.getModifiersTagList(rootCompound);
        ToolTagsHelper.setModifiersTagList(rootCompound, tagList);

        NBTTagCompound traitTag = ToolTagsHelper.getModifierTag(rootCompound, traitModifier.getModifierIdentifier());
        if (traitTag.hasNoTags()) {
            traitTag = new NBTTagCompound();
            traitModifier.updateNBT(traitTag);
            tagList.appendTag(traitTag);
        }

        traitModifier.applyEffect(rootCompound, traitTag);
    }

    public static short getEnchantmentLevel(NBTTagCompound rootTag, Enchantment enchantment) {
        NBTTagList enchantments = rootTag.getTagList("ench", 10);

        int id = enchantment.effectId;

        for (int i = 0; i < enchantments.tagCount(); i++) {
            if (enchantments.getCompoundTagAt(i)
                .getShort("id") == id) {
                return enchantments.getCompoundTagAt(i)
                    .getShort("lvl");
            }
        }

        return 0;
    }

    public static void addEnchantment(NBTTagCompound rootTag, Enchantment enchantment) {
        NBTTagList enchantments = rootTag.getTagList("ench", 10);

        NBTTagCompound enchTag = new NBTTagCompound();
        int enchId = enchantment.effectId;

        int id = -1;
        for (int i = 0; i < enchantments.tagCount(); i++) {
            if (enchantments.getCompoundTagAt(i)
                .getShort("id") == enchId) {
                enchTag = enchantments.getCompoundTagAt(i);
                id = i;
                break;
            }
        }

        int level = enchTag.getShort("lvl") + 1;
        level = Math.min(level, enchantment.getMaxLevel());
        enchTag.setShort("id", (short) enchId);
        enchTag.setShort("lvl", (short) level);

        if (id < 0) {
            enchantments.appendTag(enchTag);
        } else {
            enchantments.func_150304_a(id, enchTag);
        }

        rootTag.setTag("ench", enchantments);
    }
}
