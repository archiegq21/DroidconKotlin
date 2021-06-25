package co.touchlab.sessionize

import android.os.Build
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import co.touchlab.sessionize.file.FileLoader
import co.touchlab.sessionize.platform.AndroidAppContext
import co.touchlab.sessionize.platform.FileLoaderImpl
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import kotlin.test.BeforeTest

@RunWith(AndroidJUnit4::class)
@Config(sdk = [Build.VERSION_CODES.P])
class StaticFileLoaderTestJVM : StaticFileLoaderTest() {
    
    @BeforeTest
    fun androidSetup() {
        setUp()
        AndroidAppContext.app = ApplicationProvider.getApplicationContext()
    }
}

@RunWith(AndroidJUnit4::class)
@Config(sdk = [Build.VERSION_CODES.P])
class EventModelTestJVM : EventModelTest()

//@RunWith(AndroidJUnit4::class)
//class SettingsModelTestJVM : SettingsModelTest()