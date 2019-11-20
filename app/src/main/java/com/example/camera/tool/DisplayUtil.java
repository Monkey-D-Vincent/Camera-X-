package com.example.camera.tool;

import android.content.Context;
import android.graphics.Point;
import android.util.DisplayMetrics;

/**
 * @author libo
 * @Demo class DisplayUtil
 * @Description TODO
 * @date 2019-11-14 15:42
 */
public class DisplayUtil {

	/**
	 * 获取屏幕宽度和高度，单位为px
	 *
	 * @param context
	 * @return
	 */
	public static Point getScreenMetrics(Context context) {
		DisplayMetrics dm = context.getResources().getDisplayMetrics();
		int w_screen = dm.widthPixels;
		int h_screen = dm.heightPixels;
		return new Point(w_screen, h_screen);

	}
}
