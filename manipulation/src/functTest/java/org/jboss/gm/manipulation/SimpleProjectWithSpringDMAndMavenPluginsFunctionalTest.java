package org.jboss.gm.manipulation;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import org.apache.maven.model.Model;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.gradle.internal.Pair;
import org.gradle.testkit.runner.BuildResult;
import org.gradle.testkit.runner.GradleRunner;
import org.gradle.testkit.runner.TaskOutcome;
import org.jboss.gm.common.io.ManipulationIO;
import org.jboss.gm.common.model.ManipulationModel;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.SystemOutRule;
import org.junit.rules.TemporaryFolder;

public class SimpleProjectWithSpringDMAndMavenPluginsFunctionalTest {

    @Rule
    public final SystemOutRule systemOutRule = new SystemOutRule().enableLog().muteForSuccessfulTests();

    @Rule
    public TemporaryFolder tempDir = new TemporaryFolder();

    @Test
    public void ensureProperPomGenerated() throws IOException, URISyntaxException, XmlPullParserException {
        // this makes gradle use the set property as maven local directory
        // we do this in order to avoid polluting the maven local and also be absolutely sure
        // that no prior invocations affect the execution
        final File m2Directory = tempDir.newFolder(".m2");
        System.setProperty("maven.repo.local", m2Directory.getAbsolutePath());

        final File simpleProjectRoot = tempDir.newFolder("simple-project-with-spring-dm-and-maven-plugins");
        TestUtils.copyDirectory("simple-project-with-spring-dm-and-maven-plugins", simpleProjectRoot);
        assertThat(simpleProjectRoot.toPath().resolve("build.gradle")).exists();

        final ManipulationModel alignment = ManipulationIO.readManipulationModel(simpleProjectRoot);

        final BuildResult buildResult = GradleRunner.create()
                .withProjectDir(simpleProjectRoot)
                .withArguments("install")
                //.withDebug(true)
                .forwardOutput()
                .withPluginClasspath()
                .build();
        assertThat(buildResult.task(":install").getOutcome()).isEqualTo(TaskOutcome.SUCCESS);

        final Pair<Model, ManipulationModel> modelAndModule = TestUtils.getModelAndCheckGAV(m2Directory, alignment,
                "org/acme/root/1.0.1-redhat-00001/root-1.0.1-redhat-00001.pom", true);
        final ManipulationModel module = modelAndModule.getRight();
        assertThat(modelAndModule.getLeft().getDependencies())
                .extracting("artifactId", "version")
                .containsOnly(
                        TestUtils.getAlignedTuple(module, "commons-lang3", "3.8.1"),
                        TestUtils.getAlignedTuple(module, "hibernate-core"),
                        TestUtils.getAlignedTuple(module, "undertow-core"),
                        TestUtils.getAlignedTuple(module, "junit", "4.12"));
    }
}
