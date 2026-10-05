package md.utm.cryptography.lab1;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CaesarCipherTest {
    @Test void encodingAndWraparound() {
        CaesarCipher cipher = new CaesarCipher();
        assertEquals(31, cipher.alphabet().length());
        assertEquals("BCDEFGHIÎJKLMNOPQRSȘTȚUVWXYZAĂÂ", cipher.encrypt(CaesarCipher.ALPHABET, 3));
        assertEquals("Z", cipher.decrypt("A", 1));
        assertEquals("Ă", cipher.decrypt("A", 30));
        assertEquals("FKITXOFHÂBT", cipher.encrypt("cifrul cezar", 3));
    }

    @Test void permutationKeepsFirstOccurrencesThenRemainingLetters() {
        CaesarCipher cipher = new CaesarCipher("cryptography");
        assertEquals("CRYPTOGAHĂÂBDEFIÎJKLMNQSȘȚUVWXZ", cipher.alphabet());
        assertEquals("PKJTXQPÎYÂT", cipher.encrypt("cifrul cezar", 3));
        assertEquals("AAAAAAA", new CaesarCipher("aaaaaaa").decrypt(new CaesarCipher("aaaaaaa").encrypt("aaaaaaa", 5), 5));
    }

    @Test void everyLetterRoundTripsForAllKeysAndBothVariants() {
        for (CaesarCipher cipher : new CaesarCipher[]{new CaesarCipher(), new CaesarCipher("Criptografie")}) {
            for (int key = 1; key <= 30; key++) {
                assertEquals(CaesarCipher.ALPHABET, cipher.decrypt(cipher.encrypt(CaesarCipher.ALPHABET, key), key));
            }
        }
    }

    @Test void normalizesCaseSpacesCedillasAndDecomposedDiacritics() {
        assertEquals("ȘȚĂÂÎ", CaesarCipher.normalize(" ş ţ a\u0306 â î ", true));
    }

    @Test void rejectsInvalidKeysCharactersAndKeywords() {
        CaesarCipher cipher = new CaesarCipher();
        for (int key : new int[]{-1, 0, 31, Integer.MAX_VALUE}) {
            assertThrows(IllegalArgumentException.class, () -> cipher.encrypt("A", key));
        }
        for (String text : new String[]{"abc!", "123", "A\tB", "ß", "ı", "ſ", "😀", "", "   "}) {
            assertThrows(IllegalArgumentException.class, () -> cipher.encrypt(text, 3));
        }
        for (String keyword : new String[]{"abcdef", "abc defg", "abcdef!"}) {
            assertThrows(IllegalArgumentException.class, () -> new CaesarCipher(keyword));
        }
        assertTrue(assertThrows(IllegalArgumentException.class, () -> cipher.encrypt("A!", 3)).getMessage().contains("'!'"));
    }
}
