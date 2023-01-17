import javax.swing.*;
import javax.swing.border.MatteBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.*;
import java.util.Collections;
import java.util.Comparator;
import java.util.Vector;

public class AppointmentsForm extends JDialog{
    private JTable AppointmentsTable;
    private JPanel AppointmentsPanel;
    private JScrollPane TablePane;
    private JButton SetAppointmentButton;
    private JLabel YourAppointmentsLabel;
    private JLabel NoAppointmentsLabel;
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
        AppointmentsTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);

                int result = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete this appointment?", "Delete Appointment", JOptionPane.YES_NO_OPTION);
                if (result == JOptionPane.YES_OPTION) {
                    Point point = e.getPoint();
                    int row = AppointmentsTable.rowAtPoint(point);
                    DeleteAppointment(appointments.get(row));
                    SetTable();
                    AppointmentsTable.getTableHeader().setBackground(Color.BLUE);

                } else if (result == JOptionPane.NO_OPTION) {
                }
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
        AppointmentsTable.setSelectionBackground(new Color(217, 0, 0));
        AppointmentsTable.setBorder(new MatteBorder(0, 1, 1, 0, Color.black));

        appointments=GetAppointments();
        if(appointments.size()==0)
        {
            TablePane.setVisible(false);
            AppointmentsTable.setVisible(false);
            YourAppointmentsLabel.setVisible(false);
            NoAppointmentsLabel.setVisible(true);
        }
        else {
            NoAppointmentsLabel.setVisible(false);
            TablePane.setVisible(true);
            AppointmentsTable.setVisible(true);
            YourAppointmentsLabel.setVisible(true);
        }

        for(int i=0;i<appointments.size();i++)
        {
            String appointment[]={appointments.get(i).start_date,appointments.get(i).start_hour,appointments.get(i).finish_date,appointments.get(i).finish_hour};
            dtm.addRow(appointment);
        }
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
        AppointmentsPanel.revalidate();
        AppointmentsPanel.repaint();
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
    private void DeleteAppointment(Appointment appointment){
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
            Statement stmt = conn.createStatement();

            String sql = "DELETE FROM appointment_issue_link WHERE appointment_id="+Integer.toString(appointment.appointment_id);
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            int deleted_rows = preparedStatement.executeUpdate();

            sql = "DELETE FROM appointments WHERE appointment_id="+Integer.toString(appointment.appointment_id);
            preparedStatement = conn.prepareStatement(sql);
            deleted_rows = preparedStatement.executeUpdate();

            stmt.close();
            conn.close();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
