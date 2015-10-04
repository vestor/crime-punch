package com.crimepunch.service;

import com.crimepunch.pojo.Location;
import com.google.android.gcm.server.Message;
import com.google.android.gcm.server.Result;
import com.google.android.gcm.server.Sender;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Created by manish on 4/10/15.
 */
@Component
public class MessageBroadcaster {

    @Value("${GOOGLE_API_KEY}")
    private String GOOGLE_API_KEY;
    private static final String APP_ID = "com.crimepunch.app";
    private static final String SENDER_ID = "177001317035";


    public void sendOutMessage(String messageBody, Location location, String userGcmId) throws IOException {
        Sender sender = new Sender(GOOGLE_API_KEY);
        Message message = new Message.Builder().timeToLive(30)
                .addData("message", messageBody)
                .addData("latitude", String.valueOf(location.getLatitude()))
                .addData("longitude", String.valueOf(location.getLongitude()))
                .delayWhileIdle(true).build();
        Result result = sender.send(message, userGcmId, 1);
        System.out.println("result = " + result);
    }

}
