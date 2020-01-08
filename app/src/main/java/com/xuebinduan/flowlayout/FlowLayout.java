package com.xuebinduan.flowlayout;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

/**
 * 标题：最简单的自定义ViewGroup
 * <p>
 * 流程：就是在onMeasured调用measureChild()，然后在layout调用子View的layout()
 * <p>
 * 加一个gravity属性,center|center_horizontal|center_vertical
 * 处理measure只支持EXACTLY的问题
 * 处理margin
 * 处理子View不可见的情况
 */
public class FlowLayout extends ViewGroup {

    public FlowLayout(Context context) {
        this(context, null);
    }

    public FlowLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FlowLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new MarginLayoutParams(getContext(), attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSpecSize = MeasureSpec.getSize(heightMeasureSpec);

        int width = 0, height = 0;
        int viewHeight = 0;
        int viewWidth = 0;
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childView = getChildAt(i);
            if (childView.getVisibility() != GONE) {
                measureChild(childView, widthMeasureSpec, heightMeasureSpec);
                MarginLayoutParams mlp = (MarginLayoutParams) childView.getLayoutParams();
                viewWidth = childView.getMeasuredWidth() + mlp.leftMargin + mlp.rightMargin;
                width += viewWidth;
                viewHeight = Math.max(viewHeight, childView.getMeasuredHeight() + mlp.topMargin + mlp.bottomMargin);
                // viewHeight需要加（换行次数+1）次
                if (i == childCount - 1) {
                    height += viewHeight;
                }
                if (width > widthSpecSize) {
                    height += viewHeight;
                    viewHeight = 0;
                    width = viewWidth;
                }
            }
        }

        // 对layout_height="wrap_content"添加支持
        if (heightSpecMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(widthSpecSize, height);
        }

    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int childCount = getChildCount();
        int layoutWidth = r-l;
        int left = 0;
        int right = 0;
        int top = 0;
        int maxHeightInLine = 0; // 一行中Views中最大的高度
        for (int i = 0; i < childCount; i++) {
            View childView = getChildAt(i);
            if (childView.getVisibility() != GONE) {
                // 对margin添加支持
                MarginLayoutParams mlp = (MarginLayoutParams) childView.getLayoutParams();
                maxHeightInLine = Math.max(maxHeightInLine, mlp.topMargin + childView.getMeasuredHeight() + mlp.bottomMargin);
                left += mlp.leftMargin;
                right = left + childView.getMeasuredWidth();
                // 换行：比较right，right如果大于Layout宽度，那么要换行
                if (right > layoutWidth) {
                    left = mlp.leftMargin;
                    right = left + childView.getMeasuredWidth();
                    top = top + maxHeightInLine;
                    maxHeightInLine = 0;
                }
                getChildAt(i).layout(left, top + mlp.topMargin, right, top + mlp.topMargin + childView.getMeasuredHeight());
                left = left + childView.getMeasuredWidth() + mlp.rightMargin;
            }
        }
    }

}
