package md.utm.cryptography.lab4;

import java.util.random.RandomGenerator;

/** Task 2.1: DES 64-bit ASCII key encoding and Permuted Choice 1 (PC-1) derivation of K+. */
public final class DesKeyPermutation {
    public static final int KEY_LENGTH = 8;
    public static final int[] PC1 = {
            57, 49, 41, 33, 25, 17,  9,
             1, 58, 50, 42, 34, 26, 18,
            10,  2, 59, 51, 43, 35, 27,
            19, 11,  3, 60, 52, 44, 36,
            63, 55, 47, 39, 31, 23, 15,
             7, 62, 54, 46, 38, 30, 22,
            14,  6, 61, 53, 45, 37, 29,
            21, 13,  5, 28, 20, 12,  4
    };
    private static final String RANDOM_ALPHABET =
            "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

    private final String key;
    private final String binaryKey;
    private final String kPlus;

    public DesKeyPermutation(String key) {
        this.key = validateKey(key);
        this.binaryKey = toBinaryKey(this.key);
        this.kPlus = applyPc1(this.binaryKey);
    }

    public String key() { return key; }

    public String binaryKey() { return binaryKey; }

    public String kPlus() { return kPlus; }

    public String c0() { return kPlus.substring(0, 28); }

    public String d0() { return kPlus.substring(28, 56); }

    public String hexKey() {
        StringBuilder hex = new StringBuilder(23);
        for (int i = 0; i < key.length(); i++) {
            if (i > 0) { hex.append(' '); }
            hex.append(String.format("%02X", (int) key.charAt(i)));
        }
        return hex.toString();
    }

    public static String validateKey(String key) {
        if (key == null) {
            throw new IllegalArgumentException("Key is required.");
        }
        key.codePoints().forEach(codePoint -> {
            if (codePoint < 0x20 || codePoint > 0x7E) {
                String shown = switch (codePoint) {
                    case '\t' -> "\\t";
                    case '\n' -> "\\n";
                    case '\r' -> "\\r";
                    default -> new String(Character.toChars(codePoint));
                };
                throw new IllegalArgumentException("Rejected character '" + shown
                        + "' (U+" + String.format("%04X", codePoint)
                        + "). Allowed symbols: printable ASCII (U+0020 to U+007E).");
            }
        });
        if (key.length() != KEY_LENGTH) {
            throw new IllegalArgumentException(
                    "Key must contain exactly 8 printable ASCII symbols (got " + key.length() + ").");
        }
        return key;
    }

    public static String generateRandomKey(RandomGenerator random) {
        if (random == null) {
            throw new IllegalArgumentException("Random generator is required.");
        }
        StringBuilder generated = new StringBuilder(KEY_LENGTH);
        for (int i = 0; i < KEY_LENGTH; i++) {
            generated.append(RANDOM_ALPHABET.charAt(random.nextInt(RANDOM_ALPHABET.length())));
        }
        return generated.toString();
    }

    public static String toBinaryKey(String key) {
        String validated = validateKey(key);
        StringBuilder bits = new StringBuilder(64);
        for (int i = 0; i < validated.length(); i++) {
            bits.append(toEightBitBinary(validated.charAt(i)));
        }
        return bits.toString();
    }

    public static String applyPc1(String binaryKey) {
        if (binaryKey == null || binaryKey.length() != 64
                || !binaryKey.chars().allMatch(ch -> ch == '0' || ch == '1')) {
            throw new IllegalArgumentException("Binary key K must contain exactly 64 bits ('0' or '1').");
        }
        StringBuilder permuted = new StringBuilder(PC1.length);
        for (int position : PC1) {
            permuted.append(binaryKey.charAt(position - 1));
        }
        return permuted.toString();
    }

    public static String groupBits(String bits, int groupSize) {
        StringBuilder grouped = new StringBuilder();
        for (int i = 0; i < bits.length(); i += groupSize) {
            if (i > 0) { grouped.append(' '); }
            grouped.append(bits, i, Math.min(bits.length(), i + groupSize));
        }
        return grouped.toString();
    }

    public String formatTrace() {
        StringBuilder out = new StringBuilder();
        out.append("Step 1 — ASCII encoding and 64-bit key K (bits 8, 16, ..., 64 are parity bits):\n");
        out.append("Byte  Symbol  ASCII (dec/hex)  Bits    8-bit binary  Data (1..7)  Parity (8)\n");
        for (int i = 0; i < KEY_LENGTH; i++) {
            char symbol = key.charAt(i);
            String byteBits = binaryKey.substring(i * 8, (i + 1) * 8);
            int startBit = i * 8 + 1;
            int endBit = (i + 1) * 8;
            String range = String.format("%d..%d", startBit, endBit);
            out.append(String.format("%4d    '%c'      %3d / 0x%02X   %-6s  %s      %s      %c (k%d)%n",
                    i + 1, symbol, (int) symbol, (int) symbol, range,
                    byteBits, byteBits.substring(0, 7), byteBits.charAt(7), endBit));
        }
        out.append("K (hex): ").append(hexKey()).append('\n');
        out.append("K (64b): ").append(groupBits(binaryKey, 8)).append("\n\n");

        out.append("Step 2 — Permuted Choice 1 (PC-1) table and intermediate bit selection:\n");
        out.append("Half  Row  PC-1 bit positions    Selected bits from K                       Row bits\n");
        for (int row = 0; row < 8; row++) {
            String half = row < 4 ? "C0" : "D0";
            StringBuilder positions = new StringBuilder();
            StringBuilder selected = new StringBuilder();
            String rowBits = kPlus.substring(row * 7, (row + 1) * 7);
            for (int col = 0; col < 7; col++) {
                int pos = PC1[row * 7 + col];
                char bit = binaryKey.charAt(pos - 1);
                if (col > 0) {
                    positions.append(' ');
                    selected.append(' ');
                }
                positions.append(String.format("%2d", pos));
                selected.append(String.format("%-5s", "k" + pos + "=" + bit));
            }
            out.append(String.format("%-4s  %3d  %s  %s  %s%n",
                    half, row + 1, positions, selected, rowBits));
        }
        out.append('\n');
        out.append("Step 3 — Output 56-bit permuted key K+:\n");
        out.append("C0 (28 bits): ").append(groupBits(c0(), 7)).append('\n');
        out.append("D0 (28 bits): ").append(groupBits(d0(), 7)).append('\n');
        out.append("K+ (56 bits): ").append(groupBits(kPlus, 7));
        return out.toString();
    }

    private static String toEightBitBinary(char symbol) {
        return String.format("%8s", Integer.toBinaryString(symbol)).replace(' ', '0');
    }
}
