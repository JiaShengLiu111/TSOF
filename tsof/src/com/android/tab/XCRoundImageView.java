package com.android.tab;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;
import com.lidroid.xutils.bitmap.core.AsyncDrawable;

/**
 * * 自定义的圆形ImageView，可以直接当组件在布局中使用。
 * 
 * @author caizhiming
 * */
@SuppressLint("DrawAllocation")
public class XCRoundImageView extends ImageView {
	private Paint paint;

	public XCRoundImageView(Context context) {
		this(context, null);
	}

	public XCRoundImageView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public XCRoundImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		paint = new Paint();

	}

	/**
	 * * 绘制圆形图片
	 * 
	 * @author caizhiming
	 */
	@SuppressLint("DrawAllocation")
	@Override
	protected void onDraw(Canvas canvas) {

		Drawable drawable = getDrawable();

		if (drawable == null) {
			return;
		}

		if (getWidth() == 0 || getHeight() == 0) {
			return;
		}
		Bitmap b = null;
		if (drawable instanceof BitmapDrawable) {
			b = ((BitmapDrawable) drawable).getBitmap();
		} else if (drawable instanceof AsyncDrawable) {
			b = Bitmap
					.createBitmap(
							getWidth(),
							getHeight(),
							drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
									: Bitmap.Config.RGB_565);
			Canvas canvas1 = new Canvas(b);
			// canvas.setBitmap(bitmap);
			drawable.setBounds(0, 0, getWidth(), getHeight());
			drawable.draw(canvas1);
		}
		Bitmap bitmap = b.copy(Bitmap.Config.ARGB_8888, true);

		int w = getWidth(), h = getHeight();

		Bitmap roundBitmap = getCroppedBitmap(bitmap, w);
		canvas.drawBitmap(roundBitmap, 0, 0, null);

	}

	public static Bitmap getCroppedBitmap(Bitmap bmp, int radius) {
		Bitmap sbmp;
		if (bmp.getWidth() != radius || bmp.getHeight() != radius)
			sbmp = Bitmap.createScaledBitmap(bmp, radius, radius, false);
		else
			sbmp = bmp;
		Bitmap output = Bitmap.createBitmap(sbmp.getWidth(), sbmp.getHeight(),
				Config.ARGB_8888);
		Canvas canvas = new Canvas(output);

		final int color = 0xffa19774;
		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, sbmp.getWidth(), sbmp.getHeight());

		paint.setAntiAlias(true);
		paint.setFilterBitmap(true);
		paint.setDither(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(Color.parseColor("#BAB399"));
		canvas.drawCircle(sbmp.getWidth() / 2 + 0.7f,
				sbmp.getHeight() / 2 + 0.7f, sbmp.getWidth() / 2 + 0.1f, paint);
		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(sbmp, rect, rect, paint);

		return output;
	}

	/**
	 * * 获取圆形图片方法
	 * 
	 * @param bitmap
	 * @param pixels
	 * @return Bitmap
	 * @author caizhiming
	 */
	private Bitmap getCircleBitmap(Bitmap bitmap, int pixels) {
		Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
				bitmap.getHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(output);
		final int color = 0xff424242;
		final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		int x = bitmap.getWidth();

		canvas.drawCircle(x / 2, x / 2, x / 2, paint);
		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);
		return output;

	}
}