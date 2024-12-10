import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import android.Manifest
import android.content.pm.PackageManager
import com.example.frigozen.MainActivity
import com.example.frigozen.R

class NotificationReceiver : BroadcastReceiver() {

    companion object {
        const val notificationId = 1001
        const val channelId = "frigozen_channel"
        const val REQUEST_CODE_PERMISSION = 1
    }

    override fun onReceive(context: Context, intent: Intent) {
        // Vérification des permissions pour afficher les notifications
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_NOTIFICATION_POLICY)
            != PackageManager.PERMISSION_GRANTED) {

            // Si la permission n'est pas accordée, envoyer un Intent à l'Activity pour demander la permission
            val permissionIntent = Intent(context, MainActivity::class.java)
            permissionIntent.putExtra("request_permission", true)
            permissionIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(permissionIntent)
            return
        }

        // Créer le canal de notification pour Android 8.0 et versions ultérieures
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(channelId, "Frigozen Notifications", importance).apply {
                description = "Rappels pour votre bilan nutritif"
            }

            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }

        // Créer et afficher la notification
        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.bilan_nutritif_icon)  // Assurez-vous que l'icône est présente dans les ressources
            .setContentTitle("Frigozen")
            .setContentText("N'oubliez pas de vérifier votre bilan nutritif aujourd'hui !")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)  // La notification est supprimée lorsqu'elle est cliquée
            .build()

        // Afficher la notification
        NotificationManagerCompat.from(context).notify(notificationId, notification)
    }
}
