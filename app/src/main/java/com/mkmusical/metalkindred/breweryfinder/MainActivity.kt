package com.mkmusical.metalkindred.breweryfinder

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.mkmusical.metalkindred.breweryfinder.ui.theme.BreweryFinderTheme

class MainActivity : ComponentActivity() {
    lateinit var nameEditText: EditText
    lateinit var stateEditText: EditText
    lateinit var zipEditText: EditText
    lateinit var typeSpinner: Spinner
    lateinit var findBreweryButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        nameEditText = findViewById(R.id.nameEditText)
        stateEditText = findViewById(R.id.stateEditText)
        zipEditText = findViewById(R.id.zipEditText)
        typeSpinner = findViewById(R.id.typeSpinner)
        findBreweryButton = findViewById(R.id.findBreweryButton)

        findBreweryButton.setOnClickListener {
            // Handle button click, construct API URL
            val apiKey = resources.getString(R.string.brewery_api_key)
            val baseUrl = "https://brewery-api.metalkindred.com/api/GetBreweries"

            val name = nameEditText.text.toString()
            val state = stateEditText.text.toString()
            val zip = zipEditText.text.toString()
            val type = typeSpinner.selectedItem.toString()

            // Construct the final URL with query parameters
            val apiUrl = "$baseUrl?name=$name&state=$state&by_postal=$zip&type=$type&code=$apiKey"

            // Perform network call or any other action with the constructed API URL
            // (e.g., use AsyncTask, Retrofit, etc.)
        }
    }
}