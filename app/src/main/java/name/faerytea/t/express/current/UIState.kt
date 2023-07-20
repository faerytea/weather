package name.faerytea.t.express.current

import name.faerytea.t.express.values.City
import name.faerytea.t.express.values.Weather

sealed interface UIState {
    object Locating : UIState

    object NoLocation : UIState

    data class Loading(val city: City, val oldWeather: Weather?) : UIState

    data class NoConnection(val city: City, val oldWeather: Weather?) : UIState

    data class Ready(val city: City, val weather: Weather) : UIState
}