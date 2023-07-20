package name.faerytea.t.express.api

data class CityDto(
    val name: String,
    val lat: Double,
    val lon: Double,
    val country: String,
    val localNames: Map<String, String>?
)