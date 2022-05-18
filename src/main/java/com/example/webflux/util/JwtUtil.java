package com.example.webflux.util;

import com.example.webflux.model.Users;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


@Component
public class JwtUtil {

    @Value("${jwt.secretKey}")
    private String secretKey;

    @Value("${jwt.timeMs}")
    private String timeMs;


    public Map<String,Object> generateJwt(Users user)
    {

        String access_token="";

        Map<String,Object> jwt = new HashMap<>();

        try{

            //Algorithm hash
            SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

            //token time
            long nowMills = System.currentTimeMillis();
            Date now = new Date(nowMills);

            //get bytes from secret key
            byte [] secretKeyBites = DatatypeConverter.parseBase64Binary(secretKey);


            //create the signature to sign the jwt
            Key sign = new SecretKeySpec(secretKeyBites,signatureAlgorithm.getJcaName());


            //Extract the user's roles



            //Create claims
            Map<String,Object> claims = new HashMap<>();

            claims.put("id",user.getId());
            claims.put("username",user.getUsername());
            claims.put("role",user.getRole());



            if(!timeMs.isEmpty()){

                long expireMs= nowMills + Long.parseLong(timeMs);

                Date expirationAt = new Date(expireMs);


                access_token = Jwts.builder()
                        .setIssuedAt(new Date(System.currentTimeMillis()))
                        .setSubject(user.getEmail())
                        .addClaims(claims)
                        .setExpiration(expirationAt)
                        .signWith(signatureAlgorithm,sign).compact();

            }


        }catch (Exception e){

        }

        jwt.put("access_token",access_token);

        return jwt;
    }


    //extract claim's information

    public Claims getAllClaimsFromToken(String token){
        return Jwts.parser().setSigningKey(DatatypeConverter.parseBase64Binary(secretKey))
                .parseClaimsJwt(token).getBody();
    }

    public String getSubject(String token){
        Claims claims = getAllClaimsFromToken(token);
        return claims.getSubject();
    }

    private  Date getExpirationDateFromToken(String token){
        Claims claims = getAllClaimsFromToken(token);
        Date expiration = claims.getExpiration();
        return expiration;
    }

    private Boolean isTokenExpired(String token)
    {
        Date expiration = this.getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    public Boolean validate(String token, UserDetails user){

        return  user.getUsername().equals(getAllClaimsFromToken(token).get("username")) && !this.isTokenExpired(token);
    }














}
