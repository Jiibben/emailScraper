import java.io.IOException;

public class main {
    public static void main(String[] args) {
        EmailFinder emailFinder = new EmailFinder(30);
        emailFinder.run("https://www.pinge.ch/");
        emailFinder.saveData("test");
    }
}
