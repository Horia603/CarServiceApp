import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.util.Vector;

public class TableColorCellRenderer extends DefaultTableCellRenderer {

    private static final TableCellRenderer renderer=new DefaultTableCellRenderer();
    private char [][] WeekMatrix =new char [24][5];

    public void setAppointments(char [][] appointments){
        WeekMatrix=appointments;
    }

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
