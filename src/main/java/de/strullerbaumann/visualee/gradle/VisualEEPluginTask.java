package de.strullerbaumann.visualee.gradle;

import de.strullerbaumann.visualee.dependency.boundary.DependencyAnalyzer;
import de.strullerbaumann.visualee.filter.boundary.FilterConfigurator;
import de.strullerbaumann.visualee.filter.boundary.FilterContainer;
import de.strullerbaumann.visualee.filter.entity.Filter;
import de.strullerbaumann.visualee.resources.FileManager;
import de.strullerbaumann.visualee.source.boundary.JavaSourceContainer;
import de.strullerbaumann.visualee.ui.graph.boundary.GraphConfigurator;
import de.strullerbaumann.visualee.ui.graph.boundary.GraphCreator;
import de.strullerbaumann.visualee.ui.graph.control.HTMLManager;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;

import java.io.File;


/**
 * Created by pai on 19.03.18.
 */
public class VisualEEPluginTask extends DefaultTask {

    private static final String HEADER_FOOTER = "#######################################################";

    private static final String[] CSS_DIR_FILES = {
            "style.css",
            "jquery-ui.css"};
    private static final String CSS_DIR = "/css/";

    private static final String[] JS_DIR_FILES = {
            "d3.v3.min.js",
            "jquery-2.0.3.min.js",
            "jquery-ui-1.9.2.min.js",
            "classgraph.js",
            "LICENSE"};
    private static final String JS_DIR = "/js/";


    private Logger logger = LogManager.getLogger(this.getClass());

    @TaskAction
    public void action() {

        VisualEEPluginExtensions extensions = (VisualEEPluginExtensions) getProject()
                .getExtensions().findByName("visualEEPluginName");

        //Ensure only one execution (important for multi module poms)
        if (true) {
            logger.info(HEADER_FOOTER);
            logger.info("VisualEE-Plugin");
            checkCreateDirs(extensions.outputDir);
            for (String exportFile : CSS_DIR_FILES) {
                FileManager.export(CSS_DIR, exportFile, extensions.outputDir.getAbsoluteFile());
            }
            for (String exportFile : JS_DIR_FILES) {
                FileManager.export(JS_DIR, exportFile, extensions.outputDir.getAbsoluteFile());
            }
            //Examine all java-files in projectroot
            String sourceFolder = extensions.sourceDir.getAbsolutePath();
            if (sourceFolder != null) {
                // Set encoding
                if (extensions.encoding != null) {
                    JavaSourceContainer.setEncoding(extensions.encoding);
                }
                logger.info("Using encoding: " + JavaSourceContainer.getEncoding());

                // Set filters
                if (extensions.filters != null) {
                    FilterConfigurator.setFilterConfigs(extensions.filters);
                    logger.info("Active filters: ");
                    for (Filter filter : FilterContainer.getInstance().getFilters()) {
                        logger.info("   " + filter);
                    }
                } else {
                    logger.info("No filters configured.");
                }

                HTMLManager.generateIndexHTML(extensions.outputDir, "/html/index.html", sourceFolder);

                logger.info("Analyzing sourcefolder: " + sourceFolder);
                DependencyAnalyzer.getInstance().analyze(sourceFolder);
                logger.info("Generating graphs");

                // Set graphs
                if (extensions.graphs != null) {
                    GraphConfigurator.setGraphConfigs(extensions.graphs);
                }

                File sourceFolderDir = new File(sourceFolder);
                GraphCreator.generateGraphs(sourceFolderDir, extensions.outputDir, "/html/graphTemplate.html");
                logger.info("Done, visualization can be found in");
                logger.info(extensions.outputDir.toString() + File.separatorChar + "index.html");
                logger.info(HEADER_FOOTER);
            } else {
                logger.error("Can't find src-folder");
            }
        }


    }


    protected void checkCreateDirs(File dirPath) {
        if (!dirPath.exists()) {
            dirPath.mkdirs();
        }
    }

}
