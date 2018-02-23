package org.liuyehcf.grammar.core.definition.converter;

import org.liuyehcf.grammar.core.definition.Grammar;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GrammarConverterPipelineImpl implements GrammarConverterPipeline {
    private final List<Class<? extends GrammarConverter>> converters;

    private GrammarConverterPipelineImpl(List<Class<? extends GrammarConverter>> converters) {
        this.converters = Collections.unmodifiableList(converters);
    }

    public static Builder builder() {
        return new Builder();
    }


    @Override
    public Grammar convert(Grammar grammar) {
        Grammar convertedGrammar = grammar;
        for (Class<? extends GrammarConverter> clazz : converters) {
            try {
                Constructor<? extends GrammarConverter> constructor = clazz.getConstructor(Grammar.class);

                GrammarConverter instance = constructor.newInstance(convertedGrammar);

                convertedGrammar = instance.getConvertedGrammar();
            } catch (NoSuchMethodException
                    | InstantiationException
                    | IllegalAccessException
                    | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        }
        return convertedGrammar;
    }


    public static final class Builder {
        List<Class<? extends GrammarConverter>> converters = new ArrayList<>();

        private Builder() {

        }

        public Builder registerGrammarConverter(Class<? extends GrammarConverter> clazz) {
            converters.add(clazz);
            return this;
        }

        public GrammarConverterPipelineImpl build() {
            return new GrammarConverterPipelineImpl(converters);
        }
    }
}
