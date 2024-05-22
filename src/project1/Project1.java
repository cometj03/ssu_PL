package project1;

import java.util.*;

enum Token {
    IDENT,      // id (식별자)
    INT_LIT,    // integer literal
    ASSIGN_OP,  // =
    ADD_OP, SUB_OP, MULT_OP, DIV_OP, // +, -, *, /
    REL_EQ, REL_NEQ, REL_LT, REL_GT, REL_LE, REL_GE,     // ==, !=, <, >, <=, >=
    SEMICOLON,  // ;
    EOF,
    UNKNOWN
}

class LexicalAnalyzer {
    public Token nextToken;
    public String lexeme = "";

    private char nextChar;
    private final String input;
    private int curIdx = 0;

    LexicalAnalyzer(String input) {
        this.input = input;
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
            nextToken = Token.IDENT;
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
            case '+':
                nextToken = Token.ADD_OP;
                lexeme = "+";
                getChar();
                return;
            case '-':
                nextToken = Token.SUB_OP;
                lexeme = "-";
                getChar();
                return;
            case '*':
                nextToken = Token.MULT_OP;
                lexeme = "*";
                getChar();
                return;
            case '/':
                nextToken = Token.DIV_OP;
                lexeme = "/";
                getChar();
                return;
            case ';':
                nextToken = Token.SEMICOLON;
                lexeme = ";";
                getChar();
                return;
            case '=':
                getChar();
                if (nextChar == '=') {
                    nextToken = Token.REL_EQ;
                    lexeme = "==";
                    getChar();
                } else {
                    nextToken = Token.ASSIGN_OP;
                    lexeme = "=";
                }
                return;
            case '!':
                getChar();
                if (nextChar == '=') {
                    nextToken = Token.REL_NEQ;
                    lexeme = "!=";
                    getChar();
                } else {
                    nextToken = Token.UNKNOWN;
                }
                return;
            case '<':
                getChar();
                if (nextChar == '=') {
                    nextToken = Token.REL_LE;
                    lexeme = "<=";
                    getChar();
                } else {
                    nextToken = Token.REL_LT;
                    lexeme = "<";
                }
                return;
            case '>':
                getChar();
                if (nextChar == '=') {
                    nextToken = Token.REL_GE;
                    lexeme = ">=";
                    getChar();
                } else {
                    nextToken = Token.REL_GT;
                    lexeme = ">";
                }
                return;
        }
        nextToken = Token.UNKNOWN;
        lexeme = "";
        getChar();
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
        return ('A' <= c && c <= 'Z') || ('a' <= c && c <= 'z');
    }

    private boolean isDigit(char c) {
        return '0' <= c && c <= '9';
    }
}

class RDParser {
    private final LexicalAnalyzer la;
    private final Map<String, String> VARMAP = new HashMap<>() {
        {
            put("x", "0");
            put("y", "0");
            put("z", "0");
        }
    };
    private int prevFirstIdx;
    private char prevFirstChar;

    String expr_result = "";
    String l_value = "";
    String r_value = "";  // VARMAP 값
    String rel_op = "";

    RDParser(String input) {
        la = new LexicalAnalyzer(input);
    }

    int program() {
        la.lex();
        while (la.nextToken == null || la.nextToken != Token.EOF) {
            if (statement() < 0) return -1;
        }
        System.out.println();
        return 0;
    }

    int statement() {
        if (la.nextToken == Token.IDENT) {
            if (la.lexeme.equals("print")) {
                la.lex();
                if (var() < 0) return -1;
                System.out.print(r_value + " ");
            } else {
                if (var() < 0) return -1;
                if (la.nextToken != Token.ASSIGN_OP) return -1;
                prevFirstChar = la.getNextChar();
                prevFirstIdx = la.getCurIdx();
                la.lex();
                if (expr() < 0) return -1;
                VARMAP.put(l_value, expr_result);
            }
        } else {
            return -1;
        }

        if (la.nextToken != Token.SEMICOLON) return -1;
        la.lex();
        return 0;
    }

    int expr() {
        int res = 0;
        if ((res = bexpr()) == -2) {
            la.setNextChar(prevFirstChar);
            la.setCurIdx(prevFirstIdx);
            la.lex();
            if ((res = aexpr()) < 0) return -1;
            expr_result = String.valueOf(res);
            return 0;
        } else if (res < 0) {
            return -1;
        }
        expr_result = res == 0 ? "FALSE" : "TRUE";
        return 0;
    }

    int bexpr() {
        int a, b;
        if ((a = number()) < 0) return -1;
        if (relop() < 0) return -2;
        if ((b = number()) < 0) return -1;

        return switch (rel_op) {
            case "==" -> a == b ? 1 : 0;
            case "!=" -> a != b ? 1 : 0;
            case "<" -> a < b ? 1 : 0;
            case ">" -> a > b ? 1 : 0;
            case "<=" -> a <= b ? 1 : 0;
            case ">=" -> a >= b ? 1 : 0;
            default -> -1;
        };
    }

    int relop() {
        switch (la.nextToken) {
            case REL_EQ:
            case REL_NEQ:
            case REL_LT:
            case REL_GT:
            case REL_LE:
            case REL_GE:
                rel_op = la.lexeme;
                break;
            default:
                return -1;
        }
        la.lex();
        return 0;
    }

    int aexpr() {
        int a, b;
        if ((a = term()) < 0) return -1;
        while (la.nextToken == Token.ADD_OP || la.nextToken == Token.SUB_OP) {
            if (la.nextToken == Token.ADD_OP) {
                la.lex();
                if ((b = term()) < 0) return -1;
                a += b;
            } else {
                la.lex();
                if ((b = term()) < 0) return -1;
                a -= b;
            }
        }
        return a;
    }

    int term() {
        int a, b;
        if ((a = factor()) < 0) return -1;
        while (la.nextToken == Token.MULT_OP || la.nextToken == Token.DIV_OP) {
            if (la.nextToken == Token.MULT_OP) {
                la.lex();
                if ((b = factor()) < 0) return -1;
                a *= b;
            } else {
                la.lex();
                if ((b = factor()) < 0) return -1;
                a /= b;
            }
        }
        return a;
    }

    int factor() {
        int a;
        if ((a = number()) < 0) return -1;
        return a;
    }

    int number() {
        if (la.nextToken != Token.INT_LIT) return -1;
        int a, sum = 0;
        for (int i = 0; i < la.lexeme.length(); i++) {
            if ((a = dec(i)) < 0) return -1;
            sum = sum * 10 + a;
        }
        la.lex();
        return sum;
    }

    int dec(int idx) {
        char i = la.lexeme.charAt(idx);
        if ('0' <= i && i <= '9') {
            return i - '0';
        }
        return -1;
    }

    int var() {
        if (la.nextToken != Token.IDENT) return -1;
        if (!la.lexeme.equals("x") && !la.lexeme.equals("y") && !la.lexeme.equals("z")) return -1;
        l_value = la.lexeme;
        r_value = VARMAP.get(la.lexeme);
        la.lex();
        return 0;
    }
}

public class Project1 {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        while (true) {
            System.out.print(">> ");
            String input = sc.nextLine();
            if (input.equals("terminate")) break;

            RDParser parser = new RDParser(input);
            System.out.print(">> ");
            if (parser.program() < 0) System.out.println("syntax error!!");
        }
    }
}