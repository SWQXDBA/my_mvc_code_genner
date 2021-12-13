import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class CodeGenner {
    static class Pojo {
        public String name;
        public boolean controller = true;
        public boolean repository = true;
        public boolean service = true;
        public boolean pojo = true;

        public Pojo(String name) {
            this.name = name;
        }
    }

    public static void main(String[] args) {
        CodeGenner genner = new CodeGenner("C:\\Users\\SWQXDBA\\IdeaProjects\\my_mvc_code_genner\\src");
        genner
                .addPojo("Customer")

                .addPojo("Book")
                    .setController(false)

                .addPojo("RentInfo")
                    .setRepository(false)
                    .setService(false)
                    .setPojo(false)
                .start();

    }

    Pojo target;
    List<Pojo> pojos = new ArrayList<>();

    public String rootPath;
    public String controllerDirectory = "Controller";
    public String repositoryDirectory = "Repository";
    public String serviceDirectory = "Service";
    public String pojoDirectory = "Pojo";

    public CodeGenner(String rootPath) {
        this.rootPath = rootPath;
    }

    public CodeGenner controllerDirectory(String name) {
        controllerDirectory = name;
        return this;
    }

    public CodeGenner repositoryDirectory(String name) {
        repositoryDirectory = name;
        return this;
    }


    public CodeGenner serviceDirectory(String name) {
        serviceDirectory = name;
        return this;
    }

    public CodeGenner addPojo(String name) {
        Pojo pojo = new Pojo(name);

        pojos.add(pojo);
        target = pojo;
        return this;
    }

    public CodeGenner setController(boolean flag) {
        target.controller = flag;
        return this;
    }

    public CodeGenner setRepository(boolean flag) {
        target.repository = flag;
        return this;
    }

    public CodeGenner setService(boolean flag) {
        target.service = flag;
        return this;
    }
    public CodeGenner setPojo(boolean flag) {
        target.pojo = flag;
        return this;
    }
    public void start() {
        File rootFile = new File(rootPath);
        if (!rootFile.exists()) {
            System.out.println("根目录错误!!!");
            return;
        }
        if (!rootFile.isDirectory()) {
            System.out.println("根路径必须是一个目录!!!");
            return;
        }
        String con = rootPath + "\\" + controllerDirectory;
        String rep = rootPath + "\\" + repositoryDirectory;
        String ser = rootPath + "\\" + serviceDirectory;
        String poj = rootPath + "\\" + pojoDirectory;
        try {
            if (!Files.exists(Paths.get(con))) {

                Files.createDirectory(Paths.get(con));

            }
            if (!Files.exists(Paths.get(rep))) {

                Files.createDirectory(Paths.get(rep));

            }
            if (!Files.exists(Paths.get(ser))) {

                Files.createDirectory(Paths.get(ser));

            }
            if (!Files.exists(Paths.get(poj))) {
                Files.createDirectory(Paths.get(poj));

            }

            for (Pojo pojo : pojos) {
                if (pojo.controller) {
                    Path path = Paths.get(con + "\\" + pojo.name + "Controller.java");
                    if (!Files.exists(path)) {
                        Files.createFile(path);
                        FileWriter writer = new FileWriter(String.valueOf(path));
                        writer.write("package " + controllerDirectory + ";\n" +
                                "\n" +
                                "public class " + pojo.name + "Controller{\n" +
                                "    \n" +
                                "}");
                        writer.close();

                    }
                }

                if (pojo.repository) {
                    Path path = Paths.get(rep + "\\" + pojo.name + "Repository.java");
                    if (!Files.exists(path)) {
                        Files.createFile(path);
                        FileWriter writer = new FileWriter(String.valueOf(path));
                        writer.write("package " + repositoryDirectory + ";\n" +
                                "\n" +
                                "@Repository\n" +
                                "public interface " + pojo.name + "Repository{\n" +
                                "    \n" +
                                "}");
                        writer.close();

                    }
                }

                if (pojo.service) {
                    Path path = Paths.get(ser + "\\" + pojo.name + "Service.java");
                    if (!Files.exists(path)) {
                        Files.createFile(path);
                        FileWriter writer = new FileWriter(String.valueOf(path));
                        writer.write("package " + serviceDirectory + ";\n" +
                                "\n" +
                                "@Service\n" +
                                "public class " + pojo.name + "Service{\n" +
                                "    \n" +
                                "}");
                        writer.close();

                    }
                }

                if (pojo.pojo) {
                    Path path = Paths.get(poj + "\\" + pojo.name + ".java");
                    if (!Files.exists(path)) {
                        Files.createFile(path);
                        FileWriter writer = new FileWriter(String.valueOf(path));
                        writer.write("package " + pojoDirectory + ";\n" +
                                "\n" +
                                "@Entity\n" +
                                "public class " + pojo.name + "{\n" +
                                "    \n" +
                                "}");
                        writer.close();

                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
