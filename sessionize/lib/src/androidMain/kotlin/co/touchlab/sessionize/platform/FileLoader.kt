package co.touchlab.sessionize.platform

import android.content.Context
import co.touchlab.sessionize.file.FileLoader

class FileLoaderImpl(
    private val context: Context,
): FileLoader {

    override fun load(filePrefix: String, fileType: String) =
        context.assets.open("$filePrefix.$fileType", Context.MODE_PRIVATE)
            .bufferedReader()
            .use { it.readText() }

}