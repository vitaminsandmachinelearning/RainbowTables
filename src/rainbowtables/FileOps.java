
package rainbowtables;

import java.io.*;
import java.util.HashMap;

public class FileOps {
    
    String file;
    
    public FileOps(String filename)
    {
        file = filename + ".txt";
    }
    
    public HashMap<String, String> load() throws FileNotFoundException
    {
        //create a hashmap to return and populate it with the contents of a file containing a stored hashmap
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
        //write out every value in the hashmap to a file with the format "final_hash:initial_plaintext"
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
