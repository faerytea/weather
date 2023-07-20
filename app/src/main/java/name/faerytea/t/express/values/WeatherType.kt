package name.faerytea.t.express.values

import name.faerytea.t.express.R

enum class WeatherType(
    val textResId: Int,
    val picResId: Int
)  {
    CLEAR_SKY(R.string.clear_sky, R.drawable.weather_sunny),
    RAIN(R.string.rain, R.drawable.weather_rain),
    CLOUDS(R.string.cloudy, R.drawable.weather_cloudy),
    FEW_CLOUDS(R.string.partly_cloudy, R.drawable.weather_partly_cloudy),
}