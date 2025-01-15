package connectdb;


import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigLoader {

    private Properties properties;

    public ConfigLoader(){
        String resPath="dbconfig.properties";
        this.properties=new Properties();

        try {
            try (InputStream input=this.getClass().getClassLoader().getResourceAsStream(resPath)){
                if(input==null){
                    throw new RuntimeException("Configuration file not found: " + resPath);
                }
                this.properties.load(input);
            }
        }
        catch (IOException e){
            throw new RuntimeException("Failed to load configuration file: "+resPath,e);
        }
    }

    public String getProperty(String key){
        return this.properties.getProperty(key);
    }
}
