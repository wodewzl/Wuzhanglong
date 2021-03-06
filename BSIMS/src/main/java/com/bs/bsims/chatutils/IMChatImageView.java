
package com.bs.bsims.chatutils;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.graphics.RectF;
import android.graphics.Shader.TileMode;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;

import com.bs.bsims.R;

/**
 * IM������ʾͼƬ
 * 
 * @author zhuqian
 */
public class IMChatImageView extends View {
    // Ĭ������ξ��붥�˵ľ���
    private static final float fromTop = 12.0f;
    // Ĭ������ο��
    private static final float triangleWidth = 8.0f;
    private static final String TAG = "IMChatImageView";

    private Context mContext;
    // View�Ŀ��
    private int width;
    // View�ĸ߶�
    private int height;

    private Bitmap mBitmap;

    private Paint mPaint;
    private TextPaint mTextPaint;
    private Paint mTextAreaPaint;
    // �Ƿ������
    private boolean isLeft;
    private PathEffect mEffect;// ·��Ч��

    private String locationText;// λ����Ϣ
    private final float locationHeight = 0.2f;// Ĭ��λ����Ϣ����ΪͼƬ�߶ȵ�0.2

    private int textColor;
    private int textSize;
    private int textAreaBkg;

    private int pointW;// ָ����ƫ��
    private int pointH;// ָ��߶�ƫ����
    private int circleDismen;// Բ�ǿ��

    private int textLine = 1;// �ı�������

    public void setLocationText(String locationText) {
        if (isLocation) {
            this.locationText = locationText;
            mTextAreaPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            mTextAreaPaint.setStyle(Paint.Style.FILL);
            mTextAreaPaint.setColor(textAreaBkg);
        }
    }

    // �Ƿ���λ����Ϣ
    private boolean isLocation;

    public boolean isLocation() {
        return isLocation;
    }

    public void setLocation(boolean isLocation) {
        this.isLocation = isLocation;
    }

    public IMChatImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        initAttrs(context, attrs, defStyleAttr);
        this.mContext = context;
        this.mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setFakeBoldText(false);
        mTextPaint.density = context.getResources().getDisplayMetrics().density;
        mTextPaint.setTextSize(textSize);
        mTextPaint.setColor(textColor);

