package com.x366.asm;

import com.intellij.lang.Language;

public class AsmLanguage extends Language {
    public static final AsmLanguage INSTANCE = new AsmLanguage();
    private AsmLanguage() {
      super("x366ASM");
    }
}
