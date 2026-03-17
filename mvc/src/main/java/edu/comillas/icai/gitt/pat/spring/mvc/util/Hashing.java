package edu.comillas.icai.gitt.pat.spring.mvc.util;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.util.Base64;

@Component
public class Hashing {
    public String hash(String string){return hash(string, generateSalt());}

    public boolean compare(String string, String hashedString){
        try {
            String[] hashParts = hashedString.split((":"));
            byte[] salt = Base64.getDecoder().decode(hashParts[0]);
            return hashedString.equals(hash(string, salt));
        }catch(Exception e){
            return  false;
        }
    }
    private String hash(String string, byte[] salt) throws ResponseStatusException {
        try {
            KeySpec spec = new PBEKeySpec(string.toCharArray(), salt, 65536, 128);
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            byte[] hash = factory.generateSecret(spec).getEncoded();
            return Base64.getEncoder().encodeToString(salt) + ":" + Base64.getEncoder().encodeToString(hash);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Server not properly configured", e);
        }
    }

    private byte[] generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);
        return salt;
    }

}
