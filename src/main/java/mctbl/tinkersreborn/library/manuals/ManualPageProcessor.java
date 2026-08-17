package mctbl.tinkersreborn.library.manuals;

import java.util.List;

public interface ManualPageProcessor {

    public List<AbstractManualPage> process(ManualPageDefinition definition);

}
