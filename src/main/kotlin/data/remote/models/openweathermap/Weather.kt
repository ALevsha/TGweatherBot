package data.remote.models.openweathermap

data class Weather(
    val description: String,
    val icon: String,
    val id: Int,
    val main: String
)