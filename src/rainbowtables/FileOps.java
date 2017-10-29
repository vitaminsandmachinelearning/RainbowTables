
package rainbowtables;

import java.io.*;
import java.util.HashMap;

public class FileOps {
    
    String file;
    
    public FileOps(String filename)
    {
        setFile(filename);
    }
    
    public void setFile(String filename)
    {
        file = filename + ".txt";
    }
    
    public HashMap<String, String> load() throws FileNotFoundException
    {
        HashMap<String, String> hm = new HashMap<>();
        String[] x = new String[2];
        try{
            BufferedReader r;
            r = new BufferedReader(new FileReader(file));
            while((x[0] = r.readLine()) != null)
            {
                x = x[0].split(":");
                hm.put(x[0], x[1]);
            }
        }catch(Exception e){System.out.println(e);}
        return hm;
    }
    
    public void save(HashMap hm) throws UnsupportedEncodingException, IOException
    {
        try (Writer writer = new BufferedWriter(new OutputStreamWriter( new FileOutputStream(file), "utf-8"))) 
        {
            hm.keySet().forEach((key) -> 
            {
                try {
                    writer.write(key + ":" + hm.get(key) + "\n");
                } catch (Exception e) {System.out.println(e);}
            });
        }
    }
}
