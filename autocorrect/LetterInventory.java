import java.util.*;
import java.io.*;
import java.nio.file.*;

// The LetterInventory class keeps track of a character count vector.
public class LetterInventory {
    // Earliest valid letter in the inventory
    public static final char BEGIN = 'a';
    // Latest valid letter in the inventory
    public static final char END = 'z';

    private Map<Character, Integer> counts;

    // Constructs a letter inventory with the counts of the letters from the given string ignoring
    // the case of the letters.
    public LetterInventory(String s) {
        counts = new HashMap<>();
        for (int i = 0; i < s.length(); i++) {
            char ch = Character.toLowerCase(s.charAt(i));
            if (ch <= END && ch >= BEGIN) {
                counts.put(ch, counts.getOrDefault(ch, 0) + 1);
            }
        }
    }

    // pre : letter is a letter in the alphabet (throws IllegalArgumentException if not)
    // post: Returns the count for the given letter ignoring case.
    public int get(char letter) {
        letter = Character.toLowerCase(letter);
        if (letter < BEGIN || letter > END) {
            throw new IllegalArgumentException("invalid letter: " + letter);
        }
        return counts.getOrDefault(letter, 0);
    }

    // Returns the cosine similarity between this inventory and the other inventory.
    public double similarity(LetterInventory other) {
        long product = 0;
        double thisNorm = 0;
        double otherNorm = 0;
        for (char letter = 'a'; letter <= 'z'; letter++) {
            int a = this.get(letter);
            int b = other.get(letter);
            product += a * (long) b;
            thisNorm += a * a;
            otherNorm += b * b;
        }
        if (thisNorm <= 0 || otherNorm <= 0) {
            return 0;
        }
        return product / (Math.sqrt(thisNorm) * Math.sqrt(otherNorm));
    }

    // Returns a string representation of this letter inventory as a bracketed sequence of letters
    // in alphabetical order with n occurrences of a letter that has a count of n in the inventory.
    public String toString() {
        String result = "[";
        for (char letter = BEGIN; letter <= END; letter++) {
            int count = counts.getOrDefault(letter, 0);
            for (int j = 0; j < count; j++) {
                result += letter;
            }
        }
        return result + "]";
    }

    // Returns true if and only if the other inventory stores the same counts as this inventory.
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof LetterInventory)) {
            return false;
        }
        LetterInventory other = (LetterInventory) o;
        return this.counts.equals(other.counts);
    }

    // Returns a hash code value for this letter inventory.
    public int hashCode() {
        return counts.hashCode();
    }

    public static void main(String[] args) throws FileNotFoundException {
        // Step 0: Initialize data for the algorithm
        Map<LetterInventory, Set<String>> anagrams = new HashMap<>();
        try (Scanner input = new Scanner(new File("english.txt"))) {
            while (input.hasNextLine()) {
                String s = input.nextLine();
                LetterInventory inventory = new LetterInventory(s);
                if (!anagrams.containsKey(inventory)) {
                    anagrams.put(inventory, new HashSet<>());
                }
                anagrams.get(inventory).add(s);
            }
        }
        try (Scanner stdin = new Scanner(System.in)) {
            Random random = new Random(1 + 0x43);
            System.out.print("Query: ");
            while (stdin.hasNextLine()) {
                String s = stdin.nextLine();
                if (s.length() == 0) {
                    System.exit(0);
                }
                // Step 1: Return the top 10 most similar options
                LetterInventory target = new LetterInventory(s);
                PriorityQueue<LetterInventory> pq = new PriorityQueue<>(
                    Comparator.comparingDouble(target::similarity)
                    );
                for (LetterInventory inventory : anagrams.keySet()) {
                    pq.add(inventory);
                    if (pq.size() > 10) {
                        pq.remove();
                    }
                }
                List<String> matches = new ArrayList<>(10);
                while (!pq.isEmpty()) {
                    LetterInventory inventory = pq.remove();
                    Set<String> options = anagrams.get(inventory);
                    if (options.contains(s)) {
                        matches.add(s);
                    } else {
                        matches.add(randomChoice(options, random));
                    }
                }
                Collections.reverse(matches);
                for (String result : matches) {
                    System.out.println(result);
                }
                System.out.println();
                System.out.print("Query: ");
            }
        }
    }

    private static <E> E randomChoice(Collection<E> data, Random random) {
        int index = random.nextInt(data.size());
        Iterator<E> iter = data.iterator();
        while (index > 0) {
            iter.next();
            index--;
        }
        return iter.next();
    }
}
