import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.*;
import java.util.Collections;
import java.util.Vector;

public class MainPageForm extends JDialog{
    private JTable WeekTable;
    private JPanel MainPagePanel;
    private JButton NextButton;
    private JButton PreviousButton;
    private User loggedUser=new User();
    final private int hours_worked=8;

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

    public MainPageForm(JFrame parent, User user){
        super(parent);
        setTitle("MainPage");
        setLocation(0,0);
        setContentPane(MainPagePanel);

        ImageIcon icon = new ImageIcon("./src/car.png");
        setIconImages(Collections.singletonList(icon.getImage()));

        setSize(new Dimension((int)(Toolkit.getDefaultToolkit().getScreenSize().getWidth()),(int)(Toolkit.getDefaultToolkit().getScreenSize().getHeight()-33)));
        setResizable(false);

        setModal(true);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        DefaultTableModel dtm=(DefaultTableModel) WeekTable.getModel();

        ImageIcon PreviousArrow = new ImageIcon("./src/PreviousArrow.png");
        ImageIcon NextArrow = new ImageIcon("./src/NextArrow.png");
        PreviousButton.setIcon(PreviousArrow);
        NextButton.setIcon(NextArrow);
        PreviousButton.setContentAreaFilled(false);
        NextButton.setContentAreaFilled(false);
        PreviousButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        NextButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        loggedUser=user;
        Vector<Appointment> appointments = new Vector<Appointment>();
        char [][] WeekMatrix =new char [hours_worked*2][5];

        final String DB_URL = "jdbc:mysql://127.0.0.1:3306/sys";
        final String USERNAME = "root";
        final String PASSWORD = "Horia1975";
        try {
            Appointment appointment=null;
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
            Statement stmt = conn.createStatement();
            String sql = "SELECT * FROM user_appointment_link";

            PreparedStatement preparedStatement = conn.prepareStatement(sql);

            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                appointment = new Appointment();

                appointment.user_id = Integer.parseInt(resultSet.getString("user_id"));
                String appointmentid=resultSet.getString("appointment_id");

                String sqlAppointments = "SELECT * FROM appointments WHERE appointment_id = ";
                sqlAppointments+=appointmentid;

                PreparedStatement preparedStatementAppointments = conn.prepareStatement(sqlAppointments);

                ResultSet resultSetAppointments = preparedStatementAppointments.executeQuery();

                while(resultSetAppointments.next())
                {
                    appointment.start_day = resultSetAppointments.getString("start_day");
                    appointment.start_hour = resultSetAppointments.getString("start_hour");
                    appointment.finish_day = resultSetAppointments.getString("finish_day");
                    appointment.finish_hour = resultSetAppointments.getString("finish_hour");
                    appointments.add(appointment);
                }
            }
            stmt.close();
            conn.close();
        }catch(Exception e){
            e.printStackTrace();
        }

        for(int i=0;i<hours_worked*2;i++)
        {
            for(int j=0;j<5;j++)
            {
                WeekMatrix[i][j]='0';
            }
        }

        for(int i=0;i<appointments.size();i++)
        {
            int start_row=FindRow(appointments.get(i).start_hour);
            int start_column=FindColumn(appointments.get(i).start_day);
            String s=appointments.get(i).finish_hour;
            String substring="";
            if(s.charAt(3)=='3')
            {
                substring=s.substring(0,3);
                substring+="00";
            }
            else
            {
                int hour=Integer.parseInt(s.substring(0,2));
                hour-=1;
                if(hour<10)
                {
                    substring="0"+Integer.toString(hour);
                }
                else
                {
                    substring=Integer.toString(hour);
                }
                substring+=":30";
            }
            int finish_row=FindRow(substring);
            int finish_column=FindColumn(appointments.get(i).finish_day);
            if (appointments.get(i).user_id==loggedUser.id) {
                WeekMatrix[start_row][start_column]='2';
            }
            else{
                WeekMatrix[start_row][start_column]='1';
            }
            while(start_row!=finish_row || start_column!=finish_column)
            {
                if(start_row==hours_worked*2)
                {
                    start_row=0;
                    if(start_column==5)
                    {
                        start_column=0;
                    }
                    else
                    {
                        start_column+=1;
                    }
                }
                else
                {
                    start_row+=1;
                }
                if (appointments.get(i).user_id==loggedUser.id) {
                    WeekMatrix[start_row][start_column]='2';
                }
                else{
                    WeekMatrix[start_row][start_column]='1';
                }
            }
        }

        TableColorCellRenderer renderer=new TableColorCellRenderer();
        renderer.setAppointments(WeekMatrix);
        renderer.setHours_worked(hours_worked);
        //WeekTable.setPreferredSize(new Dimension(500,(getHeight()/2/(hours_worked*2)+hours_worked/3)*hours_worked*2));
        //WeekTable.setRowHeight(getHeight()/2/(hours_worked*2)+hours_worked/3);
        if(hours_worked==8){
            WeekTable.setRowHeight(38);
        } else if (hours_worked==12) {
            WeekTable.setRowHeight(25);
        }
        WeekTable.setDefaultRenderer(Object.class,renderer);

        //DefaultTableCellRenderer centerRenderer=(DefaultTableCellRenderer) WeekTable.getDefaultRenderer(Object.class);
        // centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        WeekTable.setCellSelectionEnabled(true);
        WeekTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        WeekTable.getTableHeader().setReorderingAllowed(false);
        WeekTable.getTableHeader().setResizingAllowed(false);

        for(int i=0;i<hours_worked*2;i++)
        {
            String ora;
            if(i%2==0)
            {
                int x=i+8-i/2;
                String s="";
                if(x<10)
                    s="0";
                ora=s+Integer.toString(x)+":00 - " + s+Integer.toString(x)+":30";
            }
            else
            {
                int x=i+7-i/2;
                String s1="",s2="";
                if(x<10)
                    s1="0";
                if(x<9)
                    s2="0";
                ora=s1+Integer.toString(x)+":30 - " + s2+Integer.toString(x+1)+":00";
            }
            String row[]={ora,ora,ora,ora,ora};
            dtm.addRow(row);
        }

        WeekTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {

                super.mouseClicked(e);
                Point point = e.getPoint();
                int column = WeekTable.columnAtPoint(point);
                int row = WeekTable.rowAtPoint(point);

                /*Component c = WeekTable.getCellRenderer(row, column).getTableCellRendererComponent(WeekTable, null, false, true, row, column);
                if(c.getBackground()==Color.WHITE)
                {
                    c.setBackground(Color.GREEN);
                }*/
            }
        });
        PreviousButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });
        NextButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });

        setVisible(true);
    }

    private int FindRow(String s){
        int row=-1;
        int hour=0;
        if(s.charAt(3)=='0')
        {
            hour=Integer.parseInt(s.substring(0,2));
            row=(hour-8)*2;
        }
        else{
            hour=Integer.parseInt(s.substring(0,2));
            row=(hour-8)*2+1;
        }
        return row;
    }
    private int FindColumn(String s){
        int column=-1;
        switch(s){
            case "Monday": {
                column = 0;
                break;
            }
            case "Tuesday": {
                column = 1;
                break;
            }
            case "Wednesday": {
                column = 2;
                break;
            }
            case "Thursday": {
                column = 3;
                break;
            }
            case "Friday": {
                column = 4;
                break;
            }
        }
        return column;
    }
}