package co.touchlab.sessionize.file

fun interface FileLoader {
    fun load(filePrefix: String, fileType: String): String?
}