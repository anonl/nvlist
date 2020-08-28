lexer grammar Nvlist4Lexer;

CODE_BLOCK_START: '@@' -> pushMode(CODE_BLOCK);
CODE_LINE_START: '@' -> pushMode(CODE_LINE);
COMMENT_BLOCK_START: '##' -> pushMode(COMMENT_BLOCK);
COMMENT_LINE_START: '#' -> pushMode(COMMENT_LINE);

NEWLINE: ('\r\n' | '\n');
WS: ' \t'+;
ANY: .;

mode CODE_BLOCK;
    CODE_BLOCK_END: '@@' -> popMode;
    CODE_BLOCK_ANY: .;

mode CODE_LINE;
    CODE_LINE_END: ('\r\n' | '\n') -> popMode;
    CODE_LINE_ANY: .;

mode COMMENT_BLOCK;
    COMMENT_BLOCK_END: '##' -> popMode;
    COMMENT_BLOCK_ANY: .;

mode COMMENT_LINE;
    COMMENT_LINE_END: ('\r\n' | '\n') -> popMode;
    COMMENT_LINE_ANY: .;
