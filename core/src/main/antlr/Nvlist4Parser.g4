parser grammar Nvlist4Parser;

options {
    tokenVocab = Nvlist4Lexer;
}

file: chunk*;

chunk: codeBlock | codeLine | commentBlock | commentLine | text;

codeBlock:
    CODE_BLOCK_START
    ~CODE_BLOCK_END*?
    (CODE_BLOCK_END | EOF);

codeLine:
    CODE_LINE_START
    ~CODE_LINE_END*?
    CODE_LINE_END;

commentBlock:
    COMMENT_BLOCK_START
    ~CODE_BLOCK_END*?
    (COMMENT_BLOCK_END | EOF);

commentLine:
    COMMENT_LINE_START
    ~COMMENT_LINE_END*?
    COMMENT_LINE_END;

text: ~NEWLINE*? NEWLINE;
