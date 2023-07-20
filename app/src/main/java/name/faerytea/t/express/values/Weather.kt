package name.faerytea.t.express.values

data class Weather(
    val type: WeatherType,
    /** Temperature in Celsius degrees */
    val temperature: Double,
    val humidity: Int,
    /** Wind speed in m/s */
    val windSpeed: Double,
    /** Wind direction in degrees clockwise from due north. */
    val windDirectionDegrees: Int,
)