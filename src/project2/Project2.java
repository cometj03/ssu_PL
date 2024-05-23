package project2;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

enum Token {
    IDENT,          // id (식별자)
    INT_LIT,        // integer literal

    // keyword
    TYPE_INT,       // int
    PRINT,          // print
    DO,             // do
    WHILE,          // while

    // 연산자 & 특수기호
    ASSIGN_OP,      // =
    ADD_OP,         // +
    SUB_OP,         // -
    MULT_OP,        // *
    DIV_OP,         // /
    REL_EQ,         // ==
    REL_NEQ,        // !=
    REL_LT,         // <
    REL_GT,         // >
    REL_LE,         // <=
    REL_GE,         // >=
    SEMICOLON,      // ;
    LEFT_PAREN,     // (
    RIGHT_PAREN,    // )
    LEFT_BRACE,     // {
    RIGHT_BRACE,    // }

    EOF,
    UNKNOWN
}

public class Project2 {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        while (true) {
            System.out.print(">> ");
            String input = sc.nextLine();
            if (input.equals("terminate")) break;

            RDParser parser = new RDParser(input);
            try {
                // 파싱을 먼저 시도한 후 실행합니다.
                parser.parse();
                parser.execute();
            } catch (SyntaxException e) {
                System.out.println(e.getMessage());
            }
        }
    }
}

class SyntaxException extends Exception {
    SyntaxException() {
        super("Syntax Error!!");
    }
}

class LexicalAnalyzer {
    private final String input;
    public Token nextToken;
    public String lexeme = "";
    private char nextChar;
    private int curIdx = 0;

    LexicalAnalyzer(String input) {
        this.input = input;
        init();
    }

    public void init() {
        curIdx = 0;
        getChar();
        if (input.isEmpty()) nextToken = Token.EOF;
        else nextToken = null;
    }

    public int getCheckPoint() {
        return curIdx;
    }

    public void setCheckPoint(int curIdx) {
        this.curIdx = curIdx;
        this.nextChar = input.charAt(curIdx - 1);
    }

    public void lex() {
        skipBlank();
        if (curIdx >= input.length() && nextChar == 0) {
            nextToken = Token.EOF;
            return;
        }

        int startIdx = curIdx - 1, endIdx = 0;

        if (isAlpha(nextChar)) {
            while (isAlpha(nextChar) || isDigit(nextChar)) {
                endIdx = curIdx;
                getChar();
            }
            lexeme = input.substring(startIdx, endIdx);
            switch (lexeme) {
                case "print" -> nextToken = Token.PRINT;
                case "do" -> nextToken = Token.DO;
                case "while" -> nextToken = Token.WHILE;
                case "int" -> nextToken = Token.TYPE_INT;
                default -> nextToken = Token.IDENT;
            }
            return;
        }

        if (isDigit(nextChar)) {
            while (isDigit(nextChar)) {
                endIdx = curIdx;
                getChar();
            }
            lexeme = input.substring(startIdx, endIdx);
            nextToken = Token.INT_LIT;
            return;
        }

        switch (nextChar) {
            case '+' -> {
                nextToken = Token.ADD_OP;
                getChar();
            }
            case '-' -> {
                nextToken = Token.SUB_OP;
                getChar();
            }
            case '*' -> {
                nextToken = Token.MULT_OP;
                getChar();
            }
            case '/' -> {
                nextToken = Token.DIV_OP;
                getChar();
            }
            case ';' -> {
                nextToken = Token.SEMICOLON;
                getChar();
            }
            case '(' -> {
                nextToken = Token.LEFT_PAREN;
                getChar();
            }
            case ')' -> {
                nextToken = Token.RIGHT_PAREN;
                getChar();
            }
            case '{' -> {
                nextToken = Token.LEFT_BRACE;
                getChar();
            }
            case '}' -> {
                nextToken = Token.RIGHT_BRACE;
                getChar();
            }
            case '=' -> {
                getChar();
                if (nextChar == '=') {
                    nextToken = Token.REL_EQ;
                    getChar();
                } else {
                    nextToken = Token.ASSIGN_OP;
                }
            }
            case '!' -> {
                getChar();
                if (nextChar == '=') {
                    nextToken = Token.REL_NEQ;
                    getChar();
                } else {
                    nextToken = Token.UNKNOWN;
                }
            }
            case '<' -> {
                getChar();
                if (nextChar == '=') {
                    nextToken = Token.REL_LE;
                    getChar();
                } else {
                    nextToken = Token.REL_LT;
                }
            }
            case '>' -> {
                getChar();
                if (nextChar == '=') {
                    nextToken = Token.REL_GE;
                    getChar();
                } else {
                    nextToken = Token.REL_GT;
                }
            }
            default -> {
                nextToken = Token.UNKNOWN;
                getChar();
            }
        }
    }

    private void getChar() {
        if (curIdx >= input.length()) {
            nextChar = 0;
            return;
        }
        nextChar = input.charAt(curIdx++);
    }

    private void skipBlank() {
        while (isSpace(nextChar))
            getChar();
    }

    private boolean isSpace(char c) {
        return c == ' ' || c == '\t' || c == '\n';
    }

    private boolean isAlpha(char c) {
        return 'a' <= c && c <= 'z';
    }

    private boolean isDigit(char c) {
        return '0' <= c && c <= '9';
    }
}

class RDParser {
    private final LexicalAnalyzer la;
    private final Map<String, Integer> VARMAP = new HashMap<>();
    private boolean exe = false;

