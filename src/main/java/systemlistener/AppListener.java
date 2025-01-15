package systemlistener;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

import java.util.logging.Level;
import java.util.logging.Logger;


@WebListener
public class AppListener implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        try {
            Class.forName("org.postgresql.Driver");
            Logger.getLogger(AppListener.class.getName()).log(Level.INFO, "PostgreSQL Driver loaded successfully.");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

}