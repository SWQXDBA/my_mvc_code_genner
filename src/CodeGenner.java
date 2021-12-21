import com.sun.corba.se.spi.orb.StringPair;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

/**
 * 为基于JPA的SpringBoot MVC写的代码生成器
 * 生成实体类的四个文件 控制层 服务层 数据访问层 和 实体类定义
 * 用于快速搭建项目文件框架
 */
public class CodeGenner {
    static class Pojo {
        private final String name;
        private boolean controller = true;
        private boolean repository = true;
        private boolean service = true;
        private boolean pojo = true;

        //用于CrudRepository
        public String idClassName = "Long";
        //Entity类的属性
        public List<StringPair> fields = new ArrayList<>();


        public Pojo(String name) {
            this.name = name;
        }
    }


/*    public static void main(String[] args) {
        CodeGenner genner = new CodeGenner("C:\\Users\\SWQXDBA\\IdeaProjects\\my_mvc_code_genner\\src");
        genner.repositoryDirectory("Dao")

                .addPojo("Book").idClassName("Integer")
                .addField("Integer", "id")
                .addField("String", "name")
                .addField("Long", "price")
                .addField("Customer")
                .closeAll().setPojo(true).setRepository(true)

                .addPojo("Customer")
                .addField("Long", "id")
                .addField("String", "userName")
                .addField("String", "password")
                .setController(false).setService(false)


                .start();

    }*/

    Pojo target;
    List<Pojo> pojos = new ArrayList<>();

    String rootPath;
    String controllerDirectory = "Controller";
    String repositoryDirectory = "Repository";
    String serviceDirectory = "Service";
    String pojoDirectory = "Pojo";

    private boolean controllerOpen = true;
    private boolean repositoryOpen = true;
    private boolean serviceOpen = true;
    private boolean pojoOpen = true;

    private String packageName;

    private boolean recover = false;
    public CodeGenner(String rootPath) {
        Package aPackage = this.getClass().getPackage();
        if (aPackage == null) {
            packageName = "";
        } else {
            packageName = aPackage.getName();
        }
        this.rootPath = rootPath;
    }

/*    //不能用
    private static CodeGenner getCodeGenner() {
        return new CodeGenner(getRootPath());

    }

    private static String getRootPath() {
        File file = new File("");
        try {
            return file.getCanonicalPath();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }*/

    /**
     *
     * @param rootPath 根文件夹 所有生成的代码将在这个文件夹下 比如 C:\Users\SWQXDBA\IdeaProjects\code_gener_demo\src\main\java\com\example\code_gener_demo
     * @param controllerOpen 全局设置 是否给Pojo默认生成Controller
     * @param repositoryOpen 同上
     * @param serviceOpen 同上
     * @param pojoOpen 同上 是否给Pojo默认生成对应的实体类
     */
    public CodeGenner(String rootPath, boolean controllerOpen, boolean repositoryOpen, boolean serviceOpen, boolean pojoOpen) {
        this(rootPath);
        this.controllerOpen = controllerOpen;
        this.repositoryOpen = repositoryOpen;
        this.serviceOpen = serviceOpen;
        this.pojoOpen = pojoOpen;
    }

    /**
     * 在给JPA生成CrudRepository时 需要给出Id的类型名称 默认为Long
     * 比如
     * public interface CustomerRepository extends CrudRepository<Customer,Long>
     * @param idClassName
     * @return
     *
     */
    public CodeGenner idClassName(String idClassName) {
        target.idClassName = idClassName;
        return this;
    }

    /**
     * 设置rootPath所在的包名
     * 如果生成器在rootPath中启动 则无需设置
     * @param name
     * @return
     */
    public CodeGenner setPackageName(String name) {
        packageName = name;
        return this;
    }

