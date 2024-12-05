package com.example.frigozen

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class NotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        // Vous pouvez récupérer les données nécessaires pour la notification ici (comme les calories)
        val notificationHelper = NotificationHelper(context)
        notificationHelper.sendNotification(
            "Notification Calories",
            "Votre notification sur les calories est arrivée !"
        )
    }
}