    RDParser(String input) {
        la = new LexicalAnalyzer(input);
    }

    // 입력을 재귀적으로 파싱하는 함수
    // print, do-while 문 실행하지 않음
    // 파싱 중 오류가 발생하면 SyntaxException을 throw 한다.
    public void parse() throws SyntaxException {
        exe = false;
        VARMAP.clear();
        la.init();
        program();
    }

    // 입력을 파싱하면서 print, do-while 문을 실행시키는 함수
    public void execute() throws SyntaxException {
        exe = true;
        VARMAP.clear();
        la.init();
        program();
        System.out.println();
    }

    void program() throws SyntaxException {
        la.lex();
        while (la.nextToken != Token.EOF && la.nextToken == Token.TYPE_INT) {
            declaration();
        }
        while (la.nextToken != Token.EOF) {
            statement();
        }
    }

    void declaration() throws SyntaxException {
        type();
        String id = var();
        VARMAP.put(id, 0);
        if (la.nextToken != Token.SEMICOLON) throw new SyntaxException();
        la.lex();
    }

    void statement() throws SyntaxException {
        switch (la.nextToken) {
            case IDENT -> {
                if (!VARMAP.containsKey(la.lexeme)) throw new SyntaxException();
                String l_value = var();
                if (la.nextToken != Token.ASSIGN_OP) throw new SyntaxException();
                la.lex();
                int r_value = aexpr();
                VARMAP.put(l_value, r_value);
            }
            case PRINT -> {
                la.lex();
                switch (la.nextToken) {
                    case REL_EQ, REL_NEQ, REL_GE, REL_GT, REL_LE, REL_LT -> {
                        boolean result = bexpr();
                        if (exe) System.out.print((result ? "TRUE" : "FALSE") + " ");
                    }
                    default -> {
                        int result = aexpr();
                        if (exe) System.out.print(result + " ");
                    }
                }
            }
            case DO -> {
                la.lex();
                if (la.nextToken != Token.LEFT_BRACE) throw new SyntaxException();

                // 블록의 시작이 되는 인덱스를 임시로 저장합니다.
                int checkpoint = la.getCheckPoint();
                boolean b;
                do {
                    la.lex();
                    while (la.nextToken != Token.RIGHT_BRACE)
                        statement();
                    la.lex();
                    if (la.nextToken != Token.WHILE) throw new SyntaxException();
                    la.lex();
                    if (la.nextToken != Token.LEFT_PAREN) throw new SyntaxException();
                    la.lex();
                    b = bexpr() && exe;
                    if (la.nextToken != Token.RIGHT_PAREN) throw new SyntaxException();
                    if (b) {
                        // bexpr의 값이 true이면 블록의 시작으로 이동합니다.
                        la.setCheckPoint(checkpoint);
                    }
                } while (b);
                la.lex();
            }
            default -> throw new SyntaxException();
        }
        if (la.nextToken != Token.SEMICOLON) throw new SyntaxException();
        la.lex();
    }

    boolean bexpr() throws SyntaxException {
        Token rel = la.nextToken;
        relop();
        int a = aexpr();
        int b = aexpr();

        return switch (rel) {
            case REL_EQ -> a == b;
            case REL_NEQ -> a != b;
            case REL_GE -> a >= b;
            case REL_GT -> a > b;
            case REL_LE -> a <= b;
            case REL_LT -> a < b;
            default -> throw new SyntaxException();
        };
    }

    void relop() throws SyntaxException {
        switch (la.nextToken) {
            case REL_EQ, REL_NEQ, REL_GE, REL_GT, REL_LE, REL_LT -> { /* do nothing */}
            default -> throw new SyntaxException();
        }
        la.lex();
    }

    int aexpr() throws SyntaxException {
        int num = term();

        Token op = la.nextToken;
        while (op == Token.ADD_OP || op == Token.SUB_OP || op == Token.MULT_OP || op == Token.DIV_OP) {
            la.lex();
            int tmp = term();
            switch (op) {
                case ADD_OP -> num += tmp;
                case SUB_OP -> num -= tmp;
                case MULT_OP -> num *= tmp;
                case DIV_OP -> num /= tmp;
            }
            op = la.nextToken;
        }
        return num;
    }

    int term() throws SyntaxException {
        return switch (la.nextToken) {
            case INT_LIT -> number();
            case IDENT -> {
                if (!VARMAP.containsKey(la.lexeme)) throw new SyntaxException();
                int num = VARMAP.get(la.lexeme);
                la.lex();
                yield num;
            }
            case LEFT_PAREN -> {
                la.lex();
                int num = aexpr();
                if (la.nextToken != Token.RIGHT_PAREN) throw new SyntaxException();
                la.lex();
                yield num;
            }
            default -> throw new SyntaxException();
        };
    }

    void type() throws SyntaxException {
        if (la.nextToken != Token.TYPE_INT) throw new SyntaxException();
        la.lex();
    }

    int number() throws SyntaxException {
        if (la.nextToken != Token.INT_LIT || la.lexeme.length() > 10)
            throw new SyntaxException();
        int num = Integer.parseInt(la.lexeme);
        la.lex();
        return num;
    }

    String var() throws SyntaxException {
        if (la.nextToken != Token.IDENT || la.lexeme.length() > 10)
            throw new SyntaxException();
        String id = la.lexeme;
        la.lex();
        return id;
    }
}

