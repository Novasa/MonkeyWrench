package com.novasa.monkeywrenchexample

import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.novasa.monkeywrench.MonkeyWrench
import com.novasa.monkeywrench.finder.*
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

                addSchematic {
                    addFinderHtmlBold()
                    addBitFakeBold()
                }

                addSchematicClickable {
                    addFinderHtmlALink()
                    addFinderHttpLink()
                    addBitTextColor(Color.RED)
                    addBitScale(1.5f)
                    addBitUnderline()

                    onClick { uri: Uri ->
                        Toast.makeText(this@MainActivity, "$uri", Toast.LENGTH_SHORT).show()
                    }
                }

                addSchematic {
                    addFinderTag("QQ", "WW")
                    addBitUnderline()
                }

                addSchematic {
                    addFinderIntervals(2 to 4, 6 to 8)
                    addBitBackgroundColor(Color.GREEN)
                }

                addSchematic {
                    addFinderRegex("amet")
                    addBitTextColor(Color.BLACK)
                    addBitScale(0.5f)
                }

                addSchematic {
                    addFinderRegex("sunt")
                    addMutaterDelete()
                }

                addSchematicHtmlFontColor()
            }
        }

        buttonReset.setOnClickListener {
            textView.text = input
        }
    }
}
