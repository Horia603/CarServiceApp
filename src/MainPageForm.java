import org.w3c.dom.css.RGBColor;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.*;
import java.util.Vector;

public class MainPageForm extends JDialog{
    private JTable WeekTable;
    private JPanel MainPagePanel;

    private void createUIComponents() {
        // TODO: place custom component creation code here
        WeekTable = new JTable() {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTableHeader tableHead=WeekTable.getTableHeader();
        tableHead.setBackground(new Color(0,0,200,150));
        tableHead.setFont(new Font("Bodoni MT Black",Font.BOLD,20));
        WeekTable.setFont(new Font("Bodoni MT Black", Font.BOLD, 20));
        WeekTable.setModel(new DefaultTableModel(
                new Object[][]{},
                new String[]{"Monday", "Tuesday", "Wednesday", "Thursday", "Friday"}
        ));
    }

    public MainPageForm(JFrame parent){
        super(parent);
        setTitle("Register");
        setContentPane(MainPagePanel);
        setMinimumSize(new Dimension(1500, 800));
        setModal(true);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        DefaultTableModel dtm=(DefaultTableModel) WeekTable.getModel();
        DefaultTableCellRenderer centerRenderer=(DefaultTableCellRenderer) WeekTable.getDefaultRenderer(Object.class);
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        WeekTable.setCellSelectionEnabled(true);

        for(int i=0;i<24;i++)
        {
            String ora;
            if(i%2==0)
            {
                ora=Integer.toString(i+8-i/2)+":00 - " + Integer.toString(i+8-i/2)+":30";
            }
            else
            {
                ora=Integer.toString(i+7-i/2)+":30 - " + Integer.toString(i+8-i/2)+":00";
            }
            String row[]={ora,ora,ora,ora,ora};
            dtm.addRow(row);
        }

        Vector<Appointment> appointments = new Vector<Appointment>();

        final String DB_URL = "jdbc:mysql://127.0.0.1:3306/sys";
        final String USERNAME = "root";
        final String PASSWORD = "Horia1975";
        try {
            Appointment appointment=null;
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
            Statement stmt = conn.createStatement();
            String sql = "SELECT * FROM appointments";
            PreparedStatement preparedStatement = conn.prepareStatement(sql);

            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                appointment = new Appointment();
                appointment.start_day = resultSet.getString("start_day");
                appointment.start_hour = resultSet.getString("start_hour");
                appointment.finish_day = resultSet.getString("finish_day");
                appointment.finish_hour = resultSet.getString("finish_hour");
                appointment.user_id = Integer.parseInt(resultSet.getString("user_id"));
                appointments.add(appointment);
            }
            stmt.close();
            conn.close();
        }catch(Exception e){
            e.printStackTrace();
        }

        for(int i=0;i<appointments.size();i++)
        {

        }
        setVisible(true);
        WeekTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {

                super.mouseClicked(e);
            }
        });
    }

}
