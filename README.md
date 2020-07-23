# AndroidStudio开发Gradle插件

## 最简单的Gradle插件

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





