import data.DataLoader
import data.EGamePhase
import data.EType
import pokemon.PhasePokemonConstructor

fun main(args: Array<String>) {
    val loader = DataLoader()
    val typeChart = loader.readTypeChart()
    val pokemonConstructor = PhasePokemonConstructor()
    val movesData = loader.readMovesData()
    val speciesData = loader.readSpeciesData()
    val options = pokemonConstructor.setupPokemonForPhase(
        EType.ROCK,
        EGamePhase.BROCK,
        speciesData,
        movesData,
        typeChart,
    )
    println(options)
}
