package com.x366.asm;

import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

public class AsmElementType extends IElementType {
    public AsmElementType(@NotNull @NonNls String debugName) {
        super(debugName, AsmLanguage.INSTANCE);
    }
}
