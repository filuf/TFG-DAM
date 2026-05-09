package com.example.slotify.fragments.general

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import com.example.slotify.R
import com.example.slotify.databinding.FragmentLanguageBinding
import com.example.slotify.viewModels.MainViewModel

class LanguageFragment : Fragment() {

    private lateinit var binding: FragmentLanguageBinding
    private val viewModel: MainViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel.setNewTitle(R.string.change_language)

        binding = FragmentLanguageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val returnToMain: TextView = binding.returnToMainText
        val currentAppLocales = AppCompatDelegate.getApplicationLocales()
        var selectedLanguageCode: String? = if (!currentAppLocales.isEmpty) currentAppLocales[0]?.language else null

        // SELECT THE RADIO THAT MATCHES THE CURRENT LANGUAGE
        if (selectedLanguageCode == null) { // null is the value of system language
            binding.radioSystem.isChecked = true
        } else {
            when (selectedLanguageCode) {
                "es" -> binding.radioSpanish.isChecked = true
                "en" -> binding.radioEnglish.isChecked = true
                "pt" -> binding.radioPortuguese.isChecked = true
                "it" -> binding.radioItalian.isChecked = true
                else -> binding.radioSystem.isChecked = true
            }
        }

        // LISTEN FOR RADIO BUTTON CHANGES
        binding.radioGroupLanguages.setOnCheckedChangeListener { _, checkedId ->
            selectedLanguageCode = when (checkedId) {
                binding.radioSpanish.id -> "es"
                binding.radioEnglish.id -> "en"
                binding.radioPortuguese.id -> "pt"
                binding.radioItalian.id -> "it"
                binding.radioSystem.id -> null // follows the system language
                else -> null
            }
        }

        returnToMain.setOnClickListener {
            val appLocale: LocaleListCompat = if (selectedLanguageCode == null) {
                LocaleListCompat.getEmptyLocaleList()
            } else {
                LocaleListCompat.forLanguageTags(selectedLanguageCode)
            }

            // Try to reduce language switch flicker
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                activity?.overrideActivityTransition(
                    FragmentActivity.OVERRIDE_TRANSITION_OPEN, 0, 0
                )
                activity?.overrideActivityTransition(
                    FragmentActivity.OVERRIDE_TRANSITION_CLOSE, 0, 0
                )
            } else {
                @Suppress("DEPRECATION")
                activity?.overridePendingTransition(0, 0)
            }

            AppCompatDelegate.setApplicationLocales(appLocale)

            viewModel.setLanguage(selectedLanguageCode?:"en")
            view.findNavController().popBackStack()
        }
    }

}