    /**
     * 设置 controller文件夹的名字 默认为Controller
     *     * 这会影响生成的文件名 如 /Controller/UserController.java
     * @param name
     * @return
     */
    public CodeGenner controllerDirectory(String name) {
        controllerDirectory = name;
        return this;
    }
    /**
     * 设置 service文件夹的名字 默认为Service
     * 这会影响生成的文件名 如 /Repository/UserRepository.java 或者  /Dao/UserDao.java

     * @param name
     * @return
     */
    public CodeGenner repositoryDirectory(String name) {
        repositoryDirectory = name;
        return this;
    }
    /**
     * 设置 repository文件夹的名字 默认为Repository
     * @param name
     * @return
     */
    public CodeGenner serviceDirectory(String name) {
        serviceDirectory = name;
        return this;
    }
    /**
     * 用来给Pojo添加一个类的属性 如 addField("String","name");
     * 如果不给属性名 则以属性类名的小写作为属性名 如 User user
     * @param filedClassName 属性的类型名称 如String Integer
     * @return
     */
    public CodeGenner addField(String filedClassName) {

        return addField(filedClassName, null);
    }
    /**
     * 用来给Pojo添加一个类的属性 如 addField("String","name");
     * @param filedClassName
     * @return
     */
    public CodeGenner addField(String filedClassName, String filedName) {

        target.fields.add(new StringPair(filedClassName, filedName));
        return this;
    }

    /**
     * 对当前pojo起效果 生成其对应的所有文件
     * @return
     */
    public CodeGenner openAll() {
        target.service = true;
        target.pojo = true;
        target.repository = true;
        target.controller = true;
        return this;
    }
    /**
     * 对当前pojo起效果 不其对应的所有文件
     * @return
     */
    public CodeGenner closeAll() {
        target.service = false;
        target.pojo = false;
        target.repository = false;
        target.controller = false;
        return this;
    }



    /**
     * 添加一个Pojo 用来生成对应的文件
     * @param name
     * @return
     */
    public CodeGenner addPojo(String name) {
        Pojo pojo = new Pojo(name);

        pojos.add(pojo);
        target = pojo;
        target.service = serviceOpen;
        target.pojo = pojoOpen;
        target.repository = repositoryOpen;
        target.controller = controllerOpen;
        return this;
    }

    /**
     * 对Pojo使用 设置是否生成对应的Controller
     * @param flag
     * @return
     */
    public CodeGenner setController(boolean flag) {
        target.controller = flag;
        return this;
    }
    /**
     * 对Pojo使用 设置是否生成对应的Repository
     * @param flag
     * @return
     */
    public CodeGenner setRepository(boolean flag) {
        target.repository = flag;
        return this;
    }
    /**
     * 对Pojo使用 设置是否生成对应的Service
     * @param flag
     * @return
     */
    public CodeGenner setService(boolean flag) {
        target.service = flag;
        return this;
    }
    /**
     * 对Pojo使用 设置是否生成对应的Pojo
     * @param flag
     * @return
     */
    public CodeGenner setPojo(boolean flag) {
        target.pojo = flag;
        return this;
    }

