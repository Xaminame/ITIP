import java.io.*;

public class FileCopy {
    public static void main(String[] args) {
        try (FileInputStream in = new FileInputStream("input.txt");
             FileOutputStream out = new FileOutputStream("output.txt")) {
            int byteData;
            while ((byteData = in.read()) != -1) {
                out.write(byteData);
            }
            System.out.println("Файл успешно скопирован");
        } catch (FileNotFoundException e) {
            System.out.println("Ошибка: файл не найден");
        } catch (IOException e) {
            System.out.println("Ошибка ввода-вывода: " + e.getMessage());
        }
    }
}
