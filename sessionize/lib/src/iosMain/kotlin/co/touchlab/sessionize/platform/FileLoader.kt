package co.touchlab.sessionize.platform

import co.touchlab.sessionize.file.FileLoader
import platform.Foundation.NSBundle

object FileLoaderImpl: FileLoader {

    override fun load(filePrefix: String, fileType: String) =
        NSBundle.mainBundle.pathForResource(filePrefix, fileType)

}