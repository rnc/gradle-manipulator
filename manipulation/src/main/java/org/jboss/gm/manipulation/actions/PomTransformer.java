package org.jboss.gm.manipulation.actions;

import static org.jboss.gm.common.ProjectVersionFactory.withGAV;

import org.commonjava.maven.atlas.ident.ref.ProjectVersionRef;
import org.gradle.api.Action;
import org.gradle.api.XmlProvider;
import org.jboss.gm.common.alignment.AlignmentModel;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Overrides data in pom generated by the old 'maven' plugin.
 */
public class PomTransformer implements Action<XmlProvider> {

    private static final String VERSION = "version";
    private static final String DEPENDENCIES = "dependencies";
    private static final String DEPENDENCY = "dependency";
    private static final String GROUPID = "groupId";
    private static final String ARTIFACTID = "artifactId";

    private final AlignmentModel.Module alignmentConfiguration;

    public PomTransformer(AlignmentModel.Module alignmentConfiguration) {
        this.alignmentConfiguration = alignmentConfiguration;
    }

    @Override
    public void execute(XmlProvider xmlProvider) {
        transformDependencies(xmlProvider);
    }

    private void transformDependencies(XmlProvider xmlProvider) {
        // find <dependencies> child
        Node dependenciesNode = null;
        NodeList childNodes = xmlProvider.asElement().getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node child = childNodes.item(i);
            if (child.getNodeType() == Node.ELEMENT_NODE
                    && DEPENDENCIES.equals(child.getNodeName())) {
                dependenciesNode = child;
                break;
            }
        }

        if (dependenciesNode == null) {
            return;
        }

        // go through dependencies
        NodeList dependencyNodes = ((Element) dependenciesNode).getElementsByTagName(DEPENDENCY);
        for (int i = 0; i < dependencyNodes.getLength(); i++) {
            Node dependencyNode = dependencyNodes.item(i);

            // collect GAV
            String group = null;
            String name = null;
            String version = null;
            Node versionNode = null;
            for (int j = 0; j < dependencyNode.getChildNodes().getLength(); j++) {
                Node child = dependencyNode.getChildNodes().item(j);
                if (child.getNodeType() == Node.ELEMENT_NODE) {
                    switch (child.getNodeName()) {
                        case GROUPID:
                            group = child.getTextContent();
                            break;
                        case ARTIFACTID:
                            name = child.getTextContent();
                            break;
                        case VERSION:
                            version = child.getTextContent();
                            versionNode = child;
                            break;
                    }
                }
            }
            if (group == null || name == null || version == null) {
                continue;
            }

            // modify version
            final ProjectVersionRef initial = withGAV(group, name, version);
            final ProjectVersionRef aligned = alignmentConfiguration.getAlignedDependencies().get(initial.toString());
            if (aligned != null) {
                versionNode.setTextContent(aligned.getVersionString());
            }
        }
    }
}
