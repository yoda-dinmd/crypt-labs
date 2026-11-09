package md.utm.cryptography.lab4;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Random;
import java.util.Scanner;
import java.util.function.Function;
import java.util.random.RandomGenerator;

public final class Main {
    private Main() { }

    public static void main(String[] args) {
        Long seed = Long.getLong("lab4.seed");
        RandomGenerator random = seed != null ? new Random(seed) : new SecureRandom();
        try (Scanner input = new Scanner(System.in, StandardCharsets.UTF_8)) {
            System.out.println("Laboratory 4 — DES key permutation K+ (task 2.1, student 12)");
            while (true) {
                Integer mode = read(input,
                        "Mode (1 = manual 8-symbol key, 2 = random 8-symbol key, 0 = exit): ",
                        value -> choice(value, 0, 2));
                if (mode == null || mode == 0) { return; }
                DesKeyPermutation permutation;
                if (mode == 1) {
                    permutation = read(input, "Key (exactly 8 printable ASCII symbols): ",
                            DesKeyPermutation::new);
                    if (permutation == null) { return; }
                } else {
                    String generated = DesKeyPermutation.generateRandomKey(random);
                    System.out.println("Generated random key: " + generated);
                    permutation = new DesKeyPermutation(generated);
                }
                System.out.println(permutation.formatTrace());
                System.out.println();
            }
        }
    }

    private static int choice(String text, int minimum, int maximum) {
        try {
            int value = Integer.parseInt(text);
            if (value >= minimum && value <= maximum) { return value; }
        } catch (NumberFormatException ignored) {
            // The same message describes malformed and out-of-range choices.
        }
        throw new IllegalArgumentException("Choose an integer from " + minimum + " to " + maximum + ".");
    }

    private static <T> T read(Scanner input, String prompt, Function<String, T> parser) {
        while (true) {
            System.out.print(prompt);
            if (!input.hasNextLine()) {
                System.out.println("\nEnd of input.");
                return null;
            }
            try {
                return parser.apply(input.nextLine());
            } catch (IllegalArgumentException exception) {
                System.out.println("Invalid input: " + exception.getMessage());
            }
        }
    }
}
