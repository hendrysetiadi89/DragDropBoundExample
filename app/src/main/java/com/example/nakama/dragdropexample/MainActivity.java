package com.example.nakama.dragdropexample;

import android.content.ClipData;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {

    private View imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        imageView = findViewById(R.id.image_view);
//        imageView.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                if (event.getAction() == MotionEvent.ACTION_DOWN) {
//                    ClipData data = ClipData.newPlainText("", "");
//                    View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(
//                            imageView);
//                    imageView.startDrag(data, shadowBuilder, imageView, 0);
//                    imageView.setVisibility(View.INVISIBLE);
//                    return true;
//                } else {
//                    return false;
//                }
//            }
//        });

        View frame = findViewById(R.id.frame);

        imageView.setOnTouchListener(new OnDragTouchListener(imageView, frame, new OnDragTouchListener.OnDragActionListener() {
            @Override
            public void onDragStart(View view) {

            }

            @Override
            public void onDragEnd(View view) {

            }
        }));


//        frame.setOnDragListener(new OnDragTouchListener(imageView));
//        frame.setOnDragListener(new View.OnDragListener() {
//            @Override
//            public boolean onDrag(View v, DragEvent event) {
//                int action = event.getAction();
//                switch (event.getAction()) {
//                    case DragEvent.ACTION_DRAG_STARTED:
//                        // do nothing
//                        break;
//                    case DragEvent.ACTION_DRAG_ENTERED:
//                        //v.setBackgroundDrawable(enterShape);
//                        break;
//                    case DragEvent.ACTION_DRAG_EXITED:
//                        //v.setBackgroundDrawable(normalShape);
//                        break;
//                    case DragEvent.ACTION_DROP:
//                        // Dropped, reassign View to ViewGroup
//                        View view = (View) event.getLocalState();
////                        ViewGroup owner = (ViewGroup) view.getParent();
////                        owner.removeView(view);
////                        LinearLayout container = (LinearLayout) v;
////                        container.addView(view);
//                        imageView.setVisibility(View.VISIBLE);
//                        break;
//                    case DragEvent.ACTION_DRAG_ENDED:
//                        //v.setBackgroundDrawable(normalShape);
//                    default:
//                        break;
//                }
//                return true;
//            }
//        });
    }

    public static class OnDragTouchListener implements View.OnTouchListener {

        /**
         * Callback used to indicate when the drag is finished
         */
        public interface OnDragActionListener {
            /**
             * Called when drag event is started
             *
             * @param view The view dragged
             */
            void onDragStart(View view);

            /**
             * Called when drag event is completed
             *
             * @param view The view dragged
             */
            void onDragEnd(View view);
        }

        private View mView;
        private View mParent;
        private boolean isDragging;
        private boolean isInitialized = false;

        private int width;
        private float maxLeft;
        private float maxRight;
        private float dX;

        private int height;
        private float maxTop;
        private float maxBottom;
        private float dY;

        private OnDragActionListener mOnDragActionListener;

        public OnDragTouchListener(View view) {
            this(view, (View) view.getParent(), null);
        }

        public OnDragTouchListener(View view, View parent) {
            this(view, parent, null);
        }

        public OnDragTouchListener(View view, OnDragActionListener onDragActionListener) {
            this(view, (View) view.getParent(), onDragActionListener);
        }

        public OnDragTouchListener(View view, View parent, OnDragActionListener onDragActionListener) {
            initListener(view, parent);
            setOnDragActionListener(onDragActionListener);
        }

        public void setOnDragActionListener(OnDragActionListener onDragActionListener) {
            mOnDragActionListener = onDragActionListener;
        }

        public void initListener(View view, View parent) {
            mView = view;
            mParent = parent;
            isDragging = false;
            isInitialized = false;
        }

        public void updateBounds() {
            updateViewBounds();
            updateParentBounds();
            isInitialized = true;
        }

        public void updateViewBounds() {
            width = mView.getWidth();
            height = mView.getHeight();
        }

        public void updateParentBounds() {
            maxLeft = 0;
            maxRight = maxLeft + mParent.getWidth();

            maxTop = 0;
            maxBottom = maxTop + mParent.getHeight();
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (isDragging) {
                float[] bounds = new float[4];
                // LEFT
                bounds[0] = event.getRawX() + dX;
                if (bounds[0] < maxLeft) {
                    bounds[0] = maxLeft;
                }
                // RIGHT
                bounds[2] = bounds[0] + width;
                if (bounds[2] > maxRight) {
                    bounds[2] = maxRight;
                    bounds[0] = bounds[2] - width;
                }
                // TOP
                bounds[1] = event.getRawY() + dY;
                if (bounds[1] < maxTop) {
                    bounds[1] = maxTop;
                }
                // BOTTOM
                bounds[3] = bounds[1] + height;
                if (bounds[3] > maxBottom) {
                    bounds[3] = maxBottom;
                    bounds[1] = bounds[3] - height;
                }

                switch (event.getAction()) {
                    case MotionEvent.ACTION_CANCEL:
                    case MotionEvent.ACTION_UP:
                        onDragFinish();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        mView.setX(bounds[0]);
                        mView.setY(bounds[1]);
                        // mView.animate().x(bounds[0]).y(bounds[1]).setDuration(0).start();
                        break;
                }
                return true;
            } else {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        isDragging = true;
                        if (!isInitialized) {
                            updateBounds();
                        }
                        dX = v.getX() - event.getRawX();
                        dY = v.getY() - event.getRawY();
                        if (mOnDragActionListener != null) {
                            mOnDragActionListener.onDragStart(mView);
                        }
                        return true;
                }
            }
            return false;
        }

        private void onDragFinish() {
            if (mOnDragActionListener != null) {
                mOnDragActionListener.onDragEnd(mView);
            }

            dX = 0;
            dY = 0;
            isDragging = false;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
