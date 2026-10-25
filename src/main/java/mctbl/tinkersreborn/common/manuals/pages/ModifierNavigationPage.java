package mctbl.tinkersreborn.common.manuals.pages;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import net.minecraft.item.ItemStack;

import com.google.common.collect.Lists;
import com.google.gson.JsonObject;

import mctbl.tinkersreborn.common.manuals.TinkersRebornNavigationButton;
import mctbl.tinkersreborn.library.TinkersRebornRegistry;
import mctbl.tinkersreborn.library.manuals.AbstractButtonsManualPage;
import mctbl.tinkersreborn.library.manuals.AbstractManualPage;
import mctbl.tinkersreborn.library.manuals.ManualPageDefinition;
import mctbl.tinkersreborn.library.manuals.ManualPageProcessor;
import mctbl.tinkersreborn.library.tools.IModifier;
import mctbl.tinkersreborn.library.tools.modifiers.AbstractModifier;
import mctbl.tinkersreborn.library.tools.modifiers.IModifierDisplay;
import mctbl.tinkersreborn.tools.modifiers.ModFortify;

public class ModifierNavigationPage extends AbstractButtonsManualPage {

    public ModifierNavigationPage(JsonObject json, List<IModifierDisplay> modifiers) {
        super(json);

        for (int idx = 0; idx < modifiers.size(); idx++) {
            IModifierDisplay mod = modifiers.get(idx);
            String modifierName = ((AbstractModifier) mod).getLocalizedName();
            ItemStack itemStack = mod.getItems()
                .get(0)
                .get(0);

            TinkersRebornNavigationButton b = new TinkersRebornNavigationButton(
                idx,
                buttonSize,
                new ItemStack[] { itemStack },
                ((AbstractModifier) mod).identifier,
                modifierName,
                mod.getColor());

            buttons.add(b);
        }

        this.resizeAndRepostitonButtons();
    }

    public static class ModifierNavigationPageProcessor implements ManualPageProcessor {

        @Override
        public List<AbstractManualPage> process(ManualPageDefinition definition) {
            List<IModifierDisplay> allModifier = TinkersRebornRegistry.getAllModifier()
                .stream()
                .filter(o -> !(o instanceof ModFortify))
                .filter(IModifier::hasItemsToApplyWith)
                .filter(IModifierDisplay.class::isInstance)
                .map(IModifierDisplay.class::cast)
                .collect(Collectors.toList());

            List<AbstractManualPage> list = new ArrayList<>();
            JsonObject json = definition.getData();
            int buttonEachRow = json.has("capacity") ? json.get("capacity")
                .getAsInt() : AbstractButtonsManualPage.BUTTON_EACH_ROW_DEFAULT;
            Lists.partition(allModifier, buttonEachRow * buttonEachRow)
                .forEach(l -> list.add(new ModifierNavigationPage(json, allModifier)));

            allModifier.forEach(m -> list.add(new ModifierPage(m)));

            return list;
        }

    }
}
