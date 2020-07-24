# AndroidStudio开发Gradle插件

## 项目结构：

gradlePlugin

- app
- - src
- - build.gradle
- buildSrc
- - src
- - - main
- - - - java
- - - - - com.lis.buildsrc
- - - - - - MyPlugin.java
- - build.gradle
- build.gradle

## 简单的Gradle插件BuildSrc

如果开发的插件仅用于当前项目，不需要发布的话，只需要注意两点:

1. 插件的Module名称必须是buildSrc(开头一定要小写)

2. 无须resources目录

   

   build.gradle的配置：

   ```java
   apply plugin:'java'
   apply plugin:'groovy'
   repositories {
       google()
       jcenter()
   }
   
   dependencies {
       implementation gradleApi() //gradle sdk
     	implementation localGroovy() //groovy sdk
       implementation 'com.android.tools.build:gradle:3.6.3'
   }
   
   sourceCompatibility = "1.7"
   targetCompatibility = "1.7"
   ```

这里引入groovy sdk和gradle sdk,因为开发Android插件，还需要Android专用的gradle（这里需要使用到google仓库）

然后我们编写插件，MyPlugin:

```java
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.logging.Logger;

public class MyPlugin implements Plugin<Project> {
    @Override
    public void apply(Project project) {
        Logger logger = project.getLogger();
        logger.error("=====================");
        logger.error("最简单的Gradle插件");
        logger.error("=====================");
    }
}
```

在app的build.gradle中添加你的插件  注意：这里不加'  '单引号

```java
apply plugin: com.lis.buildsrc.MyPlugin
```

执行：**Build-make Module 'app'**生成补丁。

![image-20200724100113259](D:\typora_data\gradle\img\build.png)

在底部的 **Buid-Build Output**中便可以看到打印日志：

=====================
简单的Gradle插件

=====================

![image-20200724100524283](D:\typora_data\gradle\img\buildoutput.png)

## 插件的发布

如果想复用你的gradle插件，就需要把它发布出去。

- 发布到本地仓库
- 发布到远程仓库

### 本地仓库

项目结构：

gradlePlugin

- app
- - src
- - build.gradle
- buildSrc
- - src
- - - main
- - - - java
- - - - - com.lis.buildsrc
- - - - - - MyPlugin.java
- - - - resources
- - - - - META-INF.gradle-plugins
- - - - - - com.lis.myplugin.properties
- - build.gradle
- build.gradle

在buildSrc的main文件夹下添加**resources**文件夹，在该文件夹下添加***META-INF***，***META-INF***文件夹下添加***gradle-plugins***

在***gradle-plugins***中添加com.lis.myplugin.properties  

这里命名为 `com.lis.myplugin.properties` ，一定要注意后缀名称，那么使用插件时的名称就是`com.lis.myplugin`，文件里面的内容填写如下：

```groovy
implementation-class=com.lis.buildsrc.MyPlugin
```

这里指定的路径为MyPlugin的类名，即插件的入口类

buidSrc中的build.gradle，添加maven插件及发布用到的配置

```groovy
apply plugin:'java'
apply plugin:'groovy'
apply plugin: 'maven'
repositories {
    google()
    jcenter()
}
tasks.withType(JavaCompile) { options.encoding = "UTF-8" } //编码格式

dependencies {
    implementation gradleApi() //gradle sdk
    implementation localGroovy() //groovy sdk
    implementation 'com.android.tools.build:gradle:3.6.3'
}
uploadArchives {
    repositories.mavenDeployer {
        repository(url: uri('../repo')) //仓库的路径，此处是项目根目录下的 repo 的文件夹
        pom.groupId = 'com.lis.gradleplugin'  //groupId ，自行定义，一般是包名
        pom.artifactId = 'myplugin' //artifactId ，自行定义
        pom.version = '1.0.0' //version 版本号
    }
}
sourceCompatibility = "1.7"
targetCompatibility = "1.7"
```

同步后在gradle模块内，会出现发布按钮

![image-20200724141451792](D:\typora_data\gradle\img\upload.png)

双击**uploadArchives** ,插件就发布到了本地的maven仓库，这里我们是在项目的根目录里，所以会在GradlePlugin下生成repo文件夹及文件

![image-20200724141759298](D:\typora_data\gradle\img\repo.png)



**使用插件:**

在GradlePlugin根目录的build.gradle中添加本地仓库及插件引用

```groovy
buildscript {

    repositories {
        google()
        jcenter()
        //首先需要配置本地的 maven 仓库地址，这里填写的是相对路径，也可以是全路径
        maven {
            url uri('./repo')
        }

    }
    dependencies {
        classpath 'com.android.tools.build:gradle:3.6.3'
        //然后，添加依赖的插件，形式是 groupId：artifactId：version
        //这些都是插件发布时，定义的名称
        classpath 'com.lis.gradleplugin:myplugin:1.0.0'
    }
}

allprojects {
    repositories {
        google()
        jcenter()
        maven {
            url uri('./repo')
        }

    }
}

task clean(type: Delete) {
    delete rootProject.buildDir
}
```

最后，在app的build.gradle里，添加插件

```groovy
apply plugin: 'com.android.application'
apply plugin: 'com.lis.myplugin'//这里就填写 .properties 文件的名称

android {
    compileSdkVersion 29
    buildToolsVersion "29.0.3"

    defaultConfig {
        applicationId "com.lis.gradleplugin"
        minSdkVersion 21
        targetSdkVersion 29
        versionCode 1
        versionName "1.0"

        testInstrumentationRunner "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            minifyEnabled false
            proguardFiles getDefaultProguardFile('proguard-android-optimize.txt'), 'proguard-rules.pro'
        }
    }

}
dependencies {
    implementation fileTree(dir: 'libs', include: ['*.jar'])
    implementation 'androidx.appcompat:appcompat:1.1.0'
    implementation 'androidx.constraintlayout:constraintlayout:1.1.3'
    testImplementation 'junit:junit:4.12'
    androidTestImplementation 'androidx.test.ext:junit:1.1.1'
    androidTestImplementation 'androidx.test.espresso:espresso-core:3.2.0'
}
```

`apply plugin: 'com.lis.myplugin'`这里的com.lis.myplugin即我们上面`com.lis.myplugin.properties`文件的名称

这就完成了本地仓库的插件使用！

