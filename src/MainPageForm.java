import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

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

        for(int i=0;i<12;i++)
        {
            String ora=Integer.toString(i+8)+":00 - " + Integer.toString(i+9)+":00";
            String row[]={ora,ora,ora,ora,ora};
            dtm.addRow(row);
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
