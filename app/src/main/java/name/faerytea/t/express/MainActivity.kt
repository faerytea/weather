package name.faerytea.t.express

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import name.faerytea.t.express.current.CurrentWeatherFragment
import name.faerytea.t.express.fav.FavFragment
import name.faerytea.t.express.location.LocationPermissionDelegate
import name.faerytea.t.express.search.SearchFragment
import name.faerytea.t.express.values.City

class MainActivity : AppCompatActivity(), LocationPermissionDelegate, Router {
    private var locationPermissionContinuation: ((Boolean) -> Unit)? = null
    @SuppressLint("MissingPermission") // lint not sees permission check on…
    private val locationPermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        locationPermissionContinuation?.invoke(granted)
        locationPermissionContinuation = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun isLocationPermissionGranted(): Boolean =
        checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED

    override fun requestLocationPermission(continuation: (Boolean) -> Unit) {
        locationPermissionContinuation = continuation
        locationPermission.launch(Manifest.permission.ACCESS_COARSE_LOCATION)
    }

    override fun goToMainScreen() {
        supportFragmentManager.popBackStack()
    }

    override fun selectCity(city: City) {
        goToMainScreen()
        val cwf = (supportFragmentManager.findFragmentByTag("CurrentWeather") as CurrentWeatherFragment)
        cwf.selectCity(city)
    }

    override fun showFavourites() {
        goToMainScreen()
        supportFragmentManager.beginTransaction()
            .add(R.id.overlay_fragment_container, FavFragment())
            .addToBackStack(null)
            .commit()
    }

    override fun showSearch() {
        supportFragmentManager.beginTransaction()
            .add(R.id.overlay_fragment_container, SearchFragment())
            .addToBackStack(null)
            .commit()
    }
}