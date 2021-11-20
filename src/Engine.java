import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

public class Engine {

    Random r;
    private static SecretKeySpec secretKey;
    private static byte[] key;

    public Engine(){
        r = new Random();
    }

    public String generatePassword(int digits, boolean num, boolean letter, boolean special){
        String result = "";

        for(int i = 0; i < digits; i++){
            char next = randomChar(num, letter, special);
            result = result + next;
        }
        return result;
    }

    private char randomChar(boolean num, boolean letter, boolean special){
        int next = r.nextInt(4);
        int nextLetter = 0;
        boolean found = false;
        while(!found) {
            if (next == 0 && num) {
                //numbers
                nextLetter = r.nextInt(9) + 48;
                found = true;
            } else if (((next == 1) || (next == 2)) && letter) {
                //letters
                nextLetter = r.nextInt(25) + 97;
                //upper case
                if (r.nextInt(2) == 1) {
                    nextLetter = nextLetter - 32;
                }
                found = true;
            } else if ((next == 3) && special) {
                //special characters
                nextLetter = r.nextInt(6) + 33;
                if (nextLetter == 34) nextLetter = 63;
                found = true;
            } else {
                next = r.nextInt(4);
            }
        }
        char result = (char) nextLetter;
        return result;
    }

    public void writeFile(String input, File filename, String serviceName, String password) throws Exception {

        FileWriter writer;

        File newFile = filename;
        if(newFile.exists()){
            writer = new FileWriter(filename,true);
        }else {
            writer = new FileWriter(filename);
                newFile.createNewFile();
        }
        if(serviceName.equals("")){
            serviceName = "password";
        }
        serviceName = encrypt(serviceName, password);
        input = encrypt(input, password);

        writer.write(serviceName + "\n" + input +"\n");
        writer.close();
    }

    public String[] openFile(File f, String passcode) throws Exception {
        Scanner in = new Scanner(f);
        ArrayList<String> lines = new ArrayList<String>();
        while (in.hasNext()) lines.add(in.next());

        String[] decrypted = new String[lines.size()];

        for (int i = 0; i < decrypted.length; i++) {
            String text = decrypt(lines.get(i), passcode).trim();
            decrypted[i] = text;
        }

        return decrypted;
    }

    public static void setKey(String myKey)
    {
        MessageDigest sha = null;
        try {
            key = myKey.getBytes("UTF-8");
            sha = MessageDigest.getInstance("SHA-1");
            key = sha.digest(key);
            key = Arrays.copyOf(key, 16);
            secretKey = new SecretKeySpec(key, "AES");
        }
        catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public static String encrypt(String strToEncrypt, String secret)
    {
        try
        {
            setKey(secret);
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            return Base64.getEncoder().encodeToString(cipher.doFinal(strToEncrypt.getBytes("UTF-8")));
        }
        catch (Exception e)
        {
            System.out.println("Error while encrypting: " + e.toString());
        }
        return null;
    }

    public static String decrypt(String strToDecrypt, String secret)
    {
        try
        {
            setKey(secret);
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            return new String(cipher.doFinal(Base64.getDecoder().decode(strToDecrypt)));
        }
        catch (Exception e)
        {
            System.out.println("Error while decrypting: " + e.toString());
        }
        return null;
    }
}

