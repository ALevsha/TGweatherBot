package data.remote.models.nominatim

data class Address(
    val ISO31662lvl4: String,
    val city: String,
    val country: String,
    val country_code: String,
    val postcode: String,
    val state: String,
    val state_district: String
)