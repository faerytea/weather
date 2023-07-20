package name.faerytea.t.express.current

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import name.faerytea.t.express.R
import name.faerytea.t.express.Router
import name.faerytea.t.express.databinding.FragmentCurrentWeatherBinding
import name.faerytea.t.express.location.LocationPermissionDelegate
import name.faerytea.t.express.utils.windDegreeToDirection
import name.faerytea.t.express.values.City
import name.faerytea.t.express.values.Weather

class CurrentWeatherFragment: Fragment() {
    private lateinit var binding: FragmentCurrentWeatherBinding
    private val vm: CurrentWeatherVM by viewModels()

    override fun onAttach(activity: Context) {
        super.onAttach(activity)
        vm.requestLocation(requireActivity() as LocationPermissionDelegate)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCurrentWeatherBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        lifecycleScope.launch(Dispatchers.Main) {
            vm.uiState.collect {
                when (it) {
                    UIState.NoLocation -> setupAsNoLocation()
                    UIState.Locating -> setupAsLocating()
                    is UIState.Loading -> setupAsLoading(it)
                    is UIState.NoConnection -> setupAsNoConnection(it)
                    is UIState.Ready -> setupNormal(it)
                }
            }
        }
        lifecycleScope.launch(Dispatchers.Main) {
            vm.favStatus.collect {
                if (it) {
                    binding.toggleFavButton.setImageResource(R.drawable.ic_star_24)
                    binding.toggleFavButton.setOnClickListener {
                        vm.unFavCurrent()
                    }
                } else {
                    binding.toggleFavButton.setImageResource(R.drawable.ic_star_outline_24)
                    binding.toggleFavButton.setOnClickListener {
                        vm.favCurrent()
                    }
                }
            }
        }
        binding.refresh.setOnRefreshListener {
            vm.refreshWeather(requireActivity() as LocationPermissionDelegate)
        }
        binding.searchButton.setOnClickListener {
            (requireActivity() as Router).showSearch()
        }
        binding.openFavButton.setOnClickListener {
            (requireActivity() as Router).showFavourites()
        }
    }

    fun selectCity(city: City) = vm.loadWeather(city, null)

    private fun setupAsNoLocation() {
        with(binding) {
            refresh.isRefreshing = false
            cityName.setText(R.string.cannot_find_you)
            weatherGroup.visibility = View.GONE
            errorPicture.visibility = View.VISIBLE
            errorPicture.setImageResource(R.drawable.ic_location_off_160)
        }
    }

    private fun setupAsNoConnection(state: UIState.NoConnection) {
        with(binding) {
            refresh.isRefreshing = false
            cityName.text = state.city.nameWithFlag()
            if (state.oldWeather == null) {
                weatherGroup.visibility = View.GONE
                errorPicture.visibility = View.VISIBLE
                errorPicture.setImageResource(R.drawable.ic_no_signal_160)
            } else {
                setupWeather(state.oldWeather)
            }
        }
    }

    private fun setupAsLocating() {
        with(binding) {
            refresh.isRefreshing = false
            cityName.setText(R.string.trying_to_find_you)
            weatherGroup.visibility = View.GONE
            errorPicture.visibility = View.VISIBLE
            errorPicture.setImageResource(R.drawable.ic_location_search_160)
//            errorPicture.animate()
        }
    }

    private fun setupAsLoading(state: UIState.Loading) {
        with(binding) {
            refresh.isRefreshing = true
            cityName.text = state.city.nameWithFlag()
            if (state.oldWeather == null) {
                errorPicture.visibility = View.GONE
                weatherGroup.visibility = View.GONE
            } else {
                setupWeather(state.oldWeather)
            }
        }
    }

    private fun setupNormal(state: UIState.Ready) {
        with(binding) {
            refresh.isRefreshing = false
            cityName.text = state.city.nameWithFlag()
            setupWeather(state.weather)
        }
    }

    private fun FragmentCurrentWeatherBinding.setupWeather(weather: Weather) {
        weatherGroup.visibility = View.VISIBLE
        errorPicture.visibility = View.GONE
        weatherPic.setImageResource(weather.type.picResId)
        weatherPic.contentDescription = getString(weather.type.textResId)
        temperature.text = getString(R.string.temperature_template, weather.temperature)
        humidity.text = getString(R.string.humidity_template, weather.humidity)
        wind.text = getString(
            R.string.wind_template,
            getString(windDegreeToDirection(weather.windDirectionDegrees)),
            weather.windSpeed
        )
    }
}