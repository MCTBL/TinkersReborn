package mctbl.tinkersreborn.common.manuals.pages;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.ItemStack;

import com.google.common.collect.Lists;
import com.google.gson.JsonObject;

import mctbl.tinkersreborn.common.manuals.TinkersRebornNavigationButton;
import mctbl.tinkersreborn.library.TinkersRebornRegistry;
import mctbl.tinkersreborn.library.manuals.AbstractButtonsManualPage;
import mctbl.tinkersreborn.library.manuals.AbstractManualPage;
import mctbl.tinkersreborn.library.manuals.ManualPageDefinition;
import mctbl.tinkersreborn.library.manuals.ManualPageProcessor;
import mctbl.tinkersreborn.library.tools.ToolCore;

public class ToolsNavigationPage extends AbstractButtonsManualPage {

    public ToolsNavigationPage(JsonObject json, List<ToolCore> allTools) {
        super(json);
        for (int idx = 0; idx < allTools.size(); idx++) {
            ToolCore toolcore = allTools.get(idx);
            String toolTypeName = toolcore.toolTypeName;
            ItemStack itemStack = TinkersRebornRegistry
                .getOrRegisterManualIcon(toolTypeName, toolcore.getToolForRender());

            TinkersRebornNavigationButton b = new TinkersRebornNavigationButton(
                idx,
                buttonSize,
                itemStack,
                toolTypeName);
            buttons.add(b);
        }
        this.resizeAndRepostitonButtons();
    }

    public static class ToolsNavigationPageProcessor implements ManualPageProcessor {

        @Override
        public List<AbstractManualPage> process(ManualPageDefinition definition) {
            List<AbstractManualPage> list = new ArrayList<>();
            List<ToolCore> allTools = TinkersRebornRegistry.getAllTools();

            JsonObject json = definition.getData();
            int buttonEachRow = json.has("capacity") ? json.get("capacity")
                .getAsInt() : 7;
            Lists.partition(allTools, buttonEachRow * buttonEachRow)
                .forEach(l -> list.add(new ToolsNavigationPage(json, l)));

            TinkersRebornRegistry.getAllTools()
                .forEach(t -> list.add(new ToolPage(t)));

            return list;
        }
    }
}
