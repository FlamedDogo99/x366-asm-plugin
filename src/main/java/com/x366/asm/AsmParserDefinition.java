package com.x366.asm;

import com.intellij.lang.ASTNode;
import com.intellij.lang.ParserDefinition;
import com.intellij.lang.PsiParser;
import com.intellij.lexer.Lexer;
import com.intellij.openapi.project.Project;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.impl.source.PsiFileImpl;
import com.intellij.psi.tree.IFileElementType;
import com.intellij.psi.tree.TokenSet;
import org.jetbrains.annotations.NotNull;
import com.intellij.lang.PsiBuilder;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.tree.IElementType;


public class AsmParserDefinition implements ParserDefinition {

    public static final IFileElementType FILE = new IFileElementType(AsmLanguage.INSTANCE);

    @Override
    public @NotNull Lexer createLexer(Project project) {
        return new AsmLexer();
    }

    @Override
    public @NotNull PsiParser createParser(Project project) {
        return (root, builder) -> {
            PsiBuilder.Marker rootMarker = builder.mark();

            while(!builder.eof()) {
                IElementType type = builder.getTokenType();
                if(type == null) {
                    break;
                }

                if(type == AsmTokenTypes.NEWLINE) {
                    PsiBuilder.Marker m = builder.mark();
                    builder.advanceLexer();
                    m.done(AsmTokenTypes.NEWLINE);
                    continue;
                }

                if(type == AsmTokenTypes.SPACE) {
                    builder.advanceLexer();
                    continue;
                }

                PsiBuilder.Marker stmtMarker = builder.mark();
                while(!builder.eof()) {
                    IElementType t = builder.getTokenType();
                    if(t == null || t == AsmTokenTypes.NEWLINE) {
                        break;
                    }
                    if(t == AsmTokenTypes.SPACE) {
                        builder.advanceLexer();
                        continue;
                    }
                    PsiBuilder.Marker tokenMarker = builder.mark();
                    builder.advanceLexer();
                    tokenMarker.done(t);
                }
                stmtMarker.done(AsmTokenTypes.STATEMENT);
            }

            rootMarker.done(root);
            return builder.getTreeBuilt();
        };
    }

    @Override
    public @NotNull IFileElementType getFileNodeType() {
        return FILE;
    }

    @Override
    public @NotNull TokenSet getWhitespaceTokens() {
        // space and newline handled by the parser. This stops PsiBuilder from silently skipping
        return TokenSet.EMPTY;
    }

    @Override
    public @NotNull TokenSet getCommentTokens() {
        return TokenSet.create(AsmTokenTypes.COMMENT);
    }

    @Override
    public @NotNull TokenSet getStringLiteralElements() {
        return TokenSet.create(AsmTokenTypes.STRING);
    }

    @Override
    public @NotNull PsiElement createElement(ASTNode node) {
        return new AsmPsiElement(node);
    }

    @Override
    public @NotNull PsiFile createFile(@NotNull FileViewProvider viewProvider) {
        return new PsiFileImpl(viewProvider) {
            {
                init(FILE, FILE);
            }

            @Override
            public @NotNull FileType getFileType() {
                return AsmFileType.INSTANCE;
            }

            @Override
            public void accept(@NotNull PsiElementVisitor visitor) {
                visitor.visitFile(this);
            }
        };
    }
}