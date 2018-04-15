package io.github.pronto.markov.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

import lombok.AllArgsConstructor;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.util.CollectionUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

public class MarkovGenerator {

    public static String generate(String data, int resultSize, int suffixSize) {
        String[] words = data.trim().split(" ");
        check(words, suffixSize, resultSize);
         
        MultiValueMap<String, String> occurrences = Arrays.stream(words).collect(new MarkovGenerator.MarkovCollector(suffixSize));
        String prefix = occurrences.keySet().iterator().next();
        List<String> result = new ArrayList<>(Arrays.asList(prefix.split(" ")));

        for (int i = suffixSize; i < resultSize; i++) {
            List<String> sufList = occurrences.get(prefix);
            if (CollectionUtils.isEmpty(sufList)) break; // means prefix contained end of corpus
            String suf = sufList.get(ThreadLocalRandom.current().nextInt(sufList.size()));
            result.add(suf);
            prefix = String.join(" ", result.subList(i + 1 - suffixSize, i + 1));

        }
        return String.join(" ", result);
    }

    private static void check(String[] words, int keySize, int outputSize) {
        //TODO: add some check
    }

    @AllArgsConstructor
    public static class MarkovCollector
        implements Collector<String, List<String>, MultiValueMap<String, String>>
    {
        private final int batchSize;
        private final MultiValueMap<String, String> res = new LinkedMultiValueMap<>();

        @Override
        public Supplier<List<String>> supplier() {
            return LinkedList::new;
        }

        @Override
        public BiConsumer<List<String>, String> accumulator() {
            return (l, s) -> {
                if (l.size() == batchSize) {
                    res.add(String.join(" ", l), s.intern()); //use RLE instead (intern is slow)
                    l.remove(0);
                }
                l.add(s);
            };
        }

        @Override
        public BinaryOperator<List<String>> combiner() {
            return (strings, strings2) ->{ throw new NotImplementedException(""); };
        }

        @Override
        public Function<List<String>, MultiValueMap<String, String>> finisher() {
            return l -> res;
        }

        @Override
        public Set<Characteristics> characteristics() {
            return Collections.emptySet();
        }
    }
}
