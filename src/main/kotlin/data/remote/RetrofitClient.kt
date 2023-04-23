package data.remote

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import data.remote.api.CityApi
import data.remote.api.WeatherApi
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

private const val WEATHER_BASE_URL = "https://api.openweathermap.org/data/2.5/" // предоставляет данные погоды
private const val REVERSE_GEOCODER_BASE_URL = "https://nominatim.openstreetmap.org" // выдает город по координатам
const val API_KEY = "701497acb82a25be030501d0155f7d8c"

enum class RetrofitType(val baseUrl: String) {
    WEATHER(WEATHER_BASE_URL),
    REVERSE_GEOCODER(REVERSE_GEOCODER_BASE_URL)
}

class RetrofitClient {

    private fun getClient(): OkHttpClient { // класс для получения логов от сервера
        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BODY // уровень логгирования BODY - тело запроса
        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(logging) // добавить перехватчика запросов и ответов
        return okHttpClient.build()
    }

    fun getRetrofit(retrofitType: RetrofitType): Retrofit {
        return Retrofit.Builder()
            .baseUrl(retrofitType.baseUrl) // Тип url
            .client(getClient())
            .addCallAdapterFactory(CoroutineCallAdapterFactory.invoke()) // фабрика поддержки корутин
            .addConverterFactory(GsonConverterFactory.create()) // фабрика конвертации (зависит от ответа сервера)
            .build()
    }

    fun getWeatherApi(retrofit: Retrofit): WeatherApi { // возвращает Api погоды
        return retrofit.create(WeatherApi::class.java)
    }

    fun getCityApi(retrofit: Retrofit): CityApi { // возвращает Api геолокации
        return retrofit.create(CityApi::class.java)
    }

}