package monzou.honeyant;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicReference;

import monzou.honeyant.preferences.model.HoneyAntPreferences;
import monzou.honeyant.support.Constants;
import monzou.honeyant.support.Logger;
import monzou.honeyant.support.LoggerFactory;
import monzou.honeyant.support.PluginUtils;
import monzou.honeyant.support.PreferenceModelFactory;
import monzou.honeyant.support.Utils;

import org.eclipse.ant.core.AntCorePlugin;
import org.eclipse.ant.core.AntCorePreferences;
import org.eclipse.ant.core.AntRunner;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.IAnnotation;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;

/**
 * HoneyAntRunner
 * 
 * @author monzou
 */
public class HoneyAntRunner extends IncrementalProjectBuilder {

    private static final Logger LOGGER = LoggerFactory.getLogger(HoneyAntRunner.class);

    private static final String KEY_GENERATED = "generated";

    private static final String KEY_DIGEST = "digest";

    private final AtomicReference<AntRunner> runnerReference = new AtomicReference<AntRunner>(); // is this need to be thread-safe ? I don't know ...

    /** {@inheritDoc} */
    @Override
    protected IProject[] build(int kind, Map<String, String> args, IProgressMonitor monitor) throws CoreException {

        if (kind == FULL_BUILD) {
            fullBuild(getPreferences(), monitor);
        } else {
            HoneyAntPreferences preferences = getPreferences();
            if (preferences.isIncrementalBuildEnabled()) {
                IResourceDelta delta = getDelta(getProject());
                if (delta != null && delta.getKind() == IResourceDelta.CHANGED) {
                    incrementalBuild(preferences, delta, monitor);
                }
            }
        }

        return null;

    }

    private void fullBuild(final HoneyAntPreferences preferences, IProgressMonitor monitor) throws CoreException {
        getProject().accept(new IResourceVisitor() {

            @Override
            public boolean visit(IResource resource) throws CoreException {
                if (resource.isAccessible() && PluginUtils.isJavaFIle(resource)) {
                    new ResourceBuilder(preferences).build((IFile) resource);
                }
                return true;
            }
        });
    }

    private void incrementalBuild(final HoneyAntPreferences preferences, IResourceDelta delta, IProgressMonitor monitor) throws CoreException {
        delta.accept(new IResourceDeltaVisitor() {

            @Override
            public boolean visit(IResourceDelta delta) throws CoreException {
                if (delta == null) {
                    return false;
                }
                IResource resource = delta.getResource();
                if (resource.isAccessible() && PluginUtils.isJavaFIle(resource)) {
                    new ResourceBuilder(preferences).build((IFile) resource);
                }
                return true;
            }
        });
    }

    private class ResourceBuilder {

        private final HoneyAntPreferences preferences;

        ResourceBuilder(HoneyAntPreferences preferences) {
            this.preferences = preferences;
        }

        boolean build(IFile resource) throws CoreException {
            ICompilationUnit cu = JavaCore.createCompilationUnitFrom(resource);
            String simpleClassName = cu.getElementName().replaceAll(".java", "");
            IType[] types = cu.getTypes();
            if (Utils.isEmpty(types)) {
                return false;
            }
            boolean builded = false;
            for (IType type : types) {
                builded |= build(resource, type, simpleClassName);
            }
            return builded;
        }

        private boolean build(IFile resource, IType type, String eventClassName) throws CoreException {
            String fqn = type.getFullyQualifiedName();
            if (fqn.endsWith(eventClassName)) {
                IAnnotation[] annotations = type.getAnnotations();
                try {
                    for (IAnnotation annotation : annotations) {
                        if (annotation.getElementName().equals(preferences.getBuildTriggerAnnotation())) {
                            if (preferences.isCacheEnabled() && !new FileChecker().isChanged(resource)) {
                                continue;
                            }
                            doBuild(resource, fqn);
                            return true;
                        }
                    }
                } catch (IOException e) {
                    LOGGER.error(e);
                    throw new CoreException(new Status(IStatus.ERROR, Constants.PLUGIN_ID, e.getMessage()));
                }
            }
            return false;
        }

