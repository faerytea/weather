package name.faerytea.t.express.search

import name.faerytea.t.express.values.City

sealed interface ListState {
    object Loading: ListState

    class Cities(val cities: List<City>): ListState
}