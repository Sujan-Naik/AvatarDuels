package org.gradle.accessors.dm;

import org.gradle.api.NonNullApi;
import org.gradle.api.artifacts.MinimalExternalModuleDependency;
import org.gradle.plugin.use.PluginDependency;
import org.gradle.api.artifacts.ExternalModuleDependencyBundle;
import org.gradle.api.artifacts.MutableVersionConstraint;
import org.gradle.api.provider.Provider;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.provider.ProviderFactory;
import org.gradle.api.internal.catalog.AbstractExternalDependencyFactory;
import org.gradle.api.internal.catalog.DefaultVersionCatalog;
import java.util.Map;
import org.gradle.api.internal.attributes.ImmutableAttributesFactory;
import org.gradle.api.internal.artifacts.dsl.CapabilityNotationParser;
import javax.inject.Inject;

/**
 * A catalog of dependencies accessible via the {@code libs} extension.
 */
@NonNullApi
public class LibrariesForLibs extends AbstractExternalDependencyFactory {

    private final AbstractExternalDependencyFactory owner = this;
    private final CaLibraryAccessors laccForCaLibraryAccessors = new CaLibraryAccessors(owner);
    private final ComLibraryAccessors laccForComLibraryAccessors = new ComLibraryAccessors(owner);
    private final OrgLibraryAccessors laccForOrgLibraryAccessors = new OrgLibraryAccessors(owner);
    private final VersionAccessors vaccForVersionAccessors = new VersionAccessors(providers, config);
    private final BundleAccessors baccForBundleAccessors = new BundleAccessors(objects, providers, config, attributesFactory, capabilityNotationParser);
    private final PluginAccessors paccForPluginAccessors = new PluginAccessors(providers, config);

    @Inject
    public LibrariesForLibs(DefaultVersionCatalog config, ProviderFactory providers, ObjectFactory objects, ImmutableAttributesFactory attributesFactory, CapabilityNotationParser capabilityNotationParser) {
        super(config, providers, objects, attributesFactory, capabilityNotationParser);
    }

    /**
     * Group of libraries at <b>ca</b>
     */
    public CaLibraryAccessors getCa() {
        return laccForCaLibraryAccessors;
    }

    /**
     * Group of libraries at <b>com</b>
     */
    public ComLibraryAccessors getCom() {
        return laccForComLibraryAccessors;
    }

    /**
     * Group of libraries at <b>org</b>
     */
    public OrgLibraryAccessors getOrg() {
        return laccForOrgLibraryAccessors;
    }

    /**
     * Group of versions at <b>versions</b>
     */
    public VersionAccessors getVersions() {
        return vaccForVersionAccessors;
    }

    /**
     * Group of bundles at <b>bundles</b>
     */
    public BundleAccessors getBundles() {
        return baccForBundleAccessors;
    }

    /**
     * Group of plugins at <b>plugins</b>
     */
    public PluginAccessors getPlugins() {
        return paccForPluginAccessors;
    }

    public static class CaLibraryAccessors extends SubDependencyFactory {
        private final CaBkawLibraryAccessors laccForCaBkawLibraryAccessors = new CaBkawLibraryAccessors(owner);

        public CaLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Group of libraries at <b>ca.bkaw</b>
         */
        public CaBkawLibraryAccessors getBkaw() {
            return laccForCaBkawLibraryAccessors;
        }

    }

    public static class CaBkawLibraryAccessors extends SubDependencyFactory {
        private final CaBkawPaperLibraryAccessors laccForCaBkawPaperLibraryAccessors = new CaBkawPaperLibraryAccessors(owner);

        public CaBkawLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Group of libraries at <b>ca.bkaw.paper</b>
         */
        public CaBkawPaperLibraryAccessors getPaper() {
            return laccForCaBkawPaperLibraryAccessors;
        }

    }

    public static class CaBkawPaperLibraryAccessors extends SubDependencyFactory {
        private final CaBkawPaperNmsLibraryAccessors laccForCaBkawPaperNmsLibraryAccessors = new CaBkawPaperNmsLibraryAccessors(owner);

        public CaBkawPaperLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Group of libraries at <b>ca.bkaw.paper.nms</b>
         */
        public CaBkawPaperNmsLibraryAccessors getNms() {
            return laccForCaBkawPaperNmsLibraryAccessors;
        }

    }

    public static class CaBkawPaperNmsLibraryAccessors extends SubDependencyFactory {
        private final CaBkawPaperNmsMavenLibraryAccessors laccForCaBkawPaperNmsMavenLibraryAccessors = new CaBkawPaperNmsMavenLibraryAccessors(owner);

