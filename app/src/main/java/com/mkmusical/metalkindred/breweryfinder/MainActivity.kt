package com.mkmusical.metalkindred.breweryfinder

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import com.mkmusical.metalkindred.breweryfinder.data.Brewery
import com.mkmusical.metalkindred.breweryfinder.data.BreweryService
import okhttp3.FormBody
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.RequestBody
import okio.IOException
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

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
            // Validate at least one search parameter
            if (nameEditText.text.isEmpty() && stateEditText.text.isEmpty() && zipEditText.text.isEmpty() && typeSpinner.selectedItem == "Type") {
                // Display the message
                showToast("At least one search parameter required")
            } else {
                // Perform the search
                this.performSearch()
            }
        }
    }

    private fun performSearch() {
        val apiKey = StringBuilder()
        apiKey.append(resources.getString(R.string.brewery_api_key))
        val encodedApiKey = URLEncoder.encode(apiKey.toString(), StandardCharsets.UTF_8.toString())
        val baseUrl = "https://getbrewery.azurewebsites.net/api/GetBreweries/"
        val httpUrl = baseUrl.toHttpUrlOrNull() ?: throw IllegalArgumentException("Invalid URL: $baseUrl")

        val retrofit = Retrofit.Builder()
            .baseUrl(httpUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()


        val service = retrofit.create(BreweryService::class.java)

        // Create a map to store non-empty parameters
        val parameters = mutableMapOf<String, String>()

        // Add parameters to the map only if they are not empty
        nameEditText.text.toString().trim().takeIf { it.isNotEmpty() }?.let { parameters["by_name"] = it }
        stateEditText.text.toString().trim().takeIf { it.isNotEmpty() }?.let { parameters["by_state"] = it }
        zipEditText.text.toString().trim().takeIf { it.isNotEmpty() }?.let { parameters["by_postal"] = it }
        typeSpinner.selectedItem?.toString()?.trim().takeIf { it!!.isNotEmpty() && it != "Type" }?.let {
            parameters["by_type"] = URLEncoder.encode(it, "UTF-8")
        }


        parameters["code"] = encodedApiKey // Add the API key

        // Perform the API call only if there's at least one non-empty parameter
        if (parameters.isNotEmpty()) {
            try{
                val call = service.getBreweries(
                    parameters["by_name"],
                    parameters["by_state"],
                    parameters["by_postal"],
                    parameters["by_type"]
                )

                call.enqueue(object : Callback<List<Brewery>> {
                    override fun onResponse(call: Call<List<Brewery>>, response: Response<List<Brewery>>) {
                        Log.d("API_RESPONSE", "Response code: ${response.code()}")
                        Log.d("API_RESPONSE", "Response body: ${response.body()}")
                        val urlBuilder = baseUrl.toHttpUrlOrNull()!!.newBuilder()
                        parameters.forEach { (key, value) -> urlBuilder.addQueryParameter(key, value) }
                        val apiUrl = urlBuilder.build().toString()

                        Log.d("API_CALL", "API URL: $apiUrl")

                        if (response.isSuccessful) {
                            val breweries = response.body()
                            displayBreweries(breweries)
                        } else {
                            Log.e("API_RESPONSE", "Unsuccessful response: ${response.errorBody()}")
                            showToast("Unsuccessful response: ${response.code()}")
                        }
                    }

                    override fun onFailure(call: Call<List<Brewery>>, t: Throwable) {
                        Log.e("API_FAILURE", "API call failed: ${t.stackTraceToString()}")

                    }
                })
            } catch (e: Exception){
                Log.e("API_CALL_ERROR", "Error during API call", e)
            }

        } else {
            // Display a message or take appropriate action when no parameters are entered
            showToast("At least one search parameter required")
        }
    }

    private fun encodeApiKey(apiKey: String): String {
        val requestBody: RequestBody = FormBody.Builder()
            .add("key", apiKey)
            .build()

        return requestBody.contentType().toString()
    }

    private fun displayBreweries(breweries: List<Brewery>?) {
        val resultTextView = findViewById<TextView>(R.id.resultTextView)

        val resultText = StringBuilder()

        breweries?.forEach { brewery ->
            resultText.append("Name: ${brewery.name}\n")
            resultText.append("Address: ${brewery.street ?: ""}, ${brewery.city ?: ""}, ${brewery.state ?: ""}, ${brewery.postalCode ?: ""}\n")
            resultText.append("Phone: ${brewery.phone ?: ""}\n\n")
        }

        if (resultText.isEmpty()) {
            resultText.append("No breweries found.")
        }

        resultTextView.text = resultText.toString()
    }

    private fun showToast(message: String) {
        // Display a toast message
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}