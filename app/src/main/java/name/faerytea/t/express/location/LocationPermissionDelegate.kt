package name.faerytea.t.express.location

interface LocationPermissionDelegate {

    fun isLocationPermissionGranted(): Boolean

    fun requestLocationPermission(continuation: (Boolean) -> Unit)
}