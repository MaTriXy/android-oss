package com.kickstarter.libs.utils.extensions

import com.kickstarter.KSRobolectricTestCase
import com.kickstarter.R
import org.junit.Test
import org.robolectric.RuntimeEnvironment
import java.util.Locale
import javax.crypto.spec.SecretKeySpec

class StringExtKtTest : KSRobolectricTestCase() {

    @Test
    fun testEncryptDecryptString() {
        val textForEncryption = "This my text that will be encrypted!"
        val key = "aesEncryptionKey"
        val secretKey = SecretKeySpec(key.toByteArray(), "AES")
        val encryptedString = textForEncryption.encrypt(secretKey = secretKey) ?: ""
        val decrypted = encryptedString.decrypt(secretKey) ?: ""

        assertEquals(textForEncryption, decrypted)
        assertTrue(decrypted.isNotEmpty())
    }

    @Test
    fun testEncryptDecryptTokenFormat() {
        val textForEncryption = "003718603ff4a25887d83157bd11d39f0c7501f0"
        val key = "aesEncryptionKey"
        val secretKey = SecretKeySpec(key.toByteArray(), "AES")
        val encryptedString = textForEncryption.encrypt(secretKey = secretKey) ?: ""
        val decrypted = encryptedString.decrypt(secretKey = secretKey) ?: ""

        assertEquals(textForEncryption, decrypted)
        assertTrue(decrypted.isNotEmpty())
    }
    @Test
    fun isEmail_whenGivenEmail_shouldReturnTrue() {
        assertTrue(VALID_EMAIL.isEmail())
    }

    @Test
    fun isEmail_whenNotGivenEmail_shouldReturnFalse() {
        assertFalse(INVALID_EMAIL.isEmail())
    }

    @Test
    fun isTrimmedEmpty_whenGivenEmptyString_shouldReturnTrue() {
        assertTrue(EMPTY_STRING.isTrimmedEmpty())
        assertTrue(EMPTY_SPACE.isTrimmedEmpty())
        assertTrue(EMPTY_MULTI_SPACE.isTrimmedEmpty())
    }

    @Test
    fun isTrimmedEmpty_whenStringNotEmpty_shouldReturnFalse() {
        assertFalse("k".isTrimmedEmpty())
        assertFalse(" k ".isTrimmedEmpty())
    }

    @Test
    fun isPresent_whenStringEmpty_shouldReturnFalse() {
        assertFalse(EMPTY_STRING.isPresent())
        assertFalse(EMPTY_SPACE.isPresent())
        assertFalse(EMPTY_MULTI_SPACE.isPresent())
    }

    @Test
    fun isPresent_whenStringNotEmpty_shouldReturnTrue() {
        assertTrue("k".isPresent())
        assertTrue(" k ".isPresent())
    }

    @Test
    fun isValidPassword_whenStringIsEmpty_shouldReturnFalse() {
        assertFalse(EMPTY_STRING.isValidPassword())
        assertFalse(EMPTY_SPACE.isValidPassword())
        assertFalse(EMPTY_MULTI_SPACE.isValidPassword())
    }

    @Test
    fun isValidPassword_whenStringIsLessThanRequiredLength_shouldReturnFalse() {
        assertFalse(PASSWORD_LENGTH_FIVE.isValidPassword())
    }

    @Test
    fun isValidPassword_whenStringIsNotNullAndGreaterThanOrEqualToRequiredLength_shouldReturnTrue() {
        assertTrue(PASSWORD_LENGTH_SIX.isValidPassword())
        assertTrue(PASSWORD_LENGTH_TEN.isValidPassword())
    }

    @Test
    fun sentenceCase_whenGivenLowerCaseString_shouldReformatToSentenceCase() {
        assertEquals("K", "k".sentenceCase())
        assertEquals("Kickstarter", "kickstarter".sentenceCase())
    }

    @Test
    fun sentenceCase_whenGivenUpperCaseString_shouldReformatToSentenceCase() {
        assertEquals("Kickstarter", "KICKSTARTER".sentenceCase())
    }

    @Test
    fun sentenceCase_whenGivenMixedCaseString_shouldReformatToSentenceCase() {
        assertEquals("Kickstarter", "KiCkSTarTer".sentenceCase())
    }

