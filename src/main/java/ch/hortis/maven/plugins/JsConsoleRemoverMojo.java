package ch.hortis.maven.plugins;

/*
 * Copyright 2001-2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.codehaus.plexus.util.Scanner;
import org.sonatype.plexus.build.incremental.BuildContext;

import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Goal which removes console output from JavaScript files.
 *
 * @goal remove
 * 
 * @phase package
 */
public class JsConsoleRemoverMojo extends AbstractMojo {

    /**
     * @component
     */
    private BuildContext buildContext;

    /**
     * The source directory containing the JavaScript sources
     *
     * @parameter expression="${js-console-remover.sourceDirectory}" default-value="${project.basedir}/src/main/javascript"
     * @required
     */
    private File sourceDirectory;

    /**
     * The directory where JavaScript files will be saved.
     *
     * @parameter expression="${js-console-remover.outputDirectory}" default-value="${project.build.directory}/${project.artifactId}-${project.version}"
     * @required
     */
    private File outputDirectory;

    /**
     * List of the files to include. Specified as fileset patterns which are relative to the source directory.
     * Default value is : {"**\/*.js"}
     *
     * @parameter
     */
    private String[] includes = new String[] {"**/*.js"};

    /**
     * List of files to exclude. Specified as fileset patterns which are relative to the source directory.
     *
     * @parameter
     */
    private String[] excludes = new String[] {};


    private String pattern = "(window\\.)?console\\.(log|warn|error|info)\\(.*\\);";
    private Pattern p = Pattern.compile(pattern);

    /**
     * Execute the Mojo
     *
     * @throws MojoExecutionException
     */
    public void execute() throws MojoExecutionException {

        getLog().info("");
        getLog().info("****************************************");
        getLog().info("JS CONSOLE REMOVER");
        getLog().info("****************************************");
        getLog().info("");

        String[] files = getIncludedFiles();
        if (files == null || files.length < 1) {
            getLog().info("Nothing to process");
        }
        else {
            for (String file : files) {
                getLog().info("* " + file);
                removeConsole(file);
            }
        }
    }

    /**
     * Scans for the JavaScript files which will have their console outputs removed.
     *
     * @return The list of JavaScript files which will be processed to remove the console outputs.
     */
    private String[] getIncludedFiles() {
        Scanner scanner = buildContext.newScanner(sourceDirectory, true);
        scanner.setIncludes(includes);
        scanner.setExcludes(excludes);
        scanner.scan();
        return scanner.getIncludedFiles();
    }

    /**
     * Remove all console occurences from the file and store it in the outputDirectory
     *
     * @param file the file name containing the path
     */
    private void removeConsole(String file) {
        try {
            String content = Files.toString(new File(sourceDirectory, file), Charsets.UTF_8);
            int numberOfOccurences = countOccurrenceOfPattern(content);
            if (numberOfOccurences > 0) {
                getLog().info("** contains " + numberOfOccurences + " occurences of console");
            }
            Files.write(content.replaceAll(pattern, "").getBytes(), new File(outputDirectory, file));
        } catch (IOException e) {
            getLog().error(e);
        }
    }

    /**
     * Count all the console occurences in a content
     *
     * @param content
     * @return the number of console occurences found
     */
    private int countOccurrenceOfPattern(String content) {
        int counter = 0;
        Matcher m = p.matcher(content);
        while (m.find()) {
            counter++;
        }
        return counter;
    }
}
