package com.github.xesam.android.unique;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Created by xesamguo@gmail.com on 17-2-4.
 */

public interface UniqueAccess {

    @NonNull
    String generateUniqueId();

    @Nullable
    String getUniqueId();

    boolean saveUniqueId(String id);

    boolean verifyUniqueId(String id);
}
