package data.remote.repository

import data.remote.api.CityApi
import data.remote.api.WeatherApi
import data.remote.models.nominatim.City
import data.remote.models.openweathermap.CurrentWeather
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class WeatherRepository(
    private val weatherApi: WeatherApi,
    private val reversedGeocodingApi: CityApi
) {

    suspend fun getCurrentWeather(lat: String, lon: String, apiKey: String): CurrentWeather {
        return withContext(Dispatchers.IO){
            weatherApi.getCurrentWeather(lat, lon, apiKey)
        }
    }

    suspend fun getCityCoord(city: String, country: String, format: String): List<City> {
        return withContext(Dispatchers.IO){
            reversedGeocodingApi.getCityCoord(city, country, format)
        }
    }

    suspend fun getCityName(lat: String, lon: String): City{
        return withContext(Dispatchers.IO){
            reversedGeocodingApi.getCityName(lat, lon)
        }
    }
}