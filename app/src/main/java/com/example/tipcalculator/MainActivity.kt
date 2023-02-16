package com.example.tipcalculator

import android.animation.ArgbEvaluator
import android.annotation.SuppressLint
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.EditText
import android.widget.SeekBar
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.widget.TextViewCompat

private const val TAG = "MainActivity"
private const val INITIAL_TIP_PERCENT = 15
private const val INITIAL_PEOPLE = 1
private const val INITIAL_DECIMAL_PLACE = 1


class MainActivity : AppCompatActivity() {
    private lateinit var etBaseAmount: EditText
    private lateinit var seekBarTip: SeekBar
    private lateinit var tvTipPercentAmount: TextView
    private lateinit var tvTipAmount: TextView
    private lateinit var tvTotalAmount: TextView
    private lateinit var tvTipDescription: TextView
    private lateinit var seekBarPeople: SeekBar
    private lateinit var seekBarDecimalPlace: SeekBar
    private lateinit var tvAverageAmount: TextView
    private lateinit var tvPeopleAmount: TextView
    private lateinit var tvDecimalPlace0: TextView
    private lateinit var tvDecimalPlace1: TextView
    private lateinit var tvDecimalPlace2: TextView


    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        etBaseAmount = findViewById(R.id.etBaseAmount)
        seekBarTip = findViewById(R.id.seekBarTip)
        tvTipAmount = findViewById(R.id.tvTipAmount)
        tvTipPercentAmount = findViewById(R.id.tvTipPercentAmount)
        tvTotalAmount = findViewById(R.id.tvTotalAmount)
        tvTipDescription = findViewById(R.id.tipDescription)
        seekBarPeople = findViewById(R.id.seekBarPeople)
        tvAverageAmount = findViewById(R.id.tvAverageAmount)
        seekBarDecimalPlace = findViewById(R.id.seekBarDecimalPlace)
        tvPeopleAmount = findViewById(R.id.tvPeopleAmount)

        tvDecimalPlace0 = findViewById(R.id.tvDecimalPlace0)
        tvDecimalPlace1 = findViewById(R.id.tvDecimalPlace1)
        tvDecimalPlace2 = findViewById(R.id.tvDecimalPlace2)

        val tvDecimalPlaceArray: Array<TextView> =
            arrayOf(tvDecimalPlace0, tvDecimalPlace1, tvDecimalPlace2)

        // initialize tip percent
        seekBarTip.progress = INITIAL_TIP_PERCENT
        tvTipPercentAmount.text = "$INITIAL_TIP_PERCENT%"
        updateTipDescription(INITIAL_TIP_PERCENT)

        // initialize decimal place
        seekBarDecimalPlace.progress = INITIAL_DECIMAL_PLACE

        // initialize people
        seekBarPeople.progress = INITIAL_PEOPLE
        tvPeopleAmount.text = "$INITIAL_PEOPLE"
        updatePeopleAmount(INITIAL_PEOPLE)


        seekBarTip.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                Log.i(TAG, "Tip Percent: onProgressChanged $progress")

                tvTipPercentAmount.text = "$progress%"
                computeTipAndTotal()
                updateTipDescription(progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            override fun onStopTrackingTouch(seekBar: SeekBar?) {}

        })

        seekBarPeople.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                Log.i(TAG, "People: onProgressChanged $progress")

                // seekBarPeople from 0 ~ max correspond to people 1 ~ max + 1
                updatePeopleAmount(progress + 1)
                computeTipAndTotal()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            override fun onStopTrackingTouch(seekBar: SeekBar?) {}

        })

        seekBarDecimalPlace.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                Log.i(TAG, "Decimal Place: onProgressChanged $progress")

                dynamicFeedbackForChangeDecimalPlace(progress, tvDecimalPlaceArray)
                computeTipAndTotal()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            override fun onStopTrackingTouch(seekBar: SeekBar?) {}

        })

        etBaseAmount.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                Log.i(TAG, "afterTextChanged $s")
                computeTipAndTotal()
            }

        })
    }

    private fun dynamicFeedbackForChangeDecimalPlace(
        decimalPlace: Int,
        tvDecimalPlaceArray: Array<TextView>
    ) {
        for (i in 0..2) {
            val color = tvDecimalPlaceArray[i].currentTextColor

            if (i == decimalPlace) {
                tvDecimalPlaceArray[i].typeface =
                    Typeface.create(tvDecimalPlaceArray[i].typeface, Typeface.BOLD)
                TextViewCompat.setTextAppearance(
                    tvDecimalPlaceArray[i],
                    android.R.style.TextAppearance_Medium
                )
                tvDecimalPlaceArray[i].setTextColor(color)
            } else {
                tvDecimalPlaceArray[i].typeface =
                    Typeface.create(tvDecimalPlaceArray[i].typeface, Typeface.NORMAL)
                TextViewCompat.setTextAppearance(
                    tvDecimalPlaceArray[i],
                    android.R.style.TextAppearance_Small
                )
            }

            tvDecimalPlaceArray[i].setTextColor(color)
        }
    }

    private fun checkBaseAmountIsEmpty(): Boolean {
        if (etBaseAmount.text.isEmpty()) {
            tvTipAmount.text = ""
            tvTotalAmount.text = ""
            tvAverageAmount.text = ""
            return true
        }
        return false
    }

    private fun updatePeopleAmount(peopleAmount: Int) {
        tvPeopleAmount.text = peopleAmount.toString()
    }

    private fun updateTipDescription(tipPercent: Int) {
        val tipDescription = when (tipPercent) {
            in 0..9 -> "Poor\uD83D\uDE22"
            in 10..14 -> "Acceptable\uD83E\uDD7A"
            in 15..19 -> "Good\uD83D\uDE42"
            in 20..24 -> "Great\uD83D\uDE09"
            else -> "Amazing\uD83D\uDE18"
        }
        tvTipDescription.text = tipDescription

        // update the color based on the tipPercent
        val color = ArgbEvaluator().evaluate(
            tipPercent.toFloat() / seekBarTip.max,
            ContextCompat.getColor(this, R.color.color_worst_tip),
            ContextCompat.getColor(this, R.color.color_best_tip),
        ) as Int
        tvTipDescription.setTextColor(color)
    }

    private fun confirmDecimalPlace(): String {
        val res = when (seekBarDecimalPlace.progress) {
            0 -> "%.0f"
            1 -> "%.1f"
            2 -> "%.2f"
            else -> "%.1f"
        }
        return res
    }

    private fun computeTipAndTotal() {
        if (checkBaseAmountIsEmpty())
            return

        // Get the value of the base, tip percent and people
        val baseAmount = etBaseAmount.text.toString().toDouble()
        val tipPercent = seekBarTip.progress

        // seekBarPeople from 0 ~ max correspond to people 1 ~ max + 1
        val people = seekBarPeople.progress + 1

        // compute the tip and total
        val tipAmount = baseAmount * tipPercent / 100
        val totalAmount = baseAmount + tipAmount
        val averageAmount = totalAmount / people

        // update UI
        val decimalPlace = confirmDecimalPlace()
        tvTipAmount.text = decimalPlace.format(tipAmount)
        tvTotalAmount.text = decimalPlace.format(totalAmount)
        tvAverageAmount.text = decimalPlace.format(averageAmount)
    }

}