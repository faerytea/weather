package name.faerytea.t.express

import android.content.Context
import dagger.BindsInstance
import dagger.Component
import name.faerytea.t.express.api.ApiModule
import name.faerytea.t.express.current.CurrentWeatherVM
import name.faerytea.t.express.db.DBModule
import name.faerytea.t.express.fav.FavFragment
import name.faerytea.t.express.location.LocationModule
import name.faerytea.t.express.search.SearchVM
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        LocationModule::class,
        ApiModule::class,
        DBModule::class,
    ]
)
interface ApplicationComponent {

    fun inject(currentWeatherVM: CurrentWeatherVM)
    fun inject(currentWeatherVM: SearchVM)
    fun inject(currentWeatherVM: FavFragment)

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance appCtx: Context): ApplicationComponent
    }
}