package nl.weeaboo.vn.langserver;

import com.google.common.base.Splitter;

final class Markdown {

    static String toMarkdown(String plain) {
        StringBuilder sb = new StringBuilder();
        for (String line : Splitter.on('\n').split(plain)) {
            line = line.trim();
            if (line.startsWith("@")) {
                sb.append("\n\n");
            } else {
                sb.append(' ');
            }
            sb.append(line);
        }
        return sb.toString();
    }

}
