import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.Collections;

public class LoginForm extends JDialog {
    private JLabel ImageLabel;
    private JPasswordField PasswordField;
    private JButton LoginButton;
    private JPanel LoginPanel;
    private JTextField EmailField;
    private JLabel SignUpLabel;
    private JButton SignUpButton;

    public LoginForm(JFrame parent) {
        super(parent);
        setTitle("Login");
        setContentPane(LoginPanel);
        setMinimumSize(new Dimension(700, 800));
        setModal(true);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        ImageIcon icon = new ImageIcon("./src/car.png");
        setIconImages(Collections.singletonList(icon.getImage()));
        LoginButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        SignUpButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        LoginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String email = EmailField.getText();
                String password = String.valueOf(PasswordField.getPassword());
                user = getAuthenticatedUser(email, password);
                if(user==null) {
                    JOptionPane.showMessageDialog(LoginForm.this,
                            "Email or Password invalid",
                            "Try again",
                            JOptionPane.ERROR_MESSAGE);
                }
                else {
                    dispose();
                    MainPageForm homePageForm=new MainPageForm(null, user);
                }
            }
        });
        SignUpButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                RegisterForm roginForm = new RegisterForm(null);
            }
        });
        setVisible(true);
    }

    public User user;
    public User getAuthenticatedUser(String email, String password){
        User user = null;
        final String DB_URL = "jdbc:mysql://127.0.0.1:3306/sys";
        final String USERNAME = "root";
        final String PASSWORD = "Horia1975";
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);

            Statement stmt = conn.createStatement();
            String sql = "SELECT * FROM users WHERE email=? AND password=?";
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setString(1,email);
            preparedStatement.setString(2,password);

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                dispose();
                user = new User();
                user.id=Integer.parseInt(resultSet.getString("user_id"));
                user.email = resultSet.getString("email");
                user.password = resultSet.getString("password");
                user.name = resultSet.getString("name");
                user.surname = resultSet.getString("surname");
                user.birthday = resultSet.getString("birthday");
                user.phone_number = resultSet.getString("phone_number");
            }
            stmt.close();
            conn.close();

        }catch(Exception e){
            e.printStackTrace();
        }
        return user;
    }
}

