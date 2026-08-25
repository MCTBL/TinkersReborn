package mctbl.tinkersreborn.common.manuals.pages;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.google.gson.JsonObject;

import mctbl.tinkersreborn.common.manuals.TinkersRebornNavigationButton;
import mctbl.tinkersreborn.common.manuals.TinkersRebornNavigationButton.ButtonSize;
import mctbl.tinkersreborn.library.TinkersRebornRegistry;
import mctbl.tinkersreborn.library.gui.GuiManual;
import mctbl.tinkersreborn.library.manuals.AbstractManualPage;
import mctbl.tinkersreborn.library.manuals.ManualPageDefinition;
import mctbl.tinkersreborn.library.manuals.ManualPageProcessor;
import mctbl.tinkersreborn.library.tools.IModifier;
import mctbl.tinkersreborn.library.tools.modifiers.AbstractModifier;
import mctbl.tinkersreborn.library.tools.modifiers.IModifierDisplay;
import mctbl.tinkersreborn.tools.modifiers.ModFortify;
import mctbl.tinkersreborn.util.ColorUtil;
import mctbl.tinkersreborn.util.TinkersRebornUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;

public class ModifierNavigationPage extends AbstractManualPage {
    
    protected final List<TinkersRebornNavigationButton> buttons = new ArrayList<>();

    protected final String title;
    protected String translatedTitle;

    protected int buttonEachRow;
    protected ButtonSize buttonSize;

    public ModifierNavigationPage(JsonObject json, List<IModifierDisplay> modifiers, boolean isEnd) {
	super(json);
        String buttonSizeStr = json.has("size") ? json.get("size")
                .getAsString() : "medium";
            this.buttonEachRow = json.has("capacity") ? json.get("capacity")
                .getAsInt() : 7;
            this.buttonSize = ButtonSize.getSize(buttonSizeStr);
            this.title = json.has("title") ? json.get("title")
                .getAsString() : "";
            
            int middleX = contentWidth / 2;
            int middleY = contentHeight / 2;
            int buttonGap = 5;
            int buttonWidth = (int) (TinkersRebornNavigationButton.defaultWidth * buttonSize.getMulti());
            int buttonHeight = (int) (TinkersRebornNavigationButton.defaultHeight * buttonSize.getMulti());
            int buttonRows = TinkersRebornUtils.ceilDiv(modifiers.size() + (isEnd? 1 :0), this.buttonEachRow);

            int buttonsGroupHeight = buttonRows * buttonHeight + (buttonRows - 1) * buttonGap;
            int buttonsGroupWidth = this.buttonEachRow * buttonWidth + (this.buttonEachRow - 1) * buttonGap;

            int buttonsGroupStartX = middleX - buttonsGroupWidth / 2;
            int buttonsGroupStartY = middleY - buttonsGroupHeight / 2;
            
            for (int idx = 0; idx < modifiers.size(); idx++) {
        	IModifierDisplay mod = modifiers.get(idx);
        	
                String modifierName = ((AbstractModifier)mod).getLocalizedName();
                ItemStack itemStack = mod.getItems().get(0).get(0);

                TinkersRebornNavigationButton b = new TinkersRebornNavigationButton(
                    idx,
                    buttonSize,
                    new ItemStack[] { itemStack },
                    ((AbstractModifier)mod).identifier,
                    modifierName,
                    mod.getColor());

                int row = idx / this.buttonEachRow;
                int column = idx % this.buttonEachRow;

                b.xPosition = buttonsGroupStartX + column * (buttonWidth + buttonGap);
                b.yPosition = buttonsGroupStartY + row * (buttonHeight + buttonGap);

                this.buttons.add(b);
            }
            
            // special for fortify
            if(isEnd) {
        	List<IModifierDisplay> fortifies = TinkersRebornRegistry.getAllModifier().stream().filter(ModFortify.class::isInstance).map(IModifierDisplay.class::cast).collect(Collectors.toList());
        	String modifierName = ModFortify.getModifierName();
                TinkersRebornNavigationButton b = new TinkersRebornNavigationButton(
                	this.buttons.size(),
                        buttonSize,
                        fortifies.stream().map(IModifierDisplay::getItems).map(l -> l.get(0).get(0)).collect(Collectors.toList()).toArray(new ItemStack[0]),
                        "fortify",
                        modifierName, 0xFFFFFF);
                int row = this.buttons.size() / this.buttonEachRow;
                int column = this.buttons.size() % this.buttonEachRow;

                b.xPosition = buttonsGroupStartX + column * (buttonWidth + buttonGap);
                b.yPosition = buttonsGroupStartY + row * (buttonHeight + buttonGap);

                this.buttons.add(b);
            }
    }
    
    @Override
    public void renderContentLayer(int pageX, int pageY, int manualMouseX, int manualMouseY, float partialTicks,
	    int manualTicks, GuiManual manual) {
        if (this.translatedTitle != null && !this.translatedTitle.isEmpty())
            this.drawStrCenterAt(ColorUtil.addUnderLine(translatedTitle), pageX + contentWidth / 2, pageY);

        this.buttons.forEach(
            b -> b.drawButton(Minecraft.getMinecraft(), manualMouseX, manualMouseY, manualTicks, pageX, pageY));
    }
    
    @Override
    public void drawToolTips(int mouseX, int mouseY, int manualX, int manualY, GuiManual manual) {
        super.drawToolTips(mouseX, mouseY, manualX, manualY, manual);
        this.buttons.forEach(b -> {
            if (b.contains(manualX, manualY)) {
                manual.drawHoveringText(b.getTooltips(), mouseX, mouseY, fontRender);
            }
        });
    }
    
    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton, GuiManual manual) {
        this.buttons.forEach(b -> {
            if (b.contains(mouseX, mouseY)) {
                manual.tryToJumpToPage(b.target);
            }
        });
        super.mouseClicked(mouseX, mouseY, mouseButton, manual);
    }

    @Override
    public void setupTranslate() {
        this.translatedTitle = TinkersRebornUtils.translate(this.title);
    }

    public static class ModifierNavigationPageProcessor implements ManualPageProcessor {

	@Override
	public List<AbstractManualPage> process(ManualPageDefinition definition) {
            List<AbstractManualPage> list = new ArrayList<>();
            List<IModifierDisplay> allModifier = TinkersRebornRegistry.getAllModifier().stream().filter(o -> !(o instanceof ModFortify)).filter(IModifier::hasItemsToApplyWith).filter(IModifierDisplay.class::isInstance).map(IModifierDisplay.class::cast).collect(Collectors.toList());
            list.add(new ModifierNavigationPage(definition.getData().getAsJsonObject(), allModifier, true));
            
            allModifier.forEach(m->list.add(new ModifierPage(m)));
            list.add(new ModifierFortifyPage());
	    return list;
	}
	
    }
}
