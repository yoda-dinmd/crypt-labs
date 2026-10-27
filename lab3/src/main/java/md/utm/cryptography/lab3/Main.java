package md.utm.cryptography.lab3;

import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import java.util.function.Function;

public final class Main {
    private Main() { }

    public static void main(String[] args) {
        try (Scanner input = new Scanner(System.in, StandardCharsets.UTF_8)) {
            System.out.println("Laboratory 3 — Romanian Vigenere cipher (task 3.2, student 12)");
            while (true) {
                Integer operation = read(input, "Operation (1 = encrypt, 2 = decrypt, 0 = exit): ",
                        value -> choice(value, 0, 2));
                if (operation == null || operation == 0) { return; }
                VigenereCipher cipher = read(input, "Key (at least 7 Romanian letters, no spaces): ",
                        VigenereCipher::new);
                if (cipher == null) { return; }
                String text = read(input, "Text: ", value -> VigenereCipher.normalize(value, true));
                if (text == null) { return; }
                System.out.println("Normalized key: " + cipher.key());
                System.out.println("Normalized text: " + text);
                String result = operation == 1 ? cipher.encrypt(text) : cipher.decrypt(text);
                System.out.println((operation == 1 ? "Ciphertext: " : "Plaintext: ") + result);
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
