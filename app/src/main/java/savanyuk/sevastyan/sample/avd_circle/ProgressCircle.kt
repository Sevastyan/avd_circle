package savanyuk.sevastyan.sample.avd_circle

import android.content.Context
import android.graphics.drawable.AnimatedVectorDrawable
import android.util.AttributeSet
import android.widget.ImageView


class ProgressCircle : ImageView {

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    var progress: Float = 0f
        set(value) {
            if (value < 0f || value > 1f) {
                throw Exception("${ProgressCircle::class.java.simpleName} - progress should be between 0 and 1: $value")
            }
            field = value
            updateDrawable(value)
        }

    private fun updateDrawable(progress: Float) {
        val avdResId = getDrawableResId(progress)
        val avd = resources.getDrawable(avdResId, context.theme)
        setImageDrawable(avd)
        if (avd is AnimatedVectorDrawable) {
            avd.start()
        }
    }

    private fun getDrawableResId(progress: Float): Int =
            when {
                progress > 0 && progress <= .15 -> R.drawable.avd_circle_progress_10
                progress > .15 && progress <= .25 -> R.drawable.avd_circle_progress_20
                progress > .25 && progress <= .35 -> R.drawable.avd_circle_progress_30
                progress > .35 && progress <= .45 -> R.drawable.avd_circle_progress_40
                progress > .45 && progress <= .55 -> R.drawable.avd_circle_progress_50
                progress > .55 && progress <= .65 -> R.drawable.avd_circle_progress_60
                progress > .65 && progress <= .75 -> R.drawable.avd_circle_progress_70
                progress > .75 && progress <= .85 -> R.drawable.avd_circle_progress_80
                progress > .85 && progress < 1f -> R.drawable.avd_circle_progress_90
                progress == 1f -> R.drawable.avd_circle_progress_100
                else -> R.drawable.vd_circle_zero_progress
            }
}