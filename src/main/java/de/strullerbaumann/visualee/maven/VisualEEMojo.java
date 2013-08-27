package de.strullerbaumann.visualee.maven;

/*
 Copyright 2013 Thomas Struller-Baumann, struller-baumann.de

 Licensed under the Apache License, Version 2.0 (the "License")
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 */
import de.strullerbaumann.visualee.dependency.boundary.DependencyAnalyzer;
import de.strullerbaumann.visualee.ui.graph.boundary.GraphCreator;
import de.strullerbaumann.visualee.ui.graph.control.HTMLManager;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;

/**
 * Goal to visualize EE-Dependencies
 *
 * @goal visualize
 * @phase process-sources
 * @author Thomas Struller-Baumann <thomas at struller-baumann.de>
 */
public class VisualEEMojo extends AbstractMojo {

   /**
    * Location of the visual files.
    *
    * @parameter expression="${project.build.directory}"
    * @required
    */
   private File outputdirectory;
   /**
    * Base directory of the project.
    *
    * @parameter default-value="${basedir}"
    * @required
    * @readonly
    */
   private File basedir;
   /**
    * @parameter expression="${session}"
    * @required
    * @readonly
    */
   private MavenSession mavenSession;
   private static final String JS_DIR = "/js/";
   private static final String CSS_DIR = "/css/";
   private static final int BUFFER_SIZE = 4096;
   private static final String[] CSS_DIR_FILES = {"style.css", "jquery-ui.css"};
   private static final String[] JS_DIR_FILES = {"d3.v3.min.js", "jquery-2.0.3.min.js", "jquery-ui-1.9.2.min.js", "classgraph.js", "LICENSE"};

   @Override
   public void execute() throws MojoExecutionException {
      //Ensure only one execution (important for multi module poms)
      if (isThisRootDir()) {
         getLog().info("#######################################################");
         getLog().info("### VisualEE-Plugin");
         InputStream indexIS = getClass().getResourceAsStream("/html/index.html");
         InputStream graphTemplateIS = getClass().getResourceAsStream("/html/graphTemplate.html");
         for (String exportFile : CSS_DIR_FILES) {
            export(CSS_DIR, exportFile, outputdirectory.getAbsoluteFile());
         }
         for (String exportFile : JS_DIR_FILES) {
            export(JS_DIR, exportFile, outputdirectory.getAbsoluteFile());
         }
         //Examine all dirs under the projectroot for java-files
         String sourceFolder = mavenSession.getExecutionRootDirectory();
         if (sourceFolder != null) {
            HTMLManager.generateIndexHTML(outputdirectory, indexIS, sourceFolder);
            getLog().info("### Analyzing sourcefolder: " + sourceFolder);
            File sourceFolderDir = new File(sourceFolder);
            DependencyAnalyzer.getInstance().analyze(sourceFolderDir, outputdirectory, graphTemplateIS);
            getLog().info("### Generating graphs");
            GraphCreator.generateGraphs(sourceFolderDir, outputdirectory, graphTemplateIS);
            getLog().info("### Done, visualization can be found in");
            getLog().info("### " + outputdirectory + File.separatorChar + "index.html");
            getLog().info("#######################################################");
         } else {
            getLog().error("### Cannot find src-folder");
         }
      }
   }

   protected String getSourceFolder(List<String> roots) {
      for (String sourceFolder : roots) {
         if (sourceFolder.indexOf(File.separatorChar + "src" + File.separatorChar) > -1) {
            return sourceFolder + File.separatorChar;
         }
      }
      return null;
   }

   //Exports Files from jar to a given directory
   private void export(String sourceFolder, String fileName, File targetFolder) {
      try {
         if (!targetFolder.exists()) {
            targetFolder.mkdir();
         }
         File dstResourceFolder = new File(targetFolder + sourceFolder + File.separatorChar);
         if (!dstResourceFolder.exists()) {
            dstResourceFolder.mkdir();
         }
         try (InputStream is = getClass().getResourceAsStream(sourceFolder + fileName);
                 OutputStream os = new FileOutputStream(targetFolder + sourceFolder + fileName)) {
            byte[] buffer = new byte[BUFFER_SIZE];
            int length;
            while ((length = is.read(buffer)) > 0) {
               os.write(buffer, 0, length);
            }
         }
      } catch (Exception exc) {
         Logger.getLogger(VisualEEMojo.class.getName()).log(Level.INFO, null, exc);
      }
   }

   protected boolean isThisRootDir() {
      return mavenSession.getExecutionRootDirectory().equalsIgnoreCase(basedir.toString());
   }
}