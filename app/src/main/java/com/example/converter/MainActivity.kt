package com.example.converter


import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.AdapterView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import okhttp3.*
import org.json.JSONObject
import java.io.IOException
import java.net.UnknownHostException
import kotlin.math.pow
import kotlin.math.round



var client = OkHttpClient()
const val baseUrl = "https://api.exchangeratesapi.io/latest"
const val precision = 6
const val base = 10.0
const val baseCurrency = "RUB"
val multiplier = base.pow(precision)
val currencies = arrayOf("RUB", "USD", "EUR", "GBP", "CNY", "TRY")
var currenciesValues = intArrayOf(0, 1)
val rates = mutableMapOf(
    baseCurrency to mutableMapOf(
        baseCurrency to 1.0,
        "USD" to 1.0/69.0,
        "EUR" to 1.0/78.0,
        "GBP" to 1.0/87.0,
        "CNY" to 1.0/10.0,
        "TRY" to 1.0/11.0))



class MainActivity : AppCompatActivity() {

    fun updateValue() {
        val s = numberEdit1.text.toString()
        if (s == "" || s == ".") {
            textView.text = ""
            buttonShare.isEnabled = false
        } else {
            textView.text = (round(s.toDouble() * rates[currencies[currenciesValues[0]]]!![currencies[currenciesValues[1]]]!! * multiplier)
                    / multiplier).toString()
            buttonShare.isEnabled = true
        }
    }

    fun showToast(text: String, length: Int = 0) {
        runOnUiThread {
            val toast = Toast.makeText(
                applicationContext,
                text,
                if (length == 0) Toast.LENGTH_SHORT else Toast.LENGTH_LONG
            )
            toast.show()
        }
    }

    private fun updateRates() {
        val url = "${baseUrl}?base=${baseCurrency}&symbols=${currencies.joinToString(",")}"
        try {
            val request = Request.Builder().url(url).build()
            client.newCall(request).enqueue(object : Callback {
                override fun onResponse(call: Call, response: Response) {
                    val body = response.body?.string()
                    if (body == null) {
                        showToast("Failed to update rates\nUsing fixed rates", 1)
                        return
                    }
                    val responseObject = JSONObject(body)
                    val responseRates = responseObject.getJSONObject("rates")
                    for (cur in currencies) {
                        rates[baseCurrency]!![cur] = responseRates.getDouble(cur)
                    }
                    unpackRates()
                }

                override fun onFailure(call: Call, e: IOException) {
                    showToast("Failed to update rates\nUsing fixed rates", 1)
                }
            })
        } catch (e: UnknownHostException) {
            showToast("Failed to update rates\nUsing fixed rates", 1)
        }
    }

    fun unpackRates(){
        val rubMap = rates.getValue(baseCurrency)
        for (cur in currencies) {
            if (cur == baseCurrency) continue
            val curRate = 1.0 / rubMap.getValue(cur)
            val curMap = mutableMapOf(baseCurrency to curRate)
            for (c in currencies) {
                if (c == baseCurrency) continue
                curMap[c] = curRate * rubMap.getValue(c)
            }
            rates[cur] = curMap
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        spinner2.setSelection(currenciesValues[1])

        unpackRates()
        updateRates()

        val clipboard: ClipboardManager =
            getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

        spinner1.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                currenciesValues[0] = spinner1.selectedItemPosition
                updateValue()
            }
        }

        spinner2.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                currenciesValues[1] = spinner2.selectedItemPosition
                updateValue()
            }
        }

        numberEdit1.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun afterTextChanged(editable: Editable) {
                updateValue()
            }
        })

        buttonChange.setOnClickListener {
            spinner1.setSelection(currenciesValues[1], true)
            spinner2.setSelection(currenciesValues[0], true)
            currenciesValues[0] = currenciesValues[1]
            currenciesValues[1] = spinner2.selectedItemPosition
        }

        textView.setOnClickListener {
            if (textView.text == "") return@setOnClickListener
            val clip = ClipData.newPlainText("Value", textView.text.toString())
            clipboard.setPrimaryClip(clip)
            showToast("Copied to clipboard")
        }

        buttonShare.setOnClickListener {
            val text = "${numberEdit1.text} ${currencies[currenciesValues[0]]} = ${textView.text} ${currencies[currenciesValues[1]]}"
            val sendIntent = Intent()
            sendIntent.action = Intent.ACTION_SEND
            sendIntent.putExtra(Intent.EXTRA_TEXT, text)
            sendIntent.type = "text/plain"
            startActivity(sendIntent)
        }
    }
}
