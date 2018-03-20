package de.strullerbaumann.visualee.gradle;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by pai on 19.03.18.
 */
public class VisualEEPlugin implements Plugin<Project> {

    @Override
    public void apply(Project project) {
        Map<String,Object> taskInfo = new HashMap<String,Object>();
        taskInfo.put("type", VisualEEPluginTask.class);
        taskInfo.put("description", "Generates Visuall EE Java Plugin Task.");
        taskInfo.put("group", "com.gundi.gradle.java.plugin");
        Task visualEEPluginTask = project.task(taskInfo, "visualEEPluginTask");


        VisualEEPluginExtensions extensions = new VisualEEPluginExtensions(project);


        project.getExtensions().add("visualEEPluginName", extensions);

    }
}
