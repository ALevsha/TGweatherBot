package data.remote.api

import kotlinx.coroutines.Deferred
import retrofit2.http.GET
import retrofit2.http.Query

interface ReverseGeocodingApi {

    @GET("reverse")
    fun getCurrentWeather(
        // здесь указываются обязательные (и не очень) параметры запроса согласно документации
        @Query("lat") latitude: String, // широта
        @Query("lon") longitude: String, // долгота
        @Query("format") formatData: String // формат получения данных (gson)
    ): Deferred<> // Спец тип от корутин. Указывается тип модели, которая приходит с сервера
}









