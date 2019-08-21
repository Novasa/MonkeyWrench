package com.novasa.monkeywrenchexample

import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.novasa.monkeywrench.MonkeyWrench
import com.novasa.monkeywrench.finder.to
import com.novasa.monkeywrench.schematic.*
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val input = getString(R.string.input)

        textView.text = input

        buttonSpan.setOnClickListener {
            MonkeyWrench.workOn(textView) {

                htmlBold {
                    fakeBold()
                }

                htmlLink {
                    textColor(Color.RED)
                    scale(1.5f)
                    underline()

                    onClick { uri: Uri? ->
                        Toast.makeText(this@MainActivity, "$uri", Toast.LENGTH_SHORT).show()
                    }
                }

                tag("QQ", "WW") {
                    underline()
                }

                interval(2 to 4, 6 to 8) {
                    backgroundColor(Color.GREEN)
                }

                regex("amet") {
                    textColor(Color.BLACK)
                    scale(0.5f)
                }

                regex("sunt") {
                    delete()
                }

                htmlFontColor()
            }
        }

        buttonReset.setOnClickListener {
            textView.text = input
        }
    }
}
