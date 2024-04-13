package gdocs

import data.DataLoader
import org.junit.Test

class GDocsManagerTest{

    private val gDocsManager = GDocsManager()
    private val loader = DataLoader()

    @Test
    fun testGoogleDocsConnection() {
        gDocsManager.readReportDocument()
    }

    @Test
    fun testDocumentParsing() {
        gDocsManager.parseMonotypeReports(loader.readSpeciesData())
    }
}