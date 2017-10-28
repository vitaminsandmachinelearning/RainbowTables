/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rainbowtables;

import java.security.*;
import java.util.Hashtable;

/**
 *
 * @author Jake
 */
public class RainbowTables {

    public static void main(String[] args) {
        
    }
    void reduce(String hash)
    {
        
    }
    static String sha1(String password) throws NoSuchAlgorithmException
    {
        MessageDigest md = MessageDigest.getInstance("SHA1");
        byte[] bytes = md.digest(password.getBytes());
        String toReturn = "";
        for(int i = 0; i < bytes.length; i++)
            toReturn += Integer.toString((bytes[i]&0xff) + 0x100, 16).substring(1);
        return toReturn;
    }
    Hashtable<String, String> GenerateTable()
    {
        Hashtable<String, String> rainbow = new Hashtable<>();
        rainbow.put("a", "f");
        return rainbow;
    }
}
