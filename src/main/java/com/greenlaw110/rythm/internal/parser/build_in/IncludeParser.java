package com.greenlaw110.rythm.internal.parser.build_in;

import com.greenlaw110.rythm.internal.Keyword;
import com.greenlaw110.rythm.internal.dialect.Rythm;
import com.greenlaw110.rythm.internal.parser.CodeToken;
import com.greenlaw110.rythm.internal.parser.ParserBase;
import com.greenlaw110.rythm.spi.IContext;
import com.greenlaw110.rythm.spi.IParser;
import com.greenlaw110.rythm.utils.S;
import com.greenlaw110.rythm.utils.TextBuilder;
import com.stevesoft.pat.Regex;

public class IncludeParser extends KeywordParserFactory {

    private static final String R = "(^%s(%s\\s*((?@()))\\s*))";

    public IncludeParser() {
    }

    protected String patternStr() {
        return R;
    }

    public IParser create(IContext c) {
        return new ParserBase(c) {
            public TextBuilder go() {
                Regex r = reg(dialect());
                if (!r.search(remain())) {
                    raiseParseException("Error parsing @include statement. Correct usage: @include(\"foo.bar, a.b.c, ...\")");
                }
                step(r.stringMatched().length());
                String s = r.stringMatched(3);
                if (S.isEmpty(s)) {
                    raiseParseException("Error parsing @include statement. Correct usage: @include(\"foo.bar, a.b.c, ...\")");
                }
                s = S.stripBraceAndQuotation(s);
                String code = ctx().getCodeBuilder().addIncludes(s, ctx().currentLine());
                return new CodeToken(code, ctx());
            }
        };
    }

    @Override
    public Keyword keyword() {
        return Keyword.INCLUDE;
    }

    public static void main(String[] args) {
        IncludeParser p = new IncludeParser();
        Regex r = p.reg(new Rythm());
        String s = "@include(\"x.y.z,foo.bar\") \n@sayHi(\"green\")";
        if (r.search(s)) {
            System.out.println(r.stringMatched());
            System.out.println(r.stringMatched(1));
            System.out.println(r.stringMatched(2));
            System.out.println(r.stringMatched(3));
        }
    }
}
