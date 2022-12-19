import javax.swing.*;
import java.awt.*;

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
        setVisible(true);
    }
}
