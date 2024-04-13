package gdocs

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.docs.v1.Docs
import com.google.api.services.docs.v1.DocsScopes
import com.google.api.services.docs.v1.model.Document
import com.google.auth.http.HttpCredentialsAdapter
import com.google.auth.oauth2.GoogleCredentials
import data.DataLoader
import data.EBoss
import data.EType
import species.FullSpeciesData
import java.io.File
import java.io.FileInputStream


class GDocsManager {

    private val loader = DataLoader()

    private fun authorize(scopes: List<String>): HttpCredentialsAdapter {
        val credentials: GoogleCredentials = GoogleCredentials.fromStream(FileInputStream(File("sunny-truth.json")))
            .createScoped(scopes)
        credentials.refreshIfExpired()
        return HttpCredentialsAdapter(credentials)
    }

    fun readReportDocument(): List<SimpleParagraph> {
        val httpTransport: NetHttpTransport = GoogleNetHttpTransport.newTrustedTransport()
        val factory = GsonFactory.getDefaultInstance()

        val service = Docs.Builder(httpTransport, factory, authorize(listOf(DocsScopes.DOCUMENTS)))
            .setApplicationName("RRReader")
            .build()
        val documentId = "14yGoJLN7nI33RfnFOvw7DzkOD4TRq-0tDJpXFedjv6c" // The RR report file.
        val document: Document = service.documents().get(documentId).execute()
        val paragraphs = document.body.content
            .filter { it.paragraph != null || it.table != null }
            .map {
                SimpleParagraph(
                    style = it.paragraph?.paragraphStyle?.namedStyleType ?: it.table?.let { "TABLE" } ?: "UNKNOWN",
                    text = it.paragraph?.elements?.first()?.textRun?.content?.trim(),
                    table = it.table?.tableRows?.first()?.tableCells?.map { cell ->
                        cell.content.mapNotNull { cont ->
                            cont.paragraph?.elements?.mapNotNull { elem -> elem?.textRun?.content?.trim() }
                        }.flatten().filter { cl -> cl.isNotBlank() }.joinToString()
                    },
                )
            }
        return paragraphs
    }

    fun parseMonotypeReports(speciesData: List<FullSpeciesData>): List<BossReport> {
        val paragraphs = readReportDocument()
        val rawTypeSorting = mutableMapOf<EType, MutableList<SimpleParagraph>>()
        var currentType = EType.MYSTERY
        paragraphs.forEach {
            if (it.style == "HEADING_1") {
                val type = EType.entries.find { tp -> tp.name == it.text?.trim()?.uppercase() } ?: EType.MYSTERY
                rawTypeSorting.putIfAbsent(type, mutableListOf())
                currentType = type
            } else {
                rawTypeSorting[currentType]?.add(it)
            }
        }
        val rawTypeWithBossSorting = mutableMapOf<EType, MutableMap<String, MutableList<SimpleParagraph>>>()
        var currentBoss = ""
        rawTypeSorting
            .filter { it.key != EType.MYSTERY }
            .forEach { tp ->
                currentBoss = ""
                tp.value.forEach { row ->
                    if (row.style == "HEADING_4") {
                        currentBoss = row.text?.trim() ?: ""
                        rawTypeWithBossSorting.putIfAbsent(tp.key, mutableMapOf())
                        rawTypeWithBossSorting[tp.key]?.putIfAbsent(currentBoss, mutableListOf())
                    } else {
                        rawTypeWithBossSorting[tp.key]?.get(currentBoss)?.add(row)
                    }
                }
            }
        val allReports = rawTypeWithBossSorting.map { (type, bosses) ->
            bosses.map { (bossName, paragraphs) ->
                val team = paragraphs.first { it.table != null }.table?.mapNotNull { member ->
                    speciesData.find { loader.comparePokemonByName(member, it) }
                } ?: emptyList()
                val difficulty = paragraphs.first { it.text?.contains("Difficulty: ", ignoreCase = true) ?: false }
                    .text?.substringAfter("Difficulty: ")?.trim()?.toInt() ?: 0
                BossReport(
                    boss = getBossEnum(bossName),
                    type = type,
                    team = team,
                    difficulty = difficulty,
                )
            }
        }.flatten()
        return allReports
    }

    /**
     * Gets the boss enum from the text - sometimes we need to first add the number one.
     */
    private fun getBossEnum(text: String): EBoss {
        return try {
            EBoss.valueOf(text.uppercase().replace(" ", "_"))
        } catch (e: IllegalArgumentException) {
            EBoss.valueOf("${text.uppercase()}_1")
        }
    }
}
