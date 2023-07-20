package name.faerytea.t.express.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [CityEntity::class],
    version = 1,
    exportSchema = false,
)
abstract class FavouritesDB: RoomDatabase() {
    abstract fun favDao(): FavouritesDao
}