import data.DataLoader
import data.EGamePhase
import data.EType
import pokemon.PhasePokemonConstructor

fun main(args: Array<String>) {
    val loader = DataLoader()
    val typeChart = loader.readTypeChart()
    val speciesData = loader.readSpeciesData()
}
