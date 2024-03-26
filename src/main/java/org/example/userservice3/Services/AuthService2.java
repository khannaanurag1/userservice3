package org.example.userservice3.Services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import org.antlr.v4.runtime.misc.Pair;
import org.example.userservice3.Clients.KafkaProducerClient;
import org.example.userservice3.Dtos.SendEmailMessageDto;
import org.example.userservice3.Dtos.UserDto;
import org.example.userservice3.Models.Session;
import org.example.userservice3.Models.SessionStatus;
import org.example.userservice3.Models.User;
import org.example.userservice3.Repositories.SessionRepository;
import org.example.userservice3.Repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

//FINAL CODE WITH JWT INCLUDING VALIDATE
@Service
public class AuthService2 {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SessionRepository sessionRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private SecretKey secretKey;

    @Autowired
    private KafkaProducerClient kafkaProducerClient;

    @Autowired
    private ObjectMapper objectMapper;


    public User signUp(String email, String password) {
        User user = new User();
        user.setEmail(email);
        user.setPassword(bCryptPasswordEncoder.encode(password));
        User savedUser = userRepository.save(user);

        // IN KAFKA INTEGRATION , CLASS 27
        UserDto userDto = new UserDto();
        userDto.setEmail(user.getEmail());

        try {
            //kafkaProducerClient.sendMessage("signUp",objectMapper.writeValueAsString(userDto));
            SendEmailMessageDto sendEmailMessageDto = new SendEmailMessageDto();
            sendEmailMessageDto.setTo(email);
            sendEmailMessageDto.setBody("Thanks for creating an account, We look forward to you growing.");
            sendEmailMessageDto.setSubject("Welcome to ");
            kafkaProducerClient.sendMessage("sendEmail",objectMapper.writeValueAsString(sendEmailMessageDto));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return savedUser;
    }

    public Pair<User, MultiValueMap<String,String>> login(String email, String password) {
        Optional<User> userOptional = userRepository.findByEmail(email);

        if(userOptional.isEmpty()) {
            return null;
        }

        User user = userOptional.get();
        if(!bCryptPasswordEncoder.matches(password,user.getPassword())) {
            return null;
        }

        Map<String,Object> jwtData = new HashMap<>();
        jwtData.put("email",user.getEmail());
        jwtData.put("roles",user.getRoles());
        long nowInMillis = System.currentTimeMillis();
        jwtData.put("expiryTime",new Date(nowInMillis+10000));
        jwtData.put("createdAt",new Date(nowInMillis));
        String token = Jwts.builder().claims(jwtData).signWith(secretKey).compact();

        Session session = new Session();
        session.setSessionStatus(SessionStatus.ACTIVE);
        session.setToken(token);
        session.setUser(user);
        sessionRepository.save(session);

        MultiValueMap<String,String> headers = new LinkedMultiValueMap<>();
        headers.add(HttpHeaders.SET_COOKIE,token);
        return new Pair<>(user, headers);
    }



    public Boolean validateToken(String token, Long id) {
        Optional<Session> sessionOptional = sessionRepository.findByTokenAndUser_Id(token,id);
        if(sessionOptional.isEmpty()) {
            System.out.println("No Valid session found");
           return false;
        }

        JwtParser jwtParser = Jwts.parser().verifyWith(secretKey).build();
        Claims claims = jwtParser.parseSignedClaims(token).getPayload();
        System.out.println(claims);

        //Validation regarding Claims for email and other things like Expiry
        Optional<User> user = userRepository.findById(id);
        if(user.isPresent()) {
            String email = user.get().getEmail();
            if(!email.equals(claims.get("email"))) {
                System.out.println(email);
                System.out.println(claims.get("email"));
                System.out.println("Input Email Claim didn't match");
               return false;
            }
        }

        return true;
    }
}





//public Pair<User, MultiValueMap<String,String>> login(String email, String password) {
//    //MacAlgorithm algorithm = Jwts.SIG.HS256;
//    //SecretKey secretKey = algorithm.key().build();
//    Map<String,Object> jwtData = new HashMap<>();
//    jwtData.put("email","abc");
//    long nowInMillis = System.currentTimeMillis();
//    jwtData.put("expiryTime",new Date(nowInMillis+10000));
//    jwtData.put("createdAt",new Date(nowInMillis));
//    String token = Jwts.builder().claims(jwtData).signWith(secretKey).compact();
//    System.out.println(token);
//    System.out.println(secretKey.toString());
//    System.out.println(secretKey.getEncoded());
//
//    //JwtParser jwtParser = Jwts.parser().verifyWith(secretKey).build();
//    //Claims claims = jwtParser.parseSignedClaims(token).getPayload();
//    //System.out.println(claims);
//
//    //User user = new User();
//    MultiValueMap<String,String> headers = new LinkedMultiValueMap<>();
//    headers.add(HttpHeaders.SET_COOKIE,token);
//    return new Pair<>(new User(), headers);
//}


//    public SessionStatus validateToken(String token, Long id) {
//        System.out.println(secretKey.toString());
//        System.out.println(secretKey.getEncoded());
//        JwtParser jwtParser = Jwts.parser().verifyWith(secretKey).build();
//        Claims claims = jwtParser.parseSignedClaims(token).getPayload();
//        System.out.println(claims);
//        return SessionStatus.ACTIVE;
//    }