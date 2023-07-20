package name.faerytea.t.express.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.launch
import name.faerytea.t.express.CityListAdapter
import name.faerytea.t.express.Router
import name.faerytea.t.express.databinding.FragmentSearchBinding

class SearchFragment: Fragment() {
    private lateinit var binding: FragmentSearchBinding
    private lateinit var cityListAdapter: CityListAdapter

    private val vm: SearchVM by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.doSearchButton.setOnClickListener { runSearch() }
        binding.cityInput.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                runSearch()
                return@setOnEditorActionListener true
            }
            return@setOnEditorActionListener false
        }
        cityListAdapter = CityListAdapter(requireActivity() as Router)
        with(binding.searchResults) {
            layoutManager = LinearLayoutManager(context)
            adapter = cityListAdapter
        }

        lifecycleScope.launch {
            vm.uiState.collect {
                when (it) {
                    ListState.Loading -> {
                        binding.searchResults.visibility = View.GONE
                        binding.pbOnSearch.visibility = View.VISIBLE
                    }
                    is ListState.Cities -> {
                        binding.searchResults.visibility = View.VISIBLE
                        binding.pbOnSearch.visibility = View.GONE
                        cityListAdapter.cities = it.cities
                    }
                }
            }
        }
    }

    private fun runSearch() {
        val text = binding.cityInput.text.toString()
        if (text.isNotBlank()) {
            vm.search(text)
        }
    }
}