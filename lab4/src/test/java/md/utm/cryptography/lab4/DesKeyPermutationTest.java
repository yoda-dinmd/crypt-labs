package md.utm.cryptography.lab4;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class DesKeyPermutationTest {
    @Test void standardFipsAndGrabbeTestVector() {
        String k = "0001001100110100010101110111100110011011101111001101111111110001";
        String expectedC0 = "1111000011001100101010101111";
        String expectedD0 = "0101010101100110011110001111";
        assertEquals(expectedC0 + expectedD0, DesKeyPermutation.applyPc1(k));
    }

    @Test void workedAsciiExampleCrypto26() {
        var permutation = new DesKeyPermutation("CRYPTO26");
        assertEquals("CRYPTO26", permutation.key());
        assertEquals("43 52 59 50 54 4F 32 36", permutation.hexKey());
        assertEquals("0100001101010010010110010101000001010100010011110011001000110110",
                permutation.binaryKey());
        assertEquals("0000000000111111110000001101", permutation.c0());
        assertEquals("1110001110110000001001001110", permutation.d0());
        assertEquals("0000000 0001111 1111000 0001101 1110001 1101100 0000100 1001110",
                DesKeyPermutation.groupBits(permutation.kPlus(), 7));
        String trace = permutation.formatTrace();
        assertTrue(trace.contains("Permuted Choice 1 (PC-1)"));
        assertTrue(trace.contains("K+ (56 bits): 0000000 0001111 1111000 0001101 1110001 1101100 0000100 1001110"));
    }

    @Test void pc1SelectsAll56NonParityBitsAndDiscardsEveryEighthBit() {
        assertEquals(56, DesKeyPermutation.PC1.length);
        Set<Integer> seen = new HashSet<>();
        for (int index = 0; index < DesKeyPermutation.PC1.length; index++) {
            int bitPosition = DesKeyPermutation.PC1[index];
            assertTrue(bitPosition >= 1 && bitPosition <= 64);
            assertNotEquals(0, bitPosition % 8, "Parity bit included at PC-1 index " + index);
            assertTrue(seen.add(bitPosition), "Duplicate bit position " + bitPosition);

            char[] unitKey = "0".repeat(64).toCharArray();
            unitKey[bitPosition - 1] = '1';
            String kPlus = DesKeyPermutation.applyPc1(new String(unitKey));
            for (int j = 0; j < 56; j++) {
                assertEquals(j == index ? '1' : '0', kPlus.charAt(j));
            }
        }
        char[] parityOnly = "0".repeat(64).toCharArray();
        for (int parityPos = 8; parityPos <= 64; parityPos += 8) {
            parityOnly[parityPos - 1] = '1';
        }
        assertEquals("0".repeat(56), DesKeyPermutation.applyPc1(new String(parityOnly)));
    }

    @Test void randomKeyGenerationProducesValidEightSymbolAsciiKeys() {
        Random random = new Random(12L);
        for (int i = 0; i < 25; i++) {
            String key = DesKeyPermutation.generateRandomKey(random);
            assertEquals(8, key.length());
            var permutation = new DesKeyPermutation(key);
            assertEquals(64, permutation.binaryKey().length());
            assertEquals(56, permutation.kPlus().length());
            // All 7-bit ASCII symbols have MSB = 0 at bit positions 1, 9, 17, 25, 33, 41, 49, 57.
            assertEquals("00000000", permutation.c0().substring(0, 8));
        }
        assertThrows(IllegalArgumentException.class, () -> DesKeyPermutation.generateRandomKey(null));
    }

    @Test void rejectsInvalidKeysAndBinaryStrings() {
        for (String invalid : new String[]{null, "", "SHORT", "7SYMBOL", "9SYMBOLS!", "Română12", "abc\tdefg", "abc\ndefg"}) {
            assertThrows(IllegalArgumentException.class, () -> new DesKeyPermutation(invalid));
        }
        for (String invalidBits : new String[]{null, "", "0".repeat(63), "0".repeat(65), "0".repeat(63) + "2"}) {
            assertThrows(IllegalArgumentException.class, () -> DesKeyPermutation.applyPc1(invalidBits));
        }
    }
}
