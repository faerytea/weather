package name.faerytea.t.express.values

import name.faerytea.t.express.utils.countryNameToCountryFlag

data class City(
    /** Unique name of place, used as id */
    val name: String,
    /** Translated city name */
    val localName: String,
    /** Locale of translated name */
    val locale: String,
    val latitude: Double,
    val longitude: Double,
    /** CC as described in ISO 3166-1 */
    val countryCode: String,
) {
    fun nameWithFlag() = countryNameToCountryFlag(countryCode) + " " + localName
}