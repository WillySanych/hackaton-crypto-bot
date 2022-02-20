package ru.neoflex.cryptBot.service;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import ru.neoflex.cryptBot.entity.User;

import java.util.ArrayList;
import java.util.List;

@Service
@Getter
@Setter
@Slf4j
public class MenuService {
    private final UserService userService;

    public MenuService(UserService userService) {
        this.userService = userService;
    }

    public SendMessage getMainMenuMessage(final long chatId, final String textMessage, final long userId) {
        log.info("Method getMainMenuMessage called for user with id {} and message '{}' from chat {}", userId,
                textMessage, chatId);
        final ReplyKeyboardMarkup replyKeyboardMarkup = getMainMenuKeyboard(userId);
        log.info("Return reply keyboard from method 'getMainMenuKeyboard' for user {}", userId);
        return createMessageWithKeyboard(chatId, textMessage, replyKeyboardMarkup);
    }

    //Main menu
    private ReplyKeyboardMarkup getMainMenuKeyboard(long userId) {
        log.info("Method getMainMenuKeyboard was called for user {}", userId);
        log.info("Create keyboard markup.");
        final ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        log.info("Set selective true to main keyboard.");
        replyKeyboardMarkup.setSelective(true);
        log.info("Set resize true to main keyboard");
        replyKeyboardMarkup.setResizeKeyboard(true);
        log.info("Set one time key true to main keyboard.");
        replyKeyboardMarkup.setOneTimeKeyboard(false);
        log.info("Get user {} from database.", userId);
        User user = userService.findById(userId);
        log.info("Creating list of keyboard rows.");
        List<KeyboardRow> keyboard = new ArrayList<>();

        log.info("Created first keyboard row.");
        KeyboardRow row1 = new KeyboardRow();
        log.info("Created second keyboard row.");
        KeyboardRow row2 = new KeyboardRow();
        log.info("Created third keyboard row.");
        KeyboardRow row3 = new KeyboardRow();
        log.info("Check if user is unsubscribe.");
        if(!user.isOn()) {
            log.info("User is unsubscribe. Add subscribe for 3 seconds button.");
            row1.add((new KeyboardButton("Подписаться на криптовалюту")));
            log.info("Add subscribe for percent button.");
            row2.add(new KeyboardButton("Подписаться на падение курса валюты"));
        } else {
            log.info("User is subscribe. Add unsubscribe button.");
            row1.add((new KeyboardButton("Отписаться")));
        }
        log.info("Add help button.");
        row3.add(new KeyboardButton("Помощь"));

        log.info("Add first keyboard row to main keyboard.");
        keyboard.add(row1);
        log.info("Add second keyboard row to main keyboard.");
        keyboard.add(row2);
        log.info("Add third keyboard row to main keyboard.");
        keyboard.add(row3);

        log.info("Set keyboard to keyboard markup.");
        replyKeyboardMarkup.setKeyboard(keyboard);
        log.info("Return ready keyboard markup.");
        return replyKeyboardMarkup;
    }

    public SendMessage getSelectCryptMenuMessage(final long chatId, final String textMessage, final long userId) {
        log.info("Method getSelectCryptMenuMessage called for user with id {} and message '{}' from chat {}", userId,
                textMessage, chatId);
        final ReplyKeyboardMarkup replyKeyboardMarkup = getSelectCryptMenuKeyboard(userId);
        log.info("Created reply keyboard from method 'getSelectCryptMenuMessage' for user {}", userId);
        return createMessageWithKeyboard(chatId, textMessage, replyKeyboardMarkup);
    }

    //Crypt select menu
    private ReplyKeyboardMarkup getSelectCryptMenuKeyboard(long userId) {

        final ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(false);
        User user = userService.findById(userId);
        if(!user.isOn()){
            List<KeyboardRow> keyboard = new ArrayList<>();
            KeyboardRow row1 = new KeyboardRow();
            KeyboardRow row2 = new KeyboardRow();
            KeyboardRow row3 = new KeyboardRow();
            row1.add((new KeyboardButton("BTC")));
            row1.add((new KeyboardButton("ETH")));
            row1.add((new KeyboardButton("BNB")));
            row2.add(new KeyboardButton("DOGE"));
            row2.add(new KeyboardButton("DOT"));
            row2.add(new KeyboardButton("ADA"));
            row3.add(new KeyboardButton("В меню"));
            keyboard.add(row1);
            keyboard.add(row2);
            keyboard.add(row3);
            replyKeyboardMarkup.setKeyboard(keyboard);
        } else {
            return getMainMenuKeyboard(userId);
        }
        return replyKeyboardMarkup;
    }

    public SendMessage getUnsubscribeMenuMessage(final long chatId, final String textMessage, final long userId) {
        final ReplyKeyboardMarkup replyKeyboardMarkup = getUnsubscribeMenuKeyboard(userId);
        return createMessageWithKeyboard(chatId, textMessage, replyKeyboardMarkup);
    }

    //Unsubscribe select menu
    private ReplyKeyboardMarkup getUnsubscribeMenuKeyboard(long userId) {

        final ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(false);
        User user = userService.findById(userId);
        if(user.isOn()){
            List<KeyboardRow> keyboard = new ArrayList<>();
            KeyboardRow row1 = new KeyboardRow();
            row1.add((new KeyboardButton("Да")));
            row1.add((new KeyboardButton("Нет")));
            keyboard.add(row1);
            replyKeyboardMarkup.setKeyboard(keyboard);
        } else {
            return getMainMenuKeyboard(userId);
        }
        return replyKeyboardMarkup;
    }

    private SendMessage createMessageWithKeyboard(final long chatId,
                                                  String textMessage,
                                                  final ReplyKeyboardMarkup replyKeyboardMarkup) {
        final SendMessage sendMessage = new SendMessage();
        sendMessage.enableMarkdown(true);
        sendMessage.setChatId(String.valueOf(chatId));
        sendMessage.setText(textMessage);
        if (replyKeyboardMarkup != null) {
            sendMessage.setReplyMarkup(replyKeyboardMarkup);
        }
        return sendMessage;
    }
}
