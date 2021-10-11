package uz.triples.smartstaffsolution

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.graphics.get
import androidx.core.graphics.set
import kotlinx.coroutines.*
import kotlin.random.Random

class PaintImage @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet?,
    defStyleAttr: Int = 0
) : View(context) {

    private lateinit var bitmap: Bitmap
    private var bitmap2 = Bitmap.createBitmap(4000, 4000, Bitmap.Config.ARGB_8888)

    private var heightInt = 0
    private var widthInt = 0
    private var bitmapOffSetX = 0
    private var bitmapOffSetY = 0
    private var scale = 1f
    private var smallBitmapX = 0
    private var smallBitmapY = 0

    private var animationStarted = false
    private var changeTo: Int = ContextCompat.getColor(context, R.color.red)
    private var changeFrom: Int = 0

    private var algorithm = 0
    private var delay: Long = 0
    private var coroutine: Job? = null

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        heightInt = MeasureSpec.getSize(heightMeasureSpec)
        widthInt = MeasureSpec.getSize(widthMeasureSpec)

        setBitmap(30, 20)
        setSpeed(100)

        setMeasuredDimension(widthMeasureSpec, heightMeasureSpec)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        setMeasuredDimension(w, h)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas!!.drawBitmap(bitmap2, bitmapOffSetX.toFloat(), bitmapOffSetY.toFloat(), null)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                if (event.x > bitmapOffSetX
                    && event.y > bitmapOffSetY
                    && event.x < widthInt - bitmapOffSetX
                    && event.y < heightInt - bitmapOffSetY
                    && !animationStarted
                ) {
                    smallBitmapX = ((event.x.toInt() - bitmapOffSetX) / scale).toInt()
                    smallBitmapY = ((event.y.toInt() - bitmapOffSetY) / scale).toInt()
                    changeFrom = bitmap[smallBitmapX, smallBitmapY]
                    startColoring()
                }
            }
        }
        return true
    }

    private fun startColoring() {
        Log.d(TAG, "startColoring: $changeFrom")
        if (changeFrom != changeTo) {
            animationStarted = true
            coroutine = CoroutineScope(Dispatchers.IO).launch {
                when (algorithm) {
                    0 -> changeColorFromLeftTopToRightBottom(smallBitmapX, smallBitmapY)
                    1 -> changeColorFromRightBottomToLeftTop(smallBitmapX, smallBitmapY)
                    2 -> changeColorFromCenter(smallBitmapX, smallBitmapY)
                }
                animationStarted = false
            }
        } else {
            Toast.makeText(context, "Same", Toast.LENGTH_SHORT).show()
            animationStarted = false
        }
    }

    fun setBitmap(width: Int, height: Int) {
        if (coroutine != null && coroutine?.isActive == true)
            coroutine!!.cancel(null)
        animationStarted = false
        bitmap = Bitmap.createBitmap(4000, 4000, Bitmap.Config.ARGB_8888)
        bitmap.height = height
        bitmap.width = width

        populateBitMap()
        bitmap2 = scaleBitmap(bitmap)
        measureOffSets()

        postInvalidate()
    }

    private fun populateBitMap() {
        for (n in 0 until bitmap.width) {
            for (k in 0 until bitmap.height) {
                val randomNumber = Random.nextInt(0, 2)
                val color = if (randomNumber == 0) Color.BLACK else Color.WHITE
                bitmap[n, k] = color
            }
        }
    }

    private fun scaleBitmap(bitmap: Bitmap): Bitmap {
        val scaleXLine = widthInt.toFloat() / bitmap.width.toFloat()
        val scaleYLine = heightInt.toFloat() / bitmap.height.toFloat()
        val matrixBitmap = Matrix()

        scale = if (scaleXLine > scaleYLine) scaleYLine else scaleXLine
        matrixBitmap.setScale(scale, scale)
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrixBitmap, false)
    }

    private fun measureOffSets() {
        bitmapOffSetX = (widthInt - bitmap2.width) / 2
        bitmapOffSetY = (heightInt - bitmap2.height) / 2
    }

    fun setSpeed(speed: Long = 100) {
        delay = 100 - speed
    }

    fun setAlgorithm(type: Int) {
        algorithm = type
    }

    fun setColor(color: Int) {
        if (!animationStarted)
            changeTo = color
    }

    private suspend fun changeColorFromRightBottomToLeftTop(x: Int, y: Int) {
        delay(delay)

        if ((x >= 0) && (x < bitmap.width - 1))
            if (bitmap[x + 1, y] == changeFrom) {
                changeColorFromRightBottomToLeftTop(x + 1, y)
            }

        if ((y >= 0) && (y < bitmap.height - 1))
            if (bitmap[x, y + 1] == changeFrom) {
                changeColorFromRightBottomToLeftTop(x, y + 1)
            }

        bitmap[x, y] = changeTo
        bitmap2 = scaleBitmap(bitmap)
        invalidate()

        if ((x > 0) && (x < bitmap.width))
            if (bitmap[x - 1, y] == changeFrom) {
                changeColorFromRightBottomToLeftTop(x - 1, y)
            }

        if ((y > 0) && (y < bitmap.height))
            if (bitmap[x, y - 1] == changeFrom) {
                changeColorFromRightBottomToLeftTop(x, y - 1)
            }
    }

    private suspend fun changeColorFromCenter(x: Int, y: Int) {
        delay(delay)

        bitmap[x, y] = changeTo
        bitmap2 = scaleBitmap(bitmap)
        invalidate()

        if ((x > 0) && (x < bitmap.width))
            if (bitmap[x - 1, y] == changeFrom) {
                changeColorFromCenter(x - 1, y)
            }

        if ((x >= 0) && (x < bitmap.width - 1))
            if (bitmap[x + 1, y] == changeFrom) {
                changeColorFromCenter(x + 1, y)
            }

        if ((y > 0) && (y < bitmap.height))
            if (bitmap[x, y - 1] == changeFrom) {
                changeColorFromCenter(x, y - 1)
            }

        if ((y >= 0) && (y < bitmap.height - 1))
            if (bitmap[x, y + 1] == changeFrom) {
                changeColorFromCenter(x, y + 1)
            }
    }

    private suspend fun changeColorFromLeftTopToRightBottom(x: Int, y: Int) {

        delay(delay)

        if ((x > 0) && (x < bitmap.width))
            if (bitmap[x - 1, y] == changeFrom) {
                changeColorFromLeftTopToRightBottom(x - 1, y)
            }

        if ((y > 0) && (y < bitmap.height))
            if (bitmap[x, y - 1] == changeFrom) {
                changeColorFromLeftTopToRightBottom(x, y - 1)
            }

        bitmap[x, y] = changeTo
        bitmap2 = scaleBitmap(bitmap)
        invalidate()

        if ((x >= 0) && (x < bitmap.width - 1))
            if (bitmap[x + 1, y] == changeFrom) {
                changeColorFromLeftTopToRightBottom(x + 1, y)
            }

        if ((y >= 0) && (y < bitmap.height - 1))
            if (bitmap[x, y + 1] == changeFrom) {
                changeColorFromLeftTopToRightBottom(x, y + 1)
            }
    }

    private val TAG = "PaintImageTAG"
}