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
import kotlin.math.pow
import kotlin.math.round


val precision = 5
val base = 10.0
val multiplier = base.pow(precision)
var currencies: IntArray = intArrayOf(0, 1)
val currenciesCodes: Map<Int, String> = mapOf(0 to "RUB", 1 to "USD", 2 to "EUR", 3 to "GBP", 4 to "CNY", 5 to "UAH")
val fixedRates: Map<String, Map<String, Double>> = mapOf(
    "RUB" to mapOf("RUB" to 1.0,
                    "USD" to 1.0/74.0,
                    "EUR" to 1.0/77.0,
                    "GBP" to 1.0/86.0,
                    "CNY" to 1.0/10.0,
                    "UAH" to 1.0/3.0),
    "USD" to mapOf("RUB" to 74.0,
                    "USD" to 1.0,
                    "EUR" to 74.0/77.0,
                    "GBP" to 74.0/86.0,
                    "CNY" to 74.0/10.0,
                    "UAH" to 74.0/3.0),
    "EUR" to mapOf("RUB" to 77.0,
                    "USD" to 77.0/74.0,
                    "EUR" to 1.0,
                    "GBP" to 77.0/86.0,
                    "CNY" to 77.0/10.0,
                    "UAH" to 77.0/3.0),
    "GBP" to mapOf("RUB" to 86.0,
                    "USD" to 86.0/74.0,
                    "EUR" to 86.0/77.0,
                    "GBP" to 1.0,
                    "CNY" to 86.0/10.0,
                    "UAH" to 86.0/3.0),
    "CNY" to mapOf("RUB" to 10.0,
                    "USD" to 10.0/74.0,
                    "EUR" to 10.0/77.0,
                    "GBP" to 10.0/86.0,
                    "CNY" to 1.0,
                    "UAH" to 10.0/3.0),
    "UAH" to mapOf("RUB" to 3.0,
                    "USD" to 3.0/74.0,
                    "EUR" to 3.0/77.0,
                    "GBP" to 3.0/86.0,
                    "CNY" to 3.0/10.0,
                    "UAH" to 1.0))


class MainActivity : AppCompatActivity() {

    fun update() {
        val s = numberEdit1.text.toString()
        if (s == "") {
            textView.setText("")
            buttonShare.isEnabled = false
        } else {
            textView.setText((round(s.toDouble() * fixedRates[currenciesCodes[currencies[0]]]!![currenciesCodes[currencies[1]]]!! * multiplier)
                    / multiplier).toString())
            buttonShare.isEnabled = true
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        spinner2.setSelection(currencies[1])
        val clipboard: ClipboardManager =
            getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

        spinner1.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                currencies[0] = spinner1.selectedItemPosition
                update()
            }
        }

        spinner2.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                currencies[1] = spinner2.selectedItemPosition
                update()
            }
        }

        numberEdit1.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun afterTextChanged(editable: Editable) {
                update()
            }
        })

        buttonChange.setOnClickListener {
            spinner1.setSelection(currencies[1], true)
            spinner2.setSelection(currencies[0], true)
            currencies[0] = currencies[1]
            currencies[1] = spinner2.selectedItemPosition
        }

        textView.setOnClickListener {
            val clip = ClipData.newPlainText("Value", textView.text.toString())
            clipboard.setPrimaryClip(clip)
            val toast = Toast.makeText(
                applicationContext,
                "Copied to clipboard",
                Toast.LENGTH_SHORT
            )
            toast.show()
        }

        buttonShare.setOnClickListener {
            val text = "${numberEdit1.text} ${currenciesCodes[currencies[0]]} = ${textView.text} ${currenciesCodes[currencies[1]]}"
            val sendIntent = Intent()
            sendIntent.action = Intent.ACTION_SEND
            sendIntent.putExtra(Intent.EXTRA_TEXT, text)
            sendIntent.type = "text/plain"
            startActivity(sendIntent)
        }
    }
}
