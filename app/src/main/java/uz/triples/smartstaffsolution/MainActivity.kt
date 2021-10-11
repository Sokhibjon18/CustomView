package uz.triples.smartstaffsolution

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import uz.triples.smartstaffsolution.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private val TAG = "MainActivity"
    private lateinit var binding: ActivityMainBinding
    private lateinit var arrayAdapter: ArrayAdapter<String>
    private var paintImage1: PaintImage? = null
    private var paintImage2: PaintImage? = null
    private val listOfAlgorithms = arrayListOf("Algorithm 1", "Algorithm 2", "Algorithm 3")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setViews()
        setOnClickListeners()
    }

    private fun setViews() {
        paintImage1 = binding.linear1.getChildAt(0) as PaintImage
        paintImage2 = binding.linear2.getChildAt(0) as PaintImage

        arrayAdapter =
            ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, listOfAlgorithms)

        binding.algorithmSpinner1.adapter = arrayAdapter
        binding.algorithmSpinner2.adapter = arrayAdapter
    }

    private fun setOnClickListeners(){
        binding.generateBtn.setOnClickListener(generateOnClickListener)
        binding.speedSeekBar.setOnSeekBarChangeListener(seekBarChangeListener)
        binding.algorithmSpinner1.onItemSelectedListener = adapterItemClickListener
        binding.algorithmSpinner2.onItemSelectedListener = adapterItemClickListener
        binding.colorsCollectionLl.getChildAt(0).setOnClickListener(colorSelectedListener)
        binding.colorsCollectionLl.getChildAt(1).setOnClickListener(colorSelectedListener)
        binding.colorsCollectionLl.getChildAt(2).setOnClickListener(colorSelectedListener)
        binding.colorsCollectionLl.getChildAt(3).setOnClickListener(colorSelectedListener)
        binding.colorsCollectionLl.getChildAt(4).setOnClickListener(colorSelectedListener)
    }

    private val generateOnClickListener = View.OnClickListener {
        if (binding.widthEt.text.isNotEmpty() && binding.widthEt.text.isNotEmpty()) {

            val width = binding.widthEt.text.toString().toInt()
            val height = binding.heightEt.text.toString().toInt()

            if ((width > 0 && height > 0) && !(width > 512 || height > 512)) {
                setValuesToPaintImage(height, width)
            } else {
                showMessage("Size should be more than 0, less than 512")
            }

        } else {
            showMessage("Please enter the size")
        }
    }

    private fun setValuesToPaintImage(height: Int, width: Int) {
        paintImage1?.setBitmap(height, width)
        paintImage2?.setBitmap(height, width)
    }

    private val seekBarChangeListener = object : SeekBar.OnSeekBarChangeListener {
        override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {

        }

        override fun onStartTrackingTouch(seekBar: SeekBar?) {

        }

        override fun onStopTrackingTouch(seekBar: SeekBar?) {
            paintImage1?.setSpeed(seekBar!!.progress.toLong())
            paintImage2?.setSpeed(seekBar!!.progress.toLong())
        }
    }

    private val adapterItemClickListener = object : AdapterView.OnItemSelectedListener {
        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            Log.d(TAG, "onItemSelected: ${parent?.id} $position")
            when (parent?.id) {
                binding.algorithmSpinner1.id -> paintImage1?.setAlgorithm(position)
                binding.algorithmSpinner2.id -> paintImage2?.setAlgorithm(position)
            }
        }

        override fun onNothingSelected(parent: AdapterView<*>?) {
            Log.d(TAG, "onNothingSelected: ")
        }
    }

    private val colorSelectedListener = View.OnClickListener {
        val img = (it as ImageView).background as ColorDrawable

        paintImage1?.setColor(img.color)
        paintImage2?.setColor(img.color)

        showMessage("Color selected !")
    }

    private fun showMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

}