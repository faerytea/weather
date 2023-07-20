package name.faerytea.t.express.location

import kotlinx.coroutines.flow.Flow
import name.faerytea.t.express.values.LocationData

interface LocationProvider {
    val lastKnownLocation: Flow<LocationData>

    fun requestKnownLocation(permissionDelegate: LocationPermissionDelegate)
}