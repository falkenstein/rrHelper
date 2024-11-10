package stats

import com.lowagie.text.Document
import com.lowagie.text.FontFactory
import com.lowagie.text.Image
import com.lowagie.text.PageSize
import com.lowagie.text.Phrase
import com.lowagie.text.Table
import com.lowagie.text.pdf.PdfPCell
import com.lowagie.text.pdf.PdfPTable
import com.lowagie.text.pdf.PdfWriter
import data.DataLoader
import data.EBoss
import data.EType
import gdocs.BossReport
import org.jfree.chart.ChartFactory
import org.jfree.chart.ChartUtils
import org.jfree.data.category.DefaultCategoryDataset
import species.FullSpeciesData
import java.io.File
import java.io.FileOutputStream

class MonotypeStatsMaker {

    private val dataLoader = DataLoader()
    private val font8 = FontFactory.getFont(FontFactory.HELVETICA, 8F);

    fun composeMonotypeStats(reports: List<BossReport>) {

        val document = Document(PageSize.A4)
        val writer = PdfWriter.getInstance(document, FileOutputStream("tables.pdf"))
        document.open()


        reports.groupBy { it.type }.forEach{type, bosses ->
            document.add(Phrase(type.name))
            document.add(setupTable(bosses, document))
        }

        document.close()
    }

    /**
     * Creates a table that contains rows with the trainer data.
     */
    fun setupTable(rows: List<BossReport>, document: Document): PdfPTable {
        val table = PdfPTable(8)
        table.defaultCell.border = 15
        table.defaultCell.setFixedHeight(40F)
        table.horizontalAlignment = 0
        table.setTotalWidth(document.pageSize.width - 84)
        table.isLockedWidth = true

        rows.forEach {row ->
            reportToRow(row, table.defaultCell).forEach { table.addCell(it) }
            table.completeRow()
        }
        return table
    }

    fun reportToRow(report: BossReport, defaultCell: PdfPCell): List<PdfPCell> {
        val result = mutableListOf<PdfPCell>()
        result.add(PdfPCell(Phrase(report.boss.niceName, font8)))
        report.team.sortedBy { it.id }.forEach { species ->
            try {
                val imageFile = dataLoader.loadSpeciesSprite(species)
                val cell = PdfPCell(defaultCell)
                cell.image = Image.getInstance(imageFile.readBytes())
                result.add(cell)
            } catch (e: Exception) {
                System.err.println(e.message)
                System.err.println("Failed row addition for ${species.niceName}")
            }
        }
        return result
    }

    /**
     * Draws a line chart of the type progression and provides file name.
     */
    fun drawLineChart(runType: EType, bosses: List<BossReport>): String {
        val lineChartDataset = DefaultCategoryDataset()
        val typeFrequency = mutableMapOf<EBoss, Map<EType, Int>>()
        bosses.forEach { boss ->
            typeFrequency[boss.boss] = boss.team
                .map { species -> getRecordedType(runType, species) }.groupingBy { it }.eachCount()
                .map { (type, counts) ->
                    type to counts + typeFrequency.values.sumOf { it.getOrDefault(type, 0) }
                }.toMap()
        }
        var i = 0
        typeFrequency.keys.forEach { boss ->
            typeFrequency[boss]!!.forEach {
                lineChartDataset.addValue(typeFrequency[boss]!![it.key], it.key.name, i)
            }
            i++
        }
        val lineChartObject = ChartFactory.createLineChart(
            "${runType.name} type",
            "used in run",
            "boss number",
            lineChartDataset
        )
        val width = 640
        val height = 480
        val fileName = "monotype_$runType.jpeg"
        val lineChart = File(fileName)
        ChartUtils.saveChartAsJPEG(lineChart, lineChartObject, width, height)
        return fileName
    }

    private fun getRecordedType(runType: EType, species: FullSpeciesData): EType {
        return if (species.types.size > 1) {
            species.types.first { it != runType }
        } else {
            species.types.first()
        }
    }
}