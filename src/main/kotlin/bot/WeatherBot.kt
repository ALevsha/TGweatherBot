package bot

import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.bot
import com.github.kotlintelegrambot.dispatch
import com.github.kotlintelegrambot.dispatcher.callbackQuery
import com.github.kotlintelegrambot.dispatcher.command
import com.github.kotlintelegrambot.dispatcher.location
import com.github.kotlintelegrambot.dispatcher.message
import com.github.kotlintelegrambot.entities.ChatAction
import com.github.kotlintelegrambot.entities.ChatId
import com.github.kotlintelegrambot.entities.InlineKeyboardMarkup
import com.github.kotlintelegrambot.entities.TelegramFile
import com.github.kotlintelegrambot.entities.keyboard.InlineKeyboardButton
import data.remote.API_KEY
import data.remote.models.openweathermap.Coord
import data.remote.repository.WeatherRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

private const val GIF_WAITING_URL = "https://media.tenor.com/OBEfKgDoCogAAAAC/pulp-fiction-john-travolta.gif"

private const val BOT_ANSWER_TIMEOUT = 30 // timeout, в котором бот может дать ответ
private const val BOT_TOKEN = "5975392537:AAFRa5MjfJOwNUkdAwh27pQo7ivRDKr7_x4" // в идеале выносится на отдельный сервер


class WeatherBot(private val weatherRepository: WeatherRepository) {

    private lateinit var cityCoord: Coord
    private lateinit var cityName: String
    private var _chatId: ChatId? = null
    private val chatId by lazy { requireNotNull(_chatId) } // выкидывает значение, если не null иначе Exception
    fun createBot(): Bot {
        return bot {
            timeout = BOT_ANSWER_TIMEOUT
            token = BOT_TOKEN
            logLevel = com.github.kotlintelegrambot.logging.LogLevel.Error

            dispatch {// внутри этой функции происходит ответ на запросы пользователя
                setUpCommands()
                setUpCallbacks()
            }
        }
    }

    private fun com.github.kotlintelegrambot.dispatcher.Dispatcher.setUpCallbacks() {
        callbackQuery(callbackData = "getMyLocation") {
            bot.sendMessage(chatId = chatId, text = "Отправь мне свою локацию")
            location {
                CoroutineScope(Dispatchers.IO).launch {
                    val userCity = weatherRepository.getCityName(
                        location.latitude.toString(),
                        location.longitude.toString()
                    )

                    cityCoord = Coord(userCity.lat.toDouble(), userCity.lon.toDouble())
                    cityName = userCity.display_name

                    val inlineKeyboardMarkup = InlineKeyboardMarkup.create(
                        listOf(
                            InlineKeyboardButton.CallbackData(
                                text = "Да, верно",
                                callbackData = "yes_label"
                            )
                        )
                    )

                    bot.sendMessage(
                        chatId = chatId,
                        text = "Твой город - ${cityName}, верно? \n Если неверно, скинь локацию еще раз",
                        replyMarkup = inlineKeyboardMarkup
                    )
                }
            }
        }

        callbackQuery(callbackData = "enterManually") {
            bot.sendMessage(chatId = chatId, text = "Хорошо, введи свой город.")
            message(com.github.kotlintelegrambot.extensions.filters.Filter.Text) {
                cityName = message.text.toString()

                val inlineKeyboardMarkup = InlineKeyboardMarkup.create(
                    listOf(
                        InlineKeyboardButton.CallbackData(
                            text = "Да, верно",
                            callbackData = "yes_label"
                        )
                    )
                )

                CoroutineScope(Dispatchers.IO).launch{
                    val request = weatherRepository.getCityCoord(
                        city = cityName,
                        country = "Russia",
                        format = "json"
                    )

                    cityCoord = Coord(request[0].lat.toDouble(),request[0].lon.toDouble())
                }


                bot.sendMessage(
                    chatId = chatId,
                    text = "Твой город - ${cityName}, верно? \n Если неверно, введи свой город еще раз",
                    replyMarkup = inlineKeyboardMarkup
                )

            }
        }

        callbackQuery(callbackData = "yes_label") {
            bot.apply {
                sendAnimation(
                    chatId = chatId,
                    animation = TelegramFile.ByUrl(GIF_WAITING_URL)
                )

                sendMessage(
                    chatId = chatId,
                    text = "Узнаем вашу погоду..."
                )

                sendChatAction(
                    chatId = chatId,
                    action = ChatAction.TYPING
                )
            }
            CoroutineScope(Dispatchers.IO).launch {
                val currentWeather = weatherRepository.getCurrentWeather(
                    lat = cityCoord.lat.toString(),
                    lon = cityCoord.lon.toString(),
                    apiKey = API_KEY
                )
                bot.sendMessage(
                    chatId = chatId,
                    text = """
                        ☁ Облачность: ${currentWeather.clouds.all}%
                        🌡 Температура (градусы): ${(currentWeather.main.temp - 273).toInt()}
                        🙎 ‍Ощущается как: ${(currentWeather.main.feels_like - 273).toInt()}
                        💧 Влажность: ${currentWeather.main.humidity}%
                        🌪 Направление ветра (азимут): ${currentWeather.wind.deg}
                        🌪 Скорость ветра (м/с): ${currentWeather.wind.speed}
                        🧭 Давление: ${currentWeather.main.pressure}
                    """.trimIndent()
                    )
                bot.sendMessage(chatId = chatId, text = "Если вы хотите запросить погоду еще раз,\n" +
                        " воспользуйтесь командой /weather")
                cityCoord = Coord(0.0,0.0)
            }
        }
    }

    private fun com.github.kotlintelegrambot.dispatcher.Dispatcher.setUpCommands() {
        command("start") {
            _chatId = ChatId.fromId(message.chat.id)
            bot.sendMessage(
                chatId = chatId,
                text = "Привет, я бот, который отображает погоду! \n Для запуска бота введи команду /weather"
            )
        }

        command("weather") {
            val inlineKeyboardMarkup = InlineKeyboardMarkup.create(
                listOf(
                    InlineKeyboardButton.CallbackData(
                        text = "Определить мой город (для мобильных устройств)",
                        callbackData = "getMyLocation"
                    )
                ),
                listOf(
                    InlineKeyboardButton.CallbackData(
                        text = "Ввести город вручную",
                        callbackData = "enterManually"
                    )
                )
            )

            bot.sendMessage(
                chatId = chatId,
                text = "Для того, чтобы я смог отправить тебе погоду, \n мне нужно знать твой город.",
                replyMarkup = inlineKeyboardMarkup
            )
        }
    }
}