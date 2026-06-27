package com.example.bookandpostroom;

import android.content.Context;
import android.util.Log;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.common.collect.Lists;

import java.io.IOException;
import java.io.InputStream;

public class Accesstoken {

    private static final String FIREBASE_MESSAGING_SCOPE = "https://www.googleapis.com/auth/firebase.messaging";
    private static final String SERVICE_ACCOUNT_FILE = "firebase-service-account.json";

    private final Context context;

    public Accesstoken(Context context) {
        this.context = context;
    }

    public String getAccessToken() {
        try (InputStream stream = context.getAssets().open(SERVICE_ACCOUNT_FILE)) {
            GoogleCredentials googleCredentials = GoogleCredentials.fromStream(stream)
                    .createScoped(Lists.newArrayList(FIREBASE_MESSAGING_SCOPE));

            googleCredentials.refresh();

            return googleCredentials.getAccessToken().getTokenValue();
        } catch (IOException e) {
            Log.e("Accesstoken", "Copy firebase-service-account.json.example to "
                    + SERVICE_ACCOUNT_FILE + " in app/src/main/assets/ and add your credentials.", e);
            return null;
        }
    }
}