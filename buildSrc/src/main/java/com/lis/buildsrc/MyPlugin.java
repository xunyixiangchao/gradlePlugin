package com.lis.buildsrc;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.logging.Logger;

public class MyPlugin implements Plugin<Project> {
    @Override
    public void apply(Project project) {
        Logger logger = project.getLogger();
        logger.error("=====================");
        logger.error("简单的Gradle插件");
        logger.error("=====================");
    }
}
