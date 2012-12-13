package monzou.honeyant.support;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.resources.ICommand;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IProjectNature;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.preferences.ScopedPreferenceStore;

/**
 * tiny Eclipse Plugin utilities
 * 
 * @author monzou
 */
public final class PluginUtils {

    public static boolean isJavaFIle(IResource resource) {
        if (resource.getType() != IResource.FILE) {
            return false;
        }
        if (resource instanceof IFile) {
            IFile file = (IFile) resource;
            return "java".equalsIgnoreCase(file.getFileExtension());
        }
        return false;
    }

    public static IPreferenceStore getProjectPreference(IProject project) {
        return new ScopedPreferenceStore(new ProjectScope(project), Constants.PLUGIN_ID);
    }

    public static void addNature(IProject project, String natureId) throws CoreException {
        if (isAccessible(project)) {
            if (project.hasNature(natureId)) {
                return;
            }
            IProjectDescription description = project.getDescription();
            String[] natureIds = description.getNatureIds();
            int nNatures = natureIds.length;
            String[] newNatureIds = new String[nNatures + 1];
            for (int i = 0; i < nNatures; i++) {
                if (natureIds[i].equals(natureId)) {
                    return;
                }
                newNatureIds[i] = natureIds[i];
            }
            newNatureIds[nNatures] = natureId;
            description.setNatureIds(newNatureIds);
            project.setDescription(description, null);
        }
    }

    public static void removeNature(IProject project, String natureId) throws CoreException {
        if (isAccessible(project)) {
            IProjectDescription description = project.getDescription();
            String[] natureIds = description.getNatureIds();
            int nNatures = natureIds.length;
            for (int i = 0; i < nNatures; i++) {
                if (natureIds[i].equals(natureId)) {
                    String[] newNatureIds = new String[nNatures - 1];
                    System.arraycopy(natureIds, 0, newNatureIds, 0, i);
                    System.arraycopy(natureIds, i + 1, newNatureIds, i, nNatures - i - 1);
                    description.setNatureIds(newNatureIds);
                    project.setDescription(description, null);
                    return;
                }
            }
        }
    }

    public static IProjectNature getNature(IProject project, String natureId) throws CoreException {
        return isOpen(project) ? project.getNature(natureId) : null;
    }

    public static boolean hasNature(IProject project, String natureId) {
        try {
            return getNature(project, natureId) != null;
        } catch (CoreException e) {
            return false;
        }
    }

    public static void addBuilders(IProject project, String... builders) throws CoreException {
        if (Utils.isEmpty(builders)) {
            return;
        }
        IProjectDescription description = project.getDescription();
        List<ICommand> newCommands = getCommands(description, builders);
        for (int i = 0; i < builders.length; i++) {
            ICommand command = description.newCommand();
            command.setBuilderName(builders[i]);
            newCommands.add(command);
        }
        setCommands(description, newCommands);
        project.setDescription(description, null);
    }

    public static void removeBuilders(IProject project, String... builders) throws CoreException {
        if (Utils.isEmpty(builders)) {
            return;
        }
        IProjectDescription description = project.getDescription();
        List<ICommand> newCommands = getCommands(description, builders);
        setCommands(description, newCommands);
        project.setDescription(description, null);
    }

    private static List<ICommand> getCommands(IProjectDescription description, String... ignores) {
        ICommand[] commands = description.getBuildSpec();
        if (commands == null) {
            return null;
        }
        if (ignores == null || ignores.length == 0) {
            return toList(commands);
        }
        List<ICommand> newCommands = new ArrayList<ICommand>();
        for (int i = 0; i < commands.length; i++) {
            if (isIgnore(commands[i], ignores)) {
                continue;
            }
            newCommands.add(commands[i]);
        }
        return newCommands;
    }

    private static boolean isIgnore(ICommand command, String[] ignores) {
        for (String ignore : ignores) {
            if (command.getBuilderName().equals(ignore)) {
                return true;
            }
        }
        return false;
    }

    private static void setCommands(IProjectDescription description, List<ICommand> commands) {
        description.setBuildSpec(toArray(commands));
    }

    private static List<ICommand> toList(ICommand[] commands) {
        if (commands == null) {
            return null;
        }
        return Arrays.<ICommand> asList(commands);
    }

    private static ICommand[] toArray(List<ICommand> commands) {
        if (commands == null) {
            return null;
        }
        return commands.toArray(new ICommand[commands.size()]);
    }

    private static boolean isAccessible(IProject project) {
        return project != null && project.isAccessible();
    }

    private static boolean isOpen(IProject project) {
        return project != null && project.isOpen();
    }

    private PluginUtils() {
    }

}
