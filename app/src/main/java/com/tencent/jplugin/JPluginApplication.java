package com.tencent.jplugin;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.tencent.jplugin.activity_plugin.HookHelper;

/**
 * author: Jeremy
 * date: 2018/8/5
 * desc:
 */
public class JPluginApplication extends Application {
    /**
     * 由于ContentProvider在onCreate之前被调用，为支持在插件中使用该组件，我们需要提前到构造方法来对之进行懒加载。
     如果不需要支持该组件，你也可以放到 onCreate 方法中。
     这个方法在应用正常启动时只做一些简单的hook，不会影响性能；但在应用异常启动(后台被杀)时会同步加载插件以保证程序正常运行。
     */
    public JPluginApplication() {
//        Small.preSetUp(this);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        try {
//            HookHelper.hookAMS();
            HookHelper.hookInstrumentation(base);
        } catch (Exception e) {
            Log.e("PLUGIN_DEMO",e.toString());
        }

    }
}
