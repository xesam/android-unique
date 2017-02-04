package com.github.xesam.android.unique;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import java.util.UUID;

/**
 * Created by xesamguo@gmail.com on 17-2-4.
 */

public class DefaultUniqueGenerator implements UniqueGenerator {
    @NonNull
    @Override
    public String generateUniqueId() {
        return UUID.randomUUID().toString();
    }

    @Override
    public boolean verifyUniqueId(String id) {
        return !TextUtils.isEmpty(id);
    }
}
