package com.example.webflux.security;

import de.mkammerer.argon2.Argon2;
import de.mkammerer.argon2.Argon2Factory;


public class PasswordEncoder {



    public static String generateHash(String password){

        Argon2 argon2 = Argon2Factory.create(Argon2Factory.Argon2Types.ARGON2id);

        return argon2.hash(5,1024*1,2,password);
    }

    public static boolean isPasswordEqual(String userPassword, String password){

        Argon2 argon2 = Argon2Factory.create(Argon2Factory.Argon2Types.ARGON2id);
        return argon2.verify(userPassword,password);
    }





}
