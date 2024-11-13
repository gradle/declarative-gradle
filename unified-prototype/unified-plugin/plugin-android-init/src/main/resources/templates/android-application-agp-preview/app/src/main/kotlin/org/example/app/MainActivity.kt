package org.example.app

import org.apache.commons.text.WordUtils

import org.example.list.LinkedList
import org.example.utilities.SplitUtils
import org.example.utilities.StringUtils

import android.widget.TextView
import android.os.Bundle
import android.app.Activity

class MainActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val textView = findViewById(R.id.textView) as TextView
        textView.text = buildMessage()
    }

    private fun buildMessage(): String {
        val tokens: LinkedList
        tokens = SplitUtils.split(MessageUtils.message())
        val result: String = StringUtils.join(tokens)
        return WordUtils.capitalize(result)
    }
}
