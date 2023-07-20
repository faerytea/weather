package name.faerytea.t.express

import android.app.Application

class App: Application() {

    override fun onCreate() {
        super.onCreate()
        component = DaggerApplicationComponent.factory().create(applicationContext)
    }

    companion object {
        lateinit var component: ApplicationComponent
            private set
    }
}