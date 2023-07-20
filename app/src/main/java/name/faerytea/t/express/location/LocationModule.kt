package name.faerytea.t.express.location

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.Reusable

@Module
class LocationModule {
    @Provides
    @Reusable
    fun provideLocationProvider(ctx: Context): LocationProvider = LocationProviderIml(ctx)
}