        mEffect = new CornerPathEffect(circleDismen);
    }

    // ��ʼ������
    private void initAttrs(Context context, AttributeSet attrs, int defStyleAttr) {
        TypedArray tArray = context.getTheme().obtainStyledAttributes(attrs,
                R.styleable.IMChatImageView, defStyleAttr, 0);

        for (int i = 0; i < tArray.getIndexCount(); i++) {
            int attr = tArray.getIndex(i);
            switch (attr) {
                case R.styleable.IMChatImageView_from:
                    int type = tArray.getInt(attr, 0);
                    if (type == 1) {
                        this.isLeft = true;
                    } else {
                        this.isLeft = false;
                    }
                    break;
                case R.styleable.IMChatImageView_image:
                    this.mBitmap = BitmapFactory.decodeResource(getResources(),
                            tArray.getResourceId(attr, 0));
                    break;
                case R.styleable.IMChatImageView_imageStyle:
                    if (tArray.getInt(attr, 0) == 1) {
                        isLocation = true;
                    } else {
                        isLocation = false;
                    }
                    break;
                case R.styleable.IMChatImageView_textColor:
                    textColor = tArray.getColor(attr, 0);
                    break;
                case R.styleable.IMChatImageView_textSize:
                    textSize = tArray.getDimensionPixelSize(attr,
                            (int) TypedValue.applyDimension(
                                    TypedValue.COMPLEX_UNIT_SP, 14, getResources()
                                            .getDisplayMetrics()));
                    break;
                case R.styleable.IMChatImageView_textAreaBkg:
                    textAreaBkg = tArray.getColor(attr, 0);
                    break;
                case R.styleable.IMChatImageView_arrowHeightOffset:
                    pointH = tArray.getDimensionPixelSize(attr,
                            (int) TypedValue.applyDimension(
                                    TypedValue.COMPLEX_UNIT_DIP, 40, getResources()
                                            .getDisplayMetrics()));
                    break;
                case R.styleable.IMChatImageView_arrowWidthOffset:
                    pointW = tArray.getDimensionPixelSize(attr,
                            (int) TypedValue.applyDimension(
                                    TypedValue.COMPLEX_UNIT_DIP, 12, getResources()
                                            .getDisplayMetrics()));
                    break;
                case R.styleable.IMChatImageView_circleDismen:
                    circleDismen = tArray.getDimensionPixelSize(attr,
                            (int) TypedValue.applyDimension(
                                    TypedValue.COMPLEX_UNIT_DIP, 6, getResources()
                                            .getDisplayMetrics()));
                    break;
                default:
                    break;
            }
        }
        if (this.mBitmap != null) {
            this.width = mBitmap.getWidth();
            this.height = mBitmap.getHeight();
        }
        tArray.recycle();
    }

    public IMChatImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // �Ȼ�·��
        drawPath(canvas);

        // �����λ��ͼƬ
        if (isLocation) {
            // drawIcon(canvas);
            drawLocation(canvas);
        }
    }

    /**
     * @Description ���ָ��
     * @param canvas ����
     * @date 2015-12-9 ����2:42:50
     * @author zhuqian
     * @return void
     */
    private void drawIcon(Canvas canvas) {
        if (BitmapTools.pointBitmap == null) {
            BitmapTools.pointBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.gaode_map_loc_ing);
        }
        int iconWidth = BitmapTools.pointBitmap.getWidth();
        int iconHeight = BitmapTools.pointBitmap.getHeight();
        // ��
        canvas.drawBitmap(BitmapTools.pointBitmap, width / 2 - iconWidth / 2 + pointW, height / 2 - iconHeight / 2 - pointH, null);
    }

    // ��λ��
    private void drawLocation(Canvas canvas) {
        Path path = new Path();
        int tempTr = dip2px(mContext, triangleWidth);
        subText(locationText);
        int textAreaH = 0;
        if (textLine == 1) {
            textAreaH = (int) (locationHeight * height);
        } else {
            textAreaH = (int) (locationHeight * height * 1.5f);
        }

        mTextAreaPaint.setAlpha(180);
        if (isLeft) {
            path.moveTo(tempTr, height - textAreaH);
            path.lineTo(width, height - textAreaH);
            path.lineTo(width, height - circleDismen);
            RectF oval = new RectF(width - 2 * circleDismen, height - 2 * circleDismen, width, height);
            path.arcTo(oval, 0, 90);
            path.lineTo(tempTr + circleDismen, height);
            oval = new RectF(tempTr, height - 2 * circleDismen, tempTr + 2 * circleDismen, height);
            path.arcTo(oval, 90, 90);
            path.lineTo(tempTr, height - textAreaH);
        } else {
            path.moveTo(0, height - textAreaH);
            path.lineTo(width - tempTr, height - textAreaH);
            path.lineTo(width - tempTr, height - circleDismen);
            RectF oval = new RectF(width - tempTr - 2 * circleDismen, height - 2 * circleDismen, width - tempTr, height);
            path.arcTo(oval, 0, 90);
            path.lineTo(circleDismen, height);
            oval = new RectF(0, height - 2 * circleDismen, 2 * circleDismen, height);
            path.arcTo(oval, 90, 90);
            path.lineTo(0, height - textAreaH);
        }
        canvas.drawPath(path, mTextAreaPaint);

        drawText(canvas);
    }

    private void drawText(Canvas canvas) {
        int tempTr = dip2px(mContext, triangleWidth);
        if (isLeft) {
            tempTr = 0;
        }
        int len = subText(locationText);
        if (textLine == 1) {
            // ���ڵ�һ��
            int textLen = (int) mTextPaint.measureText(locationText.substring(0, len), 0, locationText.substring(0, len).length());
            int textX = (width - tempTr - width / 12) / 2 - textLen / 2;
            int textAreaH = (int) (locationHeight * height);
            int textY = (int) (textAreaH / 2 - ((mTextPaint.descent() + mTextPaint.ascent()) / 2)) + (height - textAreaH);
            canvas.drawText(locationText.substring(0, len), textX, textY, mTextPaint);
        } else {
            // ������
            // ��һ��
            String firstText = locationText.substring(0, len - 1);
            int textLen = (int) mTextPaint.measureText(firstText, 0, firstText.length());
            int textX = (width - tempTr - width / 12) / 2 - textLen / 2;
            int textAreaH = (int) (locationHeight * height * 1.5f);
            int textY = (int) (textAreaH * 3 / 10 - ((mTextPaint.descent() + mTextPaint.ascent()) / 2)) + (height - textAreaH);
            canvas.drawText(firstText, textX, textY, mTextPaint);
            // �ڶ���
            String secondText = locationText.substring(firstText.length(), locationText.length());
            len = subText(secondText);
            Log.i(TAG, "secondText len = " + len);
            textY = (int) (textAreaH * 3 / 4 - ((mTextPaint.descent() + mTextPaint.ascent()) / 2)) + (height - textAreaH);
            Log.i(TAG, "secondText = " + secondText);
            if (len < secondText.length()) {
                secondText = secondText.substring(0, len) + "...";
            }
            canvas.drawText(secondText, textX, textY, mTextPaint);
        }
    }

    /**
     * @Description ��ֹ���ֹ��ȡ
     * @return ��ȡ֮����ַ�
     * @date 2015-12-8 ����5:22:04
     * @author zhuqian
     * @return String
     */
    private int subText(String text) {
        int tempTr = dip2px(mContext, triangleWidth);
        int textLen = 0;
        int len = text.length();
        do {
            textLen = (int) mTextPaint.measureText(text, 0, len);
        } while (textLen > (width - tempTr - width / 12) && --len > 0);
        if (len < text.length()) {
            textLine = 2;
        } else {
            textLine = 1;
        }
        return len;
    }

    private void drawPath(Canvas canvas) {
        Path path = new Path();
        int tempTr = dip2px(mContext, triangleWidth);
        int fromT = dip2px(mContext, fromTop);
        mPaint.setPathEffect(mEffect);
        if (isLeft) {
            // ���
            path.moveTo(tempTr, 0);
            path.lineTo(width, 0);
            path.lineTo(width, height);
            path.lineTo(tempTr, height);
            path.lineTo(tempTr, fromT + tempTr);
            path.lineTo(0, fromT + tempTr / 2);
            path.lineTo(tempTr, fromT);
            path.lineTo(tempTr, 0);
            path.lineTo(width, 0);
        } else {
            // �ұ�
            path.moveTo(0, 0);
            path.lineTo(width - tempTr, 0);
            path.lineTo(width - tempTr, fromT);
            path.lineTo(width, fromT + tempTr / 2);
            path.lineTo(width - tempTr, fromT + tempTr);
            path.lineTo(width - tempTr, height);
            path.lineTo(0, height);
            path.lineTo(0, 0);
            path.lineTo(width - tempTr, 0);
        }
        BitmapShader bitmapShader = new BitmapShader(mBitmap, TileMode.CLAMP,
                TileMode.CLAMP);
        mPaint.setShader(bitmapShader);
        canvas.drawPath(path, mPaint);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        // ��View�ĸ߶����ó�mBitmapһ��
        setMeasuredDimension(width, height);
    }

    // ����Ҫ��ʾ��ͼƬ
    public void setImageBitmap(Bitmap bitmap) {
        if (bitmap == null) {
            throw new IllegalArgumentException(
                    "bitmap not null please reset ! ");
        }
        this.width = bitmap.getWidth();
        this.height = bitmap.getHeight();
        this.mBitmap = bitmap;
        // ���¼����С
        requestLayout();
        // ���»���
        invalidate();
    }

    /** ��ȡ��Ļ�ֱ��ʿ����dialog�Ŀ�� */
    private int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }
}
