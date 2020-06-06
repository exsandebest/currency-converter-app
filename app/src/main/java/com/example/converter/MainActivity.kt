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


const val precision = 5
const val base = 10.0
val multiplier = base.pow(precision)
val currencies = arrayOf("RUB", "USD", "EUR", "GBP", "CNY", "TRY")
var currenciesValues = intArrayOf(0, 1)
val currenciesCodes = mapOf(0 to "RUB", 1 to "USD", 2 to "EUR", 3 to "GBP", 4 to "CNY", 5 to "TRY")
val fixedRates = mutableMapOf(
    "RUB" to mutableMapOf(
        "RUB" to 1.0,
        "USD" to 1.0/69.0,
        "EUR" to 1.0/78.0,
        "GBP" to 1.0/87.0,
        "CNY" to 1.0/10.0,
        "TRY" to 1.0/11.0))



fun initialize() {
    val rubMap = fixedRates.getValue("RUB")
    for (cur in currencies) {
        if (cur == "RUB") continue
        val curRate = 1.0 / rubMap.getValue(cur)
        var curMap = mutableMapOf("RUB" to curRate)
        for (c in currencies) {
            if (c == "RUB") continue
            curMap.put(c, curRate * rubMap.getValue(c))
        }
        fixedRates.put(cur, curMap)
    }
}


class MainActivity : AppCompatActivity() {

    fun update() {
        val s = numberEdit1.text.toString()
        if (s == "") {
            textView.setText("")
            buttonShare.isEnabled = false
        } else {
            textView.setText((round(s.toDouble() * fixedRates[currenciesCodes[currenciesValues[0]]]!![currenciesCodes[currenciesValues[1]]]!! * multiplier)
                    / multiplier).toString())
            buttonShare.isEnabled = true
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        spinner2.setSelection(currenciesValues[1])

        initialize()

        val clipboard: ClipboardManager =
            getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

        spinner1.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                currenciesValues[0] = spinner1.selectedItemPosition
                update()
            }
        }

        spinner2.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                currenciesValues[1] = spinner2.selectedItemPosition
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
            spinner1.setSelection(currenciesValues[1], true)
            spinner2.setSelection(currenciesValues[0], true)
            currenciesValues[0] = currenciesValues[1]
            currenciesValues[1] = spinner2.selectedItemPosition
        }

        textView.setOnClickListener {
            if (textView.text == "") return@setOnClickListener
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
            val text = "${numberEdit1.text} ${currenciesCodes[currenciesValues[0]]} = ${textView.text} ${currenciesCodes[currenciesValues[1]]}"
            val sendIntent = Intent()
            sendIntent.action = Intent.ACTION_SEND
            sendIntent.putExtra(Intent.EXTRA_TEXT, text)
            sendIntent.type = "text/plain"
            startActivity(sendIntent)
        }
    }
}
