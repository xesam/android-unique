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

/**
 * Created by xesamguo@gmail.com on 17-2-4.
 */

public class Unique {

    private static final String PREF_FILE = "android.unique";
    private static final String SDCARD_FILE = "android.unique";
    private static final String UNIQUE_KEY = "android.unique_key";

    private final SharedPreferences mPref;
    private final String mSdFileName;
    private UniqueGenerator mUniqueGenerator;

    private String mCachedId;

    public Unique(Context context) {
        this(context.getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE), SDCARD_FILE);
    }

    public Unique(SharedPreferences pref, String externalFileName) {
        this(pref, externalFileName, new DefaultUniqueGenerator());
    }

    public Unique(Context context, UniqueGenerator uniqueGenerator) {
        this(context.getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE), SDCARD_FILE, uniqueGenerator);
    }

    public Unique(SharedPreferences pref, String externalFileName, UniqueGenerator uniqueGenerator) {
        this.mPref = pref;
        this.mSdFileName = externalFileName;
        this.mUniqueGenerator = uniqueGenerator;
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
    public String getUniqueId() {
        if (!TextUtils.isEmpty(mCachedId)) {
            return mCachedId;
        }
        String innerUnique = getInnerBackup();
        String externalUnique = getExternalBackup();
        if (mUniqueGenerator.verifyUniqueId(innerUnique)) {
            backupToExternal(innerUnique);
            mCachedId = innerUnique;
        } else {
            if (mUniqueGenerator.verifyUniqueId(externalUnique)) {
                backupToInner(externalUnique);
                mCachedId = externalUnique;
            }
        }
        if (!TextUtils.isEmpty(mCachedId)) {
            return mCachedId;
        }
        String newId = mUniqueGenerator.generateUniqueId();
        if (TextUtils.isEmpty(newId)) {
            throw new RuntimeException("UniqueAccess#generateUniqueId() could not be null!");
        }
        boolean ret = doubleBackup(newId);
        if (!ret) {
            throw new RuntimeException("saving uuid -> error!");
        }
        return newId;
    }

}
