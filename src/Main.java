import controller.MedicalController;
import javax.swing.SwingUtilities;
import view.MainFrame;

public class Main {
    public static void main(String[] args) {
    SwingUtilities.invokeLater(() -> {
    MainFrame view = new MainFrame();
            try {
                new MedicalController(view);
            } catch (Throwable t) {
                System.out.println("Error controller: " + t.getMessage());
                t.printStackTrace();
            }
            view.setVisible(true);
        });
    }
}