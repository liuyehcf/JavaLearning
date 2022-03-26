package org.liuyehcf.antlr4;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.liuyehcf.antlr4.parser.Visitor;

public class Calculator {

    public static double calculate(String input) {
        CalculatorLexer lexer = new CalculatorLexer(CharStreams.fromString(input));
        CommonTokenStream tokenStream = new CommonTokenStream(lexer);
        CalculatorParser parser = new CalculatorParser(tokenStream);
        CalculatorParser.ProgContext prog = parser.prog();
        return new Visitor().visit(prog);
    }

    private static void test(String expression, double expectedResult) {
        double result = calculate(expression);
        if (result != expectedResult) {
            throw new RuntimeException(String.format("'%s' is wrong, expected result=%f", expression, expectedResult));
        }
    }

    public static void main(String[] args) {
        test("1 + 2 + 3", 1 + 2 + 3.0);
        test("1 + 2 * 3", 1 + 2 * 3.0);
        test("1 + 2 / 3", 1 + 2 / 3.0);
        test("(1 + 2) * 3", (1 + 2) * 3.0);
        test("(1 + 2) / 3", (1 + 2) / 3.0);
    }
}
