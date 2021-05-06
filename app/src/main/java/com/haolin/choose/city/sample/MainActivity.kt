package com.haolin.choose.city.sample

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    private var tvChooseCity : TextView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        tvChooseCity = findViewById(R.id.tvChooseCity)
        tvChooseCity?.setOnClickListener {
            val intent = Intent(this, ChooseCityActivity::class.java)
            startActivityForResult(intent, 103)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 103 && resultCode == RESULT_OK && data != null) {
            val bundle = data.extras ?: return
            tvChooseCity?.text = bundle.getString("city")
        }
    }
}