package newsimooc.imooc.com.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import newsimooc.imooc.com.gamepuzzles.R;
import newsimooc.imooc.com.utils.ImagePiece;
import newsimooc.imooc.com.utils.ImageSplitterUtil;

/**
 * Created by user on 2017/10/12.
 */

public class GameLayout extends RelativeLayout implements View.OnClickListener{

    private int mColumn = 3;
    //容器的内边距
    private int mPadding;
    //每张小图之间的距离(横、纵) dp
    private int mMagin = 3;
    private ImageView[] mGameItems;
    private int mItemWidth;
    //游戏的图片
    private Bitmap mbitmap;
    //游戏面板宽度
    private int mWidth;

    public int getRandomIamgeId() {
        double x = Math.random();
        //Toast.makeText(getContext(), "x = " + x, Toast.LENGTH_LONG).show();
        if (x > 0.9) {
            return R.drawable.kiku;
        } else if (x > 0.8) {
            return R.drawable.kiku2;
        } else if (x > 0.7) {
            return R.drawable.kiku3;
        } else if (x > 0.6) {
            return R.drawable.kiku4;
        } else if (x > 0.5) {
            return R.drawable.kiku5;
        } else if (x > 0.4) {
            return R.drawable.kiku6;
        } else if (x > 0.3) {
            return R.drawable.kiku7;
        } else if (x > 0.2){
            return R.drawable.kiku8;
        } else if (x > 0.1) {
            return R.drawable.kiku9;
        } else {
            return R.drawable.kiku10;
        }
    }

    private boolean isGameSuccess;
    private boolean isGameOver;

    public interface GameListener {
        void nextLevel(int nextLevel);
        void timechanged(int currentTime);
        void gameover();
    }

    public GameListener mListener;

    /**
     * 设置接口回调
     * @param mListener
     */
    public void setOnGameListener(GameListener mListener) {
        this.mListener = mListener;
    }

    private int level = 1;
    private static final int TIME_CHANGED = 0;
    private static final int NEXT_LEVEL = 1;

    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case TIME_CHANGED:
                    if (isGameSuccess || isGameOver) {
                        return ;
                    }

