package gym

import com.lowagie.text.*
import com.lowagie.text.pdf.PdfPCell
import com.lowagie.text.pdf.PdfPTable
import com.lowagie.text.pdf.PdfWriter
import data.DataLoader
import data.EBoss
import data.EType
import gdocs.BossReport
import pokemon.BattleSimulator
import pokemon.PhasePokemonConstructor
import pokemon.PokemonInstance
import species.FullSpeciesData
import java.awt.Color
import java.io.FileOutputStream
import java.math.BigDecimal
import java.math.RoundingMode
import kotlin.math.abs

class GymLeaderBattleCalculator {

    private val simulator = BattleSimulator()
    private val phaseConstructor = PhasePokemonConstructor()
    private val loader = DataLoader()
    private val font8 = FontFactory.getFont(FontFactory.HELVETICA, 8F);

    fun calculateBestTeamForGymLeader(boss: EBoss, type: EType): List<BossRecommendationDto> {
        val moves = loader.readMovesData()
        val species = loader.readSpeciesData()
        val typeChart = loader.readTypeChart()
        val possiblePokemon = phaseConstructor.setupPokemonForPhase(type, boss.phase, species, moves, typeChart)
        val bossTeam = loader.readTrainers(species, moves).first { it.boss == boss }
        val allOutcomes: MutableList<Triple<PokemonInstance, PokemonInstance, Float>> = mutableListOf()
        val battleOptions = possiblePokemon.map { myPokemon ->
            val outcomes = bossTeam.pokemon.associateWith { bossPokemon ->
                simulator.simulateBattle(myPokemon, bossPokemon)
            }
            allOutcomes.addAll(outcomes.map { Triple(myPokemon, it.key, it.value) })
            BossResultDto(
                boss = boss,
                pokemon = myPokemon,
                outcomes = outcomes,
                averageResult = outcomes.values.average().toFloat(),
                bestResult = outcomes.values.max(),
                wins = outcomes.values.count { it > 0 },
                losses = outcomes.values.count { it < 0 },
            )
        }.toMutableList()
        createOutcomesTable(type = type, boss = boss, triples = allOutcomes)
        return bossTeam.pokemon.map { bossPokemon ->
            bossPokemon to battleOptions.mapNotNull { it.outcomes[bossPokemon] }.average() // Determine the hardest opponent.
        }
            .sortedBy { it.second }
            .map { bossPokemon ->
                val bestResult = battleOptions.maxBy { it.outcomes[bossPokemon.first]!! }
                val recommendation = BossRecommendationDto(
                    myPokemon = bestResult.pokemon,
                    bossPokemon = bossPokemon.first,
                    resultThis = bestResult.outcomes[bossPokemon.first]!!,
                    resultAverage = bestResult.averageResult,
                )
                battleOptions.removeAll { it.pokemon.species == bestResult.pokemon.species }
                if (bestResult.pokemon.species.mega) {
                    battleOptions.removeAll { it.pokemon.species.mega } // Only one mega on the team.
                }
                recommendation
            }
    }

    fun createOutcomesTable(type: EType, boss: EBoss, triples: List<Triple<PokemonInstance, PokemonInstance, Float>>) {
        val document = Document(PageSize.A4)
        val writer = PdfWriter.getInstance(document, FileOutputStream("recommendations.pdf"))
        document.open()

        document.add(Phrase("${type.name} VS ${boss.niceName}"))

        val table = PdfPTable(8)
        table.defaultCell.border = 15
        table.defaultCell.setFixedHeight(40F)
        table.horizontalAlignment = 0
        table.setTotalWidth(document.pageSize.width - 84)
        table.isLockedWidth = true

        val columns = triples.map { it.second }.distinct()
        val rows = triples.map { it.first }.distinctBy { it.species }

        // First row now.
        table.addCell("Row VS column")
        columns.forEach { table.addCell(speciesToCell(it.species, table.defaultCell)) }
        table.completeRow()
        rows.forEach { row ->
            table.addCell(speciesToCell(row.species, table.defaultCell))
            columns.forEach { column ->
                val result = triples.find { it.first == row && it.second == column }?.third?.toBigDecimal()?.setScale(2, RoundingMode.HALF_EVEN)
                    ?: BigDecimal(0)
                val cell = PdfPCell(table.defaultCell)
                cell.addElement(Paragraph(result.toString()))
                cell.backgroundColor = calculateColorBasedOnValue(result.toFloat())
                table.addCell(cell)
//                table.addCell(result.toString())
            }
            table.completeRow()
        }
        document.add(table)
        document.close()
    }

    fun speciesToCell(species: FullSpeciesData, defaultCell: PdfPCell): PdfPCell {
        val imageFile = loader.loadSpeciesSprite(species)
        val cell = PdfPCell(defaultCell)
        cell.image = Image.getInstance(imageFile.readBytes())
        return cell
    }

    private fun calculateColorBasedOnValue(value: Float): Color {
        if (value < 0) {
            return Color(255, 0, 0, (255 * abs(abs(value))).toInt())
        }
        return Color(0, 255, 0, (255 * value).toInt())
    }
}