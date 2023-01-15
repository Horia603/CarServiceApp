import javax.swing.*;
import javax.swing.border.MatteBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.Collections;
import java.util.Comparator;
import java.util.Vector;

public class AppointmentsForm extends JDialog{
    private JTable AppointmentsTable;
    private JPanel AppointmentsPanel;
    private JScrollPane TablePane;
    private JButton SetAppointmentButton;
    private User logged_user = new User();
    private Vector<Appointment> appointments;
    final String DB_URL = "jdbc:mysql://127.0.0.1:3306/sys";
    final String USERNAME = "root";
    final String PASSWORD = "Horia1975";

    public AppointmentsForm(JFrame parent, User user){
        super(parent);
        setTitle("AppointmentsPage");
        setLocation(0,0);
        setContentPane(AppointmentsPanel);

        ImageIcon icon = new ImageIcon("./src/car.png");
        setIconImages(Collections.singletonList(icon.getImage()));

        setSize(new Dimension((int)(Toolkit.getDefaultToolkit().getScreenSize().getWidth()),(int)(Toolkit.getDefaultToolkit().getScreenSize().getHeight()-33)));
        setResizable(false);

        setModal(true);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        SetAppointmentButton.setContentAreaFilled(false);
        SetAppointmentButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        SetAppointmentButton.setBorder(null);

        logged_user=user;

        SetTable();

        SetAppointmentButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                MainPageForm homePageForm=new MainPageForm(null, logged_user);
            }
        });
        setVisible(true);
    }
    void SetTable(){
        JTableHeader tableHead=AppointmentsTable.getTableHeader();
        tableHead.setForeground(Color.BLACK);
        tableHead.setFont(new Font("Bodoni MT Black",Font.BOLD,20));
        tableHead.setPreferredSize(new Dimension(50,37));

        AppointmentsTable.setFont(new Font("Bodoni MT Black", Font.BOLD, 20));
        AppointmentsTable.setModel(new DefaultTableModel(
                new Object[][]{},
                new String[]{"Start Date","Start Hour","Finish Date","Finish Hour"}
        ));
        tableHead.repaint();
        AppointmentsTable.getTableHeader().setBackground(Color.BLUE);

        AppointmentsTable.setTableHeader(tableHead);

        DefaultTableModel dtm=(DefaultTableModel) AppointmentsTable.getModel();

        DefaultTableCellRenderer centerRenderer=(DefaultTableCellRenderer) AppointmentsTable.getDefaultRenderer(Object.class);
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        AppointmentsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        AppointmentsTable.getTableHeader().setReorderingAllowed(false);
        AppointmentsTable.getTableHeader().setResizingAllowed(false);
        AppointmentsTable.setSelectionBackground(Color.GREEN);
        AppointmentsTable.setBorder(new MatteBorder(0, 1, 1, 0, Color.black));

        appointments=GetAppointments();

        for(int i=0;i<appointments.size();i++)
        {
            String appointment[]={appointments.get(i).start_date,appointments.get(i).start_hour,appointments.get(i).finish_date,appointments.get(i).start_hour};
            dtm.addRow(appointment);
        }
        //AppointmentsTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        //TablePane.setSize(new Dimension(500, 100));
        AppointmentsTable.setRowHeight(37);
        int rows = AppointmentsTable.getRowCount();
        int cols = AppointmentsTable.getColumnCount();
        int rowHeight = AppointmentsTable.getRowHeight();
        int totalWidth = 0;
        for (int i = 0; i < cols; i++) {
            totalWidth += AppointmentsTable.getColumnModel().getColumn(i).getWidth();
        }
        AppointmentsTable.setPreferredSize( new Dimension(totalWidth, rowHeight * rows));
        int number_of_appointments_in_table=15;
        if(rows<=number_of_appointments_in_table)
            TablePane.setPreferredSize(new Dimension(totalWidth, rowHeight * (rows + 1) + 3));
        else
            TablePane.setPreferredSize(new Dimension(totalWidth, rowHeight * (number_of_appointments_in_table + 1) + 3));
    }
    private Vector<Appointment> GetAppointments(){
        Vector<Appointment> Appointments=new Vector<Appointment>();
        try {
            Appointment appointment=null;
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
            Statement stmt = conn.createStatement();
            String sql = "SELECT * FROM appointments WHERE user_id = ";
            sql+=Integer.toString(logged_user.id);

            PreparedStatement preparedStatement = conn.prepareStatement(sql);

            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                appointment = new Appointment();

                appointment.appointment_id=resultSet.getInt("appointment_id");
                appointment.start_date=resultSet.getString("start_date");
                appointment.start_hour = resultSet.getString("start_hour");
                appointment.finish_date=resultSet.getString("finish_date");
                appointment.finish_hour = resultSet.getString("finish_hour");
                appointment.user_id = Integer.parseInt(resultSet.getString("user_id"));
                Appointments.add(appointment);
            }
            stmt.close();
            conn.close();
        }catch(Exception e){
            e.printStackTrace();
        }
        Appointments.sort(new Comparator<Appointment>() {
            public int compare(Appointment ap1, Appointment ap2) {
                if(Integer.parseInt(ap1.start_date.substring(6,10))!=Integer.parseInt(ap2.start_date.substring(6,10)))
                    return Integer.parseInt(ap1.start_date.substring(6,10))-Integer.parseInt(ap2.start_date.substring(6,10));
                else if(Integer.parseInt(ap1.start_date.substring(3,5))!=Integer.parseInt(ap2.start_date.substring(3,5)))
                    return Integer.parseInt(ap1.start_date.substring(3,5))-Integer.parseInt(ap2.start_date.substring(3,5));
                else if(Integer.parseInt(ap1.start_date.substring(0,2))!=Integer.parseInt(ap2.start_date.substring(0,2)))
                    return Integer.parseInt(ap1.start_date.substring(0,2))-Integer.parseInt(ap2.start_date.substring(0,2));
                else if(Integer.parseInt(ap1.start_hour.substring(0,2))!=Integer.parseInt(ap2.start_hour.substring(0,2)))
                    return Integer.parseInt(ap1.start_hour.substring(0,2))-Integer.parseInt(ap2.start_hour.substring(0,2));
                else
                    return Integer.parseInt(ap1.start_hour.substring(3,5))-Integer.parseInt(ap2.start_hour.substring(3,5));
            }
        });
        return Appointments;
    }
}
