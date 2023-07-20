package name.faerytea.t.express.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface FavouritesDao {
    @Insert
    suspend fun addToFavourites(cityEntity: CityEntity)

    @Query("select * from cities")
    suspend fun list(): List<CityEntity>

    @Query("select * from cities where apiName like :name || '%'")
    suspend fun list(name: String): List<CityEntity>

    @Query("select exists (select * from cities where :lat = latitude and :lon = longitude and apiName = :name)")
    suspend fun contains(name: String, lat: Double, lon: Double): Boolean

    @Delete
    suspend fun removeFromFavourites(cityEntity: CityEntity)
}