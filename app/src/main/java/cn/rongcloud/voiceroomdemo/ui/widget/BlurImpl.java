/*
 * Copyright © 2021 RongCloud. All rights reserved.
 */

package cn.rongcloud.voiceroomdemo.ui.widget;

import android.content.Context;
import android.graphics.Bitmap;

interface BlurImpl {

	boolean prepare(Context context, Bitmap buffer, float radius);

	void release();

	void blur(Bitmap input, Bitmap output);

}
