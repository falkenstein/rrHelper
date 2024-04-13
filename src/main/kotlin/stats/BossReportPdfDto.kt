package stats

import com.lowagie.text.Element
import data.EBoss
import data.EType

data class BossReportPdfDto(
    val type: EType,
    val boss: EBoss,
    val elements: List<Element>,
)
