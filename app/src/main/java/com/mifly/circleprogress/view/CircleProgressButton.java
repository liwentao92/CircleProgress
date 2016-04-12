package com.mifly.circleprogress.view;

import java.util.Timer;
import java.util.TimerTask;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.util.MonthDisplayHelper;
import android.view.View;
import android.view.View.MeasureSpec;

import com.mifly.circleprogress.R;

/**
 * 环形进度条按钮控件
 *
 */
public class CircleProgressButton extends View {

	private static String TAG = "CircleProgressButton";
	private static final int DEFAULT_MAX_VALUE = 100; // 默认进度条最大值
	private static final int DEFAULT_PAINT_WIDTH = 10; // 默认画笔宽度
	private static final int DEFAULT_PAINT_COLOR = 0xffffcc00; // 默认画笔颜色
	private static final boolean DEFAULT_FILL_MODE = true; // 默认填充模式
	private static final int DEFAULT_INSIDE_VALUE = 0; // 默认缩进距离

	private CircleAttribute mCircleAttribute; // 圆形进度条基本属性

	private int mMaxProgress; // 进度条最大值
	private int mMainCurProgress; // 主进度条当前值


	private CartoomEngine mCartoomEngine; // 动画引擎
	private boolean isBCartoom = false;//是否正在作画
	private Drawable mBackgroundPicture; // 背景图
	private boolean isPause = false; // 是否暂停
	private int mPlayTime; // 播放时间
	private  OnCompletedListener mCompLsn;
	private boolean finishFlag = false;

	public CircleProgressButton(Context context) {
		super(context);
		defaultParam();
	}

	public CircleProgressButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub

		defaultParam();

		TypedArray array = context.obtainStyledAttributes(attrs,
				R.styleable.CircleProgressBar);

		mMaxProgress = array.getInteger(R.styleable.CircleProgressBar_max,
				DEFAULT_MAX_VALUE); // 获取进度条最大值

		boolean bFill = array.getBoolean(R.styleable.CircleProgressBar_fill,
				DEFAULT_FILL_MODE); // 获取填充模式
		int paintWidth = array.getInt(
				R.styleable.CircleProgressBar_Paint_Width, DEFAULT_PAINT_WIDTH); // 获取画笔宽度
		mCircleAttribute.setFill(bFill);
		if (bFill == false) {
			mCircleAttribute.setPaintWidth(paintWidth);
		}

		int paintColor = array.getColor(
				R.styleable.CircleProgressBar_Paint_Color, DEFAULT_PAINT_COLOR); // 获取画笔颜色

		Log.i("", "paintColor = " + Integer.toHexString(paintColor));
		mCircleAttribute.setPaintColor(paintColor);

		mCircleAttribute.mSidePaintInterval = array.getInt(
				R.styleable.CircleProgressBar_Inside_Interval,
				DEFAULT_INSIDE_VALUE);// 圆环缩进距离

