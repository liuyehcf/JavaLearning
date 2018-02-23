package org.liuyehcf.grammar.definition.converter;

import org.junit.Test;
import org.liuyehcf.grammar.definition.Grammar;
import org.liuyehcf.grammar.definition.PrimaryProduction;
import org.liuyehcf.grammar.definition.Production;
import org.liuyehcf.grammar.definition.Symbol;
import org.liuyehcf.grammar.rg.RGBuilder;
import org.liuyehcf.grammar.rg.utils.GrammarUtils;

import static org.junit.Assert.assertEquals;
import static org.liuyehcf.grammar.definition.Symbol.createNonTerminator;
import static org.liuyehcf.grammar.definition.Symbol.createTerminator;

public class TestSimplificationGrammarConverter {
    @Test
    public void convertCase1() {
        Symbol digit = createNonTerminator("digit");
        Symbol letter_ = createNonTerminator("letter_");
        Symbol id = createNonTerminator("id");

        Grammar grammar = Grammar.create(
                id,
                Production.create(
                        digit,
                        GrammarUtils.createPrimaryProduction("[0123456789]")
                ),
                Production.create(
                        letter_,
                        GrammarUtils.createPrimaryProduction("[abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ_]")
                ),
                Production.create(
                        id,
                        PrimaryProduction.create(
                                letter_,
                                createTerminator("("),
                                letter_,
                                createTerminator("|"),
                                digit,
                                createTerminator(")"),
                                createTerminator("*")
                        )
                )
        );

        Grammar convertedGrammar = RGBuilder.compile(grammar).getGrammar();

        assertEquals(
                "{\"productions\":[\"id → ( [ a b c d e f g h i j k l m n o p q r s t u v w x y z A B C D E F G H I J K L M N O P Q R S T U V W X Y Z _ ] ) ( ( [ a b c d e f g h i j k l m n o p q r s t u v w x y z A B C D E F G H I J K L M N O P Q R S T U V W X Y Z _ ] ) | ( [ 0 1 2 3 4 5 6 7 8 9 ] ) ) *\"]}",
                convertedGrammar.toReadableJSONString()
        );
    }

    @Test
    public void convertCase2() {
        Symbol digit = createNonTerminator("digit");
        Symbol letter_ = createNonTerminator("letter_");
        Symbol id = createNonTerminator("id");

        Grammar grammar = Grammar.create(
                id,
                Production.create(
                        id,
                        PrimaryProduction.create(
                                letter_,
                                createTerminator("("),
                                letter_,
                                createTerminator("|"),
                                digit,
                                createTerminator(")"),
                                createTerminator("*")
                        )
                ),
                Production.create(
                        letter_,
                        GrammarUtils.createPrimaryProduction("[abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ_]")
                ),
                Production.create(
                        digit,
                        GrammarUtils.createPrimaryProduction("[0123456789]")
                )
        );

        Grammar convertedGrammar = getGrammarConverterPipeline().convert(grammar);

        assertEquals(
                "{\"productions\":[\"id → ( [ a b c d e f g h i j k l m n o p q r s t u v w x y z A B C D E F G H I J K L M N O P Q R S T U V W X Y Z _ ] ) ( ( [ a b c d e f g h i j k l m n o p q r s t u v w x y z A B C D E F G H I J K L M N O P Q R S T U V W X Y Z _ ] ) | ( [ 0 1 2 3 4 5 6 7 8 9 ] ) ) *\"]}",
                convertedGrammar.toReadableJSONString()
        );
    }

    @Test
    public void convertCase3() {
        Symbol digit = createNonTerminator("digit");
        Symbol letter_ = createNonTerminator("letter_");
        Symbol id = createNonTerminator("id");

        Grammar grammar = Grammar.create(
                id,
                Production.create(
                        id,
                        PrimaryProduction.create(
                                letter_,
                                createTerminator("("),
                                letter_,
                                createTerminator("|"),
                                digit,
                                createTerminator(")"),
                                createTerminator("*")
                        )
                ),
                Production.create(
                        digit,
                        GrammarUtils.createPrimaryProduction("[0123456789]")
                ),
                Production.create(
                        letter_,
                        GrammarUtils.createPrimaryProduction("[abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ_]")
                )
        );

        Grammar convertedGrammar = getGrammarConverterPipeline().convert(grammar);

        assertEquals(
                "{\"productions\":[\"id → ( [ a b c d e f g h i j k l m n o p q r s t u v w x y z A B C D E F G H I J K L M N O P Q R S T U V W X Y Z _ ] ) ( ( [ a b c d e f g h i j k l m n o p q r s t u v w x y z A B C D E F G H I J K L M N O P Q R S T U V W X Y Z _ ] ) | ( [ 0 1 2 3 4 5 6 7 8 9 ] ) ) *\"]}",
                convertedGrammar.toReadableJSONString()
        );
    }

    private GrammarConverterPipeline getGrammarConverterPipeline() {
        return GrammarConverterPipelineImpl
                .builder()
                .registerGrammarConverter(MergeGrammarConverter.class)
                .registerGrammarConverter(SimplificationGrammarConverter.class)
                .build();
    }
}
