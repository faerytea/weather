package name.faerytea.t.express.db

import androidx.room.Entity

@Entity(
    tableName = "cities",
    primaryKeys = ["latitude", "longitude"],
)
data class CityEntity(
    val apiName: String,
    val localName: String?,
    val localLocale: String?,
    val latitude: Double,
    val longitude: Double,
    val countryCode: String,
)