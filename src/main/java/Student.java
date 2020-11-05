public class Student {
    private String name;
    private String surname;
    private String middleName;
    private String bday;
    private String group;

    public Student(String name, String surname, String middleName, String bday, String group){
        this.name = name;
        this.bday = bday;
        this.group = group;
        this.middleName = middleName;
        this.surname = surname;

    }

    public String getSurname(){
        return surname;
    }

    public String getBday(){
        return bday;
    }

    public String getMiddleName(){
        return middleName;
    }

    public String getGroup(){
        return group;
    }

    public String getName(){
        return name;
    }

}
