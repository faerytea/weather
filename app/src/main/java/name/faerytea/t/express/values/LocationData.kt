package name.faerytea.t.express.values

import android.location.Location

sealed interface LocationData {
    object NoLocation : LocationData
    object Searching : LocationData
    data class Ready(val location: Location): LocationData
}