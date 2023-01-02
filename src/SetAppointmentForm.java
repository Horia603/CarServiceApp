import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collections;

public class SetAppointmentForm extends JDialog{
    private JPanel SetAppointmentPanel;
    private JCheckBox OilChangeCheckBox;
    private JCheckBox BatteryChangeCheckBox;
    private JCheckBox HeadLightChangeCheckBox;
    private JCheckBox TailLightChangeCheckBox;
    private JCheckBox ExhaustRepairCheckBox;
    private JCheckBox SuspensionRepairCheckBox;
    private JCheckBox MaintenanceCheckBox;
    private JCheckBox OtherProblemCheckBox;
    private JButton SetAppointmentButton;
    private JLabel ImageLabel;
    private int current_week=0;

    public SetAppointmentForm(JFrame parent,int week){
        super(parent);
        setTitle("SetAppointment");
        setLocation(0,0);
        setContentPane(SetAppointmentPanel);

        ImageIcon icon = new ImageIcon("./src/car.png");
        setIconImages(Collections.singletonList(icon.getImage()));

        setSize(new Dimension(650,700));
        setResizable(false);

        setModal(true);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        SetAppointmentButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        OilChangeCheckBox.setCursor(new Cursor(Cursor.HAND_CURSOR));
        BatteryChangeCheckBox.setCursor(new Cursor(Cursor.HAND_CURSOR));
        HeadLightChangeCheckBox.setCursor(new Cursor(Cursor.HAND_CURSOR));
        TailLightChangeCheckBox.setCursor(new Cursor(Cursor.HAND_CURSOR));
        ExhaustRepairCheckBox.setCursor(new Cursor(Cursor.HAND_CURSOR));
        SuspensionRepairCheckBox.setCursor(new Cursor(Cursor.HAND_CURSOR));
        MaintenanceCheckBox.setCursor(new Cursor(Cursor.HAND_CURSOR));
        OtherProblemCheckBox.setCursor(new Cursor(Cursor.HAND_CURSOR));

        current_week=week;

        SetAppointmentButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });

        setVisible(true);
    }
}
