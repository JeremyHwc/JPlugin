package com.tencent.jplugin.activity_plugin;

import android.app.Instrumentation;
import android.content.Context;
import android.os.Build;

import java.lang.reflect.Field;
import java.lang.reflect.Proxy;

public class HookHelper {
    public static final String TARGET_INTENT = "target_intent";
    public static final String TARGET_INTENT_NAME = "target_intent_name";

    public static void hookAMS() throws Exception {
        Object defaultSingleton;
        if (Build.VERSION.SDK_INT >= 26) {
            Class<?> activityManagerClazz = Class.forName("android.app.ActivityManager");
            //获取activity中的IActivityManagerSingleton字段
            defaultSingleton = FieldUtil.getField(activityManagerClazz, null, "IActivityManagerSingleton");
        } else {
            Class<?> activityManagerNativeClazz = Class.forName("android.app.ActivityManagerNative");
            //获取ActivityManagerNative中的gDefault字段
            defaultSingleton = FieldUtil.getField(activityManagerNativeClazz, null, "gDefault");
        }
        Class<?> singletonClazz = Class.forName("android.util.Singleton");
        Field mInstanceField = FieldUtil.getField(singletonClazz, "mInstance");
        //获取iactivityManager
        Object iActivityManager = mInstanceField.get(defaultSingleton);
        Class<?> iActivityManagerClazz = Class.forName("android.app.IActivityManager");
        Object proxy = Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), new Class[]{iActivityManagerClazz}, new IActivityManagerProxy(iActivityManager));
        mInstanceField.set(defaultSingleton, proxy);

    }

    public static void hookInstrumentation(Context context) throws Exception {
        //获取ContextImpl类的ActivityThread类型的mMainThread
        Class<?> contextImplClass = Class.forName("android.app.ContextImpl");
        Field mMainThreadField = FieldUtil.getField(contextImplClass, "mMainThread");
        //获取当前上下文环境的ActivityThread对象
        Object activityThread = mMainThreadField.get(context);

        Class<?> activityThreadClass = Class.forName("android.app.ActivityThread");
        //获取ActivityThread中的mInstrumentation字段
        Field mInstrumentationField = FieldUtil.getField(activityThreadClass, "mInstrumentation");
        //最后用InstrumentationProxy来替换mInstrumentation
        FieldUtil.setField(activityThreadClass, activityThread, "mInstrumentation",
                new InstrumentationProxy((Instrumentation) mInstrumentationField.get(activityThread), context.getPackageManager()));

    }
}
