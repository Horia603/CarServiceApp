import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.Collections;

public class RegisterForm extends JDialog{
    private JPanel RegisterPanel;
    private JLabel EmailLabel;
    private JLabel TitleLabel;
    private JLabel PasswordLabel;
    private JLabel NameLabel;
    private JLabel SurnameLabel;
    private JLabel PhoneNumberLabel;
    private JLabel AgeLabel;
    private JTextField NameField;
    private JTextField SurnameField;
    private JTextField AgeField;
    private JTextField PhoneNumberField;
    private JTextField EmailField;
    private JTextField PasswordField;
    private JButton RegisterButton;
    private JButton LoginButton;

    public RegisterForm(JFrame parent){
        super(parent);
        setTitle("Register");
        setContentPane(RegisterPanel);
        setMinimumSize(new Dimension(700, 800));
        setModal(true);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        ImageIcon icon = new ImageIcon("./src/car.png");
        setIconImages(Collections.singletonList(icon.getImage()));
        LoginButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        RegisterButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        LoginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                LoginForm loginForm = new LoginForm(null);
            }
        });
        RegisterButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                boolean valid=true;
                User user=new User();
                user.name=NameField.getText();
                if(user.name.isEmpty()==true)
                    valid=false;
                user.surname=SurnameField.getText();
                if(user.surname.isEmpty()==true)
                    valid=false;
                user.birthday =AgeField.getText();
                if(user.birthday.isEmpty()==true)
                    valid=false;
                user.phone_number=PhoneNumberField.getText();
                if(user.phone_number.isEmpty()==true)
                    valid=false;
                user.email=EmailField.getText();
                if(user.email.isEmpty()==true)
                    valid=false;
                user.password=PasswordField.getText();
                if(user.password.isEmpty()==true)
                    valid=false;

                if(valid==true) {
                    addUserToDataBase(user);
                }
                else{
                    JOptionPane.showMessageDialog(RegisterForm.this,
                            "All field must not be empty",
                            "Try again",
                            JOptionPane.ERROR_MESSAGE);
                }
            }

            private void addUserToDataBase(User user) {
                final String DB_URL = "jdbc:mysql://127.0.0.1:3306/sys";
                final String USERNAME = "root";
                final String PASSWORD = "Horia1975";
                try{
                    Class.forName("com.mysql.cj.jdbc.Driver");
                    Connection conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);

                    Statement stmt = conn.createStatement();
                    String sql_verify_user="SELECT * FROM users WHERE email=? AND password=?";
                    String sql_add_user = "INSERT INTO users (email, password, name, surname, birthday, phone_number) " +
                            "VALUES (?, ?, ?, ?, ?, ?)";
                    PreparedStatement preparedStatement = conn.prepareStatement(sql_verify_user);
                    preparedStatement.setString(1,user.email);
                    preparedStatement.setString(2,user.password);

                    ResultSet resultSet = preparedStatement.executeQuery();

                    if (resultSet.next()) {
                        JOptionPane.showMessageDialog(RegisterForm.this,
                                "Account already exist",
                                "Try again",
                                JOptionPane.ERROR_MESSAGE);
                    }
                    else{
                        preparedStatement = conn.prepareStatement(sql_add_user);
                        preparedStatement.setString(1, user.email);
                        preparedStatement.setString(2, user.password);
                        preparedStatement.setString(3, user.name);
                        preparedStatement.setString(4, user.surname);
                        preparedStatement.setString(5, user.birthday);
                        preparedStatement.setString(6, user.phone_number);

                        //Insert row into the table
                        int addedRows = preparedStatement.executeUpdate();
                    }

                    stmt.close();
                    conn.close();

                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        });

        setVisible(true);
    }

}
