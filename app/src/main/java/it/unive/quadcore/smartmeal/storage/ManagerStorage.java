package it.unive.quadcore.smartmeal.storage;


import android.content.SharedPreferences;


import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Set;
import java.util.TreeSet;


import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

import it.unive.quadcore.smartmeal.model.ManagerTable;


public final class ManagerStorage extends Storage {

    // TODO : fare SharedPreference apposito per ManagerStorage
    //private static SharedPreferences managerSharedPref = null;

    /**
     * Rende non instanziabile questa classe.
     */
    private ManagerStorage() {}

    // TODO : cambiare

    // Genera i tavoli di default
    private static Set<String> generateTablesStrings(){

        Set<String> tables = new TreeSet<>();
        char supp = 'A';
        while(supp<'Z'+1){
            for(int i=0;i<=9;i++)
                tables.add(""+supp+i);
            supp+=1;
        }

        return tables;
    }

    // Possibilità di non tenere i tavoli in memoria secondaria ma generarli e basta
    public static Set<ManagerTable> getTables() {
        //throw new UnsupportedOperationException("Not implemented yet");
       /* ManagerTable t1 = new ManagerTable("A1");
        ManagerTable t2 = new ManagerTable("A2");
        ManagerTable t3 = new ManagerTable("B7");
        ManagerTable t4 = new ManagerTable("C1");

        Set<ManagerTable> tables = new TreeSet<>();
        tables.add(t1);
        tables.add(t2);
        tables.add(t3);
        tables.add(t4);*/

        if(!initialized)
            throw new StorageException("The storage hasn't been initialize yet");

        // Preference non esistente. Primo accesso a tale preference. Scrivo valore di deafult
        if(!sharedPreferences.contains("Tables")){ // TODO : rimpiazzare con stringa di res
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putStringSet("Tables",generateTablesStrings());
            editor.apply();
        }
        Set<String> tablesString = sharedPreferences.getStringSet("Tables",new TreeSet<>());

        Set<ManagerTable> tables = new TreeSet<>();
        for(String tableId : tablesString){
            ManagerTable table = new ManagerTable(tableId);
            tables.add(table);
        }

        return tables;
    }

    // Genera il numero di default
    private static int generateMaxNotificationNumber(){
        return 5;
    }

    // Ritorna il numero massimo di notifiche in coda di uno stesso utente
    // Possibilità di non tenere tale numero in memoria secondaria ma generarlo e basta
    public static int getMaxNotificationNumber(){
        if(!initialized)
            throw new StorageException("The storage hasn't been initialize yet");

        // Preference non esistente. Primo accesso a tale preference. Scrivo valore di deafult
        if(!sharedPreferences.contains("MaxNotificationNumber")){ // TODO : rimpiazzare con stringa di res
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt("MaxNotificationNumber",generateMaxNotificationNumber());
            editor.apply();
        }

        return sharedPreferences.getInt("MaxNotificationNumber",generateMaxNotificationNumber());
    }

    private static String getEncryptedPassword(){
        return "PaSssWord!9";
    }

    private static String encryptPassword(String password){

        byte[] plaintext = password.getBytes();

        KeyGenerator keygen = null;
        try {
            keygen = KeyGenerator.getInstance("AES");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        keygen.init(256);
        SecretKey key = keygen.generateKey();
        Cipher cipher = null;
        try {
            cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        }
        try {
            cipher.init(Cipher.ENCRYPT_MODE, key);
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }
        try {
            byte[] ciphertext = cipher.doFinal(plaintext);
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        }
        byte[] iv = cipher.getIV();

        return new String(iv, StandardCharsets.UTF_8);

        /*
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        md.update(password.getBytes());
        byte[] digest = md.digest();
        return new String(digest, StandardCharsets.UTF_8); */
    }

    public static boolean checkPassword(String password){

        String realDecryptedPassword = getEncryptedPassword();

        String decryptedPassword = encryptPassword(password);

        return realDecryptedPassword.equals(decryptedPassword);
    }
}
