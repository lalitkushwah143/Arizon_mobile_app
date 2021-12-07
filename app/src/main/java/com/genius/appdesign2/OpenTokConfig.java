package com.genius.appdesign2;

import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.MetadataChanges;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class OpenTokConfig {
  /*  // Replace with your OpenTok API key
    public static final String API_KEY = "47070554";
    // Replace with a generated Session ID
    public static final String SESSION_ID = "1_MX40NzA3MDU1NH5-MTYxNTUzMDMxNzYzM35neThFTk1YcWZIM2RxV1NPcCtYQmxUNDZ-fg";
    // Replace with a generated token (from the dashboard or using an OpenTok server SDK)
    public static final String TOKEN = "T1==cGFydG5lcl9pZD00NzA3MDU1NCZzaWc9NDA1YTcyZGQ3ZGY1OTVlYmU1ZGRmYzE0MTc0OThjNGMxZTM2NDNjMzpzZXNzaW9uX2lkPTFfTVg0ME56QTNNRFUxTkg1LU1UWXhOVFV6TURNeE56WXpNMzVuZVRoRlRrMVljV1pJTTJSeFYxTlBjQ3RZUW14VU5EWi1mZyZjcmVhdGVfdGltZT0xNjE1NTMwMzMxJm5vbmNlPTAuODc5MjgwMzAyMjMyNDU0OSZyb2xlPXB1Ymxpc2hlciZleHBpcmVfdGltZT0xNjE4MTE4NzM1JmluaXRpYWxfbGF5b3V0X2NsYXNzX2xpc3Q9";


   */
    public static String API_KEY = "";
    // Replace with a generated Session ID
    public static String SESSION_ID = "";
    // Replace with a generated token (from the dashboard or using an OpenTok server SDK)
    public static String TOKEN = "";

    public static boolean isValid() {
        if (TextUtils.isEmpty(OpenTokConfig.API_KEY)
                || TextUtils.isEmpty(OpenTokConfig.SESSION_ID)
                || TextUtils.isEmpty(OpenTokConfig.TOKEN)) {
            return false;
        }

        return true;



    }

    @NonNull
    public static String getDescription() {
        return "OpenTokConfig:" + "\n"
                + "API_KEY: " + OpenTokConfig.API_KEY + "\n"
                + "SESSION_ID: " + OpenTokConfig.SESSION_ID + "\n"
                + "TOKEN: " + OpenTokConfig.TOKEN + "\n";
    }
}

