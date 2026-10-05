package md.utm.cryptography.lab1;

import java.text.Normalizer;
import java.util.Locale;

/** Caesar shifts use positions in the handout alphabet, never character codes. */
public final class CaesarCipher {
    public static final String ALPHABET = "AĂÂBCDEFGHIÎJKLMNOPQRSȘTȚUVWXYZ";
    private final String alphabet;

    public CaesarCipher() {
        alphabet = ALPHABET;
    }

    public CaesarCipher(String keyword) {
        String normalized = normalize(keyword, false);
        if (normalized.length() < 7) {
            throw new IllegalArgumentException("Keyword must contain at least 7 Romanian letters; spaces are not allowed.");
        }
        StringBuilder permutation = new StringBuilder();
        for (char letter : (normalized + ALPHABET).toCharArray()) {
            if (permutation.indexOf(String.valueOf(letter)) < 0) {
                permutation.append(letter);
            }
        }
        alphabet = permutation.toString();
    }

    public String alphabet() {
        return alphabet;
    }

    public String encrypt(String text, int key) {
        return transform(text, key, false);
    }

    public String decrypt(String text, int key) {
        return transform(text, key, true);
    }

    public static int validateKey(int key) {
        if (key < 1 || key > 30) {
            throw new IllegalArgumentException("Key must be an integer from 1 to 30 inclusive.");
        }
        return key;
    }

    private String transform(String text, int key, boolean decrypt) {
        validateKey(key);
        String normalized = normalize(text, true);
        StringBuilder result = new StringBuilder();
        for (char letter : normalized.toCharArray()) {
            int position = alphabet.indexOf(letter);
            int shifted = Math.floorMod(position + (decrypt ? -key : key), alphabet.length());
            result.append(alphabet.charAt(shifted));
        }
        return result.toString();
    }

    public static String normalize(String text, boolean allowSpaces) {
        if (text == null) {
            throw new IllegalArgumentException("Text is required.");
        }
        String composed = Normalizer.normalize(text, Normalizer.Form.NFC);
        StringBuilder result = new StringBuilder();
        // Validate before uppercasing: non-Romanian letters must not expand into valid ones.
        composed.codePoints().forEach(codePoint -> {
            if (allowSpaces && codePoint == ' ') {
                return;
            }
            String original = new String(Character.toChars(codePoint));
            String letter = original.toUpperCase(Locale.ROOT).replace('Ş', 'Ș').replace('Ţ', 'Ț');
            String allowed = ALPHABET + ALPHABET.toLowerCase(Locale.ROOT) + "ŞşŢţ";
            if (allowed.indexOf(original) < 0 || letter.length() != 1
                    || ALPHABET.indexOf(letter.charAt(0)) < 0) {
                String shown = switch (codePoint) {
                    case '\t' -> "\\t";
                    case '\n' -> "\\n";
                    case '\r' -> "\\r";
                    default -> original;
                };
                throw new IllegalArgumentException("Rejected character '" + shown
                        + "' (U+" + String.format("%04X", codePoint) + "). Allowed letters: "
                        + ALPHABET + " (upper/lowercase, cedilla variants accepted)"
                        + (allowSpaces ? "; spaces are removed." : "; no spaces in the keyword."));
            }
            result.append(letter);
        });
        if (result.isEmpty()) {
            throw new IllegalArgumentException("Enter at least one Romanian letter. Allowed letters: " + ALPHABET);
        }
        return result.toString();
    }
}
