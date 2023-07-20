package name.faerytea.t.express.db

import androidx.room.Entity

@Entity(
    tableName = "weather",
    primaryKeys = ["latitude", "longitude"],
)
data class WeatherCacheEntity(
    val latitude: Double,
    val longitude: Double,
    val typeOrdinal: Int,
    val temperature: Double,
    val humidity: Int,
    val windSpeed: Double,
    val windDirectionDegrees: Int,
)