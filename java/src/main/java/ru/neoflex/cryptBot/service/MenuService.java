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
            log.info("User is unsubscribe. Add subscribe for 3 seconds button in first row.");
            row1.add((new KeyboardButton("Подписаться на криптовалюту")));
            log.info("Add subscribe for percent button in second row.");
            row2.add(new KeyboardButton("Подписаться на падение курса валюты"));
        } else {
            log.info("User is subscribe. Add unsubscribe button in third row.");
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
        log.info("Return ready main keyboard markup.");
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
        log.info("Method getSelectCryptMenuKeyboard was called for user {}", userId);
        log.info("Create keyboard markup.");
        final ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        log.info("Set selective true to select crypt keyboard.");
        replyKeyboardMarkup.setSelective(true);
        log.info("Set resize true to select crypt keyboard");
        replyKeyboardMarkup.setResizeKeyboard(true);
        log.info("Set one time key true to select crypt keyboard.");
        replyKeyboardMarkup.setOneTimeKeyboard(false);
        log.info("Get user {} from database.", userId);
        User user = userService.findById(userId);
        log.info("Check if user is unsubscribe.");
        if(!user.isOn()){
            log.info("User is subscribe. Create list of keyboard rows.");
            List<KeyboardRow> keyboard = new ArrayList<>();
            log.info("Created first keyboard row.");
            KeyboardRow row1 = new KeyboardRow();
            log.info("Created second keyboard row.");
            KeyboardRow row2 = new KeyboardRow();
            log.info("Created third keyboard row.");
            KeyboardRow row3 = new KeyboardRow();
            log.info("Add BTC button in first row.");
            row1.add((new KeyboardButton("BTC")));
            log.info("Add ETH button in first row.");
            row1.add((new KeyboardButton("ETH")));
            log.info("Add BNB button in first row.");
            row1.add((new KeyboardButton("BNB")));
            log.info("Add DOGE button in second row.");
            row2.add(new KeyboardButton("DOGE"));
            log.info("Add DOT button in second row.");
            row2.add(new KeyboardButton("DOT"));
            log.info("Add ADA button in second row.");
            row2.add(new KeyboardButton("ADA"));
            log.info("Add in menu button in third row.");
            row3.add(new KeyboardButton("В меню"));
            log.info("Add first keyboard row to select crypt keyboard.");
            keyboard.add(row1);
            log.info("Add second keyboard row to select crypt keyboard.");
            keyboard.add(row2);
            log.info("Add third keyboard row to select crypt keyboard.");
            keyboard.add(row3);
            log.info("Set keyboard to keyboard markup.");
            replyKeyboardMarkup.setKeyboard(keyboard);
        } else {
            log.info("User is subscribe, return main keyboard.");
            return getMainMenuKeyboard(userId);
        }
        log.info("Return ready select crypt keyboard markup.");
        return replyKeyboardMarkup;
    }

    public SendMessage getUnsubscribeMenuMessage(final long chatId, final String textMessage, final long userId) {
        log.info("Method getUnsubscribeMenuMessage called for user with id {} and message '{}' from chat {}", userId,
                textMessage, chatId);
        final ReplyKeyboardMarkup replyKeyboardMarkup = getUnsubscribeMenuKeyboard(userId);
        log.info("Created reply keyboard from method 'getUnsubscribeMenuKeyboard' for user {}", userId);
        return createMessageWithKeyboard(chatId, textMessage, replyKeyboardMarkup);
    }

    //Unsubscribe select menu
    private ReplyKeyboardMarkup getUnsubscribeMenuKeyboard(long userId) {
        log.info("Method getUnsubscribeMenuKeyboard was called for user {}", userId);
        log.info("Create keyboard markup.");
        final ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        log.info("Set selective true to unsubscribe keyboard.");
        replyKeyboardMarkup.setSelective(true);
        log.info("Set resize true to unsubscribe keyboard");
        replyKeyboardMarkup.setResizeKeyboard(true);
        log.info("Set one time key true to unsubscribe keyboard.");
        replyKeyboardMarkup.setOneTimeKeyboard(false);
        log.info("Get user {} from database.", userId);
        User user = userService.findById(userId);
        log.info("Check if user is unsubscribe.");
        if(user.isOn()){
            log.info("User is subscribe. Create list of keyboard rows.");
            List<KeyboardRow> keyboard = new ArrayList<>();
            log.info("Created first keyboard row.");
            KeyboardRow row1 = new KeyboardRow();
            log.info("Add yes button in first row.");
            row1.add((new KeyboardButton("Да")));
            log.info("Add no button in first row.");
            row1.add((new KeyboardButton("Нет")));
            log.info("Add first keyboard row to unsubscribe keyboard.");
            keyboard.add(row1);
            log.info("Set keyboard to keyboard markup.");
            replyKeyboardMarkup.setKeyboard(keyboard);
        } else {
            log.info("User is subscribe, return main keyboard.");
            return getMainMenuKeyboard(userId);
        }
        log.info("Return ready unsubscribe keyboard markup.");
        return replyKeyboardMarkup;
    }

    private SendMessage createMessageWithKeyboard(final long chatId,
                                                  String textMessage,
                                                  final ReplyKeyboardMarkup replyKeyboardMarkup) {
        log.info("Call createMessageWithKeyboard method.");
        log.info("Create SendMessage object.");
        final SendMessage sendMessage = new SendMessage();
        log.info("Set enable markdown true to SendMessage object.");
        sendMessage.enableMarkdown(true);
        log.info("Set chat id {} to SendMessage object.", chatId);
        sendMessage.setChatId(String.valueOf(chatId));
        log.info("Set text {} to SendMessage object.", textMessage);
        sendMessage.setText(textMessage);
        log.info("Check if reply keyboard markup is null.");
        if (replyKeyboardMarkup != null) {
            log.info("Reply keyboard markup is null. Set reply markup.");
            sendMessage.setReplyMarkup(replyKeyboardMarkup);
        }
        log.info("Return SendMessage object.");
        return sendMessage;
    }
}
