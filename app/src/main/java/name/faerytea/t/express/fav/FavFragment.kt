package name.faerytea.t.express.fav

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.launch
import name.faerytea.t.express.App
import name.faerytea.t.express.CityListAdapter
import name.faerytea.t.express.Router
import name.faerytea.t.express.databinding.FragmentFavBinding
import name.faerytea.t.express.repository.Repository
import javax.inject.Inject

class FavFragment: Fragment() {
    private lateinit var binding: FragmentFavBinding
    private lateinit var cityListAdapter: CityListAdapter

    @Inject
    lateinit var repository: Repository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        App.component.inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFavBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding.favList) {
            layoutManager = LinearLayoutManager(context)
            cityListAdapter = CityListAdapter(requireActivity() as Router)
            adapter = cityListAdapter
        }
        lifecycleScope.launch {
            cityListAdapter.cities = repository.favList()
        }
    }
}