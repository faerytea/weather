package name.faerytea.t.express.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [CityEntity::class, WeatherCacheEntity::class],
    version = 1,
    exportSchema = false,
)
abstract class CacheDB: RoomDatabase() {
    abstract fun cacheDao(): CacheDao
}