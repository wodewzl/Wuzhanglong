
package com.bs.bsims.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.EditText;

public class BSTextView extends EditText {
    public BSTextView(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
    }

    public BSTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
    }

    public BSTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        // TODO Auto-generated constructor stub
    }

    @Override
    protected boolean getDefaultEditable() {// 等同于在布局文件中设置 android:editable="false"
        return false;
    }
}
