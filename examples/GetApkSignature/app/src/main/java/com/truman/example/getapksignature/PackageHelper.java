package com.truman.example.getapksignature;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.content.pm.SigningInfo;
import android.os.Build;

public class PackageHelper {

    private Context mContext;

    public PackageHelper(Context context) {
        mContext = context;
    }

    public Signature[] getApkSignatures() {
        return getSignatures(mContext.getPackageName());
    }

    public Signature[] getSignatures(String packageName) {
        Signature[] signatures = null;
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                PackageInfo packageInfo = mContext.getPackageManager().getPackageInfo(packageName,
                        PackageManager.GET_SIGNING_CERTIFICATES);
                if (packageInfo != null) {
                    SigningInfo signingInfo = packageInfo.signingInfo;
                    signatures = signingInfo.hasMultipleSigners() ?
                            signingInfo.getApkContentsSigners() :
                            signingInfo.getSigningCertificateHistory();
                }
            } else {
                PackageInfo packageInfo = mContext.getPackageManager().getPackageInfo(packageName,
                        PackageManager.GET_SIGNATURES);
                if (packageInfo != null) {
                    signatures = packageInfo.signatures;
                }
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            // Catch unexpected runtime exception. Just in case!
            e.printStackTrace();
        }
        return signatures;
    }
}