                    if (mListener != null) {
                        mListener.timechanged(mTime);
                        if (mTime == 0) {
                            isGameOver = true;
                            mListener.gameover();
                            return ;
                        }
                    }
                    mTime --;
                    Log.d("MainActivity ", "mTime = " + mTime);
                    //延迟1s发送
                    mHandler.sendEmptyMessageDelayed(TIME_CHANGED, 1000);
                    Log.d("MainActivity ", "延迟1s");
                    break;
                case NEXT_LEVEL:
                    level ++;
                    if (mListener != null) {
                        mListener.nextLevel(level);
                    } else {
                        nextLevel();
                    }
                    break;
            }
        }
    };


    private boolean isTimeEnabled = false;
    private int mTime;
    /**
     * 设置是否开启时间
     * @param isTimeEnabled
     */
    public void setTimeEnabled(boolean isTimeEnabled) {
        this.isTimeEnabled = isTimeEnabled;
    }

    private List<ImagePiece> mItemBitmaps;
    private boolean once;

    public GameLayout(Context context) {
        this(context, null);
    }

    public GameLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GameLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init();
    }

    private void init() {
        //把2dp转换成2px
        mMagin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2,
                getResources().getDisplayMetrics());
        mPadding = min(getPaddingRight(), getPaddingLeft(), getPaddingTop(), getPaddingBottom());
    }

    //决定View本身大小多少
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //取宽和高 中的较小值
        mWidth = Math.min(getMeasuredHeight(), getMeasuredWidth());
        if (!once) {
            //进行切图，以及排序
            initBitmap();
            //设置ImageView的宽高的属性
            initItem();

            //判断是否开启时间
            checkTimeEnable();

            once = true;
        }
        //对View的宽高进行赋值
        setMeasuredDimension(mWidth, mWidth);
    }

    private void checkTimeEnable() {
        if (isTimeEnabled) {
            //根据当前等级设置时间
            countTimeBaseLevel();
            mHandler.sendEmptyMessage(TIME_CHANGED);
        }
    }

    private void countTimeBaseLevel() {
        mTime = (int) Math.pow(2, level-1) * 60;
    }

    /**
     * 切图、排序
     */
    private void initBitmap() {
        //if (mbitmap == null) {
            mbitmap = BitmapFactory.decodeResource(getResources(), getRandomIamgeId());
        //}
        mItemBitmaps = ImageSplitterUtil.splitImage(mbitmap, mColumn);

        //使用sort实现乱序
        Collections.sort(mItemBitmaps, new Comparator<ImagePiece>() {
            @Override
            public int compare(ImagePiece a, ImagePiece b) {
                return Math.random() > 0.5 ? 1 : -1;
            }
        });
    }

    /**
     * 设置ImageView的宽高的属性
     */
    private void initItem() {
        mItemWidth = (mWidth - mPadding * 2 - mMagin * (mColumn - 1)) / mColumn;
        mGameItems = new ImageView[mColumn * mColumn];
        //生成Item，设置Rule
        for (int i=0; i<mGameItems.length; i++) {
            ImageView item = new ImageView(getContext());
            item.setOnClickListener(this);
            //此时图片已经实现乱序
            item.setImageBitmap(mItemBitmaps.get(i).getBitmap());
            mGameItems[i] = item;
            item.setId(i+1);

            //在Item的Tag中存储了index
            item.setTag(i + "_" + mItemBitmaps.get(i).getIndex());

            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(mItemWidth, mItemWidth);

            //设置Item横向间距，通过rightMargin
            //不是最后一列
            if ( (i+1) % mColumn != 0) {
                lp.rightMargin = mMagin;
            }
            //不是第一列
            if (i % mColumn != 0) {
                lp.addRule(RelativeLayout.RIGHT_OF, mGameItems[i-1].getId());
            }
            //如果不是第一行，设置topmargin和rule
            if ( (i+1) > mColumn) {
                lp.topMargin = mMagin;
                lp.addRule(RelativeLayout.BELOW, mGameItems[i - mColumn].getId());
            }

            addView(item, lp);
        }
    }

    public void nextLevel() {
        this.removeAllViews();
        mAnimLayout = null;
        mColumn ++;
        isGameSuccess = false;
        checkTimeEnable();
        initBitmap();
        initItem();
    }

    /**
     * 获取多个参数的最小值
     */
    private int min(int ... params) {
        int min = params[0];
        for (int param : params) {
            if (param < min) {
                min = param;
            }
        }
        return min;
    }

    private ImageView mFirst;
    private ImageView mSecond;
    @Override
    public void onClick(View v) {
        if (isAniming)
            return ;

        //两次点击同一个Item
        if (mFirst == v) {
            mFirst.setColorFilter(null);
            mFirst = null;
            return ;
        }
        if (mFirst == null) {
            mFirst = (ImageView) v;
            //mFirst.setColorFilter(Color.parseColor("#55FF0000"));
            mFirst.setColorFilter(Color.parseColor("#555E5E5E"));
        } else {
            mSecond = (ImageView) v;
            exchangeView();
        }

    }

    //动画层
    private RelativeLayout mAnimLayout;
    private boolean isAniming;

    /**
     * 交换Item
     */
    private void exchangeView() {
        mFirst.setColorFilter(null);
        //构造动画层
        setUpAnimLayout();

        ImageView first = new ImageView(getContext());
        final Bitmap firstBitmap = mItemBitmaps.get(getImageIdByTag((String) mFirst.getTag())).getBitmap();
        first.setImageBitmap(firstBitmap);
        //传入图片的宽高
        LayoutParams lp = new LayoutParams(mItemWidth, mItemWidth);
        lp.leftMargin = mFirst.getLeft() - mPadding;
        lp.topMargin = mFirst.getTop() - mPadding;
        first.setLayoutParams(lp);
        mAnimLayout.addView(first);

        ImageView second = new ImageView(getContext());
        final Bitmap secondBitmap = mItemBitmaps.get(getImageIdByTag((String) mSecond.getTag())).getBitmap();
        second.setImageBitmap(secondBitmap);
        //传入图片的宽高
        LayoutParams lp2 = new LayoutParams(mItemWidth, mItemWidth);
        lp2.leftMargin = mSecond.getLeft() - mPadding;
        lp2.topMargin = mSecond.getTop() - mPadding;
        second.setLayoutParams(lp2);
        mAnimLayout.addView(second);

        //设置动画
        TranslateAnimation anim = new TranslateAnimation(0,
                mSecond.getLeft() - mFirst.getLeft(), 0, mSecond.getTop() - mFirst.getTop());
        anim.setDuration(300);
        anim.setFillAfter(true);
        first.startAnimation(anim);

        TranslateAnimation animSecond = new TranslateAnimation(0,
                -mSecond.getLeft() + mFirst.getLeft(), 0, -mSecond.getTop() + mFirst.getTop());
        animSecond.setDuration(300);
        animSecond.setFillAfter(true);
        second.startAnimation(animSecond);

        //监听动画
        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                mFirst.setVisibility(INVISIBLE);
                mSecond.setVisibility(INVISIBLE);
                isAniming = true;
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                String firstTag = (String) mFirst.getTag();
                String secondTag = (String) mSecond.getTag();
                /*
                String firstTag = (String) mFirst.getTag();
                String secondTag = (String) mSecond.getTag();

                String[] firstParams = firstTag.split("_");
                String[] secondParams = secondTag.split("_");
                */

                //交换两张图片
                mFirst.setImageBitmap(secondBitmap);
                mSecond.setImageBitmap(firstBitmap);

                mFirst.setTag(secondTag);
                mSecond.setTag(firstTag);

                mFirst.setVisibility(VISIBLE);
                mSecond.setVisibility(VISIBLE);

                mFirst = mSecond = null;
                mAnimLayout.removeAllViews();
                //判断用户游戏是否成功
                checkSuccess();
                isAniming = false;
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });



    }

    /**
     * 判断用户游戏是否成功
     */
    private void checkSuccess() {
        boolean isSuccess = true;
        for (int i=0; i < mGameItems.length; i++) {
            ImageView imageView = mGameItems[i];
            if (getImageindex((String) imageView.getTag()) != i) {
                isSuccess = false;
            }
        }
        if (isSuccess == true) {
            Toast.makeText(getContext(), "恭喜成功！难度升级！", Toast.LENGTH_SHORT).show();
            mHandler.sendEmptyMessage(NEXT_LEVEL);
        }
    }

    //根据Tag获取Id
    public int getImageIdByTag(String tag) {
        String[] split = tag.split("_");
        return Integer.parseInt(split[0]);
    }

    public int getImageindex(String tag) {
        String[] split = tag.split("_");
        return Integer.parseInt(split[1]);
    }

    /**
     * 构造动画层
     */
    private void setUpAnimLayout() {
        if (mAnimLayout == null) {
            mAnimLayout = new RelativeLayout(getContext());
            addView(mAnimLayout);
        }
    }
}
