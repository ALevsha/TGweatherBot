import bot.WeatherBot
import data.remote.RetrofitClient
import data.remote.RetrofitType
import data.remote.repository.WeatherRepository

fun main() {
    /**
     * Сперва создаем объекты Retrofit
     * потом экземпляры Api
     * далее - репозиторий
     * потом бота
     * далее запускаем polling (опрос)
     */
    val weatherRetrofit = RetrofitClient().getRetrofit(RetrofitType.WEATHER)
    val reverseRetrofit = RetrofitClient().getRetrofit(RetrofitType.REVERSE_GEOCODER)
    val weatherApi = RetrofitClient().getWeatherApi(weatherRetrofit)
    val cityApi = RetrofitClient().getCityApi(reverseRetrofit)
    val weatherRepository = WeatherRepository(weatherApi, cityApi)
    val weatherBot = WeatherBot(weatherRepository).createBot()
    weatherBot.startPolling()
}