    /**
     * 设置后 会覆盖文件 请谨慎使用
     * @return
     */
    public CodeGenner recover(){
        this.recover = true;
        return this;
    }
    /**
     * 启动生成器
     */
    public void start() {
        if(recover){
            System.out.println("WARN!!! 请注意 该次生成会覆盖掉原来的文件!!!");
            String pass = "Yes";
            System.out.println("输入 "+pass+" 继续");
            Scanner scanner = new Scanner(System.in);
            String next = scanner.next();
            if(!pass.equals(next)){
                return;
            }
            scanner.close();
        }
        File rootFile = new File(rootPath);
        if (!rootFile.exists()) {
            System.out.println("根目录不存在!!!");
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

                    Path path = Paths.get(con + "\\" + pojo.name + controllerDirectory+".java");
                    if (!Files.exists(path)||recover) {
                        if (Files.exists(path)&&recover) {
                          Files.delete(path);
                        }
                        Files.createFile(path);
                        FileWriter writer = new FileWriter(String.valueOf(path));
                        String packageStr;
                        if (!"".equals(packageName)) {
                            packageStr = "package " + packageName + "." + controllerDirectory + ";\n";
                        } else {
                            packageStr = "package " + controllerDirectory + ";\n";
                        }
                        writer.write(packageStr +
                                "\n" +
                                "public class " + pojo.name + controllerDirectory+"{\n" +
                                "    \n" +
                                "}");
                        writer.close();

                    }
                }

                if (pojo.repository) {
                    Path path = Paths.get(rep + "\\" + pojo.name + repositoryDirectory+".java");
                    if (!Files.exists(path)||recover) {
                        if (Files.exists(path)&&recover) {
                            Files.delete(path);
                        }
                        Files.createFile(path);
                        FileWriter writer = new FileWriter(String.valueOf(path));
                        String packageStr = "";
                        if (!"".equals(packageName)) {
                            packageStr = "package " + packageName + "." + repositoryDirectory + ";\n";
                        }
                        //导入对应的实体类包
                        String importStr = packageStr.replace("package", "import").replace(repositoryDirectory+";\n", pojoDirectory) + "." + pojo.name+";\n";
                        writer.write(packageStr +
                                "import org.springframework.data.repository.CrudRepository;\n" +
                                "import org.springframework.stereotype.Repository;\n" +
                                importStr +
                                "@Repository\n" +
                                "public interface " + pojo.name + repositoryDirectory+" extends CrudRepository<" + pojo.name + "," + pojo.idClassName + "> {\n" +
                                "    \n" +
                                "}");
                        writer.close();

                    }
                }

                if (pojo.service) {
                    Path path = Paths.get(ser + "\\" + pojo.name + serviceDirectory+".java");
                    if (!Files.exists(path)||recover) {
                        if (Files.exists(path)&&recover) {
                            Files.delete(path);
                        }
                        Files.createFile(path);
                        FileWriter writer = new FileWriter(String.valueOf(path));
                        String packageStr;
                        if (!"".equals(packageName)) {
                            packageStr = "package " + packageName + "." + serviceDirectory + ";\n";
                        } else {
                            packageStr = "package " + serviceDirectory + ";\n";
                        }
                        writer.write(packageStr +
                                "\n" +
                                "@Service\n" +
                                "public class " + pojo.name + serviceDirectory+"{\n" +
                                "    \n" +
                                "}");
                        writer.close();

                    }
                }

                if (pojo.pojo) {
                    Path path = Paths.get(poj + "\\" + pojo.name + ".java");
                    if (!Files.exists(path)||recover) {
                        if (Files.exists(path)&&recover) {
                            Files.delete(path);
                        }
                        Files.createFile(path);
                        FileWriter writer = new FileWriter(String.valueOf(path));
                        StringBuilder fieldsStrBuilder = new StringBuilder();
                        for (StringPair pair : pojo.fields) {
                            String field = pair.getFirst();
                            String val = pair.getSecond();
                            if (val == null) {
                                //取出属性的首字母 改成小写作为属性名
                                String firstWord = field.charAt(0) + "";
                                String s = firstWord.toLowerCase(Locale.ROOT);
                                val = field.replaceFirst(firstWord, s);
                            }

                            fieldsStrBuilder.append("    ").append(field).append(" ").append(val).append(";\n\n");
                        }

                        String packageStr;
                        if (!"".equals(packageName)) {
                            packageStr = "package " + packageName + "." + pojoDirectory + ";\n";
                        } else {
                            packageStr = "package " + pojoDirectory + ";\n";
                        }
                        writer.write(packageStr +
                                "\n" +
                                "import lombok.Data;\n" +
                                "import javax.persistence.*;\n" +
                                "@Data\n" +

                                "@Entity\n" +
                                "public class " + pojo.name + "{\n" +
                                "    \n" +
                                "    @Id\n" +
                                "    @GeneratedValue(strategy = GenerationType.IDENTITY)\n" +
                                fieldsStrBuilder.toString() +
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
