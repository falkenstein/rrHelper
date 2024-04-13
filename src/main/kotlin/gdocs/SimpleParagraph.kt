package gdocs

data class SimpleParagraph(
    val style: String,
    val text: String?,
    val table: List<String>?,
)
