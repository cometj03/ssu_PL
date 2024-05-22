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
    }

    public int getCurIdx() {
        return curIdx;
    }

    public void setCurIdx(int curIdx) {
        this.curIdx = curIdx;
    }

    public char getNextChar() {
        return nextChar;
    }

    public void setNextChar(char nextChar) {
        this.nextChar = nextChar;
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
    private final Map<String, String> VARMAP = new HashMap<>();
    private boolean exe = false;

    RDParser(String input) {
        la = new LexicalAnalyzer(input);
    }

    public void parse() throws Exception {
        exe = false;
        la.init();
        program();
    }

    public void execute() {
        exe = true;
        VARMAP.clear();
        la.init();
        try {
            program();
        } catch (Exception e) {
        }
    }

    void program() throws Exception {

    }

    void declaration() throws Exception {

    }

    void statement() throws Exception {

    }

    void bexpr() throws Exception {

    }

    void relop() throws Exception {

    }

    void aexpr() throws Exception {

    }

    void term() throws Exception {

    }

    void type() throws Exception {

    }

    void number() throws Exception {

    }

    void var() throws Exception {

    }
}

public class Project2 {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        while (true) {
            System.out.print(">> ");
            String input = sc.nextLine();
            if (input.equals("terminate")) break;

//            LexicalAnalyzer la = new LexicalAnalyzer(input);
//            while (la.nextToken != Token.EOF && la.nextToken != Token.UNKNOWN) {
//                la.lex();
//                System.out.println(la.nextToken);
//            }

            RDParser parser = new RDParser(input);
            try {
                parser.parse();
            } catch (Exception e) {
                System.out.println("Syntax Error!!");
            }
            parser.execute();
        }
    }
}