		array.recycle(); // 一定要调用，否则会有问题

	}

	/*
	 * 默认参数
	 */
	private void defaultParam() {
		mCircleAttribute = new CircleAttribute();

		mCartoomEngine = new CartoomEngine();

		mMaxProgress = DEFAULT_MAX_VALUE;
		mMainCurProgress = 0;

	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) { // 设置视图大小
		// TODO Auto-generated method stub
		// super.onMeasure(widthMeasureSpec, heightMeasureSpec);

		int width = MeasureSpec.getSize(widthMeasureSpec);
		int height = MeasureSpec.getSize(heightMeasureSpec);

		mBackgroundPicture = getBackground();
		if (mBackgroundPicture != null) {
			width = mBackgroundPicture.getMinimumWidth();
			height = mBackgroundPicture.getMinimumHeight();
		}

		setMeasuredDimension(resolveSize(width, widthMeasureSpec),
				resolveSize(height, heightMeasureSpec));

	}

	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		// TODO Auto-generated method stub
		super.onSizeChanged(w, h, oldw, oldh);

		mCircleAttribute.autoFix(w, h);

	}

	public void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);

		if (mBackgroundPicture == null) // 没背景图的话就绘制底色
		{
			canvas.drawArc(mCircleAttribute.mRoundOval, 0, 360,
					mCircleAttribute.mBRoundPaintsFill,
					mCircleAttribute.mBottomPaint);
		}



		float rate = (float) mMainCurProgress / mMaxProgress;
		float sweep = 360 * rate;
		canvas.drawArc(mCircleAttribute.mRoundOval, mCircleAttribute.mDrawPos,
				sweep, mCircleAttribute.mBRoundPaintsFill,
				mCircleAttribute.mMainPaints);
		postInvalidate();
	}

	/*
	 * 设置主进度值
	 */
	public synchronized void setMainProgress(int progress) {
		mMainCurProgress = progress;
		if (mMainCurProgress < 0) {
			mMainCurProgress = 0;
		}

		if (mMainCurProgress > mMaxProgress) {
			mMainCurProgress = mMaxProgress;
		}

		postInvalidate();
	}

	public synchronized int getMainProgress() {
		return mMainCurProgress;
	}

	public Drawable getmBackgroundPicture() {
		return mBackgroundPicture;
	}


	public void setmBackgroundPicture(Drawable mBackgroundPicture) {
		this.mBackgroundPicture = mBackgroundPicture;
	}



	public int getmPlayTime() {
		return mPlayTime;
	}

	/**
	 * 设置播放时间，单位（s）
	 *
	 * @param mPlayTime
	 */
	public void setmPlayTime(int mPlayTime) {
		this.mPlayTime = mPlayTime;
	}


	public boolean isPause() {
		return isPause;
	}

	public boolean isBCartoom() {
		return isBCartoom;
	}

	/**
	 * 开启动画
	 */
	public void startCartoom() {

			mCartoomEngine.startCartoom();


		isPause=true;
	}
	/**
	 * 暂停动画
	 */
	public void pauseCartoom(){
		mCartoomEngine.pauseCartoom();
		isPause=false;
	}

	/**
	 * 结束动画
	 */
	public void stopCartoom() {
		mCartoomEngine.stopCartoom();
	}

	/**
	 * 回调接口
	 * @author zengjiyang
	 */
	public interface OnCompletedListener{
		public void OnCompleted(CircleProgressButton progressButton,boolean finishflag);
	}
	/**
	 * 为控件设置的监听，供外部方法调用，监听动画是否播放结束
	 * @param completedListener
	 */
	public void setOnCompletedListener(OnCompletedListener completedListener){
		finishFlag = true;
		this.mCompLsn = completedListener;
	}

	class CircleAttribute {
		public RectF mRoundOval; // 圆形所在矩形区域
		public boolean mBRoundPaintsFill; // 是否填充以填充模式绘制圆形
		public int mSidePaintInterval; // 圆形向里缩进的距离
		public int mPaintWidth; // 圆形画笔宽度（填充模式下无视）
		public int mPaintColor; // 画笔颜色 （即主进度条画笔颜色，子进度条画笔颜色为其半透明值）
		public int mDrawPos; // 绘制圆形的起点（默认为-90度即12点钟方向）

		public Paint mMainPaints; // 主进度条画笔


		public Paint mBottomPaint; // 无背景图时绘制所用画笔

		public CircleAttribute() {
			mRoundOval = new RectF();
			mBRoundPaintsFill = DEFAULT_FILL_MODE;
			mSidePaintInterval = DEFAULT_INSIDE_VALUE;
			mPaintWidth = 0;
			mPaintColor = DEFAULT_PAINT_COLOR;
			mDrawPos = -90;

			mMainPaints = new Paint();
			mMainPaints.setAntiAlias(true);
			mMainPaints.setStyle(Paint.Style.FILL);
			mMainPaints.setStrokeWidth(mPaintWidth);
			mMainPaints.setColor(mPaintColor);



			mBottomPaint = new Paint();
			mBottomPaint.setAntiAlias(true);
			mBottomPaint.setStyle(Paint.Style.FILL);
			mBottomPaint.setStrokeWidth(mPaintWidth);
			mBottomPaint.setColor(Color.TRANSPARENT);

		}

		/*
		 * 设置画笔宽度
		 */
		public void setPaintWidth(int width) {
			mMainPaints.setStrokeWidth(width);
			mBottomPaint.setStrokeWidth(width);
		}

		/*
		 * 设置画笔颜色
		 */
		public void setPaintColor(int color) {
			mMainPaints.setColor(color);
		}

		/*
		 * 设置填充模式
		 */
		public void setFill(boolean fill) {
			mBRoundPaintsFill = fill;
			if (fill) {
				mMainPaints.setStyle(Paint.Style.FILL);
				mBottomPaint.setStyle(Paint.Style.FILL);
			} else {
				mMainPaints.setStyle(Paint.Style.STROKE);
				mBottomPaint.setStyle(Paint.Style.STROKE);
			}
		}

		/*
		 * 自动修正
		 */
		public void autoFix(int w, int h) {
			if (mSidePaintInterval != 0) {
				mRoundOval.set(mPaintWidth / 2 + mSidePaintInterval,
						mPaintWidth / 2 + mSidePaintInterval, w - mPaintWidth
								/ 2 - mSidePaintInterval, h - mPaintWidth / 2
								- mSidePaintInterval);
			} else {

				int sl = getPaddingLeft();
				int sr = getPaddingRight();
				int st = getPaddingTop();
				int sb = getPaddingBottom();

				mRoundOval.set(sl + mPaintWidth / 2, st + mPaintWidth / 2, w
						- sr - mPaintWidth / 2, h - sb - mPaintWidth / 2);
			}
		}

	}

	class CartoomEngine {
		public Handler mHandler = null;
		public boolean mBCartoom; // 是否正在作动画
		public Timer mTimer = null; // 用于作动画的TIMER
		public MyTimerTask mTimerTask = null; // 动画任务
		public int mSaveMax; // 在作动画时会临时改变MAX值，该变量用于保存值以便恢复
		public int mTimerInterval; // 定时器触发间隔时间(ms)
		public float mCurFloatProcess; // 作动画时当前进度值
		private int time;
		//private long timeMil;

		public CartoomEngine() {

			mBCartoom = false;
			isBCartoom = mBCartoom;
			mTimer = new Timer();
			mSaveMax = 0;
			mTimerInterval = 50;
			mCurFloatProcess = 0;

			mHandler = new Handler() {

				@Override
				public void handleMessage(Message msg) {
					// TODO Auto-generated method stub
					switch (msg.what) {
						case TIMER_ID: {
							if (mBCartoom == false) {
								return;
							}

							mCurFloatProcess += 1;
							setMainProgress((int) mCurFloatProcess);
//						Log.d("start", ""+mCurFloatProcess);
							//long curtimeMil = System.currentTimeMillis();

							//timeMil = curtimeMil;

							if (mCurFloatProcess >= mMaxProgress) {
								stopCartoom();
								mCurFloatProcess = 0;
								if(finishFlag&&!isBCartoom){
									mCompLsn.OnCompleted(CircleProgressButton.this, !isBCartoom);
								}
							}
						}
						break;
					}
				}

			};

		}

		public synchronized void startCartoom() {
			setTime(mPlayTime);
			if (time <= 0 ) {
				return;
			}
			if (mTimer == null) {
				mTimer = new Timer();
			}

			if (mTimerTask == null) {
				mTimerTask = new MyTimerTask();
			}

			//timeMil = 0;

			mBCartoom = true;
			isBCartoom = mBCartoom;

			mSaveMax = mMaxProgress;
			mMaxProgress = (1000 / mTimerInterval) * time;


			if (mTimer != null && mTimerTask != null) {
				mTimer.schedule(mTimerTask, mTimerInterval, mTimerInterval);
			}

		}

		public synchronized void stopCartoom() {


			mBCartoom = false;
			isBCartoom = mBCartoom;
			isPause = false;
			mMaxProgress = mSaveMax;
			mCurFloatProcess = 0;
			setMainProgress(0);

			if (mTimerTask != null) {
				mTimerTask.cancel();
				mTimerTask = null;

			}
		}

		public synchronized void pauseCartoom() {

			mBCartoom = false;
			isBCartoom = mBCartoom;
			isPause = true;
//			Log.d("pause1", ""+mCurFloatProcess);
			setMainProgress((int) mCurFloatProcess);


//			Log.d("pause2", ""+mCurFloatProcess);
			if (mTimerTask != null) {
				mTimerTask.cancel();
				mTimerTask = null;

			}
		}

		public int getTime() {
			return time;
		}

		public void setTime(int time) {
			this.time = time;
		}

		private final static int TIMER_ID = 0x0010;

		class MyTimerTask extends TimerTask {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				Message msg = mHandler.obtainMessage(TIMER_ID);
				msg.sendToTarget();
			}

		}
	}


}
