package com.highstreet.lib.utils

import android.app.Activity
import android.content.Context
import android.graphics.Rect
import android.os.Build
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.LinearLayout

/**
 * @author Yang Shihao
 * @Date 2020/7/20
 */
object DisplayUtils {

    private var statusBarHeight = 0

    /**
     * 获取屏幕宽度(像素)
     */
    fun getScreenWidth(context: Context): Int {
        return context.resources.displayMetrics.widthPixels
    }

    /**
     * 获取屏幕高度(像素)
     */
    fun getScreenHeight(context: Context): Int {
        return context.resources.displayMetrics.heightPixels
    }

    /**
     * 将px值转换为dip或dp值，保证尺寸大小不变
     */
    fun px2dp(context: Context, px: Int): Int {
        val scale = context.resources.displayMetrics.density
        return (px / scale + 0.5f).toInt()
    }

    /**
     * 将dip或dp值转换为px值，保证尺寸大小不变
     */
    fun dp2px(context: Context, dp: Int): Int {
        val scale = context.resources.displayMetrics.density
        return (dp * scale + 0.5f).toInt()
    }

    /**
     * 将px值转换为sp值，保证文字大小不变
     */
    fun px2sp(context: Context, px: Int): Int {
        val fontScale = context.resources.displayMetrics.scaledDensity
        return (px / fontScale + 0.5f).toInt()
    }

    /**
     * 将sp值转换为px值，保证文字大小不变
     */
    fun sp2px(context: Context, spValue: Int): Int {
        val fontScale = context.resources.displayMetrics.scaledDensity
        return (spValue * fontScale + 0.5f).toInt()
    }

    /**
     * 获取状态栏高度
     */
    fun getStatusBarHeight(context: Context): Int {
        if (statusBarHeight == 0) {
            val resources = context.resources
            val resourceId: Int =
                context.resources.getIdentifier("status_bar_height", "dimen", "android")
            var height = resources.getDimensionPixelSize(resourceId)
            if (height < 1) {
                height = dp2px(context, 25)
            }
            statusBarHeight = height
        }

        return statusBarHeight
    }

    fun setStatusBarHolder(view: View) {
        val layoutParams = view.layoutParams
        val statusBarHeight = getStatusBarHeight(view.context)
        if (layoutParams is FrameLayout.LayoutParams) {
            layoutParams.topMargin = statusBarHeight
        } else if (layoutParams is LinearLayout.LayoutParams) {
            layoutParams.topMargin = statusBarHeight
        }
        view.layoutParams = layoutParams
    }

    fun getDecorViewHeight(activity: Activity?): Int {
        return if (activity == null) {
            0
        } else {
            val rect = Rect()
            activity.window.decorView.getWindowVisibleDisplayFrame(rect)
            rect.height()
        }
    }

    fun useShortEdges(window: Window) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val lp = window.attributes
            // 使用刘海区域
            lp.layoutInDisplayCutoutMode =
                WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
            window.attributes = lp
        }
    }
}