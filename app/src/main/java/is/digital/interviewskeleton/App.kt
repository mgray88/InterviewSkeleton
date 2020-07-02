package `is`.digital.interviewskeleton

import android.app.Application
import android.content.Context
import androidx.multidex.MultiDex

internal class App : Application() {
    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }
}
