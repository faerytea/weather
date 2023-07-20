package name.faerytea.t.express.api

import retrofit2.http.GET
import retrofit2.http.Query

private const val API_KEY = "ea343d806ef97513b4eaac3625bf345a"

interface OpenWeatherMapApi {
    @GET(
        "data/2.5/weather?" +
                "units=metric&" +
                "appid=$API_KEY"
    )
    suspend fun fetchWeather(@Query("lat") latitude: Double, @Query("lon") longitude: Double, @Query("lang") lang: String): WeatherDto

    @GET(
        "geo/1.0/reverse?" +
                "limit=1&" +
                "appid=$API_KEY"
    )
    suspend fun findCity(@Query("lat") latitude: Double, @Query("lon") longitude: Double): List<CityDto>

    @GET(
        "geo/1.0/direct?" +
                "appid=$API_KEY"
    )
    suspend fun findCity(@Query("q") name: String, @Query("limit") limit: Int): List<CityDto>
}