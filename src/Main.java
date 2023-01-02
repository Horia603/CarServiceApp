public class Main {
    public static void main(String[] args) {

        LoginForm loginForm = new LoginForm(null);
        User user = loginForm.user;

        if (user != null) {
            System.out.println("          Email: " + user.email);
            System.out.println("          Password: " + user.password);
            System.out.println("          Name: " + user.name);
            System.out.println("          Surname: " + user.surname);
            System.out.println("          Age: " + user.age);
            System.out.println("          Phone number: " + user.phone_number);
        }
        else {
            System.out.println("Authentication canceled");
        }
    }

}