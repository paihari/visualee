![Gundi](https://paihari.github.io/repo/gundi-logo.png)

visualee
========

A gradle plugin to visualize a java ee project.

This is the extension of maven plugin project created by thomas-s-b. 
https://github.com/Thomas-S-B/visualee

Some notes to the implementation:
- The source code from VisualEE maven plugin is used
- The gradle plugin is published online at 
  https://paihari.github.io/repo/

## To Compile the plugin

- Run ./gradlew clean build uploadArchives
- Compiled plugin will be created in folder repo

## To use the plugin in a project

- Add the below code snippet to your build.gradle

```groovy
buildscript {
    repositories {
        maven {
            url 'https://paihari.github.io/repo/'
        }
        mavenCentral()
    }
    dependencies {
        classpath group: 'com.gundi', name: 'visualee.plugin', version: '1.0'
    }
}

apply plugin: 'com.gundi.visualee.plugin'

visualEEPluginName {

    sourceDir = file('./src/')
    outputDir = file('./out')
}
```
- The sourceDir should point to the Java source folder where the visualization is intended
- outputDir, folder where the visualiazion artifacts will be rendered
- If the classes are not shown, try python -m SimpleHTTPServer through terminal in out folder
- Access the visualization through http://localhost:8000

