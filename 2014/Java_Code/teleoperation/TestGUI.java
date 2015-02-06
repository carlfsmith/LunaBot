
package teleoperation;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;

public class TestGUI extends JFrame {
    private JPanel Panel;
    protected JTextArea txtLog;
    private int WindowWidth = 550, WindowHeight = 550;
    SerialClient comm = null;

    public TestGUI() {
        super("Serial Log");
        setSize(WindowWidth, WindowHeight);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        buildPanel();
        add(Panel);
        setVisible(true);
        //initComponents();
        createObjects();
    }

    private void buildPanel() {           
            txtLog = new JTextArea(100,100);         
            Panel = new JPanel();            
            Panel.add(txtLog);
            txtLog.setEditable(false);
        }
    
    private void createObjects() {
        comm = new SerialClient(this);
    }

    public void guiConnect(String port) {
        if (comm.getConnected() == true)
        {
            if (comm.initIOStream() == true)
            {
                comm.initListener();
            }
        }
    }

    public void guiDisconnect() {
        comm.disconnect();
    }

    public void GUIwriteData(int data) {
        comm.writeData(data);
}
}
