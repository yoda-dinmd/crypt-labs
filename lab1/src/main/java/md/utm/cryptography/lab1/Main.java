package md.utm.cryptography.lab1;

import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import java.util.function.Function;

public final class Main {
    private Main() { }

    public static void main(String[] args) {
        try (Scanner input = new Scanner(System.in, StandardCharsets.UTF_8)) {
            System.out.println("Laboratory 1 — Romanian Caesar cipher");
            while (true) {
                Integer variant = read(input, "Cipher (1 = standard, 2 = keyword, 0 = exit): ",
                        value -> choice(value, 0, 2));
                if (variant == null || variant == 0) {
                    return;
                }
                Integer operation = read(input, "Operation (1 = encrypt, 2 = decrypt): ",
                        value -> choice(value, 1, 2));
                if (operation == null) { return; }
                Integer key = read(input, "Numeric key (1–30): ", value -> {
                    try {
                        return CaesarCipher.validateKey(Integer.parseInt(value));
                    } catch (NumberFormatException exception) {
                        throw new IllegalArgumentException("Key must be an integer from 1 to 30 inclusive.");
                    }
                });
                if (key == null) { return; }
                CaesarCipher cipher = new CaesarCipher();
                if (variant == 2) {
                    cipher = read(input, "Keyword (at least 7 Romanian letters, no spaces): ", CaesarCipher::new);
                    if (cipher == null) { return; }
                    System.out.println("Permuted alphabet: " + cipher.alphabet());
                }
                String text = read(input, "Text: ", value -> CaesarCipher.normalize(value, true));
                if (text == null) { return; }
                String result = operation == 1 ? cipher.encrypt(text, key) : cipher.decrypt(text, key);
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
