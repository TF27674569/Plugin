# Plugin
gradle插件

#### 功能
配置activity在debug时正常编译，在release时注释activity

#### 使用
在根目录的build.gradle 添加
```java
buildscript {
   ...
    repositories {
    ...

        maven {
            // 插件仓库
            url "https://dl.bintray.com/tf27674569/maven"
        }

    }
    dependencies {
        classpath 'com.android.tools.build:gradle:3.6.2'
        // 插件
        classpath 'org.tianfeng.plugin:plugin:1.0.0'
    }
}

...
```

在model(一般是app) build.gradle 添加

```java
apply plugin: 'org.tianfeng.plugin' // 插件

android {
    ...

    buildTypes {
        release {
            // 插件对应的task
            manifest{
                // 需要被过滤掉的activity
                className="com.tianfeng.plugin.MainActivity"
            }
            ...
        }
    }

}



```
