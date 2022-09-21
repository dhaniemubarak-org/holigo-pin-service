package id.holigo.services.holigopinservice;

import id.holigo.services.holigopinservice.components.Decryption;
import id.holigo.services.holigopinservice.components.Encryption;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class HoligoPinServiceApplication {

    public static void main(String[] args) {
        Encryption en = new Encryption();
        String encryptedWord = null;
        try {
            encryptedWord = en.encrypt("2222");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        System.out.println("Encrypted word is : " + encryptedWord);
        Decryption de = new Decryption();
        try {
            System.out.println("Decrypted word is : " + de.decrypt(encryptedWord));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        SpringApplication.run(HoligoPinServiceApplication.class, args);
    }

}