    @Test
    fun sentenceCase_whenGivenTitleCaseString_shouldReformatToSentenceCase() {
        assertEquals("Kickstarter kickstarter", "Kickstarter Kickstarter".sentenceCase())
        assertEquals("Kickstarter is great", "Kickstarter Is Great".sentenceCase())
    }

    @Test
    fun sentenceCase_whenGivenEmptyString_shouldEmptyString() {
        assertEquals(EMPTY_STRING, EMPTY_STRING.sentenceCase())
    }

    @Test
    fun trimAllWhitespace_whenGivenEmptyString_shouldReturnEmptyString() {
        assertEquals(EMPTY_STRING, EMPTY_STRING.trimAllWhitespace())
        assertEquals(EMPTY_STRING, EMPTY_SPACE.trimAllWhitespace())
    }

    @Test
    fun trimAllWhitespace_whenGivenStringWithLeadingOrTrailingSpace_shouldReturnTrimmedString() {
        assertEquals("A", "A ".trimAllWhitespace())
        assertEquals("A", " A".trimAllWhitespace())
        assertEquals("A", " A ".trimAllWhitespace())
    }

    @Test
    fun trimAllWhitespace_whenGivenStringWithNonBreakingSpaceWithChar_shouldReturnTrimmedString() {
        assertEquals("A", "\u00A0A".trimAllWhitespace())
        assertEquals("A", "A\u00A0".trimAllWhitespace())
        assertEquals("A", "\u00A0A\u00A0".trimAllWhitespace())
        assertEquals("A", "\u00A0 A".trimAllWhitespace())
        assertEquals("A", "A \u00A0".trimAllWhitespace())
        assertEquals("A", "A\u00A0 ".trimAllWhitespace())
        assertEquals("A", "\u00A0 A \u00A0".trimAllWhitespace())
    }

    @Test
    fun trimAllWhitespace_whenGivenStringWithNonBreakingSpace_shouldReturnEmptydString() {
        assertEquals("", "\u00A0".trimAllWhitespace())
        assertEquals("", "\u00A0 ".trimAllWhitespace())
    }

    @Test
    fun wrapInParentheses_whenGivenString_shouldReturnFlankedString() {
        assertEquals(KICKSTARTER.wrapInParentheses(), KICKSTARTER_PARENTHESES)
        assertEquals(EMPTY_STRING.wrapInParentheses(), "()")
    }

    @Test
    fun isGif_whenGivenLink_shouldReturnTrue() {
        assertTrue(VALID_GIF_URL.isGif())
    }

    @Test
    fun isGif_whenGivenLink_shouldReturnFalse() {
        assertFalse(VALID_PNG_URL.isGif())
    }

    @Test
    fun parseStringToDouble() {
        val number = "90"
        val smallNumber = "0.3"

        assertTrue(number.parseToDouble() == 90.0)
        assertTrue(smallNumber.parseToDouble() == 0.3)

        val notNumber: String? = null
        assertTrue(notNumber.parseToDouble() == 0.0)

        val alsoNotNumber = "Hola 9"
        assertTrue(alsoNotNumber.parseToDouble() == 0.0)

        assertEquals(0.0, "".parseToDouble())
        assertEquals(1.0, "1".parseToDouble())
        assertEquals(10.0, "10.0".parseToDouble())
        assertEquals(100.0, "100".parseToDouble())
        assertEquals(1000.0, "1000.0".parseToDouble())

        // Change locale to use different formatting, make sure it still recognizes numbers
        Locale.setDefault(Locale.GERMAN)
        assertEquals(1.5, "1,5".parseToDouble())
        assertEquals(100.5, "100,50".parseToDouble())
        assertEquals(1000.0, "1.000".parseToDouble())
        assertEquals(1000.5, "1.000,50".parseToDouble())
        assertEquals(10000.0, "10.000".parseToDouble())
        assertEquals(10000.5, "10.000,50".parseToDouble())
    }

    @Test
    fun maskEmail() {
        assertEquals("native.****@kickstarter.com", "native.team@kickstarter.com".maskEmail())
        assertEquals("****@kickstarter.com", "test@kickstarter.com".maskEmail())
        assertEquals("****@kickstarter.com", "had@kickstarter.com".maskEmail())
    }

    @Test
    fun isValidMP3Format() {
        val url = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3"
        assertTrue(url.isMP3Url())
    }

    @Test
    fun testValidPassword() {
        assertTrue("123456".validPassword())
        assertFalse("1236".validPassword())
        assertFalse("".validPassword())
        assertFalse("   ".validPassword())
        assertTrue("123 23".validPassword())
    }

