package ru.neoflex.cryptBot.model.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.neoflex.cryptBot.CryptBot;
import ru.neoflex.cryptBot.config.ApplicationContextProvider;
import ru.neoflex.cryptBot.entity.Candle;
import ru.neoflex.cryptBot.entity.User;
import ru.neoflex.cryptBot.model.BotState;
import ru.neoflex.cryptBot.model.BotStateChanger;
import ru.neoflex.cryptBot.model.CryptState;
import ru.neoflex.cryptBot.service.CandleService;
import ru.neoflex.cryptBot.service.MenuService;
import ru.neoflex.cryptBot.service.UserService;

import java.util.HashMap;
import java.util.Map;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class MessageHandler {
    private final UserService userService;
    private final MenuService menuService;
    private final CandleService candleService;
    private final EventHandler eventHandler;
    private final BotStateChanger botStateChanger;

    private Map<Long, ScheduledExecutorService> executorMap = new HashMap<>();

    public MessageHandler(UserService userService, MenuService menuService, CandleService candleService,
                          EventHandler eventHandler, BotStateChanger botStateChanger) {
        this.userService = userService;
        this.menuService = menuService;
        this.candleService = candleService;
        this.eventHandler = eventHandler;
        this.botStateChanger = botStateChanger;
    }

    public BotApiMethod<?> handle(Message message, BotState botState, CryptState cryptState) {
        long userId = message.getFrom().getId();
        long chatId = message.getChatId();
        User user = userService.findById(userId);
        //if new user
        if (!userService.isExist(userId)) {
            return eventHandler.saveNewUser(message, userId);
        }
        //save state in to cache
        botStateChanger.saveBotState(userId, botState);
        if(!user.isOn()) {
            try {
                switch (cryptState.name()) {
                    case ("BTC"):
                    case ("ETH"):
                    case ("BNB"):
                    case ("DOGE"):
                    case ("DOT"):
                    case ("ADA"):
                        user.setOn(true);
                        userService.save(user);
                        // just repeatable messages
                        CryptBot telegramBot = ApplicationContextProvider.getApplicationContext().getBean(CryptBot.class);
                        executorMap.put(userId, Executors.newSingleThreadScheduledExecutor());
                        TimerTask task = new TimerTask() {
                            @Override
                            public void run() {
                                try {
                                    Candle candle = candleService.findById(cryptState.name());
                                    telegramBot.execute(new SendMessage(String.valueOf(chatId), candle.getFigi() +
                                            "\r\nInterval: " + candle.getInterval() + "\r\nLow: " + candle.getLow() +
                                            "\r\nHigh: " + candle.getHigh() + "\r\nOpen: " + candle.getOpen() +
                                            "\r\nClose: " + candle.getClose() + "\r\nOpen time: " + candle.getOpenTime()));
                                } catch (TelegramApiException e) {
                                    botStateChanger.saveBotState(userId, BotState.UNSUBSCRIBE_ACCEPTED);
                                    executorMap.get(userId).shutdown();
                                }
                            }
                        };
                        int period = botState.equals(BotState.SUBSCRIBE_FOR_CHANGE_ACCEPTED) ?
                                (int)(Math.random() * ((50 - 1) + 1)) : 3;
                        executorMap.get(userId).scheduleAtFixedRate(task, 0, period, TimeUnit.SECONDS);
                        return menuService.getMainMenuMessage(message.getChatId(),
                                "Меню к Вашим услугам.", userId);
                }
            } catch (NullPointerException ex) {
                log.error("User is not subscribed.");
            }
        }
        try {
            switch (botState.name()) {
                case ("START"):
                    return menuService.getMainMenuMessage(message.getChatId(),
                            "Добро пожаловать, в данном боте Вы можете получать актуальную информацию о " +
                                    "цене популярных криптовалют, для этого Вам необходимо подписаться. Чтобы это " +
                                    "сделать, нажмите на cоответствующую кнопку из меню ниже и следуйте инструкции. " +
                                    "Да прибудет с Вами богатство.", userId);
                case ("HELP"):
                    return menuService.getMainMenuMessage(message.getChatId(),
                            "В меню расположены 3 кнопки:\r\n" +
                                    "1. Подписаться на криптовалюту - данная подписка подразумевает, что Вы будете " +
                                    "получать актуальные данные по цене на выбранную Вами криптовалюту каждые 3 секунды." +
                                    "\r\n2.Подписаться на падение курса валюты - данная подписка подразумевает, что Вы " +
                                    "будете получать уведомления об изменении цены на выбранную Вами криптовалюту на Х%, " +
                                    "где Х - заданный Вами процент.\r\n" +
                                    "3. Помощь - ну тут Вы уже сами все поняли.\r\n" +
                                    "Если Вы хотите перестать получать сообщения от меня, то в списке ваших каналов:" +
                                    "\r\nЕсли вы используете компьютер, то нажмите правой кнопкой мыши и выберите " +
                                    "'Остановить и блокировать.';\r\n" +
                                    "Если вы используете смартфон, то зажмите канал с ботом и выберите 'Заблокировать'" +
                                    "\r\nВНИМАНИЕ, если Вы удалили чат, при этом не отписались от уведомлений, " +
                                    "у Вас пропало меню и вы не знаете как дальше жить, просто напишите мне любое" +
                                    " сообщение и меню вернется.",
                            userId);
                case ("SUBSCRIBE_FOR_3_SECONDS"):
                case ("SUBSCRIBE_FOR_CHANGE_ACCEPTED"):
                    return menuService.getSelectCryptMenuMessage(message.getChatId(), "Выберите криптовалюту, " +
                            "которую Вы хотите отслеживать.", userId);
                case ("SUBSCRIBE_FOR_CHANGE"):
                    return new SendMessage(String.valueOf(userId),"Введите количество процентов (0-100, число должно " +
                            "быть целым), при падении на которые Вы хотите получать уведомление.");
                case ("UNKNOWN_COMMAND"):
                    return menuService.getMainMenuMessage(message.getChatId(),
                            "Это вне моего сознания, пожалуйста, выберите действие из меню.", userId);
                case ("UNSUBSCRIBE"):
                    return menuService.getUnsubscribeMenuMessage(message.getChatId(),
                            "Вы действительно хотите отписаться?", userId);
                case ("UNSUBSCRIBE_ACCEPTED"):
                    try {
                        executorMap.get(userId).shutdown();
                    } catch (NullPointerException ex){
                        botStateChanger.saveBotState(userId, BotState.MENU);
                    }
                    return menuService.getMainMenuMessage(message.getChatId(),
                            "Вы успешно отписались.", userId);
                case ("MENU"):
                    return menuService.getMainMenuMessage(message.getChatId(),
                            "Меню к Вашим услугам.", userId);
                default:
                    throw new IllegalStateException("Unexpected value: " + botState);
            }
        } catch (NullPointerException ex) {
            botStateChanger.saveBotState(userId, BotState.START);
            return menuService.getMainMenuMessage(message.getChatId(),
                    "Добро пожаловать, в данном боте Вы можете получать актуальную информацию о " +
                            "цене популярных криптовалют, для этого Вам необходимо подписаться. Чтобы это " +
                            "сделать, нажмите на cоответствующую кнопку из меню ниже и следуйте инструкции. " +
                            "Да прибудет с Вами богатство.", userId);
        }
    }
}