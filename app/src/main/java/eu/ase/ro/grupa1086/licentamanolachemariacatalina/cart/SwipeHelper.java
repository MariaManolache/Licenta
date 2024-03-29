package eu.ase.ro.grupa1086.licentamanolachemariacatalina.cart;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.GestureDetector;
import android.view.MotionEvent;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;

public abstract class SwipeHelper extends ItemTouchHelper.SimpleCallback {

    int buttonWidth;
    private RecyclerView recyclerView;
    private List<MyButton> buttonList;
    private GestureDetector gestureDetector;
    private int swipePosition = -1;
    private float swipeThreshold = 0.5f;
    private Map<Integer, List<MyButton>> buttonBuffer;
    private Queue<Integer> removeQueue;

    private GestureDetector.SimpleOnGestureListener gestureListener = new GestureDetector.SimpleOnGestureListener() {
        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            for(MyButton button : buttonList) {
                if(button.onClick(e.getX(), e.getY()))
                    break;

            }
            return true;
        }
    };

    private View.OnTouchListener onTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if(swipePosition < 0) {
                return false;
            }
            Point point = new Point((int)motionEvent.getRawX(), (int)motionEvent.getRawY());
            RecyclerView.ViewHolder swipeViewHolder = recyclerView.findViewHolderForAdapterPosition(swipePosition);
            if(swipeViewHolder != null) {
                View swipedItem = swipeViewHolder.itemView;
                Rect rect = new Rect();
                swipedItem.getGlobalVisibleRect(rect);

                if(motionEvent.getAction() == MotionEvent.ACTION_DOWN ||
                        motionEvent.getAction() == MotionEvent.ACTION_UP ||
                        motionEvent.getAction() == MotionEvent.ACTION_MOVE ) {
                    if(rect.top < point.y && rect.bottom > point.y) {
                        gestureDetector.onTouchEvent(motionEvent);
                    } else {
                        removeQueue.add(swipePosition);
                        swipePosition = -1;
                    }
                }
            }

            return false;
        }
    };

    public SwipeHelper(Context context, RecyclerView recyclerView, int buttonWidth) {
        super(0, ItemTouchHelper.LEFT);
        this.recyclerView = recyclerView;
        this.buttonList = new ArrayList<>();
        this.gestureDetector = new GestureDetector(context, gestureListener);
        this.recyclerView.setOnTouchListener(onTouchListener);
        this.buttonBuffer = new HashMap<>();
        this.buttonWidth = buttonWidth;

        removeQueue = new LinkedList<Integer>() {
            @Override
            public boolean add(Integer integer) {
                if(contains(integer)) {
                    return false;
                } else {
                    return super.add(integer);
                }
            }
        };
        
        attachSwipe();
    }

    private void attachSwipe() {
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(this);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    private synchronized  void recoverSwipedItem() {
        while(!removeQueue.isEmpty()) {
            int position = removeQueue.poll();
            if(position > -1) {
                Objects.requireNonNull(recyclerView.getAdapter()).notifyItemChanged(position);
            }
        }
    }

    public class MyButton{
        private String text;
        private int imageResId;
        private int textSize;
        private int color;
        private int position;
        private RectF clickRegion;
        private ButtonClickListener listener;
        private Context context;
        private Resources resources;

        public MyButton(Context context, String text, int textSize, int imageResId, int color, ButtonClickListener listener) {
            this.text = text;
            this.imageResId = imageResId;
            this.textSize = textSize;
            this.color = color;
            this.listener = listener;
            this.context = context;
        }

        public boolean onClick(float x, float y)
        {
            if(clickRegion != null && clickRegion.contains(x,y)) {
                listener.onClick(position);
                return true;
            }
            return false;
        }

        public void onDraw(Canvas c, RectF rectF, int position) {
            Paint paint = new Paint();
            paint.setColor(color);
            c.drawRect(rectF, paint);

            paint.setColor(Color.WHITE);
            paint.setTextSize(textSize);

            Rect rect = new Rect();
            float cHeight = rectF.height();
            float cWidth = rectF.width();
            paint.setTextAlign(Paint.Align.LEFT);
            paint.getTextBounds(text, 0, text.length(), rect);
            float x = 0, y = 0;
            if(imageResId == 0) {
                x = cWidth / 2f - rect.width()/2 - rect.left;
                y = cHeight / 2f + rect.height()/2f - rect.bottom;
                c.drawText(text, rectF.left + x, rectF.top + y, paint);
            } else {
                Drawable drawable = ContextCompat.getDrawable(context, imageResId);
                Bitmap bitmap = drawableToBitmap(drawable);
                c.drawBitmap(bitmap, (rectF.left + rectF.right) / 2, (rectF.top + rectF.bottom)/2, paint);
            }

            clickRegion = rectF;
            this.position = position;
        }
    }

    private Bitmap drawableToBitmap(Drawable drawable) {
        if( drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable)drawable).getBitmap();
        }
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        int position = viewHolder.getAbsoluteAdapterPosition();
        if(swipePosition != position) {
            removeQueue.add(swipePosition);
        }
        swipePosition = position;
        if(buttonBuffer.containsKey(swipePosition)) {
            buttonList = buttonBuffer.get(swipePosition);
        } else {
            buttonList.clear();
        }
        buttonBuffer.clear();
        swipeThreshold = 0.5f * buttonList.size() * buttonWidth;
        recoverSwipedItem();
    }

    @Override
    public float getSwipeThreshold(@NonNull RecyclerView.ViewHolder viewHolder) {
        return swipeThreshold;
    }

    @Override
    public float getSwipeEscapeVelocity(float defaultValue) {
        return 0.1f * defaultValue;
    }

    @Override
    public float getSwipeVelocityThreshold(float defaultValue) {
        return 5.0f * defaultValue;
    }

    @Override
    public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        int position = viewHolder.getAbsoluteAdapterPosition();
        float translationX = dX;
        View itemView = viewHolder.itemView;
        if(position < 0) {
            swipePosition = position;
            return ;
        }
        if(actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
            if(dX < 0) {
                List<MyButton> buffer = new ArrayList<>();
                if(!buttonBuffer.containsKey(position)) {
                    instantiateMyButton(viewHolder, buffer);
                    buttonBuffer.put(position, buffer);
                } else {
                    buffer = buttonBuffer.get(position);
                }
                if (buffer != null) {
                    translationX = dX * buffer.size() * buttonWidth / itemView.getWidth();
                    drawButton(c, itemView, buffer, position, translationX);
                }
            }
        }
        super.onChildDraw(c, recyclerView, viewHolder, translationX, dY, actionState, isCurrentlyActive);
    }

    private void drawButton(Canvas c, View itemView, List<MyButton> buffer, int position, float translationX) {
        float right = itemView.getRight();
        float dButtonWidth = -1 * translationX / buffer.size();
        for(MyButton button : buffer) {
            float left = right - dButtonWidth;
            button.onDraw(c, new RectF(left, itemView.getTop(), right, itemView.getBottom()), position);
            right = left;
        }
    }

    public abstract void instantiateMyButton(RecyclerView.ViewHolder viewHolder, List<MyButton> buffer);
}
