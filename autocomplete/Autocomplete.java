import java.util.*;
import java.util.stream.*;
import java.io.*;

public class Autocomplete {
    private final Map<String, Integer> cityWeights;

    public Autocomplete(String filename) throws FileNotFoundException {
        cityWeights = new LinkedHashMap<>();
        try (Scanner input = new Scanner(new File(filename))) {
            while (input.hasNextLine()) {
                String[] parts = input.nextLine().split("\t");
                int weight = Integer.parseInt(parts[0]);
                String term = parts[1];
                cityWeights.put(term, weight);
            }
        }
    }

    // Returns all terms that start with the given prefix, in descending order of weight.
    public List<String> allMatches(String prefix) {
        if (prefix == null || prefix.length() == 0) {
            return List.of();
        }
        return cityWeights.keySet()
                          .stream()
                          .filter(city -> city.startsWith(prefix))
                          .collect(Collectors.toList());
    }

    // Return the weight for the given city
    public int getWeight(String city) {
        return cityWeights.get(city);
    }

    public static void main(String[] args) throws FileNotFoundException {
        Autocomplete autocomplete = new Autocomplete("cities.tsv");
        try (Scanner stdin = new Scanner(System.in)) {
            System.out.print("Query: ");
            while (stdin.hasNextLine()) {
                String prefix = stdin.nextLine();
                if (prefix.length() == 0) {
                    System.exit(0);
                }
                List<String> matches = autocomplete.allMatches(prefix);
                if (matches.size() > 5) {
                    matches = matches.subList(0, 5);
                }
                for (String city : matches) {
                    System.out.println(getWeight(city) + " " + city);
                }
                System.out.println();
                System.out.print("Query: ");
            }
        }
    }
}