        private void doBuild(IFile resource, String fqn) throws CoreException {
            IProject project = getProject();
            IResource buildFile = project.findMember(preferences.getBuildFileName());
            if (buildFile == null) {
                String message = String.format("build file does not exist: %s", preferences.getBuildFileName());
                Status status = new Status(IStatus.ERROR, Constants.PLUGIN_ID, message);
                throw new CoreException(status);
            }
            File file = buildFile.getLocation().toFile();
            try {
                AntRunner runner = getRunner();
                runner.setBuildFileLocation(file.getAbsolutePath());
                String path = resource.getLocation().toFile().getAbsolutePath();
                String args = String.format("-Dclazz=%s -Dpath=%s %s", fqn, path, preferences.getBuildTarget());
                runner.setArguments(args);
                runner.run();
            } catch (IOException e) {
                LOGGER.error(e);
                throw new CoreException(new Status(IStatus.ERROR, Constants.PLUGIN_ID, e.getMessage()));
            }
        }

        private class FileChecker {

            boolean isChanged(IFile eventSource) throws IOException {

                File cacheFile = getCacheFile(eventSource);
                int digest = calculateDigest(eventSource.getLocation().toFile());
                Properties properties = new Properties();
                boolean changed;

                if (cacheFile.exists()) {
                    BufferedInputStream in = new BufferedInputStream(new FileInputStream(cacheFile));
                    try {
                        properties.load(in);
                        int existence = Integer.parseInt(properties.getProperty(KEY_DIGEST));
                        changed = existence != digest;
                    } finally {
                        in.close();
                    }
                } else {
                    changed = true;
                    properties.setProperty(KEY_GENERATED, String.valueOf(System.currentTimeMillis()));
                }

                if (changed) {
                    BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(cacheFile));
                    try {
                        properties.setProperty(KEY_DIGEST, String.valueOf(digest));
                        properties.store(out, cacheFile.getName());
                    } finally {
                        out.close();
                    }
                }

                return changed;

            }

            private File getCacheFile(IFile eventSource) {

                IPath path = eventSource.getProject().getLocation().append(preferences.getCacheFileDir());
                File dir = path.toFile();
                if (!dir.exists()) {
                    dir.mkdirs();
                }

                File sourceFile = eventSource.getLocation().toFile();
                String fileName = String.format("%s.cache", sourceFile.getName());
                return new File(dir, fileName);

            }

            private int calculateDigest(File file) throws IOException {
                DigestInputStream in = null;
                try {
                    in = new DigestInputStream(new BufferedInputStream(new FileInputStream(file)), MessageDigest.getInstance("MD5"));
                    byte[] buffer = new byte[1024];
                    for (;;) {
                        if (in.read(buffer) <= 0) {
                            break;
                        }
                    }
                    return ByteBuffer.wrap(in.getMessageDigest().digest()).getInt();
                } catch (NoSuchAlgorithmException e) {
                    throw new RuntimeException(e);
                } finally {
                    if (in != null) {
                        in.close();
                    }
                }

            }

        }
    }

    private AntRunner getRunner() throws IOException {

        AntRunner runner = runnerReference.get();
        if (runner != null) {
            return runner;
        }

        runnerReference.compareAndSet(null, createRunner());
        return runnerReference.get();

    }

    private AntRunner createRunner() throws IOException {

        AntRunner runner = new AntRunner();
        //        runner.addBuildLogger("org.apache.tools.ant.DefaultLogger");

        runner.addBuildLogger(HoneyAntBuildLogger.class.getName());
        AntCorePreferences corePreferences = AntCorePlugin.getPlugin().getPreferences();
        URL[] urls = corePreferences.getURLs();
        File bundleFile = FileLocator.getBundleFile(HoneyAnt.getInstance().getBundle());
        URL url = bundleFile.toURI().toURL();
        List<URL> classpath = new ArrayList<URL>();
        classpath.addAll(Arrays.asList(urls));
        classpath.add(url);
        runner.setCustomClasspath(classpath.toArray(new URL[classpath.size()]));
        return runner;

    }

    private HoneyAntPreferences getPreferences() {
        return PreferenceModelFactory.create(HoneyAntPreferences.class, getProject());
    }

}
