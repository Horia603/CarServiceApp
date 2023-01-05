import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.*;

public class MainPageForm extends JDialog{
    private  JTable WeekTable;
    private JPanel MainPagePanel;
    private JButton NextButton;
    private JButton PreviousButton;
    private JLabel WhiteLabel;
    private JLabel GreyLabel;
    private JLabel RedLabel;
    private JLabel BlueLabel;
    private User loggedUser=new User();
    private Calendar cal = Calendar.getInstance();
    private Vector<String> days=new Vector<String>(7);
    final private int hours_worked=8;
    final private int hours_worked_on_Saturday=4;
    final private int hours_worked_on_Sunday=0;
    private int current_day;
    private int current_week=0;
    final private int number_of_weeks=53;
    private String [][] Tables_head=new String [number_of_weeks][7];
    private Vector<Appointment> appointments;
    private char [][][] WeekMatrix =new char [number_of_weeks][hours_worked*2][7];

    private void createUIComponents() {
        // TODO: place custom component creation code here

        WeekTable = new JTable() {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
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

        ImageIcon PreviousArrow = new ImageIcon("./src/PreviousArrow.png");
        ImageIcon NextArrow = new ImageIcon("./src/NextArrow.png");
        PreviousButton.setIcon(PreviousArrow);
        NextButton.setIcon(NextArrow);
        PreviousButton.setContentAreaFilled(false);
        NextButton.setContentAreaFilled(false);
        PreviousButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        NextButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        Border border = BorderFactory.createLineBorder(Color.BLACK, 2);
        Border margin = new EmptyBorder(2,2,2,2);
        WhiteLabel.setBorder(new CompoundBorder(border, margin));
        GreyLabel.setBorder(new CompoundBorder(border, margin));
        RedLabel.setBorder(new CompoundBorder(border, margin));
        BlueLabel.setBorder(new CompoundBorder(border, margin));

        loggedUser=user;

        days.add("Monday");
        days.add("Tuesday");
        days.add("Wednesday");
        days.add("Thursday");
        days.add("Friday");
        days.add("Saturday");
        days.add("Sunday");

        appointments=GetAppointments();

        SetWeekMatrix();

        SetTable();
        WeekTable.getTableHeader().setBackground(Color.BLUE);

        WeekTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                Point point = e.getPoint();
                int column = WeekTable.columnAtPoint(point);
                int row = WeekTable.rowAtPoint(point);
                if(WeekMatrix[current_week][row][column]=='0')
                {
                    SetAppointmentForm AppointmentForm = new SetAppointmentForm(null, number_of_weeks, current_week, hours_worked, hours_worked_on_Saturday, hours_worked_on_Sunday, WeekMatrix, row, column, Tables_head, loggedUser);
                    SetTable();
                    WeekTable.getTableHeader().setBackground(Color.BLUE);
                }
            }
        });
        PreviousButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(current_week>0){
                    current_week--;
                    SetTable();
                    WeekTable.getTableHeader().setBackground(Color.BLUE);
                }
            }
        });
        NextButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(current_week<number_of_weeks-1){
                    current_week++;
                    SetTable();
                    WeekTable.getTableHeader().setBackground(Color.BLUE);
                }
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
            if(hour<8) {
                row=0;
            } else if (hour>hours_worked+8) {
                row=hours_worked*2-1;
            } else {
                row=(hour-8)*2;
            }
        }
        else{
            hour=Integer.parseInt(s.substring(0,2));
            if(hour<8) {
                row=0;
            } else if (hour>hours_worked+8) {
                row=hours_worked*2-1;
            } else {
                row=(hour-8)*2+1;
            }
        }
        return row;
    }
    private int FindColumn(String s){
        int column=-1;
        switch(s){
            case "MONDAY":
            case "Monday": {
                column = 0;
                break;
            }
            case "TUESDAY":
            case "Tuesday": {
                column = 1;
                break;
            }
            case "WEDNESDAY":
            case "Wednesday": {
                column = 2;
                break;
            }
            case "THURSDAY":
            case "Thursday": {
                column = 3;
                break;
            }
            case "FRIDAY":
            case "Friday": {
                column = 4;
                break;
            }
            case "SATURDAY":
            case "Saturday": {
                column = 5;
                break;
            }
            case "SUNDAY":
            case "Sunday": {
                column = 6;
                break;
            }
        }
        return column;
    }
    private Vector<Appointment> GetAppointments(){
        Vector<Appointment> Appointments=new Vector<Appointment>();
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
                appointment.appointment_id=resultSet.getInt("appointment_id");

                String sqlAppointments = "SELECT * FROM appointments WHERE appointment_id = ";
                sqlAppointments+=Integer.toString(appointment.appointment_id);

                PreparedStatement preparedStatementAppointments = conn.prepareStatement(sqlAppointments);

                ResultSet resultSetAppointments = preparedStatementAppointments.executeQuery();

                while(resultSetAppointments.next())
                {
                    appointment.start_date=resultSetAppointments.getString("start_date");
                    appointment.start_hour = resultSetAppointments.getString("start_hour");
                    appointment.finish_date=resultSetAppointments.getString("finish_date");
                    appointment.finish_hour = resultSetAppointments.getString("finish_hour");
                    Appointments.add(appointment);
                }
            }
            stmt.close();
            conn.close();
        }catch(Exception e){
            e.printStackTrace();
        }
        return Appointments;
    }
    private void DeleteAppointment(Appointment appointment){
        final String DB_URL = "jdbc:mysql://127.0.0.1:3306/sys";
        final String USERNAME = "root";
        final String PASSWORD = "Horia1975";
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
            Statement stmt = conn.createStatement();

            String sql = "DELETE FROM user_appointment_link WHERE appointment_id="+Integer.toString(appointment.appointment_id);
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            int deleted_rows = preparedStatement.executeUpdate();

            sql = "DELETE FROM appointment_issue_link WHERE appointment_id="+Integer.toString(appointment.appointment_id);
            preparedStatement = conn.prepareStatement(sql);
            deleted_rows = preparedStatement.executeUpdate();

            sql = "DELETE FROM appointments WHERE appointment_id="+Integer.toString(appointment.appointment_id);
            preparedStatement = conn.prepareStatement(sql);
            deleted_rows = preparedStatement.executeUpdate();

            stmt.close();
            conn.close();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    private void SetWeekMatrix(){

        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

        LocalDate date1 = LocalDate.now();
        DayOfWeek dow = date1.getDayOfWeek();
        String day=dow.getDisplayName(TextStyle.FULL, Locale.ENGLISH);
        current_day=FindColumn(day);
        cal.add(Calendar.DAY_OF_MONTH, -current_day);

        for(int k=0;k<number_of_weeks;k++) {

            for (int i = 0; i < hours_worked * 2; i++) {
                for (int j = 0; j < 7; j++) {
                    if((j==5&&i>hours_worked_on_Saturday*2-1)||(j==6&&i>hours_worked_on_Sunday*2-1)) {
                        WeekMatrix[k][i][j] = '1';
                    }
                    else {
                        WeekMatrix[k][i][j] = '0';
                    }
                }
            }
            for(int j=0;j<7;j++) {
                Tables_head[k][j]=days.get(j)+" "+formatter.format(cal.getTime()).substring(0,10);
                cal.add(Calendar.DAY_OF_MONTH,1);
            }
        }

        Calendar first_day=(Calendar) Calendar.getInstance();
        first_day.add(first_day.DAY_OF_MONTH, -current_day);
        first_day.set(first_day.HOUR_OF_DAY,8);
        first_day.set(first_day.MINUTE,0);
        first_day.set(first_day.SECOND,0);

        Calendar now=(Calendar) Calendar.getInstance();
        now.set(now.SECOND,0);

        java.util.Date date2=first_day.getTime();
        java.util.Date date3=now.getTime();

        long difference_Time = date3.getTime() - date2.getTime();
        long difference_Minutes = difference_Time / (1000 * 60);

        while(difference_Minutes > 0)
        {
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
            int row = FindRow(sdf.format(date2).substring(11,16));
            LocalDate localDate = LocalDate.of(Integer.parseInt(sdf.format(date2).substring(6,10)),Integer.parseInt(sdf.format(date2).substring(3,5)),Integer.parseInt(sdf.format(date2).substring(0,2)));
            int column = FindColumn(localDate.getDayOfWeek().toString());

            if(row>hours_worked*2-1||(row>hours_worked_on_Saturday*2-1&&column==5)||(row>hours_worked_on_Sunday*2-1&&column==6))
            {
                first_day.add(first_day.DAY_OF_MONTH, 1);
                first_day.set(first_day.HOUR_OF_DAY, 7);
                first_day.set(first_day.MINUTE, 30);
            }else if(WeekMatrix[0][row][column]=='0')
            {
                WeekMatrix[0][row][column]='1';
            }

            first_day.add(first_day.MINUTE,30);
            date2 = first_day.getTime();
            date3 = now.getTime();
            difference_Time = date3.getTime() - date2.getTime();
            difference_Minutes = difference_Time / (1000 * 60);
        }

        first_day.add(first_day.DAY_OF_MONTH, -current_day);
        first_day.set(first_day.HOUR_OF_DAY,0);
        first_day.set(first_day.MINUTE,0);
        first_day.set(first_day.SECOND,0);

        Calendar calendar = (Calendar) Calendar.getInstance();
        java.util.Date current_day = calendar.getTime();

        for (int i = 0; i < appointments.size(); i++) {
            Calendar start_date=(Calendar) Calendar.getInstance();
            start_date.set(start_date.DAY_OF_MONTH,Integer.parseInt(appointments.get(i).start_date.substring(0,2)));
            start_date.set(start_date.MONTH,Integer.parseInt(appointments.get(i).start_date.substring(3,5))-1);
            start_date.set(start_date.YEAR,Integer.parseInt(appointments.get(i).start_date.substring(6,10)));
            start_date.set(start_date.HOUR_OF_DAY,Integer.parseInt(appointments.get(i).start_hour.substring(0,2)));
            start_date.set(start_date.MINUTE,Integer.parseInt(appointments.get(i).start_hour.substring(3,5)));
            start_date.set(start_date.SECOND,0);

            Calendar finish_date=(Calendar) Calendar.getInstance();
            finish_date.set(finish_date.DAY_OF_MONTH,Integer.parseInt(appointments.get(i).finish_date.substring(0,2)));
            finish_date.set(finish_date.MONTH,Integer.parseInt(appointments.get(i).finish_date.substring(3,5))-1);
            finish_date.set(finish_date.YEAR,Integer.parseInt(appointments.get(i).finish_date.substring(6,10)));
            finish_date.set(finish_date.HOUR_OF_DAY,Integer.parseInt(appointments.get(i).finish_hour.substring(0,2)));
            finish_date.set(finish_date.MINUTE,Integer.parseInt(appointments.get(i).finish_hour.substring(3,5)));
            finish_date.set(finish_date.SECOND,0);

            java.util.Date d0=first_day.getTime();
            java.util.Date d1=start_date.getTime();
            java.util.Date d2=finish_date.getTime();

            if(d2.getTime() - current_day.getTime() < 0)
            {
                DeleteAppointment(appointments.get(i));
                appointments.remove(i);
            }
            else{
                long difference_In_Time = d2.getTime() - d1.getTime();
                long difference_In_Minutes = difference_In_Time / (1000 * 60);

                while(difference_In_Minutes>0)
                {
                    long difference_In_Time_for_week_number = d1.getTime() - d0.getTime();
                    long difference_In_Days_for_week_number = (difference_In_Time_for_week_number / (1000 * 60 * 60 * 24)) % 365;
                    int week_number= (int) (difference_In_Days_for_week_number/7);

                    SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                    int row = FindRow(sdf.format(d1).substring(11,16));
                    LocalDate localDate = LocalDate.of(Integer.parseInt(sdf.format(d1).substring(6,10)),Integer.parseInt(sdf.format(d1).substring(3,5)),Integer.parseInt(sdf.format(d1).substring(0,2)));
                    int column = FindColumn(localDate.getDayOfWeek().toString());

                    if(row>hours_worked*2-1||(row>hours_worked_on_Saturday*2-1&&column==5)||(row>hours_worked_on_Sunday*2-1&&column==6))
                    {
                        start_date.add(start_date.DAY_OF_MONTH, 1);
                        start_date.set(start_date.HOUR_OF_DAY, 7);
                        start_date.set(start_date.MINUTE, 30);
                    }else if(WeekMatrix[week_number][row][column]=='0')
                    {
                        if (appointments.get(i).user_id == loggedUser.id)
                        {
                            WeekMatrix[week_number][row][column] = '3';
                        } else
                        {
                            WeekMatrix[week_number][row][column] = '2';
                        }
                    }
                    start_date.add(start_date.MINUTE,30);
                    d1=start_date.getTime();
                    difference_In_Time = d2.getTime() - d1.getTime();
                    difference_In_Minutes = difference_In_Time / (1000 * 60);
                }
            }
        }
    }
    private void SetTable(){

        JTableHeader tableHead=WeekTable.getTableHeader();
        tableHead.setBackground(new Color(17, 17, 255,150));
        tableHead.setForeground(Color.BLACK);
        tableHead.setFont(new Font("Bodoni MT Black",Font.BOLD,20));

        if(hours_worked==8){
            tableHead.setPreferredSize(new Dimension(50,37));
        }
        if(hours_worked==12){
            tableHead.setPreferredSize(new Dimension(50,30));
        }
        tableHead.repaint();

        WeekTable.setFont(new Font("Bodoni MT Black", Font.BOLD, 20));
        WeekTable.setModel(new DefaultTableModel(
                new Object[][]{},
                new String[]{Tables_head[current_week][0],Tables_head[current_week][1],Tables_head[current_week][2],
                        Tables_head[current_week][3],Tables_head[current_week][4],Tables_head[current_week][5],Tables_head[current_week][6]}
        ));

        WeekTable.setTableHeader(tableHead);

        DefaultTableModel dtm=(DefaultTableModel) WeekTable.getModel();



        TableColorCellRenderer renderer=new TableColorCellRenderer();
        renderer.setNumberOfWeeks(number_of_weeks);
        renderer.setAppointments(WeekMatrix);
        renderer.setHoursWorked(hours_worked);
        renderer.setHoursWorkedOnSaturday(hours_worked_on_Saturday);
        renderer.setHoursWorkedOnSunday(hours_worked_on_Sunday);
        renderer.setCurrentWeek(current_week);

        if(hours_worked==8){
            WeekTable.setRowHeight(37);
            WeekTable.setPreferredSize(new Dimension(-1,593));
        } else if (hours_worked==12) {
            WeekTable.setRowHeight(25);
        }
        WeekTable.setDefaultRenderer(Object.class,renderer);

        //DefaultTableCellRenderer centerRenderer=(DefaultTableCellRenderer) WeekTable.getDefaultRenderer(Object.class);
        //centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
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
            String row[]={ora,ora,ora,ora,ora,ora,ora};
            dtm.addRow(row);
        }
    }
}