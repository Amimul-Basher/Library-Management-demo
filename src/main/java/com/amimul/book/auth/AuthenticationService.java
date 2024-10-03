package com.amimul.book.auth;

import com.amimul.book.email.EmailService;
import com.amimul.book.email.EmailTemplateName;
import com.amimul.book.role.RoleRepository;
import com.amimul.book.security.JwtService;
import com.amimul.book.user.Token;
import com.amimul.book.user.TokenRepository;
import com.amimul.book.user.User;
import com.amimul.book.user.UserRepository;

import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final EmailService emailService;
    private final JwtService jwtService;

    private final AuthenticationManager authenticationManager;

    @Value("${application.mailing.frontend.activation-url}")
    private String activationUrl;

    public void register(RegistrationRequest request) throws MessagingException {
        //"USER" is the default role
        var userRole = roleRepository.findByName("USER")

                //todo --> better acception handling
                .orElseThrow(() -> new IllegalArgumentException("Role User wasn't initialized"));
        var user = User.builder()
                .firstname(request.getFirstname())
                .lastname(request.getLastname())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .accountLocked(false)
                .accountEnabled(false)
                .roles(List.of(userRole))
                .build();
        userRepository.save(user);
        sendValidationEmail(user);
    }

    private void sendValidationEmail(User user) throws MessagingException {
        var newToken=generateAndSendActivationToken(user);
        emailService.sendEmail(user.getEmail(),
                user.getFullName(),
                EmailTemplateName.ACTIVATE_ACCOUNT,
                activationUrl,
                newToken,
                "Account Activation");
    }
    private String generateAndSendActivationToken(User user) {
        //This is the length of activation code
        String activationCode = generateActivationCode(6);

        //Token creation for the user
        var token = Token.builder()
                .token(activationCode)
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusMinutes(15))
                .user(user)
                .build();
        //Save token in the database

        tokenRepository.save(token);
        return activationCode;
    }

    private String generateActivationCode(int length) {
        String characters = "0123456789";
        StringBuilder activationCode = new StringBuilder();
        //Secure Random Class is used to make the randomization more effective and Standard
        SecureRandom secureRandom = new SecureRandom();
        for(int i = 0; i < length; i++) {
            int randomIndex = secureRandom.nextInt(characters.length());
            activationCode.append(characters.charAt(randomIndex));
        }
        return activationCode.toString();
    }

    public AuthenticationResponse authenticate(@Valid AuthenticationRequest request) {
        var auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(), request.getPassword()
                )
        );
        var claims = new HashMap<String, Object>();

        //Don't need to fetch again from the database. Rather we will use auth.
        //auth gets the user object from authenticationManager
        var user = ((User)auth.getPrincipal()); //Is it type casting? **
        claims.put("fullName", user.getFullName());
        var jwtToken = jwtService.generateToken(claims, user);

        return AuthenticationResponse.builder()
                .token(jwtToken).build();
    }
    @Transactional
    public void activateAccount(String token) throws MessagingException {
        //Get the token from database to validate
        Token savedToken = tokenRepository.findByToken(token)
                // todo --> exception has to be defined
                .orElseThrow(()-> new IllegalArgumentException("Invalid token"));
        //Validate the token
        if(LocalDateTime.now().isAfter(savedToken.getExpiresAt())) {
            sendValidationEmail(savedToken.getUser());
            throw new RuntimeException("Token is expired. A new token has been sent...");
        }
        //get the user from that token and enable the user

        //While getting user from token we can verify with database whether the user exist or not
        var user = userRepository.findById(savedToken.getUser().getId())
                .orElseThrow(()-> new UsernameNotFoundException("User not found"));
        user.setAccountEnabled(true);
        userRepository.save(user);

        //update the token validation time
        savedToken.setValidatedAt(LocalDateTime.now());
        //save the updated token
        tokenRepository.save(savedToken);
    }
}