        public CaBkawPaperNmsLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Group of libraries at <b>ca.bkaw.paper.nms.maven</b>
         */
        public CaBkawPaperNmsMavenLibraryAccessors getMaven() {
            return laccForCaBkawPaperNmsMavenLibraryAccessors;
        }

    }

    public static class CaBkawPaperNmsMavenLibraryAccessors extends SubDependencyFactory {

        public CaBkawPaperNmsMavenLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Dependency provider for <b>plugin</b> with <b>ca.bkaw:paper-nms-maven-plugin</b> coordinates and
         * with version reference <b>ca.bkaw.paper.nms.maven.plugin</b>
         * <p>
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> getPlugin() {
            return create("ca.bkaw.paper.nms.maven.plugin");
        }

    }

    public static class ComLibraryAccessors extends SubDependencyFactory {
        private final ComGithubLibraryAccessors laccForComGithubLibraryAccessors = new ComGithubLibraryAccessors(owner);
        private final ComProjectkorraLibraryAccessors laccForComProjectkorraLibraryAccessors = new ComProjectkorraLibraryAccessors(owner);

        public ComLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Group of libraries at <b>com.github</b>
         */
        public ComGithubLibraryAccessors getGithub() {
            return laccForComGithubLibraryAccessors;
        }

        /**
         * Group of libraries at <b>com.projectkorra</b>
         */
        public ComProjectkorraLibraryAccessors getProjectkorra() {
            return laccForComProjectkorraLibraryAccessors;
        }

    }

    public static class ComGithubLibraryAccessors extends SubDependencyFactory {
        private final ComGithubJavafakerLibraryAccessors laccForComGithubJavafakerLibraryAccessors = new ComGithubJavafakerLibraryAccessors(owner);

        public ComGithubLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Group of libraries at <b>com.github.javafaker</b>
         */
        public ComGithubJavafakerLibraryAccessors getJavafaker() {
            return laccForComGithubJavafakerLibraryAccessors;
        }

    }

    public static class ComGithubJavafakerLibraryAccessors extends SubDependencyFactory {

        public ComGithubJavafakerLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Dependency provider for <b>javafaker</b> with <b>com.github.javafaker:javafaker</b> coordinates and
         * with version reference <b>com.github.javafaker.javafaker</b>
         * <p>
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> getJavafaker() {
            return create("com.github.javafaker.javafaker");
        }

    }

    public static class ComProjectkorraLibraryAccessors extends SubDependencyFactory {

        public ComProjectkorraLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Dependency provider for <b>projectkorra</b> with <b>com.projectkorra:ProjectKorra</b> coordinates and
         * with version reference <b>com.projectkorra.projectkorra</b>
         * <p>
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> getProjectkorra() {
            return create("com.projectkorra.projectkorra");
        }

    }

    public static class OrgLibraryAccessors extends SubDependencyFactory {
        private final OrgJavatuplesLibraryAccessors laccForOrgJavatuplesLibraryAccessors = new OrgJavatuplesLibraryAccessors(owner);
        private final OrgSpigotmcLibraryAccessors laccForOrgSpigotmcLibraryAccessors = new OrgSpigotmcLibraryAccessors(owner);

        public OrgLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Group of libraries at <b>org.javatuples</b>
         */
        public OrgJavatuplesLibraryAccessors getJavatuples() {
            return laccForOrgJavatuplesLibraryAccessors;
        }

        /**
         * Group of libraries at <b>org.spigotmc</b>
         */
        public OrgSpigotmcLibraryAccessors getSpigotmc() {
            return laccForOrgSpigotmcLibraryAccessors;
        }

    }

    public static class OrgJavatuplesLibraryAccessors extends SubDependencyFactory {

        public OrgJavatuplesLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Dependency provider for <b>javatuples</b> with <b>org.javatuples:javatuples</b> coordinates and
         * with version reference <b>org.javatuples.javatuples</b>
         * <p>
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> getJavatuples() {
            return create("org.javatuples.javatuples");
        }

    }

    public static class OrgSpigotmcLibraryAccessors extends SubDependencyFactory {
        private final OrgSpigotmcSpigotLibraryAccessors laccForOrgSpigotmcSpigotLibraryAccessors = new OrgSpigotmcSpigotLibraryAccessors(owner);

        public OrgSpigotmcLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Group of libraries at <b>org.spigotmc.spigot</b>
         */
        public OrgSpigotmcSpigotLibraryAccessors getSpigot() {
            return laccForOrgSpigotmcSpigotLibraryAccessors;
        }

    }

