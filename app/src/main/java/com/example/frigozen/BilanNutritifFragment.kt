package com.example.frigozen

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import android.widget.TimePicker
import com.example.frigozen.R.*
import java.util.*

class BilanNutritifFragment : Fragment(layout.fragment_bilan_nutritif) {

    private lateinit var databaseHelper: DatabaseHelper
    private val currentUserEmail = "user@example.com" // À remplacer par l'email dynamique de l'utilisateur connecté

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        databaseHelper = DatabaseHelper(requireContext()) // Initialiser la base

        // Initialisation des vues
        val etPoids: EditText = view.findViewById(R.id.etPoids)
        val etTaille: EditText = view.findViewById(R.id.etTaille)
        val btnCalculer: Button = view.findViewById(R.id.btnCalculer)
        val tvIMC: TextView = view.findViewById(R.id.tvIMC)
        val tvCalories: TextView = view.findViewById(R.id.tvCalories)
        val progressBar: ProgressBar = view.findViewById(R.id.progressBarCalories)
        val btnSetNotification: Button = view.findViewById(R.id.btnSetNotification)

        // Charger les données utilisateur
        val user = databaseHelper.getUser(currentUserEmail)

        if (user != null && user.imc != null && user.calories != null) {
            displayUserData(user, tvIMC, tvCalories, etPoids, etTaille, btnCalculer, progressBar)
        } else {
            setupInputView(tvIMC, tvCalories, etPoids, etTaille, btnCalculer)
        }

        requestNotificationPermission(btnSetNotification)

        btnCalculer.setOnClickListener {
            handleCalculation(etPoids, etTaille, tvIMC, tvCalories, progressBar, btnCalculer, etPoids, etTaille)
        }

        btnSetNotification.setOnClickListener {
            showTimePickerDialog()
        }
    }

    private fun displayUserData(
        user: User,
        tvIMC: TextView,
        tvCalories: TextView,
        etPoids: EditText,
        etTaille: EditText,
        btnCalculer: Button,
        progressBar: ProgressBar
    ) {
        tvIMC.visibility = View.VISIBLE
        tvCalories.visibility = View.VISIBLE
        etPoids.visibility = View.GONE
        etTaille.visibility = View.GONE
        btnCalculer.visibility = View.GONE

        tvIMC.text = "IMC: %.2f".format(user.imc)
        tvCalories.text = "Calories recommandées: %.0f".format(user.calories)
        progressBar.max = user.calories!!.toInt()
    }

    private fun setupInputView(
        tvIMC: TextView,
        tvCalories: TextView,
        etPoids: EditText,
        etTaille: EditText,
        btnCalculer: Button
    ) {
        tvIMC.visibility = View.GONE
        tvCalories.visibility = View.GONE
        etPoids.visibility = View.VISIBLE
        etTaille.visibility = View.VISIBLE
        btnCalculer.visibility = View.VISIBLE
    }

    private fun handleCalculation(
        etPoids: EditText,
        etTaille: EditText,
        tvIMC: TextView,
        tvCalories: TextView,
        progressBar: ProgressBar,
        btnCalculer: Button,
        vararg inputFields: View
    ) {
        val poids = etPoids.text.toString().toFloatOrNull()
        val taille = etTaille.text.toString().toFloatOrNull()

        if (poids != null && taille != null) {
            val imc = poids / (taille * taille)
            val options = arrayOf("Sédentaire", "Modérément actif", "Très actif")
            showActivityLevelDialog(imc, poids, options, tvIMC, tvCalories, progressBar, btnCalculer, inputFields)
        } else {
            Toast.makeText(requireContext(), "Poids ou taille invalide.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showActivityLevelDialog(
        imc: Float,
        poids: Float,
        options: Array<String>,
        tvIMC: TextView,
        tvCalories: TextView,
        progressBar: ProgressBar,
        btnCalculer: Button,
        inputFields: Array<out View>
    ) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Niveau d'activité")
        builder.setItems(options) { _, which ->
            val facteur = when (which) {
                0 -> 1.2f
                1 -> 1.5f
                else -> 1.8f
            }
            var calories = poids * 23 * facteur

            if (imc < 18.5) calories += 400
            if (imc > 25) calories -= 400

            databaseHelper.updateHealthData(currentUserEmail, imc, calories)

            tvIMC.text = "IMC: %.2f".format(imc)
            tvCalories.text = "Calories recommandées: %.0f".format(calories)
            progressBar.max = calories.toInt()

            tvIMC.visibility = View.VISIBLE
            tvCalories.visibility = View.VISIBLE
            progressBar.visibility = View.VISIBLE
            inputFields.forEach { it.visibility = View.GONE }
        }
        builder.show()
    }

    private fun showTimePickerDialog() {
        if (!isAdded) return // Vérifie que le Fragment est attaché

        val timePickerDialog = AlertDialog.Builder(requireContext())
        val timePickerView = layoutInflater.inflate(R.layout.time_picker_dialog, null)
        val timePicker: TimePicker? = timePickerView.findViewById(R.id.time_picker)

        if (timePicker == null) {
            Toast.makeText(requireContext(), "Erreur lors du chargement de l'interface", Toast.LENGTH_SHORT).show()
            return
        }

        timePickerDialog.setView(timePickerView)
        timePickerDialog.setPositiveButton("OK") { _, _ ->
            val hour: Int
            val minute: Int

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                hour = timePicker.hour
                minute = timePicker.minute
            } else {
                hour = timePicker.currentHour
                minute = timePicker.currentMinute
            }

            if (isAdded) {
                scheduleNotification(hour, minute)
            }
        }
        timePickerDialog.setNegativeButton("Annuler", null)
        timePickerDialog.show()
    }


    private fun scheduleNotification(hour: Int, minute: Int) {
        if (!isAdded) return // Vérifie que le Fragment est encore attaché

        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)

            // S'assurer que l'heure est dans le futur
            if (timeInMillis < System.currentTimeMillis()) {
                add(Calendar.DAY_OF_YEAR, 1)
            }
        }

        val intent = Intent(requireContext(), NotificationReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            requireContext(),
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val alarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)

        if (isAdded && activity != null) {
            Toast.makeText(requireContext(), "Notification planifiée à $hour:$minute", Toast.LENGTH_SHORT).show()
        }
    }


    private fun requestNotificationPermission(btnSetNotification: Button) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    android.Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
                    REQUEST_NOTIFICATION_PERMISSION_CODE
                )
            } else {
                btnSetNotification.visibility = View.VISIBLE
            }
        } else {
            btnSetNotification.visibility = View.VISIBLE
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_NOTIFICATION_PERMISSION_CODE) {
            val btnSetNotification: Button? = view?.findViewById(R.id.btnSetNotification)
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                btnSetNotification?.visibility = View.VISIBLE
            } else {
                Toast.makeText(requireContext(), "Permission refusée.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    companion object {
        private const val REQUEST_NOTIFICATION_PERMISSION_CODE = 1001
    }
}