//package com.amimul.book.auth;
//
//import com.amimul.book.email.EmailService;
//import com.amimul.book.email.EmailTemplateName;
//import com.amimul.book.role.RoleRepository;
//import com.amimul.book.security.JwtService;
//import com.amimul.book.user.Token;
//import com.amimul.book.user.TokenRepository;
//import com.amimul.book.user.User;
//import com.amimul.book.user.UserRepository;
//import jakarta.mail.MessagingException;
//import lombok.RequiredArgsConstructor;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.userdetails.UsernameNotFoundException;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.security.SecureRandom;
//import java.time.LocalDateTime;
//import java.util.HashMap;
//import java.util.List;
//
//@Service
//@RequiredArgsConstructor
//public class AuthenticationService {
//
//    private final UserRepository userRepository;
//    private final PasswordEncoder passwordEncoder;
//    private final JwtService jwtService;
//    private final AuthenticationManager authenticationManager;
//    private final RoleRepository roleRepository;
//    private final EmailService emailService;
//    private final TokenRepository tokenRepository;
//
//    @Value("${application.mailing.frontend.activation-url}")
//    private String activationUrl;
//
//    public void register(RegistrationRequest request) throws MessagingException {
//        var userRole = roleRepository.findByName("USER")
//                // todo - better exception handling
//                .orElseThrow(() -> new IllegalStateException("ROLE USER was not initiated"));
//        var user = User.builder()
//                .firstname(request.getFirstName())
//                .lastname(request.getLastname())
//                .email(request.getEmail())
//                .password(passwordEncoder.encode(request.getPassword()))
//                .accountLocked(false)
//                .accountEnabled(false)
//                .roles(List.of(userRole))
//                .build();
//        //userRepository.save(user);
//        sendValidationEmail(user);
//    }
//
////    public AuthenticationResponse authenticate(AuthenticationRequest request) {
////        var auth = authenticationManager.authenticate(
////                new UsernamePasswordAuthenticationToken(
////                        request.getEmail(),
////                        request.getPassword()
////                )
////        );
////
////        var claims = new HashMap<String, Object>();
////        var user = ((User) auth.getPrincipal());
////        claims.put("fullName", user.getFullName());
////
////        var jwtToken = jwtService.generateToken(claims, (User) auth.getPrincipal());
////        return AuthenticationResponse.builder()
////                .token(jwtToken)
////                .build();
////    }
////
////    @Transactional
////    public void activateAccount(String token) throws MessagingException {
////        Token savedToken = tokenRepository.findByToken(token)
////                // todo exception has to be defined
////                .orElseThrow(() -> new RuntimeException("Invalid token"));
////        if (LocalDateTime.now().isAfter(savedToken.getExpiresAt())) {
////            sendValidationEmail(savedToken.getUser());
////            throw new RuntimeException("Activation token has expired. A new token has been send to the same email address");
////        }
////
////        var user = userRepository.findById(savedToken.getUser().getId())
////                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
////        user.setEnabled(true);
////        userRepository.save(user);
////
////        savedToken.setValidatedAt(LocalDateTime.now());
////        tokenRepository.save(savedToken);
////    }
//
//    private String generateAndSaveActivationToken(User user) {
//        // Generate a token
//        String generatedToken = generateActivationCode(6);
//        var token = Token.builder()
//                .token(generatedToken)
//                .createdAt(LocalDateTime.now())
//                .expiresAt(LocalDateTime.now().plusMinutes(15))
//                .user(user)
//                .build();
//        tokenRepository.save(token);
//
//        return generatedToken;
//    }
//
//    private void sendValidationEmail(User user) throws MessagingException {
//        var newToken = generateAndSaveActivationToken(user);
//
//        emailService.sendEmail(
//                user.getEmail(),
//                user.getFullName(),
//                EmailTemplateName.ACTIVATE_ACCOUNT,
//                activationUrl,
//                newToken,
//                "Account activation"
//                );
//    }
//
//    private String generateActivationCode(int length) {
//        String characters = "0123456789";
//        StringBuilder codeBuilder = new StringBuilder();
//
//        SecureRandom secureRandom = new SecureRandom();
//
//        for (int i = 0; i < length; i++) {
//            int randomIndex = secureRandom.nextInt(characters.length());
//            codeBuilder.append(characters.charAt(randomIndex));
//        }
//
//        return codeBuilder.toString();
//    }
//}