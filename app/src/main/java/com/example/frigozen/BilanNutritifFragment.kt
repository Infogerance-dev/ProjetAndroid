package com.example.frigozen

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import android.widget.TimePicker
import java.util.*

class BilanNutritifFragment : Fragment(R.layout.fragment_bilan_nutritif) {

    private lateinit var databaseHelper: DatabaseHelper

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        databaseHelper = DatabaseHelper(requireContext()) // Initialiser la base

        val etPoids: EditText = view.findViewById(R.id.etPoids)
        val etTaille: EditText = view.findViewById(R.id.etTaille)
        val btnCalculer: Button = view.findViewById(R.id.btnCalculer)
        val tvIMC: TextView = view.findViewById(R.id.tvIMC)
        val tvCalories: TextView = view.findViewById(R.id.tvCalories)
        val progressBar: ProgressBar = view.findViewById(R.id.progressBarCalories)

        val btnSetNotification: Button = view.findViewById(R.id.btnSetNotification) // Récupérer le bouton de notification

        // Charger les données utilisateur (IMC et calories)
        val currentUserEmail = "user@example.com" // À remplacer par l'email de l'utilisateur connecté
        val user = databaseHelper.getUser(currentUserEmail)

        if (user != null && user.imc != null && user.calories != null) {
            // Si les données existent, les afficher
            tvIMC.visibility = View.VISIBLE
            tvCalories.visibility = View.VISIBLE
            etPoids.visibility = View.GONE
            etTaille.visibility = View.GONE
            btnCalculer.visibility = View.GONE

            tvIMC.text = "IMC: %.2f".format(user.imc)
            tvCalories.text = "Calories recommandées: %.0f".format(user.calories)
            progressBar.max = user.calories.toInt()
        } else {
            // Affichage par défaut (pas de données)
            tvIMC.visibility = View.GONE
            tvCalories.visibility = View.GONE
            etPoids.visibility = View.VISIBLE
            etTaille.visibility = View.VISIBLE
            btnCalculer.visibility = View.VISIBLE
        }

        requestNotificationPermission()

        // Logique du bouton calculer
        btnCalculer.setOnClickListener {
            val poids = etPoids.text.toString().toFloatOrNull()
            val taille = etTaille.text.toString().toFloatOrNull()

            if (poids != null && taille != null) {
                val imc = poids / (taille * taille)
                var calories = poids * 23

                // Afficher un popup pour le niveau d'activité et ajuster les calories
                val options = arrayOf("Sédentaire", "Modérément actif", "Très actif")
                val builder = AlertDialog.Builder(requireContext())
                builder.setTitle("Niveau d'activité")
                builder.setItems(options) { _, which ->
                    val facteur = when (which) {
                        0 -> 1.2f
                        1 -> 1.5f
                        else -> 1.8f
                    }
                    calories *= facteur
                    if (imc < 18.5) {
                        calories += 400
                    } else if (imc > 25) {
                        calories -= 400
                    }

                    // Enregistrer les résultats dans la base de données
                    databaseHelper.updateHealthData(currentUserEmail, imc, calories)

                    // Mettre à jour l'interface
                    tvIMC.text = "IMC: %.2f".format(imc)
                    tvCalories.text = "Calories recommandées: %.0f".format(calories)
                    progressBar.max = calories.toInt()

                    tvIMC.visibility = View.VISIBLE
                    tvCalories.visibility = View.VISIBLE
                    etPoids.visibility = View.GONE
                    etTaille.visibility = View.GONE
                    btnCalculer.visibility = View.GONE
                }
                builder.show()
            } else {
                tvIMC.text = "Veuillez entrer un poids et une taille valides."
            }
        }

        // Logic to show TimePicker in a popup when notification permission is granted
        btnSetNotification.setOnClickListener {
            val timePickerDialog = AlertDialog.Builder(requireContext())
            val timePickerView = layoutInflater.inflate(R.layout.time_picker_dialog, null)
            val timePicker: TimePicker = timePickerView.findViewById(R.id.timePicker)

            timePickerDialog.setView(timePickerView)
            timePickerDialog.setPositiveButton("OK") { _, _ ->
                val hour = timePicker.hour
                val minute = timePicker.minute
                scheduleNotification(hour, minute)
            }
            timePickerDialog.setNegativeButton("Annuler", null)
            timePickerDialog.show()
        }
    }

    private fun scheduleNotification(hour: Int, minute: Int) {
        // Obtenir l'heure et la minute pour planifier la notification
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, hour)
        calendar.set(Calendar.MINUTE, minute)
        calendar.set(Calendar.SECOND, 0)

        val intent = Intent(requireContext(), NotificationReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(requireContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

        val alarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)

        Toast.makeText(requireContext(), "Notification planifiée à $hour:$minute", Toast.LENGTH_SHORT).show()
    }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { // Android 13 ou supérieur
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    android.Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // Demander la permission
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
                    REQUEST_NOTIFICATION_PERMISSION_CODE
                )
            } else {
                // Permission accordée, afficher le bouton
                view?.findViewById<Button>(R.id.btnSetNotification)?.visibility = View.VISIBLE
            }
        } else {
            // Si la version Android est inférieure à 13, afficher directement le bouton
            view?.findViewById<Button>(R.id.btnSetNotification)?.visibility = View.VISIBLE
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_NOTIFICATION_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission accordée : afficher le bouton pour définir la notification
                view?.findViewById<Button>(R.id.btnSetNotification)?.visibility = View.VISIBLE
            } else {
                // Permission refusée
                Toast.makeText(requireContext(), "Permission refusée pour les notifications.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    companion object {
        private const val REQUEST_NOTIFICATION_PERMISSION_CODE = 1001
    }
}
