package monzou.honeyant.example.task;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

/**
 * DumpTask
 * 
 * @author monzou
 */
public class DumpTask extends Task {

    public static void main(String[] args) {

        File file = new File("foo");
        System.err.println(file.getAbsolutePath());

    }

    private String fqn;

    private String path;

    public void setFqn(String fqn) {
        this.fqn = fqn;
    }

    public void setPath(String path) {
        this.path = path;
    }

    /** {@inheritDoc} */
    @Override
    public void execute() throws BuildException {

        // Get a event source object
        Object source = getSource();

        // dump event source properties to txt
        try {
            dump(source);
        } catch (IOException e) {
            throw new BuildException(e);
        }

    }

    private void dump(Object source) throws IOException {

        File dir = new File("dest");
        if (!dir.exists()) {
            dir.mkdirs();
        }

        File file = new File(dir, "dump.txt");
        BufferedWriter writer = new BufferedWriter(new FileWriter(file));

        try {
            writer.write(String.format("path=%s\n", path));
            writer.write(String.format("class=%s\n", source.getClass().getName()));
            writer.write(String.format("package=%s\n", source.getClass().getPackage().getName()));

            Field[] fs = source.getClass().getDeclaredFields();
            if (fs != null && fs.length > 0) {
                for (Field f : fs) {
                    writer.write(String.format("\t%s (%s)\n", f.getName(), f.getType()));
                }
            }
        } finally {
            try {
                writer.flush();
            } finally {
                writer.close();
            }
        }

    }

    private Object getSource() {
        try {
            Class<?> clazz = Class.forName(fqn);
            return clazz.newInstance();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

}
