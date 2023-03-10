package bot

import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.bot
import com.github.kotlintelegrambot.dispatch
import com.github.kotlintelegrambot.dispatcher.command
import com.github.kotlintelegrambot.entities.ChatId
import data.remote.repository.WeatherRepository

private const val BOT_ANSWER_TIMEOUT = 30 // timeout, в котором бот может дать ответ
private const val BOT_TOKEN = "5975392537:AAFRa5MjfJOwNUkdAwh27pQo7ivRDKr7_x4" // в идеале выносится на отдельный сервер


class WeatherBot(private val weatherRepository: WeatherRepository) {

    private var _chatId: ChatId? = null
    private val chatId: ChatId get() = requireNotNull(_chatId) // выкидывает значение, если не null иначе Exception
    fun createBot(): Bot{
        return bot {
            timeout = BOT_ANSWER_TIMEOUT
            token = BOT_TOKEN

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
    }
}