import java.sql.SQLException;

public class Main {
    public static void main(String[] args){
        String message = "Вы неверное ввели данные.\n"+
                "Введите их в следующем виде: update DB path"+
                "Если же вы хотите вывести всех участников по группам, введите: printAll.";

        StudentManager sm = new StudentManager();
        try {
            if (!args[0].equals("updateDB")) {
                if(!args[0].equals("printAll")){
                    System.out.println(message);
                }else{
                    sm.printAll();
                }
            }else{
                System.out.println(sm.updateDB(args[1]));
            }
        }catch (ArrayIndexOutOfBoundsException e){
            System.out.println(message);
        }
    }
}