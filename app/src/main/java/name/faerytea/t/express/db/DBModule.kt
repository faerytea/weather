package name.faerytea.t.express.db

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.Reusable
import javax.inject.Singleton

@Module
class DBModule {
    @Provides
    @Singleton
    fun provideCacheDB(ctx: Context) =
        Room.databaseBuilder(ctx, CacheDB::class.java, ctx.cacheDir.path + "cache")
            .build()

    @Provides
    @Singleton
    fun provideFavDB(ctx: Context) =
        Room.databaseBuilder(ctx, FavouritesDB::class.java, "fav")
            .build()

    @Provides
    @Reusable
    fun provideCacheDao(db: CacheDB) = db.cacheDao()

    @Provides
    @Reusable
    fun provideFavDao(db: FavouritesDB) = db.favDao()
}