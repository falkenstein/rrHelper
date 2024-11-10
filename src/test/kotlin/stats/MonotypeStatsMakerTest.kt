package stats

import data.DataLoader
import data.EBoss
import data.EGamePhase
import data.EType
import gdocs.BossReport
import gdocs.GDocsManager
import org.junit.Assert.*
import org.junit.Test
import species.FullSpeciesData
import java.io.File

class MonotypeStatsMakerTest {

    private val monotypeStatsMaker = MonotypeStatsMaker()
    private val docsManager = GDocsManager()
    private val loader = DataLoader()

    @Test
    fun testPdfSetup() {
        monotypeStatsMaker.composeMonotypeStats(docsManager.parseMonotypeReports(loader.readSpeciesData()))
    }

    @Test
    fun testDrawChart() {
        monotypeStatsMaker.drawLineChart(EType.ICE, docsManager.parseMonotypeReports(loader.readSpeciesData()))
    }

    @Test
    fun testTypeFrequencyComposition() {
        val runType = EType.ICE
        val baseSpeciesData = FullSpeciesData(
            name = "",
            id = 0,
            attributes = emptyMap(),
            abilities = emptyList(),
            types = listOf(runType),
            levelUpMoves = emptyList(),
            tmMoves = emptyList(),
            eggMoves = emptyList(),
            tutorMoves = emptyList(),
            evolutionLine = emptyList(),
            available = EGamePhase.BROCK,
            niceName = "",
            region = null,
            form = null,
            mega = false,
        )
        val baseBossReport = BossReport(
            boss = EBoss.BROCK_1,
            type = runType,
            team = listOf(),
            difficulty = 3,
        )
        val bosses = listOf(
            baseBossReport.copy(
                boss = EBoss.BROCK_1,
                team = listOf(
                    baseSpeciesData.copy(types = listOf(runType, EType.DARK)),
                    baseSpeciesData.copy(types = listOf(runType, EType.BUG)),
                    baseSpeciesData.copy(types = listOf(runType, EType.ELECTRIC)),
                    baseSpeciesData.copy(types = listOf(runType, EType.ROCK)),
                    baseSpeciesData.copy(types = listOf(runType, EType.GRASS)),
                    baseSpeciesData.copy(types = listOf(runType, EType.GROUND)),
                )
            ),
            baseBossReport.copy(
                boss = EBoss.MISTY_1,
                team = listOf(
                    baseSpeciesData.copy(types = listOf(runType, EType.DARK)),
                    baseSpeciesData.copy(types = listOf(runType, EType.BUG)),
                    baseSpeciesData.copy(types = listOf(runType, EType.ELECTRIC)),
                    baseSpeciesData.copy(types = listOf(runType, EType.ROCK)),
                    baseSpeciesData.copy(types = listOf(runType, EType.GRASS)),
                    baseSpeciesData.copy(types = listOf(runType, EType.GROUND)),
                )
            ),
        )
        val output = monotypeStatsMaker.composeTypeFrequency(bosses, runType)
        assertEquals(6, output[EBoss.BROCK_1]!!.size)
    }
}
