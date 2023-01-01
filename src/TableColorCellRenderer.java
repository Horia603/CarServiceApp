import javax.swing.*;
import javax.swing.border.MatteBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.util.Vector;

public class TableColorCellRenderer extends DefaultTableCellRenderer {

    private static final TableCellRenderer renderer=new DefaultTableCellRenderer();
    private int hours_worked;
    private int hours_worked_on_saturday=0;
    private int hours_worked_on_sunday=0;
    private char [][][] WeekMatrix;
    private int current_week=0;
    private int number_of_weeks=0;
    public void setAppointments(char [][][] appointments){

        WeekMatrix = new char [number_of_weeks][hours_worked*2][7];
        WeekMatrix=appointments;
    }
    public void setHoursWorked(int hours){hours_worked=hours;}
    public void setHoursWorkedOnSaturday(int hours){hours_worked_on_saturday=hours;}
    public void setHoursWorkedOnSunday(int hours){hours_worked_on_sunday=hours;}
    public void setCurrentWeek(int week_number){current_week=week_number;}
    public void setNumberOfWeeks(int weeks){number_of_weeks=weeks;}

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        JLabel c=(JLabel)renderer.getTableCellRendererComponent(table,value,isSelected,hasFocus,row,column);

        c.setHorizontalAlignment(JLabel.CENTER);

       if(WeekMatrix[current_week][row][column]=='3') {
            c.setBackground(new Color(43, 90, 255));
            c.setBorder(new MatteBorder(0, 1, 1, 0, Color.black));
        } else if (WeekMatrix[current_week][row][column]=='2') {
            c.setBackground(new Color(252, 46, 46));
            c.setBorder(new MatteBorder(0, 1, 1, 0, Color.black));
        } else if (WeekMatrix[current_week][row][column]=='1') {
           c.setBackground(new Color(210, 210, 210, 255));
           c.setBorder(new MatteBorder(0, 1, 1, 0, Color.black));
        } else if (isSelected) {
            c.setBackground(Color.GREEN);
            c.setBorder(new MatteBorder(0, 1, 1, 0, Color.black));
        } else {
            c.setBackground(Color.WHITE);
            c.setBorder(new MatteBorder(0, 1, 1, 0, Color.black));
        }

        return c;
    }
}
