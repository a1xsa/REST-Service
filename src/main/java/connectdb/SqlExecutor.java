package connectdb;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class SqlExecutor {
    public SqlExecutor(){

    }

    public void executeScriptFromResources(Connection connect, String resPath){
        StringBuilder script = new StringBuilder();

        try (
                InputStream inputStream = SqlExecutor.class.getClassLoader().getResourceAsStream(resPath);
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        )
        {
            if (inputStream == null) {
                throw new RuntimeException("Resource not found: " + resPath);
            }

            String line;
            while((line = reader.readLine()) != null) {
                script.append(line).append("\n");
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to load SQL script from resource: " + resPath, e);
        }


        try (Statement statement=connect.createStatement()){
            for (String sql:script.toString().split(";")){
                sql=sql.strip();
                if (!sql.isEmpty()){
                    statement.execute(sql);
                    System.out.println("Executed: "+ sql);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
