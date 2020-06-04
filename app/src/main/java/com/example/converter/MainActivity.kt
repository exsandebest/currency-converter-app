package com.example.converter


import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.AdapterView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*



var currencies: IntArray = intArrayOf(0, 1)
val currenciesMap: Map<Int, String> = mapOf(0 to "RUB", 1 to "USD", 2 to "EUR", 3 to "GBP", 4 to "CNY", 5 to "UAH")
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
        } else {
            textView.setText((s.toDouble() * fixedRates[currenciesMap[currencies[0]]]!![currenciesMap[currencies[1]]]!!).toString())
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        spinner2.setSelection(currencies[1])

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
    }
}
