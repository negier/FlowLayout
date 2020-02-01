# FlowLayout
子View们的宽度加起来超过一行，会自动换行显示。

**有谷歌官方实现，实际项目开发请用![谷歌FlexLayout](https://github.com/google/flexbox-layout)**

<br/>

![](https://github.com/negier/FlowLayout/blob/master/screenshot/flowlayout.png)


核心就两步：
* 在Layout中的onMeasure方法中**调用子View的measure()**，这儿虽然用的是measureChild方法，但最终还是去调用子View的measure()
* 在Layout中的onLayout方法中**调用子View的layout()**

<br/>


*再复杂的自定义View都是这样从最简单的形式，不断增加代码，迭代出来的* 

<br/>


最简单的自定义ViewGroup：
```
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
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            measureChild(getChildAt(i), widthMeasureSpec, heightMeasureSpec);
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int childCount = getChildCount();
        int layoutWidth = r-l;
        int left = 0;
        int right = 0;
        int top = 0;
        for (int i = 0; i < childCount; i++) {
            View view = getChildAt(i);
            // 换行：比较right，right如果大于Layout宽度，那么要换行
            right = left + view.getMeasuredWidth();
            if (right > layoutWidth) {
                left = 0;
                right = left + view.getMeasuredWidth();
                top += view.getMeasuredHeight();
            }
            getChildAt(i).layout(left, top, right, top + view.getMeasuredHeight());
            left += view.getWidth();
        }
    }

}

```

<br/>


## 在最简单的实现下，我们可以考虑更多，当然代码也就更多，如：
* 处理子View不可见的情况
* 添加对MeasureSpec.AT_MOST的支持
* 添加对layout_margin的支持

<br/>


上述这些我也都实现了

<br/>

[完整代码查看](https://github.com/negier/FlowLayout/blob/master/app/src/main/java/com/xuebinduan/flowlayout/FlowLayout.java)

