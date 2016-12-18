/**
 * Copyright Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.firebase.codelab.friendlychat;

import android.app.NotificationManager;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Vibrator;
import android.util.Log;

import android.support.v7.app.NotificationCompat.Builder;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFMService";
    private static final String AppName = "fitnessFriend";
    public NotificationManager notificationManger ;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // Handle data payload of FCM messages.
        Log.d(TAG, "FCM Message Id: " + remoteMessage.getMessageId());
        Log.d(TAG, "FCM Notification Message: " +
                remoteMessage.getNotification());
        Log.d(TAG, "FCM Data Message: " + remoteMessage.getData());
        //      **************
        //      設定推播
        String title = remoteMessage.getData().get("type");
        String context = remoteMessage.getData().get("message");
        Builder builder = new Builder(this);
        notificationManger = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        int defaults = 0;
        //取得震動服務
        Vibrator myVibrator = (Vibrator) getApplication().getSystemService(Service.VIBRATOR_SERVICE);
        // 加入震動效果
        defaults |= android.app.Notification.DEFAULT_VIBRATE;
        // 設定通知效果
        builder.setDefaults(defaults);

        int NOTIFICATION_ID = 0;    //  to do 如何做到號碼牌概念


        Intent intent= new Intent();
        // 當點擊推播訊息時，會跑到Notification畫面
        intent.setClass(MyFirebaseMessagingService.this, Notification.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,NOTIFICATION_ID,intent,0);

        builder
                .setSmallIcon(android.R.drawable.sym_def_app_icon)
                .setWhen(System.currentTimeMillis())
                .setContentTitle(AppName)
                .setContentText(context)
                .setContentIntent(pendingIntent)
                .build(); // available from API level 11 and onwards

        android.app.Notification notification = builder.build();
        // 向系統發出推播訊息
        notificationManger.notify(NOTIFICATION_ID, notification);


    }
}

