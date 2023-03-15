package bot

import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.bot
import com.github.kotlintelegrambot.dispatch
import com.github.kotlintelegrambot.dispatcher.command
import com.github.kotlintelegrambot.dispatcher.text
import com.github.kotlintelegrambot.entities.ChatId
import com.github.kotlintelegrambot.entities.InlineKeyboardMarkup
import com.github.kotlintelegrambot.entities.keyboard.InlineKeyboardButton
import data.remote.repository.WeatherRepository
import jdk.jfr.internal.LogLevel

private const val BOT_ANSWER_TIMEOUT = 30 // timeout, в котором бот может дать ответ
private const val BOT_TOKEN = "5975392537:AAFRa5MjfJOwNUkdAwh27pQo7ivRDKr7_x4" // в идеале выносится на отдельный сервер


class WeatherBot(private val weatherRepository: WeatherRepository) {

    private var _chatId: ChatId? = null
    private val chatId by lazy { requireNotNull(_chatId)} // выкидывает значение, если не null иначе Exception
    fun createBot(): Bot{
        return bot {
            timeout = BOT_ANSWER_TIMEOUT
            token = BOT_TOKEN
            logLevel = com.github.kotlintelegrambot.logging.LogLevel.Error

            dispatch {// внутри этой функции происходит ответ на запросы пользователя
                setUpCommands()
            }
        }
    }

    private fun com.github.kotlintelegrambot.dispatcher.Dispatcher.setUpCommands() {
        command("start"){
            _chatId = ChatId.fromId(message.chat.id)
            bot.sendMessage(
                chatId = chatId,
                text = "Привет, я бот, который отображает погоду! \n Для запуска бота введи команду /weather"
                )
        }

        command("weather"){
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