package com.ncinga.chatservice.service.impl;

import com.ncinga.chatservice.service.NewPasswordService;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Random;

@Service
public class NewPasswordServiceImpl implements NewPasswordService {

    private static final String UPPERCASE = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String LOWERCASE = "abcdefghijklmnopqrstuvwxyz";
    private static final String DIGITS = "0123456789";
    private static final String SPECIAL_CHARACTERS = "!@#$%^&*()-_=+<>?";
    private static final String ALL_CHARACTERS = UPPERCASE + LOWERCASE + DIGITS + SPECIAL_CHARACTERS;
    private static final int Length = 8;

    private static final Random random = new SecureRandom();

       @Override
        public String generatePassword(){

           StringBuilder password = new StringBuilder(Length);

           password.append(UPPERCASE.charAt(random.nextInt(UPPERCASE.length())));
           password.append(LOWERCASE.charAt(random.nextInt(LOWERCASE.length())));
           password.append(DIGITS.charAt(random.nextInt(DIGITS.length())));
           password.append(SPECIAL_CHARACTERS.charAt(random.nextInt(SPECIAL_CHARACTERS.length())));

           for (int i = 4; i < Length; i++) {
               password.append(ALL_CHARACTERS.charAt(random.nextInt(ALL_CHARACTERS.length())));
           }
           return shuffleString(password.toString());
        }

    private String shuffleString(String input) {
        char[] characters = input.toCharArray();
        for (int i = characters.length - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            char temp = characters[i];
            characters[i] = characters[j];
            characters[j] = temp;
        }
        return new String(characters);
    }
}

