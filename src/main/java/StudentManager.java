import java.io.*;
import java.sql.*;
import java.util.*;

public class StudentManager {
    //java -jar target\lesson7-1.0-SNAPSHOT-jar-with-dependencies.jar updateDB C:\Users\Ksusha\IdeaProjects\lesson7\src\m
    //ain\studentDir
    private static Connection con = null;
    private static Statement st = null;

    public static final String url = "jdbc:mysql://localhost:3306/students?useUnicode=true&characterEncoding=utf8";
    public static final String user = "root";
    public static final String pwd = "";

    public void startConnection(){
        try {
            Class.forName("com.mysql.jdbc.Driver");
            con = DriverManager.getConnection(url, user, pwd);
            st = con.createStatement();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public String updateDB(String path){
        String result = "Изменений не было обнаружено.";
        boolean control = false;

        List<Student> students = new ArrayList<>();

        File dir = new File(path);
        File[] arrFiles = dir.listFiles();

        try {
            startConnection();
            if (arrFiles != null) {
                for (File file : arrFiles){
                    //Проверка на соответствие названия файла стандарту;
                    if (checkFileName(file.getName())){
                        students.addAll(getStudent(file));
                    }
                }
            }else{
                result = "Директория пуста.";
            }

            if (students.size() != 0) {
                for (Student stud : students) {
                    // Проверка наличия данной группы/студента в базе: true - не добавляю в базу,
                    // false - добавляю в базу;
                    boolean groupExistence = checkDB("group", "group_name", stud.getGroup());
                    boolean studentExistence = checkDB("student", "second_name", stud.getSurname());

                    if (!groupExistence) {
                        String queryGroup = "insert into `group` (group_name) values ('" + stud.getGroup() + "');";
                        st.executeUpdate(queryGroup);
                        control = true;
                    }

                    if (!studentExistence) {
                        control = true;
                        // Нахожу id группы студента;
                        ResultSet rs = st.executeQuery("select id from `group` where group_name = '" + stud.getGroup() + "';");

                        String groupID = null;

                        while (rs.next()) {
                            groupID = rs.getString(1);
                        }

                        // Добавляю студента в базу;
                        String queryStudent = "insert into student (group_id, first_name, second_name, last_name, birthday_date)" +
                                " values ('" + groupID + "', '" + stud.getName() + "', '" + stud.getSurname() + "', '" + stud.getMiddleName() +
                                "', '" + stud.getBday() + "');";

                        st.executeUpdate(queryStudent);
                        rs.close();
                    }
                }
            }else{
                result = "Группы пустые.";
            }

        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            try {
                st.close();
                con.close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
        if (control){
            result = "База данных успешно обновлена";
        }
        return result;
    }
    
    public List<Student> getStudent(File file){
        List<Student> targetStudents = new ArrayList<>();

        try(BufferedReader reader = new BufferedReader(new FileReader(file))){
            String line;
            while ((line = reader.readLine()) != null){
                Student targetStudent = checkStudent(line, file.getName());
                if (targetStudent != null)
                    targetStudents.add(targetStudent);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return targetStudents;
    }

    public Student checkStudent(String line, String fileName){
        Student targetStudent = null;
        String[] splitLine = line.split(",");

        if(splitLine.length == 4 && !splitLine[0].matches("[0-9]")
                                 && !splitLine[1].matches("[0-9]")
                                 && !splitLine[2].matches("[0-9]")
                                 && splitLine[3].matches("\\d{4}-\\d{2}-\\d{2}")){

            targetStudent = new Student(splitLine[1], splitLine[0], splitLine[2],
                            splitLine[3], fileName.split("\\.")[0]);
        }

        return targetStudent;
    }


    public boolean checkDB(String dbName, String field, String value) throws SQLException {
        String query = "SELECT EXISTS(SELECT id FROM `" + dbName +"` WHERE " + field +" = '"+ value + "');";
        ResultSet rs = st.executeQuery(query);

        while (rs.next()){
            if (rs.getString(1).equals("0")){
                rs.close();
                return false;
            }
        }
        return true;
    }


    public boolean checkFileName(String file){
        int digitsNumber = 5;
        String csv = "csv";

        String fileName = file.split("\\.")[0];
        String fileExtension = file.split("\\.")[1];

        if ((fileName.split("").length == digitsNumber && fileExtension.equals(csv))) {
            for (char s : fileName.toCharArray()) {
                if (!Character.isDigit(s)) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    public void printAll(){
        try {
            startConnection();
            HashSet<String> groups = new HashSet<>();

            String query1 = "select group_name from `group`";

            ResultSet rs = st.executeQuery(query1);
            while (rs.next()){
                groups.add(rs.getString(1));
            }

            for (String group : groups) {
                String query = "select * from (select group.group_name, student.first_name,  student.second_name,  student.last_name," +
                        " student.birthday_date from student inner join `group` on `group`.id = student.group_id) as new_table where group_name = '"+
                        group + "';";

                rs = st.executeQuery(query);

                System.out.println("Группа " + group);

                while (rs.next()) {
                    System.out.println(rs.getString(2) + " " + rs.getString(3) +
                            " " + rs.getString(4) + " " + rs.getString(5));
                }
                System.out.println();
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            try {
                st.close();
                con.close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
    }

}
