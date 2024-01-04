package com.project.myheavyequipment.utils

import android.annotation.SuppressLint
import android.util.Log
import java.text.SimpleDateFormat

object Constants {
    @SuppressLint("SimpleDateFormat")
    fun dateStringGenerator(date: String?): String {
        Log.i("TAGGAL", "dateStringGenerator: $date")
        val tempDate = date?.substring(0, 18)
        val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
        val formatter = SimpleDateFormat("dd MMMM yyyy")
        return formatter.format(parser.parse(tempDate)!!)
    }

    fun reparationTextGenerator(date: String, hoursMeter: String): String {
        return "${dateStringGenerator(date)}, $hoursMeter Jam"
    }
}