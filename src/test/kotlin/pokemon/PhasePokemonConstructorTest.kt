package pokemon

import data.DataLoader
import data.EGamePhase
import data.EType
import org.junit.Test

class PhasePokemonConstructorTest{

    private val phaseConstructor = PhasePokemonConstructor()
    private val loader = DataLoader()

    @Test
    fun testSetupPhaseOptions() {
        val availableTypes = phaseConstructor.setupPokemonForPhase(
            EType.POISON, EGamePhase.ERIKA, loader.readSpeciesData(), loader.readMovesData(), loader.readTypeChart()
        )
        println(availableTypes)
    }
}