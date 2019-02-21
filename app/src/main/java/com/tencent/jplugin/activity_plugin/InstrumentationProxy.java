package com.tencent.jplugin.activity_plugin;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Instrumentation;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.os.IBinder;
import android.text.TextUtils;

import java.lang.reflect.Method;
import java.util.List;

public class InstrumentationProxy extends Instrumentation {
    private Instrumentation mInstrumentation;
    private PackageManager mPackageManager;

    public InstrumentationProxy(Instrumentation instrumentation, PackageManager packageManager) {
        mInstrumentation = instrumentation;
        mPackageManager = packageManager;
    }

    public ActivityResult execStartActivity(Context who, IBinder contextThread, IBinder token, Activity target,
                                            Intent intent, int requestCode, Bundle options) {
        //首先查找要启动的Activity是否已经在AndroidManifest.xml中注册了
        List<ResolveInfo> infos = mPackageManager.queryIntentActivities(intent, PackageManager.MATCH_ALL);
        //没有注册，将要启动的Activity(Target)的ClassName保存起来用于后面还原TargetActivity
        if (infos == null || infos.size() == 0) {
            intent.putExtra(HookHelper.TARGET_INTENT_NAME, intent.getComponent().getClassName());
            //将要启动的Activity指定为StubActivity
            intent.setClassName(who, "com.tencent.jplugin.activity_plugin.StubActivity");
        }
        try {
            @SuppressLint("PrivateApi")
            Method excMethod = Instrumentation.class.getDeclaredMethod("execStartActivity", Context.class, IBinder.class,
                    IBinder.class, Activity.class,
                    Intent.class, int.class, Bundle.class);
            //通过反射调用execStartActivity方法，这样就可以用StubActivity通过AMS验证。
            return (ActivityResult) excMethod.invoke(mInstrumentation, who, contextThread, token, target, intent, requestCode, options);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Activity newActivity(ClassLoader cl, String className, Intent intent) throws IllegalAccessException, ClassNotFoundException, InstantiationException {
        String intentName = intent.getStringExtra(HookHelper.TARGET_INTENT_NAME);
        if (!TextUtils.isEmpty(intentName)) {
            return super.newActivity(cl, intentName, intent);
        }
        return super.newActivity(cl, className, intent);
    }
}
