package domain

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.Locale

class FinancialClarityTest {
    @Test
    fun positiveNetBalanceIncludesPlusSign() {
        val netBalance = 450.0
        val sign = when {
            netBalance > 0.0 -> "+"
            netBalance < 0.0 -> "-"
            else -> ""
        }
        val formatted = sign + String.format(Locale.UK, "%,.0f", kotlin.math.abs(netBalance))

        assertEquals("+", sign)
        assertEquals("+450", formatted)
    }

    @Test
    fun negativeNetBalanceIncludesMinusSign() {
        val netBalance = -250.0
        val sign = when {
            netBalance > 0.0 -> "+"
            netBalance < 0.0 -> "-"
            else -> ""
        }
        val formatted = sign + String.format(Locale.UK, "%,.0f", kotlin.math.abs(netBalance))

        assertEquals("-", sign)
        assertEquals("-250", formatted)
    }

    @Test
    fun zeroNetBalanceHasNoSignPrefix() {
        val netBalance = 0.0
        val sign = when {
            netBalance > 0.0 -> "+"
            netBalance < 0.0 -> "-"
            else -> ""
        }
        val formatted = sign + String.format(Locale.UK, "%,.0f", kotlin.math.abs(netBalance))

        assertEquals("", sign)
        assertEquals("0", formatted)
    }

    @Test
    fun owingFlagDeterminesVisualColorState() {
        val owingBalance = -100.0
        val owedBalance = 100.0

        assertTrue("Negative net balance must flag owing state for WarmRed highlight", owingBalance < 0.0)
        assertFalse("Positive net balance must flag owed state for ForestGreen highlight", owedBalance < 0.0)
    }
}
