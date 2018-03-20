package de.strullerbaumann.visualee.gradle;

import de.strullerbaumann.visualee.filter.entity.FilterConfig;
import de.strullerbaumann.visualee.ui.graph.entity.GraphConfig;
import org.gradle.api.Project;

import java.io.File;
import java.util.List;

/**
 * Created by pai on 19.03.18.
 */
public class VisualEEPluginExtensions {

    File sourceDir;
    File outputDir;
    String encoding;
    List<FilterConfig> filters;
    List<GraphConfig> graphs;


    public VisualEEPluginExtensions(Project project) {
        sourceDir = new File(project.getProjectDir(), "src");
        outputDir = new File(project.getProjectDir(), "out");
        encoding = "UTF-8";
        filters = null;
        graphs = null;
    }
}
