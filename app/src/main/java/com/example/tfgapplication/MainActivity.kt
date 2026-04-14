package com.example.tfgapplication

import android.content.Context
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.tfgapplication.databinding.ActivityMainBinding
import com.example.tfgapplication.models.SongResponse
import com.example.tfgapplication.viewModel.CancionesViewModel

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel = CancionesViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val spinner = binding.spinner
        val petitionButton: Button = binding.petitionButton

        val inputEdit = binding.inputEditText
        val outputEdit = binding.outputEditText

        val arrayOfObjects = mutableListOf<String>()

        val spinnerArray = arrayOf("POST", "PATCH", "GET", "GET BY CODE", "DELETE")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, spinnerArray)

        spinner.adapter = adapter

        petitionButton.setOnClickListener {

            // Desactiva temporante los campos de entrada para no sobrecargar el servidor
            petitionButton.isEnabled = false
            inputEdit.isEnabled = false
            spinner.isEnabled = false

            val songCodeText: String = binding.codigoCancionText.text.toString()
            val inputEditText: String = binding.inputEditText.text.toString()

            val methodSelected = spinnerArray[spinner.selectedItemPosition]

            var song: SongResponse? = null

            if (inputEditText.isNotEmpty())
                song = Transformer.deserializeSongObject(inputEditText)

            when (methodSelected) {
                "POST" -> {
                    if(song != null)
                        viewModel.postSong(song)
                    else
                        showAlertDialog(this, "Introduce una canción en formato JSON")
                }
                "PATCH" -> {
                    if(song != null)
                        viewModel.updateSong(song)
                    else
                        showAlertDialog(this, "Introduce una canción en formato JSON")
                }
                "GET" -> {
                    viewModel.getSongs()
                }
                "GET BY CODE" -> {
                    if(songCodeText.isNotEmpty())
                        viewModel.getSongByCode(songCodeText)
                    else
                        showAlertDialog(this, "Introduce el código de la canción")
                }
                "DELETE" -> {
                    if(songCodeText.isNotEmpty())
                        viewModel.deleteSongByCode(songCodeText)
                    else
                        showAlertDialog(this, "Introduce el código de la canción")
                }
                else -> ""
            }

            petitionButton.isEnabled = true
            inputEdit.isEnabled = true
            spinner.isEnabled = true
        }

        fun loadSongs(songs: List<SongResponse?>) {
            val array = songs.map{
                song -> Transformer.serializeSongObject(song)
            }.toMutableList()

            arrayOfObjects.clear()
            arrayOfObjects.addAll(array)
            outputEdit.setText(arrayOfObjects.toString())
        }

        viewModel.songs.observe(this){ songs ->
            loadSongs(songs)
        }
    }

    fun showAlertDialog(context: Context, message: String) {
        AlertDialog.Builder(context)
            .setTitle("Falta algún campo por rellenar")
            .setMessage(message)
            .setPositiveButton("Aceptar") { dialog, _ ->
                // Acción al pulsar Aceptar
                dialog.dismiss()
            }
            .create()
            .show()
    }

}