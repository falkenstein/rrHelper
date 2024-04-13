package stats

import data.DataLoader
import gdocs.GDocsManager
import org.junit.Assert.*
import org.junit.Test
import java.io.File

class MonotypeStatsMakerTest {

    private val monotypeStatsMaker = MonotypeStatsMaker()
    private val docsManager = GDocsManager()
    private val loader = DataLoader()

    @Test
    fun testPdfSetup() {
        monotypeStatsMaker.composeMonotypeStats(docsManager.parseMonotypeReports(loader.readSpeciesData()))
    }
}