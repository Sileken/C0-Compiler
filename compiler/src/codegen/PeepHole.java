package codegen;

import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
public class PeepHole {
    String replacement;
    List<String>  pattern;

    public PeepHole(List<String> pattern, String replacement) throws PatternSyntaxException {
        this.pattern = pattern;
        this.replacement = replacement;
    }

    public List<String> getPattern() {
        return this.pattern;
    }

    public String getReplacement() {
        return this.replacement;
    }
}