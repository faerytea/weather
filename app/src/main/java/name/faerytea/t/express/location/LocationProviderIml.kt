package name.faerytea.t.express.location

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import androidx.annotation.RequiresPermission
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import name.faerytea.t.express.values.LocationData

class LocationProviderIml(appCtx: Context): LocationProvider {
    private val locationService = LocationServices.getFusedLocationProviderClient(appCtx)

    private val _lastKnownLocation: MutableStateFlow<LocationData> = MutableStateFlow(LocationData.NoLocation)
    override val lastKnownLocation: Flow<LocationData>
        get() = _lastKnownLocation.asStateFlow()

    @SuppressLint("MissingPermission")
    override fun requestKnownLocation(permissionDelegate: LocationPermissionDelegate) {
        _lastKnownLocation.tryEmit(LocationData.Searching)
        if (permissionDelegate.isLocationPermissionGranted()) {
            emitLastKnownLocation()
        } else {
            permissionDelegate.requestLocationPermission { granted ->
                if (granted) {
                    emitLastKnownLocation()
                } else {
                    _lastKnownLocation.tryEmit(LocationData.NoLocation)
                }
            }
        }
    }

    @RequiresPermission("android.permission.ACCESS_COARSE_LOCATION")
    private fun emitLastKnownLocation() {
        locationService.lastLocation.addOnCompleteListener {
            val location: Location? = it.result
            val data = if (location != null) LocationData.Ready(location) else LocationData.NoLocation
            _lastKnownLocation.tryEmit(data)
        }
    }
}