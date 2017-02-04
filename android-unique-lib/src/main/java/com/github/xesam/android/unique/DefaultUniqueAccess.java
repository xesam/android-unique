package com.github.xesam.android.unique;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.UUID;

/**
 * Created by xesamguo@gmail.com on 17-2-4.
 */

public class DefaultUniqueAccess implements UniqueAccess {
    private static final String PREF_FILE = "android.unique";
    private static final String SDCARD_FILE = "android.unique";
    private static final String UNIQUE_KEY = "android.unique_key";

    private final SharedPreferences mPref;
    private final String mSdFileName;

    public DefaultUniqueAccess(Context context) {
        mPref = context.getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE);
        mSdFileName = SDCARD_FILE;
    }

    public DefaultUniqueAccess(SharedPreferences pref, String mSdFileName) {
        this.mPref = pref;
        this.mSdFileName = mSdFileName;
    }

    @Nullable
    private File getExternalFile() {
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            File dir;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                dir = new File(Environment.getExternalStorageDirectory(), Environment.DIRECTORY_DOCUMENTS);
            } else {
                dir = new File(Environment.getExternalStorageDirectory(), "Documents");
            }
            if (!dir.exists()) {
                if (!dir.mkdirs()) {
                    return null;
                }
            }

            File file = new File(dir, mSdFileName);
            if (!file.exists()) {
                try {
                    if (!file.createNewFile()) {
                        return null;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
            }
            return file;
        } else {
            return null;
        }
    }

    @Nullable
    private String getInnerBackup() {
        return mPref.getString(UNIQUE_KEY, null);
    }

    @Nullable
    private String getExternalBackup() {
        File file = getExternalFile();
        if (file == null) {
            return null;
        } else {
            BufferedReader br = null;
            try {
                br = new BufferedReader(new FileReader(file));
                return br.readLine();
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            } finally {
                if (br != null) {
                    try {
                        br.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private boolean backupToExternal(String innerUnique) {
        File file = getExternalFile();
        if (file == null) {
            return false;
        }
        if (!file.exists()) {
            try {
                boolean result = file.createNewFile();
                if (!result) {
                    return false;
                }
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
        BufferedWriter bw = null;
        try {
            bw = new BufferedWriter(new FileWriter(file));
            bw.write(innerUnique);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            if (bw != null) {
                try {
                    bw.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return true;
    }

    private boolean backupToInner(String externalUnique) {
        mPref.edit().putString(UNIQUE_KEY, externalUnique).apply();
        return true;
    }

    private boolean doubleBackup(String id) {
        boolean priority = backupToInner(id);
        backupToExternal(id);
        return priority;
    }

    @NonNull
    @Override
    public String generateUniqueId() {
        return UUID.randomUUID().toString();
    }

    @Nullable
    @Override
    public String getUniqueId() {
        String innerUnique = getInnerBackup();
        String externalUnique = getExternalBackup();
        if (verifyUniqueId(innerUnique)) {
            backupToExternal(innerUnique);
            return innerUnique;
        } else {
            if (verifyUniqueId(externalUnique)) {
                backupToInner(externalUnique);
                return externalUnique;
            }
        }
        return null;
    }

    @Override
    public boolean saveUniqueId(String id) {
        return doubleBackup(id);
    }

    @Override
    public boolean verifyUniqueId(@Nullable String id) {
        return !TextUtils.isEmpty(id);
    }
}
