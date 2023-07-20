package name.faerytea.t.express.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface CacheDao {
    @Query("select * from weather where latitude = :latitude and longitude = :longitude")
    suspend fun loadWeather(latitude: Double, longitude: Double): WeatherCacheEntity?

    @Query(
        "select * from weather " +
                "where latitude " +
                "between :latitude - :latitudeRadius and :latitude + :latitudeRadius " +
                "and longitude " +
                "between :longitude - :longitudeRadius and :longitude + :longitudeRadius"
    )
    suspend fun loadNearCachedWeather(
        latitude: Double,
        longitude: Double,
        latitudeRadius: Double,
        longitudeRadius: Double
    ): List<WeatherCacheEntity>

    @Query("select * from cities where apiName like :name || '%'")
    suspend fun findCity(name: String): List<CityEntity>

    @Query("select * from cities where latitude = :latitude and longitude = :longitude")
    suspend fun getCity(latitude: Double, longitude: Double): CityEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun putCities(cities: List<CityEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun putWeather(weatherCacheEntity: WeatherCacheEntity)
}