package hexlet.code.service;

import hexlet.code.dto.user.UserCreateDTO;
import hexlet.code.dto.user.UserDTO;
import hexlet.code.dto.user.UserUpdateDTO;
import hexlet.code.exception.ResourceNotFoundException;
import hexlet.code.mapper.UserMapper;
import hexlet.code.model.User;
import hexlet.code.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    private final UserMapper userMapper;

    private final PasswordEncoder passwordEncoder;

    public List<UserDTO> getAllUsers() {
        var users = userRepository.findAll();

        return users.stream()
                .map(userMapper::map)
                .toList();
    }

    public UserDTO getUserById(Long id) {

        return userMapper.map(getById(id));
    }

    public UserDTO createUser(UserCreateDTO userData) {
        var user = userMapper.map(userData);
        user.setHashedPassword(passwordEncoder.encode(userData.getPassword()));
        userRepository.save(user);

        return userMapper.map(user);
    }

    public UserDTO updateUser(Long id, UserUpdateDTO userData) {
        var user = getById(id);
        userMapper.update(userData, user);
        if (userData.getPassword() != null) {
            user.setHashedPassword(passwordEncoder.encode(userData.getPassword().get()));
        }
        userRepository.save(user);

        return userMapper.map(user);
    }

    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException(String.format("User with id '%s' not found", id));
        }

        userRepository.deleteById(id);
    }

    private User getById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("User with id '%d' not found", id)));
    }
}
