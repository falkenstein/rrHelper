package stats

import data.DataLoader
import data.EType
import gdocs.GDocsManager
import org.junit.Test

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
}
