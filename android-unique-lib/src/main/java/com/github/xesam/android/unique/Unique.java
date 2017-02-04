package com.github.xesam.android.unique;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;

/**
 * Created by xesamguo@gmail.com on 17-2-4.
 */

public class Unique {
    private UniqueAccess mUniqueAccess;

    public Unique(Context context) {
        mUniqueAccess = new DefaultUniqueAccess(context);
    }

    @NonNull
    public String getUniqueId() {
        String unique = mUniqueAccess.getUniqueId();
        if (TextUtils.isEmpty(unique)) {
            unique = mUniqueAccess.generateUniqueId();
            if (TextUtils.isEmpty(unique)) {
                throw new RuntimeException("UniqueAccess#generateUniqueId() could not be null!");
            }
            mUniqueAccess.saveUniqueId(unique);
        }
        return unique;
    }
}
