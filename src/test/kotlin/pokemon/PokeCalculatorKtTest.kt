package pokemon

import data.DataLoader
import data.EType
import org.junit.Assert.assertEquals
import org.junit.Test

class PokeCalculatorKtTest {

    private val typeChart = DataLoader().readTypeChart()

    @Test
    fun calculateTypeEffectivenessTest() {
        val offensiveType = EType.FIRE
        assertEquals(0.5F, calculateTypeEffectiveness(offensiveType, listOf(EType.WATER), typeChart))
        assertEquals(1F, calculateTypeEffectiveness(offensiveType, listOf(EType.NORMAL), typeChart))
        assertEquals(2F, calculateTypeEffectiveness(offensiveType, listOf(EType.GRASS), typeChart))
        assertEquals(4F, calculateTypeEffectiveness(offensiveType, listOf(EType.GRASS, EType.BUG), typeChart))
    }
}