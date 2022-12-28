import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.util.Vector;

public class TableColorCellRenderer extends DefaultTableCellRenderer {

    private static final TableCellRenderer renderer=new DefaultTableCellRenderer();
    private int hours_worked;
    private char [][] WeekMatrix =new char [hours_worked*2][5];

    public void setAppointments(char [][] appointments){
        WeekMatrix=appointments;
    }
    public void setHours_worked(int hours){hours_worked=hours;}

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        JLabel c=(JLabel)renderer.getTableCellRendererComponent(table,value,isSelected,hasFocus,row,column);

        c.setHorizontalAlignment(JLabel.CENTER);

        if(WeekMatrix[row][column]=='1')
        {
            c.setBackground(Color.RED);
        } else if (WeekMatrix[row][column]=='2')
        {
            c.setBackground(new Color(43, 90, 255));
        } else if (isSelected)
        {
            c.setBackground(Color.GREEN);
        }
        else
        {
            c.setBackground(Color.WHITE);
        }

        return c;
    }
}
