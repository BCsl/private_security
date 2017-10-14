package com.tools.security.common;

import android.text.Editable;
import android.text.TextWatcher;

/**
 * Created by lzx on 2016/12/30.
 * TextWatcher 适配器模式
 */

public abstract class TextChangedListenerAdapter implements TextWatcher {
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {

    }
}
