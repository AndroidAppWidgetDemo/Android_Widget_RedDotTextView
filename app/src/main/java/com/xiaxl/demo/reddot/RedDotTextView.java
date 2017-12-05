package com.xiaxl.demo.reddot;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;

/**
 * 显示红点的TextView
 */
public final class RedDotTextView extends android.support.v7.widget.AppCompatTextView {

    private RedDotDrawable mRedDotDrawable;

    public RedDotTextView(Context context) {
        this(context, null);
    }

    public RedDotTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    /**
     * 构造方法
     *
     * @param context
     * @param attrs
     * @param defStyle
     */
    public RedDotTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        // 获取自定义属性
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.RedDotTextView);
        // 徽标文本，为空时显示一个小红点
        String badgeText = a.getString(R.styleable.RedDotTextView_redDotText);
        // 徽标背景色
        int badgeColor = a.getColor(R.styleable.RedDotTextView_redDotColor, 0xffFF4081);
        // 徽标高度，宽度自适应
        int badgeHeight = a.getDimensionPixelSize(R.styleable.RedDotTextView_redDotHeight, (int) (getResources().getDisplayMetrics().density * 12));
        // 徽标是否可见
        boolean badgeVisible = a.getBoolean(R.styleable.RedDotTextView_redDotVisible, false);
        a.recycle();

        // 初始化Drawable
        mRedDotDrawable = new RedDotDrawable(badgeHeight, badgeColor);
        // 设置显示状态
        mRedDotDrawable.setVisible(badgeVisible);
        // 设置Drawable显示文字
        mRedDotDrawable.setText(badgeText);

        //
        setIcon(getCompoundDrawables()[1]);

    }

    public RedDotTextView setIcon(Drawable drawable) {
        if (drawable != null && drawable.getBounds().isEmpty()) {
            drawable.setBounds(0, 0, drawable.getIntrinsicHeight(), drawable.getIntrinsicHeight());
        }
        Drawable[] cds = getCompoundDrawables();
        setCompoundDrawables(cds[0], drawable, cds[2], cds[3]);
        return this;
    }

    @Override
    public void setText(CharSequence text, BufferType type) {
        super.setText(text, type);
    }

    /**
     * 设置红点上的显示文字
     *
     * @param text
     * @return
     */
    public RedDotTextView setBadgeText(String text) {
        mRedDotDrawable.setText(text);
        return this;
    }

    /**
     * 红点是否显示
     *
     * @param visible
     * @return
     */
    public RedDotTextView setBadgeVisible(boolean visible) {
        mRedDotDrawable.setVisible(visible);
        return this;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //
        int width = getMeasuredWidth();
        if (getCompoundDrawables()[1] != null) {
            mRedDotDrawable.layout((width + getCompoundDrawables()[1].getIntrinsicWidth()) / 2, getPaddingTop(), width);
        } else {
            mRedDotDrawable.layout((width + (int) getLayout().getLineWidth(0)) / 2, getPaddingTop(), width);
        }

    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        // 绘制drawable
        mRedDotDrawable.draw(canvas);
    }

    /**
     *
     */
    private static class RedDotDrawable extends GradientDrawable {
        private String mText;
        // BadgeDrawable是否显示
        private boolean mIsVisible;
        private TextPaint mPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);

        private int mHeight = 0;

        /**
         * @param height 高度
         * @param color  Drawable颜色
         */
        public RedDotDrawable(int height, int color) {
            // 高度
            mHeight = height;
            // Drawable颜色
            setColor(color);
            // 画笔设置
            // 画笔颜色
            mPaint.setColor(0xffffffff);
            mPaint.setTextAlign(Paint.Align.CENTER);
            // 字体大小
            mPaint.setTextSize(height * 0.8f);
        }

        /**
         * @param x
         * @param y
         * @param max
         */
        void layout(int x, int y, int max) {
            Rect rect = getBounds();
            rect.offsetTo(Math.min(x - rect.width() / 2, max - rect.width() - (int) (0.2f * mHeight)), Math.max(0, y - rect.height() / 2));
            setBounds(rect);
        }

        /**
         * drawable 宽高设置
         *
         * @param w 宽
         * @param h 高
         */
        void resize(int w, int h) {
            Rect rect = getBounds();
            setBounds(rect.left, rect.top, rect.left + w, rect.top + h);
            invalidateSelf();
        }

        /**
         * 设置红点上显示的文字
         *
         * @param text
         */
        public void setText(String text) {
            mText = text;
            //
            if (TextUtils.isEmpty(mText)) {
                int size = (int) (mHeight * 0.65);
                resize(size, size);
            } else {
                int width = (int) (mPaint.measureText(mText) + 0.4 * mHeight);
                resize(Math.max(width, mHeight), mHeight);
            }
        }

        /**
         * Drawable显示状态调整
         *
         * @param visible
         */
        public void setVisible(boolean visible) {
            if (mIsVisible != visible) {
                invalidateSelf();
            }
            mIsVisible = visible;
        }

        @Override
        protected void onBoundsChange(Rect bounds) {
            super.onBoundsChange(bounds);
            setCornerRadius(getBounds().height() / 2f);
        }

        @Override
        public void draw(Canvas canvas) {
            // BadgeDrawable是否显示
            if (!mIsVisible) {
                return;
            }
            super.draw(canvas);
            // 文字判空处理
            if (TextUtils.isEmpty(mText)) {
                return;
            }
            // 绘制文字
            float x = getBounds().exactCenterX();
            float y = getBounds().exactCenterY() - (mPaint.descent() + mPaint.ascent()) / 2;
            canvas.drawText(mText, x, y, mPaint);
        }
    }


}