    @Test
    fun newPasswordValidationWarnings() {
        assertEquals(R.string.Password_min_length_message, "12345".newPasswordValidationWarnings("12345"))
        assertEquals(R.string.Passwords_matching_message, "".newPasswordValidationWarnings("12345"))
        assertEquals(R.string.Password_min_length_message, "12345".newPasswordValidationWarnings(""))
        assertEquals(null, "123456".newPasswordValidationWarnings(""))
        assertEquals(R.string.Passwords_matching_message, "123456".newPasswordValidationWarnings("12346"))
        assertEquals(null, "123456".newPasswordValidationWarnings("123456"))
    }

    @Test
    fun testRegexToSplitStringResourceWithLink() {
        val html = RuntimeEnvironment.getApplication().getString(R.string.Projects_may_not_offer)
        val list = html.stringsFromHtmlTranslation()
        assertEquals(list.size, 3)

        val html2 = RuntimeEnvironment.getApplication().getString(R.string.Our)
        val list2 = html2.stringsFromHtmlTranslation()
        assertEquals(list2.size, 3)

        val html3 = RuntimeEnvironment.getApplication().getString(R.string.This_comment_is_under_review_for_potentially_violating_kickstarters_community_guidelines)
        val list3 = html3.stringsFromHtmlTranslation()
        assertEquals(list3.size, 3)
    }

    @Test
    fun testRegexToGetHrefParameter() {
        val html = RuntimeEnvironment.getApplication().getString(R.string.Projects_may_not_offer)
        val href = html.hrefUrlFromTranslation()
        assertEquals(href, "{prohibited_items}")

        val html2 = RuntimeEnvironment.getApplication().getString(R.string.Our)
        val href2 = html2.hrefUrlFromTranslation()
        assertEquals(href2, "{community_guidelines}")

        val html3 = RuntimeEnvironment.getApplication().getString(R.string.This_comment_is_under_review_for_potentially_violating_kickstarters_community_guidelines)
        val href3 = html3.hrefUrlFromTranslation()
        assertEquals(href3, "{community_guidelines}")
    }

    @Test
    fun testToHtml() {
        val value = RuntimeEnvironment.getApplication().getString(R.string.This_comment_is_under_review_for_potentially_violating_kickstarters_community_guidelines)
        assertEquals(value, value.toHtml().toString())
    }

    @Test
    fun testToInteger() {
        val numString = "0"
        val errorNumString = "gfdg"
        assertEquals(0, numString.toInteger())
        assertNull(errorNumString.toInteger())
        assertNull(null.toInteger())
    }

    @Test
    fun findCurrencySymbolIndex_whenCurrencySymbolIsMultiple_returnsFirstIndex() {
        val text = "USD $123.45 €"
        val index = text.findCurrencySymbolIndex()
        assertEquals(index, 4)
    }

    @Test
    fun findCurrencySymbolIndex_whenNoCurrencySymbolIsPresent_returnsNull() {
        val text = "123.45"
        val index = text.findCurrencySymbolIndex()
        assertNull(index)
    }

    companion object {
        private const val VALID_EMAIL = "hello@kickstarter.com"
        private const val VALID_GIF_URL = "https://i.kickstarter.com/assets/035/272/960/eae68383730822ffe949f3825600a80a_original.gif?origin=ugc-qa&q=92&sig=C1dWB6NvmlwKGw4lty6s4FGU6Dn3rzNv%2F3p%2B4bhSpzk%3D"
        private const val VALID_PNG_URL = "https://i.kickstarter.com/assets/035/272/960/eae68383730822ffe949f3825600a80a_original.png?origin=ugc-qa&q=92&sig=iSSBt1qHa27bdxfYfQ74P9TI7MVDjHLOsUXaLxxPFmU%3D"
        private const val INVALID_EMAIL = "hello@kickstarer"
        private const val EMPTY_STRING = ""
        private const val EMPTY_SPACE = " "
        private const val EMPTY_MULTI_SPACE = "    "
        private const val KICKSTARTER = "kickstarter"
        private const val KICKSTARTER_PARENTHESES = "(kickstarter)"
        private const val PASSWORD_LENGTH_FIVE = "kicks"
        private const val PASSWORD_LENGTH_SIX = "kickst"
        private const val PASSWORD_LENGTH_TEN = "kickstarts"
    }
}
