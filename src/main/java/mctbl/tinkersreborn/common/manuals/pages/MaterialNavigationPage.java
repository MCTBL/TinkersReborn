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
import mctbl.tinkersreborn.library.materials.MaterialStatusType;
import mctbl.tinkersreborn.library.materials.TinkersRebornMaterial;

public class MaterialNavigationPage extends AbstractButtonsManualPage {

    public MaterialNavigationPage(JsonObject json, List<TinkersRebornMaterial> materials) {
        super(json);

        for (int idx = 0; idx < materials.size(); idx++) {
            TinkersRebornMaterial material = materials.get(idx);
            String materialName = material.localizedName();
            ItemStack itemStack = material.getRepresentativeItem();

            TinkersRebornNavigationButton b = new TinkersRebornNavigationButton(
                idx,
                buttonSize,
                new ItemStack[] { itemStack },
                material.identifier,
                materialName,
                material.materialTextColor);

            buttons.add(b);
        }

        this.resizeAndRepostitonButtons();
    }

    public static class MaterialNavigationPageProcessor implements ManualPageProcessor {

        @Override
        public List<AbstractManualPage> process(ManualPageDefinition definition) {
            List<TinkersRebornMaterial> allMaterials = TinkersRebornRegistry.getAllMaterialList()
                .stream()
                .filter(m -> m.hasStats(MaterialStatusType.HEAD))
                .collect(Collectors.toList());

            List<AbstractManualPage> list = new ArrayList<>();
            JsonObject json = definition.getData();
            int buttonEachRow = json.has("capacity") ? json.get("capacity")
                .getAsInt() : 7;
            Lists.partition(allMaterials, buttonEachRow * buttonEachRow)
                .forEach(l -> list.add(new MaterialNavigationPage(json, allMaterials)));
            allMaterials.forEach(m -> list.add(new MaterialPage(m)));

            return list;
        }
    }

}
