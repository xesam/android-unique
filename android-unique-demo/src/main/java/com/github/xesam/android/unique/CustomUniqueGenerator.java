package com.github.xesam.android.unique;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import java.util.Date;

/**
 * Created by xesamguo@gmail.com on 17-2-4.
 */

public class CustomUniqueGenerator implements UniqueGenerator {
    @NonNull
    @Override
    public String generateUniqueId() {
        return new Date().getTime() + "";
    }

    @Override
    public boolean verifyUniqueId(@Nullable String id) {
        if (TextUtils.isEmpty(id)) {
            return false;
        }
        try {
            return Long.parseLong(id) > 1486192600334L;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
