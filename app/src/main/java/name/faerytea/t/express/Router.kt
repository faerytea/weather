package name.faerytea.t.express

import name.faerytea.t.express.values.City

interface Router {
    fun goToMainScreen()
    fun selectCity(city: City)
    fun showFavourites()
    fun showSearch()
}