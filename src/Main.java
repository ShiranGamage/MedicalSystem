import controller.MedicalController;
import javax.swing.SwingUtilities;
import view.MainFrame;

public class Main {
        public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
        public void run() {
        MainFrame view = new MainFrame();
                try {


                    MedicalController controller = new MedicalController(view);
                } catch (Exception e) {
                    System.err.println("Error Info: " + e.getMessage());
                    e.printStackTrace();
                }
                view.setVisible(true);
            }
        });
    }
}
