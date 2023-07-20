package name.faerytea.t.express.current

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import name.faerytea.t.express.App
import name.faerytea.t.express.location.LocationPermissionDelegate
import name.faerytea.t.express.location.LocationProvider
import name.faerytea.t.express.repository.Repository
import name.faerytea.t.express.utils.mkFakeCityFromCoordinates
import name.faerytea.t.express.values.City
import name.faerytea.t.express.values.LocationData
import name.faerytea.t.express.values.Weather
import javax.inject.Inject

class CurrentWeatherVM : ViewModel() {
    val uiState: Flow<UIState>
        get() = _uiState
    private val _uiState: MutableStateFlow<UIState> = MutableStateFlow(UIState.Locating)
    val favStatus: Flow<Boolean>
        get() = _favStatus
    private val _favStatus = MutableStateFlow(false)

    @Inject
    lateinit var locationProvider: LocationProvider

    @Inject
    lateinit var repository: Repository

    init {
        App.component.inject(this)
        viewModelScope.launch(Dispatchers.Unconfined) {
            locationProvider.lastKnownLocation.collect {
                when (it) {
                    LocationData.NoLocation -> _uiState.tryEmit(UIState.NoLocation)
                    LocationData.Searching -> _uiState.tryEmit(UIState.Locating)
                    is LocationData.Ready -> withContext(Dispatchers.Main) {
                        if (_uiState.value == UIState.Locating) {
                            loadWeather(it.location.latitude, it.location.longitude, null)
                        }
                    }
                }
            }
        }
    }

    fun requestLocation(locationPermissionDelegate: LocationPermissionDelegate) {
        locationProvider.requestKnownLocation(locationPermissionDelegate)
    }

    fun favCurrent() {
        val city = extractCity()
        viewModelScope.launch {
            repository.favCity(city)
            _favStatus.emit(true)
        }
    }

    fun unFavCurrent() {
        val city = extractCity()
        viewModelScope.launch {
            repository.unFavCity(city)
            _favStatus.emit(false)
        }
    }

    fun refreshWeather(locationPermissionDelegate: LocationPermissionDelegate) {
        val (c, w) = when (val v = _uiState.value) {
            is UIState.Loading -> return // impossible
            UIState.Locating -> return // noop
            UIState.NoLocation -> {
                locationProvider.requestKnownLocation(locationPermissionDelegate)
                return
            }
            is UIState.NoConnection -> v.city to v.oldWeather
            is UIState.Ready -> v.city to v.weather
        }
        loadWeather(c, w)
    }

    fun loadWeather(city: City, oldWeather: Weather?) {
        _uiState.tryEmit(UIState.Loading(city, oldWeather))
        viewModelScope.launch(Dispatchers.IO) {
            _favStatus.emit(repository.isFavCity(city))
        }
        viewModelScope.launch(Dispatchers.IO) {
            val cacheJob = async {
                repository.getCachedWeather(city)?.let { w ->
                    updateFromCache(city, w)
                }
            }

            try {
                val weather = repository.fetchWeather(city)
                cacheJob.cancel()
                _uiState.emit(UIState.Ready(city, weather))
            } catch (e: Exception) {
                failWithNoConnection(e)
            }
        }
    }

    private fun loadWeather(latitude: Double, longitude: Double, oldWeather: Weather?) {
        _uiState.tryEmit(UIState.Loading(mkFakeCityFromCoordinates(latitude, longitude), oldWeather))
        viewModelScope.launch(Dispatchers.IO) {
            val cacheJob = async {
                repository.nearestCachedWeather(latitude, longitude)?.let { (c, w) ->
                    updateFromCache(c, w)
                    _favStatus.emit(repository.isFavCity(c))
                }
            }
            try {
                val (city, weather) = repository.fetchWeatherInLocation(latitude, longitude)
                cacheJob.cancel()
                _favStatus.emit(repository.isFavCity(city))
                _uiState.emit(UIState.Ready(city, weather))
            } catch (e: Exception) {
                failWithNoConnection(e)
            }
        }
    }

    private suspend fun failWithNoConnection(e: Exception) {
        Log.e("CWVM", "Cannot fetch data from network", e)
        val curState = _uiState.value
        if (curState is UIState.Loading) {
            _uiState.emit(UIState.NoConnection(curState.city, curState.oldWeather))
        }
    }

    private suspend fun updateFromCache(
        city: City,
        w: Weather
    ) {
        withContext(Dispatchers.Main) {
            when (_uiState.value) {
                is UIState.Loading -> {
                    _uiState.emit(UIState.Loading(city, w))
                }

                is UIState.NoConnection -> {
                    _uiState.emit(UIState.Ready(city, w))
                }

                else -> Unit
            }
        }
    }

    private fun extractCity(): City {
        return when (val v = _uiState.value) {
            is UIState.Loading -> v.city
            is UIState.NoConnection -> v.city
            is UIState.Ready -> v.city
            else -> throw IllegalStateException("State is $v")
        }
    }

}