package org.liuyehcf.antlr4.parser;

import org.liuyehcf.antlr4.CalculatorBaseVisitor;
import org.liuyehcf.antlr4.CalculatorParser;

public class Visitor extends CalculatorBaseVisitor<Double> {

    @Override
    public Double visitProg(CalculatorParser.ProgContext ctx) {
        return visit(ctx.getChild(0));
    }

    @Override
    public Double visitExpr(CalculatorParser.ExprContext ctx) {
        return visit(ctx.getChild(0));
    }

    @Override
    public Double visitAdditiveExpr(CalculatorParser.AdditiveExprContext ctx) {
        if (ctx.getChildCount() == 1) {
            return visit(ctx.getChild(0));
        } else {
            String op = ctx.getChild(1).getText();
            if ("+".equals(op)) {
                return visit(ctx.getChild(0)) + visit(ctx.getChild(2));
            } else {
                return visit(ctx.getChild(0)) - visit(ctx.getChild(2));
            }
        }
    }

    @Override
    public Double visitMultiplicativeExpr(CalculatorParser.MultiplicativeExprContext ctx) {
        if (ctx.getChildCount() == 1) {
            return visit(ctx.getChild(0));
        } else {
            String op = ctx.getChild(1).getText();
            if ("*".equals(op)) {
                return visit(ctx.getChild(0)) * visit(ctx.getChild(2));
            } else {
                return visit(ctx.getChild(0)) / visit(ctx.getChild(2));
            }
        }
    }

    @Override
    public Double visitPrimaryExpr(CalculatorParser.PrimaryExprContext ctx) {
        if (ctx.getChildCount() == 1) {
            return visit(ctx.getChild(0));
        } else {
            return visit(ctx.getChild(1));
        }
    }

    @Override
    public Double visitIntLiteral(CalculatorParser.IntLiteralContext ctx) {
        return Double.valueOf(ctx.getText());
    }
}