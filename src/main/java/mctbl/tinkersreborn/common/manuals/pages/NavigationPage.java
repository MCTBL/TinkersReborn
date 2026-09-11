package mctbl.tinkersreborn.common.manuals.pages;

import java.util.Arrays;
import java.util.List;

import net.minecraft.item.ItemStack;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import mctbl.tinkersreborn.common.manuals.TinkersRebornNavigationButton;
import mctbl.tinkersreborn.library.TinkersRebornRegistry;
import mctbl.tinkersreborn.library.manuals.AbstractButtonsManualPage;
import mctbl.tinkersreborn.library.manuals.AbstractManualPage;
import mctbl.tinkersreborn.library.manuals.ManualPageDefinition;
import mctbl.tinkersreborn.library.manuals.ManualPageProcessor;

public class NavigationPage extends AbstractButtonsManualPage {

    public NavigationPage(JsonObject json) {
        super(json);
        JsonArray buttonArrays = json.get("buttons")
            .getAsJsonArray();

        for (int idx = 0; idx < buttonArrays.size(); idx++) {
            JsonObject buttonJsonObject = buttonArrays.get(idx)
                .getAsJsonObject();
            String target = buttonJsonObject.has("to") ? buttonJsonObject.get("to")
                .getAsString() : null;
            String iconStr = buttonJsonObject.has("icon") ? buttonJsonObject.get("icon")
                .getAsString() : null;
            String text = buttonJsonObject.has("text") ? buttonJsonObject.get("text")
                .getAsString() : null;
            ItemStack itemStack = TinkersRebornRegistry.getOrRegisterManualIcon(iconStr);

            TinkersRebornNavigationButton b = new TinkersRebornNavigationButton(
                idx,
                buttonSize,
                itemStack,
                text,
                target);

            buttons.add(b);
        }

        this.resizeAndRepostitonButtons();
    }

    public static class NavigationPageProcessor implements ManualPageProcessor {

        @Override
        public List<AbstractManualPage> process(ManualPageDefinition definition) {
            return Arrays.asList(new NavigationPage(definition.getData()));
        }

    }

}
