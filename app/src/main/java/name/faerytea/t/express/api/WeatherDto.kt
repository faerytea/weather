package name.faerytea.t.express.api

data class WeatherDto(
    val coord: Coord,
    val weather: List<WeatherConditions>,
    val main: MainData,
    val wind: WindData,
) {
    data class Coord(val lat: Double, val lon: Double)
    data class WeatherConditions(val id: Int, val description: String)
    data class MainData(val temp: Double, val humidity: Int)
    data class WindData(val speed: Double, val deg: Int)
}