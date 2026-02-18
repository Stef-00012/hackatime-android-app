package com.stefdp.hackatime

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.media.RingtoneManager
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.stefdp.hackatime.network.backendapi.requests.sendPushNotificationToken
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import androidx.core.graphics.createBitmap

// for this class I took https://github.com/firebase/snippets-android/blob/a413b0658ff2fc7a72c4b0c59e84a889ff7fac45/messaging/app/src/main/java/com/google/firebase/example/messaging/kotlin/MyFirebaseMessagingService.kt
// and slightly modified it

class HackatimeFirebaseMessagingService : FirebaseMessagingService() {
    // [START receive_message]
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
//        Log.d(TAG, "From: ${remoteMessage.from}")
//        super.onMessageReceived(remoteMessage)

        // Check if message contains a data payload.
//        if (remoteMessage.data.isNotEmpty()) {
//            Log.d(TAG, "Message data payload: ${remoteMessage.data}")
//
//            // Check if data needs to be processed by long running job
//            if (needsToBeScheduled()) {
//                // For long-running tasks (10 seconds or more) use WorkManager.
//                scheduleJob()
//            } else {
//                // Handle message within 10 seconds
//                handleNow()
//            }
//        }

        // Check if message contains a notification payload.
        remoteMessage.notification?.let {
//            Log.d(TAG, "Message Notification Body: ${it.body}\nChannel: ${it.channelId}\nTitle: ${it.title}\nTag: ${it.tag}")

            val title = it.title
            val body = it.body
            val channelId = it.channelId

            sendNotification(
                title = title,
                body = body,
                channelId = channelId ?: DEFAULT_NOTIFICATION_CHANNEL_ID
            )
        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }
    // [END receive_message]

//    private fun needsToBeScheduled() = true

    // [START on_new_token]
    /**
     * Called if the FCM registration token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the
     * FCM registration token is initially generated so this is where you would retrieve the token.
     */
    override fun onNewToken(token: String) {
//        Log.d(TAG, "Refreshed token: $token")

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // FCM registration token to your app server.
        sendRegistrationToServer(token)
    }
    // [END on_new_token]

//    private fun scheduleJob() {
//        val work = OneTimeWorkRequest.Builder(MyWorker::class.java)
//            .build()
//        WorkManager.getInstance(this)
//            .beginWith(work)
//            .enqueue()
//    }

//    private fun handleNow() {
//        Log.d(TAG, "Short lived task is done.")
//    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun sendRegistrationToServer(token: String?) {
        if (token is String) {
//            Log.d(TAG, "Sending token to server: $token")

            GlobalScope.launch {
                sendPushNotificationToken(
                    context = applicationContext,
                    token = token
                )
            }
            return
        }

        Log.e(TAG, "Token is null, cannot send to server.")
    }

    private fun sendNotification(
        title: String? = DEFAULT_NOTIFICATION_TITLE,
        body: String?,
        channelId: String = DEFAULT_NOTIFICATION_CHANNEL_ID,
    ) {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val requestCode = 0
        val pendingIntent = PendingIntent.getActivity(
            this,
            requestCode,
            intent,
            PendingIntent.FLAG_IMMUTABLE,
        )

        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_stat_name)
            .setContentTitle(title)
            .setContentText(body)
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

//        val channel = NotificationChannel(
//            channelId,
//            "Channel human readable title",
//            NotificationManager.IMPORTANCE_DEFAULT,
//        )
//        notificationManager.createNotificationChannel(channel)

        val notificationId = 0
        notificationManager.notify(notificationId, notificationBuilder.build())
    }

    private fun getVectoredBitmap(drawableId: Int): Bitmap? {
        val drawable = ContextCompat.getDrawable(this, drawableId) ?: return null
        val bitmap = createBitmap(
            drawable.intrinsicWidth,
            drawable.intrinsicHeight
        )

        val canvas = Canvas(bitmap)

        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)

        return bitmap
    }

    companion object {
        private const val TAG = "HackatimeFirebaseMessagingService"
        private const val DEFAULT_NOTIFICATION_TITLE = "Hackatime Stats"
        private const val DEFAULT_NOTIFICATION_CHANNEL_ID = "goals"
    }

//    internal class MyWorker(appContext: Context, workerParams: WorkerParameters) : Worker(appContext, workerParams) {
//        override fun doWork(): Result {
//            // TODO(developer): add long running task here.
//            return Result.success()
//        }
//    }
}