    public static class OrgSpigotmcSpigotLibraryAccessors extends SubDependencyFactory {

        public OrgSpigotmcSpigotLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Dependency provider for <b>api</b> with <b>org.spigotmc:spigot-api</b> coordinates and
         * with version reference <b>org.spigotmc.spigot.api</b>
         * <p>
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> getApi() {
            return create("org.spigotmc.spigot.api");
        }

    }

    public static class VersionAccessors extends VersionFactory  {

        private final CaVersionAccessors vaccForCaVersionAccessors = new CaVersionAccessors(providers, config);
        private final ComVersionAccessors vaccForComVersionAccessors = new ComVersionAccessors(providers, config);
        private final OrgVersionAccessors vaccForOrgVersionAccessors = new OrgVersionAccessors(providers, config);
        public VersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

        /**
         * Group of versions at <b>versions.ca</b>
         */
        public CaVersionAccessors getCa() {
            return vaccForCaVersionAccessors;
        }

        /**
         * Group of versions at <b>versions.com</b>
         */
        public ComVersionAccessors getCom() {
            return vaccForComVersionAccessors;
        }

        /**
         * Group of versions at <b>versions.org</b>
         */
        public OrgVersionAccessors getOrg() {
            return vaccForOrgVersionAccessors;
        }

    }

    public static class CaVersionAccessors extends VersionFactory  {

        private final CaBkawVersionAccessors vaccForCaBkawVersionAccessors = new CaBkawVersionAccessors(providers, config);
        public CaVersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

        /**
         * Group of versions at <b>versions.ca.bkaw</b>
         */
        public CaBkawVersionAccessors getBkaw() {
            return vaccForCaBkawVersionAccessors;
        }

    }

    public static class CaBkawVersionAccessors extends VersionFactory  {

        private final CaBkawPaperVersionAccessors vaccForCaBkawPaperVersionAccessors = new CaBkawPaperVersionAccessors(providers, config);
        public CaBkawVersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

        /**
         * Group of versions at <b>versions.ca.bkaw.paper</b>
         */
        public CaBkawPaperVersionAccessors getPaper() {
            return vaccForCaBkawPaperVersionAccessors;
        }

    }

    public static class CaBkawPaperVersionAccessors extends VersionFactory  {

        private final CaBkawPaperNmsVersionAccessors vaccForCaBkawPaperNmsVersionAccessors = new CaBkawPaperNmsVersionAccessors(providers, config);
        public CaBkawPaperVersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

        /**
         * Group of versions at <b>versions.ca.bkaw.paper.nms</b>
         */
        public CaBkawPaperNmsVersionAccessors getNms() {
            return vaccForCaBkawPaperNmsVersionAccessors;
        }

    }

    public static class CaBkawPaperNmsVersionAccessors extends VersionFactory  {

        private final CaBkawPaperNmsMavenVersionAccessors vaccForCaBkawPaperNmsMavenVersionAccessors = new CaBkawPaperNmsMavenVersionAccessors(providers, config);
        public CaBkawPaperNmsVersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

        /**
         * Group of versions at <b>versions.ca.bkaw.paper.nms.maven</b>
         */
        public CaBkawPaperNmsMavenVersionAccessors getMaven() {
            return vaccForCaBkawPaperNmsMavenVersionAccessors;
        }

    }

    public static class CaBkawPaperNmsMavenVersionAccessors extends VersionFactory  {

        public CaBkawPaperNmsMavenVersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

        /**
         * Version alias <b>ca.bkaw.paper.nms.maven.plugin</b> with value <b>1.4.3</b>
         * <p>
         * If the version is a rich version and cannot be represented as a
         * single version string, an empty string is returned.
         * <p>
         * This version was declared in catalog libs.versions.toml
         */
        public Provider<String> getPlugin() { return getVersion("ca.bkaw.paper.nms.maven.plugin"); }

    }

    public static class ComVersionAccessors extends VersionFactory  {

        private final ComGithubVersionAccessors vaccForComGithubVersionAccessors = new ComGithubVersionAccessors(providers, config);
        private final ComProjectkorraVersionAccessors vaccForComProjectkorraVersionAccessors = new ComProjectkorraVersionAccessors(providers, config);
        public ComVersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

        /**
         * Group of versions at <b>versions.com.github</b>
         */
        public ComGithubVersionAccessors getGithub() {
            return vaccForComGithubVersionAccessors;
        }

