package web.internship.SODSolutions.services;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import web.internship.SODSolutions.dto.request.ReqLoginDTO;
import web.internship.SODSolutions.dto.request.ReqUserDTO;
import web.internship.SODSolutions.dto.response.ResLoginDTO;
import web.internship.SODSolutions.dto.response.ResUserDTO;
import web.internship.SODSolutions.mapper.UserMapper;
import web.internship.SODSolutions.model.Role;
import web.internship.SODSolutions.model.User;
import web.internship.SODSolutions.repository.RoleRepository;
import web.internship.SODSolutions.repository.UserRepository;
import web.internship.SODSolutions.util.SecurityUtil;
import web.internship.SODSolutions.util.error.AppException;

import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;


@Service
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
public class AuthService {

    AuthenticationManagerBuilder authenticationManagerBuilder;
    UserService userService;
    UserMapper userMapper;
    SecurityUtil securityUtil;
    PasswordEncoder passwordEncoder;
    UserRepository userRepository;
    EmailService emailService;
    RoleRepository roleRepository;

    private static final SecureRandom random = new SecureRandom();

    public static String generateOtp() {
        int otp = 100000 + random.nextInt(900000); // đảm bảo luôn có 6 chữ số
        return String.valueOf(otp);
    }

    public ResLoginDTO login(ReqLoginDTO rqLogin) throws AppException {
        UsernamePasswordAuthenticationToken loginToken = new UsernamePasswordAuthenticationToken(rqLogin.getEmail(),
                rqLogin.getPassword());

        Authentication authentication = this.authenticationManagerBuilder.getObject().authenticate(loginToken);

        SecurityContextHolder.getContext().setAuthentication(authentication);

        User userDB = userService.getUserByEmail(rqLogin.getEmail());

        if(!userDB.isActive()){
            throw new AppException("User is not active");
        }

        ResUserDTO user = userMapper.toResUserDTO(userDB);
        String accessToken = securityUtil.createAccessToken(userDB.getEmail());
        return ResLoginDTO.builder()
                .accessToken(accessToken)
                .user(user)
                .build();
    }

    public ResUserDTO register(ReqUserDTO req) throws AppException {
        User user = userService.getUserByEmail(req.getEmail());
        if (user != null) {
            throw new AppException("Email already exists");
        }
        if(!isStrongPassword(req.getPassword())) {
            throw new AppException("Passwords not strong");
        }

        String hashPassword = passwordEncoder.encode(req.getPassword());
        User newUser = userMapper.toUser(req);
        Instant codeExpired = Instant.now().plus(5, ChronoUnit.MINUTES);
        Role userRole = roleRepository.getRoleUser();
        newUser.setRole(userRole);
        newUser.setCodeExpired(codeExpired);
        newUser.setPassword(hashPassword);
        userRepository.save(newUser);

        return userMapper.toResUserDTO(newUser);
    }

    public ResLoginDTO checkCode(String email, String code) throws AppException {
        User user = this.userService.getUserByEmail(email);
        if (user == null) {
            throw new AppException("User not found");
        }
        if (user.getCodeExpired().isBefore(Instant.now())) {
            throw new AppException("Code expired");
        }
        if (!user.getCodeId().equals(code)) {
            throw new AppException("Code not match");
        }
        user.setCodeId(null);
        user.setCodeExpired(null);
        user.setActive(true);
        this.userRepository.save(user);

        ResUserDTO userDTO = userMapper.toResUserDTO(user);
        String accessToken = securityUtil.createAccessToken(user.getEmail());
        return ResLoginDTO.builder()
                .accessToken(accessToken)
                .user(userDTO)
                .build();
    }

    public void resendCode(String email) throws AppException {
        User user = this.userService.getUserByEmail(email);
        if (user == null) {
            throw new AppException("User not found");
        }
        String otp = generateOtp();
        user.setCodeId(otp);
        Instant codeExpired = Instant.now().plus(5, ChronoUnit.MINUTES);
        user.setCodeExpired(codeExpired);
        this.userRepository.save(user);

        this.emailService.sendEmailFromTemplateSync(
                user.getEmail(),
                "SODSolution - Verify your email",
                "register",
                user.getName(),
                otp);
    }

    public void sendAccountInfoEmail(String email, String rawPassword, String loginUrl) {
        User user = this.userService.getUserByEmail(email);
        if (user == null) {
            throw new AppException("User not found");
        }
        Map<String, Object> variables = new HashMap<>();
        variables.put("name", user.getName());
        variables.put("username", user.getEmail());
        variables.put("password", rawPassword);
        variables.put("loginUrl", loginUrl);

        emailService.sendEmailWithVariables(
                user.getEmail(),
                "SOD Solution - Your Project Account",
                "account",
                variables
        );
    }



    public boolean isStrongPassword(String password) {
        if (password == null || password.length() < 8) {
            return false;
        }

        boolean hasUpper = false;
        boolean hasLower = false;
        boolean hasDigit = false;
        boolean hasSpecial = false;

        for (char c : password.toCharArray()) {
            if (Character.isUpperCase(c)) {
                hasUpper = true;
            } else if (Character.isLowerCase(c)) {
                hasLower = true;
            } else if (Character.isDigit(c)) {
                hasDigit = true;
            } else if ("!@#$%^&*()-_=+[]{}|;:'\",.<>?/`~".indexOf(c) >= 0) {
                hasSpecial = true;
            }
        }

        return hasUpper && hasLower && hasDigit && hasSpecial;
    }

    public ResUserDTO getCurrentUser() throws AppException {
        String email = SecurityUtil.getCurrentUserLogin().isPresent() ? SecurityUtil.getCurrentUserLogin().get()
                : "";
        User userDB = userService.getUserByEmail(email);
        if (userDB == null) {
            throw new AppException("User not found");
        }
        ResUserDTO res = userMapper.toResUserDTO(userDB);
        return res;
    }

    public ResUserDTO changePassword(String code, String password, String email) throws AppException {
        User user = userService.getUserByEmail(email);
        if (user == null) {
            throw new AppException("User not found");
        }

        if (user.getCodeExpired().isBefore(Instant.now())) {
            throw new AppException("Code expired");
        }
        if (!user.getCodeId().equals(code)) {
            throw new AppException("Code not match");
        }
        if (!isStrongPassword(password)) {
            throw new AppException("Passwords not strong");
        }
        String hashPassword = passwordEncoder.encode(password);
        user.setPassword(hashPassword);
        this.userRepository.save(user);
        return userMapper.toResUserDTO(user);
    }
}
