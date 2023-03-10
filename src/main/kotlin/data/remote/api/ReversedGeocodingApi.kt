package data.remote.api

import data.remote.models.ReversedCountry
import kotlinx.coroutines.Deferred
import retrofit2.http.GET
import retrofit2.http.Query

interface ReversedGeocodingApi {

    @GET("reverse")
    fun getCountryName(
        // здесь указываются обязательные (и не очень) параметры запроса согласно документации
        @Query("lat") latitude: String, // широта
        @Query("lon") longitude: String, // долгота
        @Query("format") formatData: String // формат получения данных (gson)
    ): Deferred<ReversedCountry> // Спец тип от корутин. Указывается тип модели, которая приходит с сервера
}









