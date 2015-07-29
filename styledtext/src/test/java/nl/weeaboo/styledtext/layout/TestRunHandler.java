package nl.weeaboo.styledtext.layout;

import java.util.ArrayList;
import java.util.List;

import nl.weeaboo.styledtext.layout.RunSplitter.RunState;

final class TestRunHandler implements RunSplitter.RunHandler {

    private final List<CharSequence> chunks = new ArrayList<CharSequence>();

    @Override
    public void processRun(CharSequence stext, RunState rs) {
        chunks.add(stext);
    }

    public List<String> getStrings() {
        List<String> strings = new ArrayList<String>();
        for (CharSequence stext : chunks) {
            strings.add(stext.toString());
        }
        return strings;
    }

}
