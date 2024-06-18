# SSU Programming Language

2024 1학기

## Project 1

다음 EBNF로 정의되는 언어를 위한 Recursive Descent Parser 구현

### EBNF

```
<program> ::= { <statement> }
<statement> ::= <var> = <expr> ; | print <var> ;
<expr> ::= <bexpr> | <aexpr>
<bexpr> ::= <number> <relop> <number>
<relop> ::= == | != | < | > | <= | >=
<aexpr> ::= <term> {( + | - ) <term>}
<term> ::= <factor> {( * | / ) <factor>}
<factor> ::= <number>
<number> ::= <dec> {<dec>}
<dec> ::= 0 | 1 | 2 | 3 | 4 | 5 | 6 | 7 | 8 | 9
<var> ::= x | y | z
```

## Project 2

다음 EBNF와 Static Semantics로 정의되는 언어를 위한 Parser 구현 (Project1과 동일하게 RD Parser로 구현)

### EBNF

```
<program> ::= {<declaration>} {<statement>}
<declaration> ::= <type> <var> ;
<statement> ::= <var> = <aexpr> ; | print <bexpr> ; | print <aexpr> ; | do `{` {<statement>} `}` while ( <bexpr> ) ;
<bexpr> ::= <relop> <aexpr> <aexpr>
<relop> ::= == | != | < | > | <= | >=
<aexpr> ::= <term> {( + | - | * | / ) <term>}
<term> ::= <number> | <var> | ( <aexpr> )
<type> ::= int
<number> ::= <dec>{<dec>}
<dec> ::= 0 | 1 | 2 | 3 | 4 | 5 | 6 | 7 | 8 | 9
<var> ::= <alphabet>{<alphabet>}
<alphabet> ::= a | b | c | d | e | f | g | h | i | j | k | l | m | n | o | p | q | r | s | t | u | v | w | x | y | z
```

### Static Semantics

- 선언하지 않은 변수는 사용할 수 없다.
- 선언된 변수의 값은 0으로 초기화 한다.
- `<aexpr>`의 결과값은 정수이다.
- 산술연산은 왼쪽 결합성(left associative)이다.
- `<var>`, `<number>`의 최대 길이는 10이다.

### test case

```
int abcdabcdabcd ;

int a = 1 ; print a ;

int abc ; int k ; abc = 100 ; k = 1 + 4 * (1 + 2) ; print abc + k ;

int var ; print == 1 var + 1 ;

print ( 1 ) ;

int a ; int b ; int c ; a = 2 ; b = a + 3 / 2 ; c = a * b ; print a + b + c ;

int PascalCase ; PascalCase = 1 ;

int a ; do { print a ; a = a + 1 ; } while ( < a 5 ) ; print == 1 1 ; print ( a ) ;

int a ; int b ; int c ; do { print == 1 1 ; b = 0 ; do { print c ; c = c + 1 ; b = b + 1 ; } while ( < b 3 ) ; a = a + 1 ; } while ( < a 5 ) ;

do { print 1 ; } while ( == 1 1 ) ; print x ;
```