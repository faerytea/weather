package name.faerytea.t.express.repository

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import name.faerytea.t.express.api.CityDto
import name.faerytea.t.express.api.OpenWeatherMapApi
import name.faerytea.t.express.api.WeatherDto
import name.faerytea.t.express.db.CacheDao
import name.faerytea.t.express.db.CityEntity
import name.faerytea.t.express.db.FavouritesDao
import name.faerytea.t.express.db.WeatherCacheEntity
import name.faerytea.t.express.utils.mkFakeCityFromCoordinates
import name.faerytea.t.express.values.City
import name.faerytea.t.express.values.WeatherType
import name.faerytea.t.express.values.Weather
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.abs
import kotlin.math.cos

@Singleton
class Repository @Inject constructor(
    private val api: OpenWeatherMapApi,
    private val cacheDao: CacheDao,
    private val favouritesDao: FavouritesDao,
) {
    private val scope = CoroutineScope(Dispatchers.IO)

    suspend fun fetchWeatherInLocation(latitude: Double, longitude: Double): Pair<City, Weather> {
        val language = Locale.getDefault().language
        val cityJob = scope.async { api.findCity(latitude, longitude).first() }
        val weatherJob = scope.async { api.fetchWeather(latitude, longitude, language) }

        val cityDto = cityJob.await()
        val weatherDto = weatherJob.await()
        val localNameInLang = cityDto.localNames?.get(language)
        val city = cityDto.toDomain(language, localNameInLang)
        val weather = weatherDto.toDomain()

        cacheDao.putCities(
            listOf(
                cityDto.toEntity(language, localNameInLang)
            ))
        cacheDao.putWeather(
            WeatherCacheEntity(
                cityDto.lat,
                cityDto.lon,
                weather.type.ordinal,
                weather.temperature,
                weather.humidity,
                weather.windSpeed,
                weather.windDirectionDegrees,
            )
        )

        return city to weather
    }

    /**
     * Any cached weather in ~ 20km x 20km square centered in this location
     */
    suspend fun nearestCachedWeather(latitude: Double, longitude: Double): Pair<City, Weather>? {
        val earthRKm = 6371.2
        val latitudeRadius = 0.2
        val kmIn1deg = (Math.PI / 180) * earthRKm * cos(Math.toRadians(latitude))
        val longitudeRadius = 20.0 / kmIn1deg
        val cachedWeather = cacheDao.loadNearCachedWeather(latitude, longitude, latitudeRadius, longitudeRadius)
        val nearestCacheEntry = cachedWeather.minByOrNull {
            val latDiff = abs(it.latitude - latitude)
            val lonDiff = abs(it.longitude - longitude)
            val weDiffKm = latDiff * 111 // approx of 1 deg
            val nsDiffKm = lonDiff * kmIn1deg
            weDiffKm * weDiffKm + nsDiffKm * nsDiffKm // square of Euclid distance
        } ?: return null
        val city =
            cacheDao.getCity(nearestCacheEntry.latitude, nearestCacheEntry.longitude)?.toDomain()
                ?: mkFakeCityFromCoordinates(nearestCacheEntry.latitude, nearestCacheEntry.longitude)
        val nearest = nearestCacheEntry.toDomain()
        return city to nearest
    }

    suspend fun fetchWeather(city: City): Weather {
        val lang = Locale.getDefault().language
        val weatherDto = api.fetchWeather(city.latitude, city.longitude, lang)
        val weather = weatherDto.toDomain()
        cacheDao.putWeather(
            WeatherCacheEntity(
                city.latitude,
                city.longitude,
                weather.type.ordinal,
                weather.temperature,
                weather.humidity,
                weather.windSpeed,
                weather.windDirectionDegrees,
            )
        )
        return weather
    }

    suspend fun getCachedWeather(city: City): Weather? =
        cacheDao.loadWeather(city.latitude, city.longitude)?.toDomain()

    suspend fun findCity(name: String): List<City> {
        val cacheJob = scope.async { cacheDao.findCity(name) }
        val netJob = scope.async { api.findCity(name, 10) }
        val cached = cacheJob.await().map { it.toDomain() }
        val fetchedDTOs = netJob.await()
        val toSave = fetchedDTOs.map { it.toEntity() }
        val fetched = fetchedDTOs.map { it.toDomain() }
        cacheDao.putCities(toSave)
        return fetched + cached
    }

    suspend fun favCity(city: City) {
        favouritesDao.addToFavourites(city.toEntity())
    }

    suspend fun unFavCity(city: City) {
        favouritesDao.removeFromFavourites(city.toEntity())
    }

    suspend fun isFavCity(city: City): Boolean =
        favouritesDao.contains(city.name, city.latitude, city.longitude)

    suspend fun favList(): List<City> =
        favouritesDao.list().map { it.toDomain() }

    companion object {

        private fun WeatherDto.toDomain() = Weather(
            type = weather.first().toDomain(),
            temperature = main.temp,
            humidity = main.humidity,
            windSpeed = wind.speed,
            windDirectionDegrees = wind.deg,
        )

        private fun WeatherDto.WeatherConditions.toDomain() = when (id) {
            800 -> WeatherType.CLEAR_SKY
            801 -> WeatherType.FEW_CLOUDS
            in 802..899, in 700..799 -> WeatherType.CLOUDS
            else -> WeatherType.RAIN
        }

        private fun CityDto.toDomain(
            language: String = Locale.getDefault().language,
            localNameInLang: String? = localNames?.get(language),
        ) = City(
            name,
            localNameInLang ?: name,
            language,
            lat,
            lon,
            country,
        )

        private fun CityEntity.toDomain() = City(
            apiName,
            localName ?: apiName,
            localLocale ?: "en",
            latitude,
            longitude,
            countryCode,
        )

        private fun WeatherCacheEntity.toDomain() = Weather(
            WeatherType.values()[typeOrdinal],
            temperature,
            humidity,
            windSpeed,
            windDirectionDegrees,
        )

        private fun CityDto.toEntity(
            language: String? = Locale.getDefault().language,
            localNameInLang: String? = localNames?.get(language)
        ) = CityEntity(
            name,
            localNameInLang,
            if (localNameInLang != null) language else null,
            lat,
            lon,
            country,
        )

        private fun City.toEntity() = CityEntity(
            name,
            localName,
            locale,
            latitude,
            longitude,
            countryCode
        )
    }
}