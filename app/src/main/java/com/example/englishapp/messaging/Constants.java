package com.example.englishapp.messaging;

import java.util.HashMap;

public class Constants {
    public static final String KEY_COLLECTION_USERS = "USERS";
    public static final String KEY_USER_UID = "USER_UID";
    public static final String KEY_NAME = "NAME";
    public static final String KEY_EMAIL = "EMAIL";
    public static final String KEY_MOBILE = "MOBILE";
    public static final String KEY_BOOKMARKS = "BOOKMARKS";
    public static final String KEY_SCORE = "TOTAL_SCORE";
    public static final String KEY_DOB = "DATE_OF_BIRTH";
    public static final String KEY_GENDER = "GENDER";
    public static final String KEY_PROFILE_IMG = "PATH_TO_IMG";
    public static final String KEY_TOTAL_USERS = "TOTAL_USERS";
    public static final String PATH_PROFILE_IMG = "PROFILE_IMAGES";
    public static final String NAME_USER_PROFILE_IMG = "PROFILE_IMAGE";
    public static final String KEY_FCM_TOKEN = "FCM_TOKEN";
    public static final String KEY_CHOSEN_USER_DATA = "USER_DATA";
    public static final String KEY_COLLECTION_CHAT = "CHAT";
    public static final String KEY_SENDER_ID = "SENDER_ID";
    public static final String KEY_RECEIVER_ID = "RECEIVER_ID";
    public static final String KEY_MESSAGE = "MESSAGE_TEXT";
    public static final String KEY_TIME_STAMP = "TIME_STAMP";
    public static final String KEY_COLLECTION_CONVERSATION = "CONVERSATIONS";
    public static final String KEY_LAST_MESSAGE = "CONVERSATION_LAST_MESSAGE";
    public static final String KEY_AVAILABILITY = "AVAILABILITY";
    public static final String KEY_COLLECTION_STATISTICS = "STATISTICS";
    public static final String SHOW_FRAGMENT_DIALOG = "SHOW_FRAGMENT_DIALOG";
    public static final String KEY_AMOUNT_SENT_MESSAGES = "KEY_AMOUNT_SENT_MESSAGES";
    public static final String KEY_AMOUNT_DISCUSSIONS = "KEY_AMOUNT_DISCUSSIONS";

    public static final String KEY_ADD_SCORE = "KEY_ADD_SCORE";
    public static final String REMOTE_MSG_AUTHORIZATION = "Authorization";
    public static final String REMOTE_MESSAGE_DATA = "data";
    public static final String REMOTE_MESSAGE_REGISTRATION_IDS = "registration_ids";

    public static HashMap<String, String> remoteMessageHandlers = null;
    public static HashMap<String, String> getRemoteMessageHandlers() {
        if (remoteMessageHandlers == null) {
            remoteMessageHandlers = new HashMap<>();
            remoteMessageHandlers.put(
                    REMOTE_MSG_AUTHORIZATION,
                    "key=AAAAruW7VZQ:APA91bEGsaX1nwXqifR3pJZOaCVMFv-ZeyiTuzSkhBlhtIR6KALDU1PqpDSX4GZIilVrPQVZlS_hr48F-OxgVBtN2k21fS-Ewgx-dAXZ4G7HQfayTC5pIb6LcYmi1xnG4ojprknOrqWN"
            );
            remoteMessageHandlers.put(
                    "content-type",
                    "application/json"
            );
        }

        return remoteMessageHandlers;
    }

    public static String BASE_URL = "https://fcm.googleapis.com";
    public static final String SERVER_KEY = "AAAAruW7VZQ:APA91bEGsaX1nwXqifR3pJZOaCVMFv-ZeyiTuzSkhBlhtIR6KALDU1PqpDSX4GZIilVrPQVZlS_hr48F-OxgVBtN2k21fS-Ewgx-dAXZ4G7HQfayTC5pIb6LcYmi1xnG4ojprknOrqWN";
    public static final String CONTENT_TYPE = "Content-Type:application/json";


}
