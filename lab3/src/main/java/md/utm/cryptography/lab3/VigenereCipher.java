package md.utm.cryptography.lab3;

import java.text.Normalizer;
import java.util.Locale;

/** Task 3.2: repeating-key Vigenere over 31 Romanian letters. */
public final class VigenereCipher {
    public static final String ALPHABET = "AĂÂBCDEFGHIÎJKLMNOPQRSȘTȚUVWXYZ";
    private final String key;

    public VigenereCipher(String keyword) {
        key = normalize(keyword, false);
        if (key.length() < 7) {
            throw new IllegalArgumentException("Key must contain at least 7 Romanian letters; no spaces.");
        }
    }

    public String key() { return key; }

    public String encrypt(String text) { return transform(text, false); }

    public String decrypt(String text) { return transform(text, true); }

    private String transform(String text, boolean decrypt) {
        String normalized = normalize(text, true);
        StringBuilder result = new StringBuilder(normalized.length());
        for (int i = 0; i < normalized.length(); i++) {
            int position = ALPHABET.indexOf(normalized.charAt(i));
            int shift = ALPHABET.indexOf(key.charAt(i % key.length()));
            int transformed = Math.floorMod(position + (decrypt ? -shift : shift), 31);
            result.append(ALPHABET.charAt(transformed));
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
