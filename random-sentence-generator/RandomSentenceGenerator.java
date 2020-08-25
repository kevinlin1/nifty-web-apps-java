import java.io.*;
import java.util.*;
import java.util.stream.*;

// The RandomSentenceGenerator class generates grammatical sentences.
public class RandomSentenceGenerator {
    // Pseudorandom number generator for picking rule
    private Random random;

    // Constructs a new RandomSentenceGenerator instance
    public RandomSentenceGenerator() {
        this.random = new Random();
    }

    // Returns a collection of all legal nonterminals in the grammar
    public Collection<String> nonterminals()  {
        return Arrays.stream(Rules.values()).map(Enum::name).collect(Collectors.toList());
    }

    private boolean isTerminal(String target) {
        try {
            Rules.valueOf(target);
            return false;
        } catch (IllegalArgumentException e) {
            return true;
        }
    }

    // pre : The given symbol is a nonterminal in the grammar and times is not negative (throws an
    //       IllegalArgumentException if either precondition is not satisfied)
    // post: Uses the grammar to random generate a random grammatically correct text for the given
    //       nonterminal the given number of times (returning the values as an array of strings)
    public List<String> generate(String target, int times) {
        if (isTerminal(target) || times <= 0) {
            return List.of();
        }
        return IntStream.range(0, times)
                        .mapToObj(i -> generate(target).trim())
                        .collect(Collectors.toList());
    }

    // Returns either the given target if it is a terminal symbol or a randomly generated string
    // generated from the grammar for the given nonterminal, possibly with a trailing space
    private String generate(String target) {
        try {
            String result = "";
            for (String part : Rules.valueOf(target).expression(random)) {
                result += generate(part);
            }
            return result;
        } catch (IllegalArgumentException e) {
            return target + " ";
        }
    }

    private enum Rules {
        SENTENCE(Rule.of("NOUNP VERBP")),
        NOUNP(Rule.split("DET ADJS NOUN|PROPNOUN")),
        PROPNOUN(Rule.split("John|Jane|Sally|Spot|Fred|Elmo")),
        ADJS(Rule.split("ADJ|ADJ ADJS")),
        ADJ(Rule.split("big|green|wonderful|faulty|subliminal|pretentious")),
        DET(Rule.split("the|a")),
        NOUN(Rule.split("dog|cat|man|university|father|mother|child|television")),
        VERBP(Rule.split("TRANSVERB NOUNP|INTRANSVERB")),
        TRANSVERB(Rule.split("hit|honored|kissed|helped")),
        INTRANSVERB(Rule.split("died|collapsed|laughed|wept"));

        private final List<Rule> rules;

        private Rules(Rule... rules) {
            this.rules = Arrays.asList(rules);
        }

        public List<String> expression(Random random) {
            return rules.get(random.nextInt(rules.size())).expression;
        }
    }

    private static class Rule {
        private final List<String> expression;

        private Rule(String... expression) {
            this.expression = Arrays.asList(expression);
        }

        private static Rule of(String spaceSeparated) {
            return new Rule(spaceSeparated.split("\\s+"));
        }

        private static Rule[] split(String pipeSeparated) {
            String[] rules = pipeSeparated.split("\\|");
            Rule[] result = new Rule[rules.length];
            for (int i = 0; i < rules.length; i += 1) {
                result[i] = Rule.of(rules[i]);
            }
            return result;
        }
    }

    public static void main(String[] args) throws FileNotFoundException {
        // Step 0: Initialize data for the algorithm
        RandomSentenceGenerator generator = new RandomSentenceGenerator();
        try (Scanner stdin = new Scanner(System.in)) {
            System.out.println("Valid symbols: " + generator.nonterminals());
            System.out.print("Target: ");
            while (stdin.hasNextLine()) {
                String target = stdin.nextLine();
                if (target.length() == 0) {
                    System.exit(0);
                }
                // Step 1: Return 10 randomly-generated strings
                for (String result : generator.generate(target, 10)) {
                    System.out.println(result);
                }
                System.out.println();
                System.out.print("Target: ");
            }
        }
    }
}
