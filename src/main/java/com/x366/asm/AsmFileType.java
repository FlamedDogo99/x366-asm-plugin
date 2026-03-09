package com.x366.asm;

import com.intellij.openapi.fileTypes.LanguageFileType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import javax.swing.*;

public class AsmFileType extends LanguageFileType {
    public static final AsmFileType INSTANCE = new AsmFileType();
    private AsmFileType() { super(AsmLanguage.INSTANCE); }

    @NotNull @Override public String getName() {
      return "x366 ASM";
    }
    @NotNull @Override public String getDescription() {
      return "x366 Assembly file";
    }
    @NotNull @Override public String getDefaultExtension() {
      return "asm";
    }
    @Nullable @Override public Icon getIcon() {
      return null;
    }
}
