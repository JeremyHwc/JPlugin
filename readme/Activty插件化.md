# Activity插件化

## Activity启动流程
    在讲到Activity插件化的实现方式前，我们有必要熟悉一下Activity的启动流程，以便我们能找到合理的Hook
    点。以下是根Activity的启动流程：
    (1)Launcher进程向AMS请求创建根Activity;
    (2)AMS判断Activity所需要的应用程序进程是否存在并启动，如果不存在就会请求Zygote进程创建应用程序进程；
    (3)应用程序进程启动后，AMS会请求应用程序进程创建并启动根Activity。
    
## 实现方式
1. Hook IActivityManager
    
2. Hook Instrumentation
    原理：就是在Instrumenteaion的execStartActivity方法中用占坑StubActivity来通过AMS的验证，
    在Instrumentation的newActivity方法中还原TargetActivity,这两步操作都和Instrumentation有关，
    因此我们可以用自定义的Instrumentation来替换掉mInstrumentation。