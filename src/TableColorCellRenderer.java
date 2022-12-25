import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.util.Vector;

public class TableColorCellRenderer implements TableCellRenderer {

    private static final TableCellRenderer renderer=new DefaultTableCellRenderer();
    private Vector<Appointment> appointments=new Vector<Appointment>();

    public void setAppointments(Vector<Appointment> appointments1){
        appointments=appointments1;
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        Component c=renderer.getTableCellRendererComponent(table,value,isSelected,hasFocus,row,column);

        if(isSelected)
        {
            c.setBackground(Color.GREEN);
        }
        else if (row==1 && column==1)
        {
            c.setBackground(Color.RED);
        }
        else
        {
            c.setBackground(Color.WHITE);
        }

        return c;
    }
}
