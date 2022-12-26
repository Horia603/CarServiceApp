import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.util.Vector;

public class TableColorCellRenderer extends DefaultTableCellRenderer {

    private static final TableCellRenderer renderer=new DefaultTableCellRenderer();
    private boolean [][] WeekMatrix =new boolean [24][5];

    public void setAppointments(boolean [][] appointments){
        WeekMatrix=appointments;
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        JLabel c=(JLabel)renderer.getTableCellRendererComponent(table,value,isSelected,hasFocus,row,column);

        c.setHorizontalAlignment(JLabel.CENTER);

        if(WeekMatrix[row][column]==true)
        {
            c.setBackground(Color.RED);
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