        /**
         * Group of versions at <b>versions.com.projectkorra</b>
         */
        public ComProjectkorraVersionAccessors getProjectkorra() {
            return vaccForComProjectkorraVersionAccessors;
        }

    }

    public static class ComGithubVersionAccessors extends VersionFactory  {

        private final ComGithubJavafakerVersionAccessors vaccForComGithubJavafakerVersionAccessors = new ComGithubJavafakerVersionAccessors(providers, config);
        public ComGithubVersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

        /**
         * Group of versions at <b>versions.com.github.javafaker</b>
         */
        public ComGithubJavafakerVersionAccessors getJavafaker() {
            return vaccForComGithubJavafakerVersionAccessors;
        }

    }

    public static class ComGithubJavafakerVersionAccessors extends VersionFactory  {

        public ComGithubJavafakerVersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

        /**
         * Version alias <b>com.github.javafaker.javafaker</b> with value <b>1.0.2</b>
         * <p>
         * If the version is a rich version and cannot be represented as a
         * single version string, an empty string is returned.
         * <p>
         * This version was declared in catalog libs.versions.toml
         */
        public Provider<String> getJavafaker() { return getVersion("com.github.javafaker.javafaker"); }

    }

    public static class ComProjectkorraVersionAccessors extends VersionFactory  {

        public ComProjectkorraVersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

        /**
         * Version alias <b>com.projectkorra.projectkorra</b> with value <b>1.11.3</b>
         * <p>
         * If the version is a rich version and cannot be represented as a
         * single version string, an empty string is returned.
         * <p>
         * This version was declared in catalog libs.versions.toml
         */
        public Provider<String> getProjectkorra() { return getVersion("com.projectkorra.projectkorra"); }

    }

    public static class OrgVersionAccessors extends VersionFactory  {

        private final OrgJavatuplesVersionAccessors vaccForOrgJavatuplesVersionAccessors = new OrgJavatuplesVersionAccessors(providers, config);
        private final OrgSpigotmcVersionAccessors vaccForOrgSpigotmcVersionAccessors = new OrgSpigotmcVersionAccessors(providers, config);
        public OrgVersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

        /**
         * Group of versions at <b>versions.org.javatuples</b>
         */
        public OrgJavatuplesVersionAccessors getJavatuples() {
            return vaccForOrgJavatuplesVersionAccessors;
        }

        /**
         * Group of versions at <b>versions.org.spigotmc</b>
         */
        public OrgSpigotmcVersionAccessors getSpigotmc() {
            return vaccForOrgSpigotmcVersionAccessors;
        }

    }

    public static class OrgJavatuplesVersionAccessors extends VersionFactory  {

        public OrgJavatuplesVersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

        /**
         * Version alias <b>org.javatuples.javatuples</b> with value <b>1.2</b>
         * <p>
         * If the version is a rich version and cannot be represented as a
         * single version string, an empty string is returned.
         * <p>
         * This version was declared in catalog libs.versions.toml
         */
        public Provider<String> getJavatuples() { return getVersion("org.javatuples.javatuples"); }

    }

    public static class OrgSpigotmcVersionAccessors extends VersionFactory  {

        private final OrgSpigotmcSpigotVersionAccessors vaccForOrgSpigotmcSpigotVersionAccessors = new OrgSpigotmcSpigotVersionAccessors(providers, config);
        public OrgSpigotmcVersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

        /**
         * Group of versions at <b>versions.org.spigotmc.spigot</b>
         */
        public OrgSpigotmcSpigotVersionAccessors getSpigot() {
            return vaccForOrgSpigotmcSpigotVersionAccessors;
        }

    }

    public static class OrgSpigotmcSpigotVersionAccessors extends VersionFactory  {

        public OrgSpigotmcSpigotVersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

        /**
         * Version alias <b>org.spigotmc.spigot.api</b> with value <b>1.21.1-R0.1-SNAPSHOT</b>
         * <p>
         * If the version is a rich version and cannot be represented as a
         * single version string, an empty string is returned.
         * <p>
         * This version was declared in catalog libs.versions.toml
         */
        public Provider<String> getApi() { return getVersion("org.spigotmc.spigot.api"); }

    }

    public static class BundleAccessors extends BundleFactory {

        public BundleAccessors(ObjectFactory objects, ProviderFactory providers, DefaultVersionCatalog config, ImmutableAttributesFactory attributesFactory, CapabilityNotationParser capabilityNotationParser) { super(objects, providers, config, attributesFactory, capabilityNotationParser); }

    }

    public static class PluginAccessors extends PluginFactory {

        public PluginAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

    }

}
