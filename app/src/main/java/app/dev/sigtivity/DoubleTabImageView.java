package app.dev.sigtivity;

import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

/**
 * Created by Ravi on 10/10/2015.
 */
public class DoubleTabImageView extends ImageView {
    private Context context;
    private GestureListner mGestureListner;
    private GestureDetector mGestureDetector;

    public DoubleTabImageView(Context context) {
        super(context);
        sharedConstructing(context);
    }

    public DoubleTabImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        sharedConstructing(context);
    }

    public DoubleTabImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        sharedConstructing(context);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(getMeasuredWidth(), getMeasuredWidth()); //Snap to width
    }

    private void sharedConstructing(Context context){
        super.setClickable(true);
        this.context = context;
        mGestureListner = new GestureListner();
        mGestureDetector = new GestureDetector(context, mGestureListner, null, true);
        setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mGestureDetector.onTouchEvent(event);
                invalidate();
                return false;
            }
        });
    }


    public class GestureListner extends GestureDetector.SimpleOnGestureListener{
        @Override
        public boolean onDoubleTap(MotionEvent e) {
            return true;
        }
    }
}
