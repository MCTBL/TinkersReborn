package mctbl.tinkersreborn.common.manuals.processor;

import java.util.Arrays;
import java.util.List;

import mctbl.tinkersreborn.common.manuals.pages.NavigationPage;
import mctbl.tinkersreborn.library.manuals.AbstractManualPage;
import mctbl.tinkersreborn.library.manuals.ManualPageDefinition;
import mctbl.tinkersreborn.library.manuals.ManualPageProcessor;

public class NavigationPageProcessor implements ManualPageProcessor {

    @Override
    public List<AbstractManualPage> process(ManualPageDefinition definition) {
        return Arrays.asList(new NavigationPage(definition.getData()));
    }

}
