package data.remote.models.nominatim

data class City(
    val address: Address,
    val boundingbox: List<String>,
    val `class`: String,
    val display_name: String,
    val extratags: Extratags,
    val icon: String,
    val importance: Double,
    val lat: String,
    val licence: String,
    val lon: String,
    val osm_id: String,
    val osm_type: String,
    val place_id: String,
    val type: String
)