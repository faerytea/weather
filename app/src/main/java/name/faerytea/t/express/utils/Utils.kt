package name.faerytea.t.express.utils

import androidx.annotation.StringRes
import name.faerytea.t.express.R
import name.faerytea.t.express.values.City

/** Planet Earth symbol */
private const val NO_SUCH_COUNTRY = "\uD83C\uDF0D"

private const val FLAG_SHIFT = '\uDDE6'.code - 'A'.code

fun countryNameToCountryFlag(name: String): String {
    if (name.length != 2 || name == "??") {
        return NO_SUCH_COUNTRY
    }
    val arr = name.uppercase()
    val resArr = CharArray(4)
    for (i in arr.indices) {
        if (arr[i] !in 'A'..'Z') return NO_SUCH_COUNTRY
        resArr[i * 2] = '\uD83C'
        resArr[i * 2 + 1] = (arr[i] + FLAG_SHIFT)
    }
    return String(resArr)
}

@StringRes
fun windDegreeToDirection(deg: Int): Int = when (deg) {
    in 0..22, in 338..360 -> R.string.wind_n
    in 23..67 -> R.string.wind_ne
    in 68..112 -> R.string.wind_e
    in 113..157 -> R.string.wind_se
    in 158..202 -> R.string.wind_s
    in 203..247 -> R.string.wind_sw
    in 248..292 -> R.string.wind_w
    in 293..337 -> R.string.wind_nw
    else -> R.string.wind_unknown
}

fun mkFakeCityFromCoordinates(
    latitude: Double, longitude: Double
) = City(
    "$latitude; $longitude",
    "$latitude; $longitude",
    "en",
    latitude,
    longitude,
    "??",
)