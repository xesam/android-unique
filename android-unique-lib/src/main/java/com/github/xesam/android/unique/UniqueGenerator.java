package com.github.xesam.android.unique;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Created by xesamguo@gmail.com on 17-2-4.
 */

public interface UniqueGenerator {

    @NonNull
    String generateUniqueId();

    boolean verifyUniqueId(@Nullable String id);
}
