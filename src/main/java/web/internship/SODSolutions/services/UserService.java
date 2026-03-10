package web.internship.SODSolutions.services;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import web.internship.SODSolutions.dto.request.ReqUpdateUserDTO;
import web.internship.SODSolutions.dto.request.ReqUserDTO;
import web.internship.SODSolutions.dto.response.ResUserDTO;
import web.internship.SODSolutions.mapper.UserMapper;
import web.internship.SODSolutions.model.User;
import web.internship.SODSolutions.repository.RoleRepository;
import web.internship.SODSolutions.repository.UserRepository;
import web.internship.SODSolutions.util.SecurityUtil;
import web.internship.SODSolutions.util.error.AppException;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;


@Service
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
public class UserService {
    UserRepository userRepository;
    PasswordEncoder passwordEncoder;
    UserMapper userMapper;
    RoleRepository roleRepository;

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }

    public List<ResUserDTO> getAllUsers() {

        return userMapper.toResListUserDTO(userRepository.getUserByRole_id());
    }

    public ResUserDTO createUser(ReqUserDTO rqUser) {
        User user = getUserByEmail(rqUser.getEmail());
        if (user != null) {
            throw new AppException("Email already exists");
        }

        String hashPassword = passwordEncoder.encode(rqUser.getPassword());
        User newUser = userMapper.toUser(rqUser);
        newUser.setPassword(hashPassword);


        newUser.setRole(roleRepository.getRoleUser());

        Instant codeExpired = Instant.now().plus(5, ChronoUnit.MINUTES);
        newUser.setCodeExpired(codeExpired);

        newUser = userRepository.save(newUser);

        return userMapper.toResUserDTO(newUser);
    }

    public ResUserDTO updateUser(ReqUpdateUserDTO rqUser) throws AppException {
        String email = SecurityUtil.getCurrentUserLogin().isPresent() ? SecurityUtil.getCurrentUserLogin().get()
                : "";
        User user = getUserByEmail(email);
        if (user == null) {
            throw new AppException("User not found");
        }

        if (rqUser.getName() != null) {
            user.setName(rqUser.getName());
        }
        if (rqUser.getPhone() != null) {
            user.setPhone(rqUser.getPhone());
        }
        if(rqUser.getAddress() != null) {
            user.setAddress(rqUser.getAddress());
        }
        if(rqUser.getAvatar() != null) {
            user.setAvatar(rqUser.getAvatar());
        }
        if (rqUser.getCompanyName() != null) {
            user.setCompanyName(rqUser.getCompanyName());
        }

        user = userRepository.save(user);
        return userMapper.toResUserDTO(user);
    }

    public ResUserDTO changePassword(String oldPassword, String newPassword) throws AppException {
        String email = SecurityUtil.getCurrentUserLogin().isPresent() ? SecurityUtil.getCurrentUserLogin().get()
                : "";
        User user = getUserByEmail(email);
        if (user == null) {
            throw new AppException("User not found");
        }

        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new AppException("Wrong password");
        }

        if(!isStrongPassword(newPassword)){
            throw new AppException("Password is too weak, change to a stronger password");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        user = userRepository.save(user);
        return userMapper.toResUserDTO(user);
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
}