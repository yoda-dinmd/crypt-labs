package md.utm.cryptography.lab3;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class VigenereCipherTest {
    @Test void knownRomanianExample() {
        var cipher = new VigenereCipher("criptografie");
        assertEquals("CRIPTOGRAFIE", cipher.key());
        assertEquals("VJRXGIGANXNEQDÎDOHLHE", cipher.encrypt("Știința înseamnă putere"));
        assertEquals("ȘTIINȚAÎNSEAMNĂPUTERE", cipher.decrypt("VJRXGIGANXNEQDÎDOHLHE"));
        assertEquals(cipher.encrypt("ȘTIINȚAÎNSEAMNĂPUTERE"), cipher.encrypt(" șTiința înseamnă putere "));
    }

    @Test void everyLetterAtEveryShift() {
        String alphabet = "AĂÂBCDEFGHIÎJKLMNOPQRSȘTȚUVWXYZ";
        assertEquals(alphabet, VigenereCipher.ALPHABET);
        for (int shift = 0; shift < 31; shift++) {
            var cipher = new VigenereCipher(String.valueOf(alphabet.charAt(shift)).repeat(7));
            String expected = alphabet.substring(shift) + alphabet.substring(0, shift);
            assertEquals(expected, cipher.encrypt(alphabet));
            assertEquals(alphabet, cipher.decrypt(expected));
        }
    }

    @Test void repeatingKeyAndRestartForEachMessage() {
        var cipher = new VigenereCipher("ĂÂBCDEF");
        assertEquals("ĂÂBCDEFĂÂBCDEFĂ", cipher.encrypt("AAAAAAAAAAAAAAA"));
        assertEquals("Ă", cipher.encrypt("a"));
        assertEquals("Z", cipher.decrypt("A"));
        assertEquals("ĂÂBCDEF", cipher.key());
    }

    @Test void romanianUnicodeNormalization() {
        assertEquals("ĂÂÎȘȚȘȚ", VigenereCipher.normalize("a\u0306a\u0302i\u0302s\u0326t\u0326şţ", false));
        var cipher = new VigenereCipher("a\u0306a\u0302i\u0302s\u0326t\u0326şţ");
        assertEquals("ĂÂÎȘȚȘȚ", cipher.key());
        assertEquals("ĂÂÎȘȚȘȚ", cipher.decrypt(cipher.encrypt("ăâîșțşţ")));
    }

    @Test void invalidKeysAndMessages() {
        for (String key : new String[]{null, "", "abcdef", "abc defg", "abcdef1", "abcdef!", "abcdefß"}) {
            assertThrows(IllegalArgumentException.class, () -> new VigenereCipher(key));
        }
        var cipher = new VigenereCipher("AAAAAAA");
        for (String text : new String[]{null, "", "   ", "Salut!", "abc1", "a\tb", "a\nb", "é", "ß", "🙂", "a\u00a0b"}) {
            assertThrows(IllegalArgumentException.class, () -> cipher.encrypt(text));
            assertThrows(IllegalArgumentException.class, () -> cipher.decrypt(text));
        }
        assertEquals("ABC", cipher.encrypt("abc"));
    }
}
