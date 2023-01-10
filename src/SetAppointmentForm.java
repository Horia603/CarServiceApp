import com.mysql.cj.conf.ConnectionUrlParser;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Collections;
import java.util.Vector;

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
    private User logged_user;
    private int number_of_weeks=0;
    private int hours_worked=0;
    private int hours_worked_on_Saturday;
    private int hours_worked_on_Sunday;
    private int current_week=0;
    private char [][][] WeekMatrix;
    private String [][] Tables_head;
    int selected_row=0;
    int selected_column=0;

    public SetAppointmentForm(JFrame parent, int weeks, int week, int hours, int hours_Saturday, int hours_Sunday, char[][][] matrix, int row, int column, String [][] dates, User user){
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

        number_of_weeks=weeks;
        current_week=week;
        hours_worked=hours;
        hours_worked_on_Saturday=hours_Saturday;
        hours_worked_on_Sunday=hours_Sunday;
        WeekMatrix=new char[number_of_weeks][hours_worked*2][7];
        WeekMatrix=matrix;
        Tables_head=new String[number_of_weeks][7];
        Tables_head=dates;
        selected_row=row;
        selected_column=column;
        logged_user=user;
        SetAppointmentButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int total_cost = AddAppointmentToDataBase();
                JOptionPane.showMessageDialog(SetAppointmentForm.this,
                        "Appointment set!\n" +
                                "Total cost is $" + Integer.toString(total_cost),
                        "",
                        JOptionPane.INFORMATION_MESSAGE);
                dispose();
            }
        });

        setVisible(true);
    }
    int AddAppointmentToDataBase(){
        ConnectionUrlParser.Pair<Appointment,Vector<Integer>> pair= MakeAppointment();
        Appointment appointment = pair.left;
        Vector<Integer> issues = pair.right;

        final String DB_URL = "jdbc:mysql://127.0.0.1:3306/sys";
        final String USERNAME = "root";
        final String PASSWORD = "Horia1975";
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);

            Statement stmt = conn.createStatement();
            String sql = "INSERT INTO appointments (start_date,start_hour,finish_date,finish_hour,user_id) " +
                    "VALUES (?, ?, ?, ?, ?)";

            PreparedStatement preparedStatement = conn.prepareStatement(sql);

            preparedStatement.setString(1, appointment.start_date);
            preparedStatement.setString(2, appointment.start_hour);
            preparedStatement.setString(3, appointment.finish_date);
            preparedStatement.setString(4, appointment.finish_hour);
            preparedStatement.setInt(5, logged_user.id);

            //Insert row into the table
            int addedRows = preparedStatement.executeUpdate();

            sql = "SELECT * FROM appointments";
            preparedStatement=conn.prepareStatement(sql);
            ResultSet resultSet = preparedStatement.executeQuery();
            int appointment_id=0;
            while(resultSet.next()) {
                if(resultSet.isLast()) {
                    appointment_id=Integer.parseInt(resultSet.getString("appointment_id"));
                }
            }

            sql = "INSERT INTO appointment_issue_link (appointment_id, issue_id) " +
                    "VALUES (?, ?)";
            preparedStatement=conn.prepareStatement(sql);
            for(int i=0;i<issues.size();i++)
            {
                preparedStatement.setInt(1, appointment_id);
                preparedStatement.setInt(2, issues.get(i));
                addedRows = preparedStatement.executeUpdate();
            }

            stmt.close();
            conn.close();

        }catch(Exception e){
            e.printStackTrace();
        }
        return appointment.total_cost;
    }
    ConnectionUrlParser.Pair<Appointment,Vector<Integer>> MakeAppointment(){
        int cost=0;
        int duration=0;
        Issue issue=null;
        Vector<Integer> issues=new Vector<Integer>();
        if(OilChangeCheckBox.isSelected()){
            issue=new Issue();
            issue=GetIssue(2);
            cost+=issue.cost;
            duration+=issue.duration;
            issues.add(2);
        }
        if(BatteryChangeCheckBox.isSelected()){
            issue=new Issue();
            issue=GetIssue(7);
            cost+=issue.cost;
            duration+=issue.duration;
            issues.add(7);
        }
        if(HeadLightChangeCheckBox.isSelected()){
            issue=new Issue();
            issue=GetIssue(3);
            cost+=issue.cost;
            duration+=issue.duration;
            issues.add(3);
        }
        if(TailLightChangeCheckBox.isSelected()){
            issue=new Issue();
            issue=GetIssue(4);
            cost+=issue.cost;
            duration+=issue.duration;
            issues.add(4);
        }
        if(ExhaustRepairCheckBox.isSelected()){
            issue=new Issue();
            issue=GetIssue(5);
            cost+=issue.cost;
            duration+=issue.duration;
            issues.add(5);
        }
        if(SuspensionRepairCheckBox.isSelected()){
            issue=new Issue();
            issue=GetIssue(6);
            cost+=issue.cost;
            duration+=issue.duration;
            issues.add(6);
        }
        if(MaintenanceCheckBox.isSelected()){
            issue=new Issue();
            issue=GetIssue(8);
            cost+=issue.cost;
            duration+=issue.duration;
            issues.add(8);
        }
        if(OtherProblemCheckBox.isSelected()){
            issue=new Issue();
            issue=GetIssue(1);
            cost+=issue.cost;
            duration+=issue.duration;
            issues.add(1);
        }
        Appointment appointment = new Appointment();
        appointment.total_cost=cost;
        appointment.start_date=Tables_head[current_week][selected_column].substring(Tables_head[current_week][selected_column].length()-10);
        appointment.start_hour=GetHour(selected_row).substring(0,5);

        Calendar calendar=(Calendar) Calendar.getInstance();
        calendar.set(calendar.DAY_OF_MONTH, Integer.parseInt(appointment.start_date.substring(0,2)));
        calendar.set(calendar.MONTH,Integer.parseInt(appointment.start_date.substring(3,5))-1);
        calendar.set(calendar.YEAR,Integer.parseInt(appointment.start_date.substring(6,10)));
        calendar.set(calendar.SECOND,0);
        calendar.set(calendar.HOUR_OF_DAY,Integer.parseInt(appointment.start_hour.substring(0,2)));
        calendar.set(calendar.MINUTE,Integer.parseInt((appointment.start_hour.substring(3,5))));

        java.util.Date date=calendar.getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

        while(duration>0){
            int row = FindRow(sdf.format(date).substring(11,16));
            LocalDate localDate = LocalDate.of(Integer.parseInt(sdf.format(date).substring(6,10)),Integer.parseInt(sdf.format(date).substring(3,5)),Integer.parseInt(sdf.format(date).substring(0,2)));
            int column = FindColumn(localDate.getDayOfWeek().toString());

            if(row>hours_worked*2-1||(row>hours_worked_on_Saturday*2-1&&column==5)||(row>hours_worked_on_Sunday*2-1&&column==6))
            {
                calendar.add(calendar.DAY_OF_MONTH, 1);
                calendar.set(calendar.HOUR_OF_DAY, 7);
                calendar.set(calendar.MINUTE, 30);
                if(column==6)
                    current_week+=1;
            }else if(WeekMatrix[current_week][row][column]=='0'){
                WeekMatrix[current_week][row][column]='3';
                duration-=30;
            }
            calendar.add(calendar.MINUTE,30);
            date=calendar.getTime();
        }
        appointment.finish_date=sdf.format(date).substring(0,10);
        appointment.finish_hour=sdf.format(date).substring(11,16);

        return new ConnectionUrlParser.Pair<>(appointment,issues);
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
    String GetHour(int row){
        String ora;
        if(row%2==0)
        {
            int x=row+8-row/2;
            String s="";
            if(x<10)
                s="0";
            ora=s+Integer.toString(x)+":00 - " + s+Integer.toString(x)+":30";
        }
        else
        {
            int x=row+7-row/2;
            String s1="",s2="";
            if(x<10)
                s1="0";
            if(x<9)
                s2="0";
            ora=s1+Integer.toString(x)+":30 - " + s2+Integer.toString(x+1)+":00";
        }
        return ora;
    }
    void AddAppointmentToWeekMatrix(int duration){

    }
    Issue GetIssue(int issue_id){
        final String DB_URL = "jdbc:mysql://127.0.0.1:3306/sys";
        final String USERNAME = "root";
        final String PASSWORD = "Horia1975";
        Issue issue=null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
            Statement stmt = conn.createStatement();
            String sql = "SELECT * FROM issues WHERE issue_id = ";
            sql+=Integer.toString(issue_id);

            PreparedStatement preparedStatement = conn.prepareStatement(sql);

            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                issue=new Issue();
                issue.duration=Integer.parseInt(resultSet.getString("duration").substring(0,2)) * 60 + Integer.parseInt(resultSet.getString("duration").substring(3,5));
                issue.cost=Integer.parseInt(resultSet.getString("cost"));
            }
            stmt.close();
            conn.close();
        }catch(Exception e){
            e.printStackTrace();
        }
        return issue;
    }
}
