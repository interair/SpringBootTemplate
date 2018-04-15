package io.github.pronto.markov.service;

import org.junit.Assert;
import org.junit.Test;

public class MarkovGeneratorTest {
    //TODO: improve test
    @Test
    public void markov() {
        String markov = MarkovGenerator.generate("1 2 3 4 5 6 7 8 9 10 11 12 13 14 15 16", 10, 2);
        Assert.assertEquals(markov, "1 2 3 4 5 6 7 8 9 10");

        markov = MarkovGenerator.generate("1 2 1 2 3 2 1 2 3 4 3 2 1 2 3 4 5 4 3 2 1", 10, 3);
        Assert.assertEquals(markov, "1 2 1 2 3 2 1 2 3 4");

        markov = MarkovGenerator.generate("1 2 1 2 3 2 1 2 3 4 3 2 1 2 3 4 5 4 3 2 1", 10, 1);
//        Assert.assertEquals(markov, "1 2 1 2 1 2 1 2 1 2");
    }

}
