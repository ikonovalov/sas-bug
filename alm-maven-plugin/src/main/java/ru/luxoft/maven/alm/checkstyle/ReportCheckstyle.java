package ru.luxoft.maven.alm.checkstyle;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;

/**
 * Generate test-set runs by checkstyle result.
 */
@Mojo(name="report-checkstyle")
public class ReportCheckstyle extends AbstractMojo {

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        // todo: parse checkstyle-result.xml
    }
}
