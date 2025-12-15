package in.vembarasan.billingsoftware.service.impl;

import in.vembarasan.billingsoftware.Exception.ApiException;
import in.vembarasan.billingsoftware.entity.UserEntity;
import in.vembarasan.billingsoftware.io.UserRequest;
import in.vembarasan.billingsoftware.io.UserResponse;
import in.vembarasan.billingsoftware.repository.UserRepository;
import in.vembarasan.billingsoftware.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserResponse createUser(UserRequest request) {

        String email = (request.getEmail() != null && !request.getEmail().trim().isEmpty())
                ? request.getEmail().trim()
                : null;

        // Check email uniqueness only if email is provided
        if (email != null) {
            if (userRepository.existsByEmail(email)) {
                throw new ApiException("Email already exists: " + email, HttpStatus.CONFLICT);
            }
        }



        UserEntity newUser = convertToEntity(request);
        newUser = userRepository.save(newUser);
        return convertToResponse(newUser);
    }

    private UserResponse convertToResponse(UserEntity newUser) {
        return UserResponse.builder()
                .name(newUser.getName())
                .email(newUser.getEmail())
                .userId(newUser.getUserId())
                .createdAt(newUser.getCreatedAt())
                .updatedAt(newUser.getUpdatedAt())
                .role(newUser.getRole())
                .build();
    }

    private UserEntity convertToEntity(UserRequest request) {
        return UserEntity.builder()
                .userId(UUID.randomUUID().toString())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole().toUpperCase())
                .name(request.getName())
                .build();
    }

    @Override
    public String getUserRole(String email) {
        UserEntity existingUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new ApiException("User not found for the email: "+email, HttpStatus.NOT_FOUND ) );
        return existingUser.getRole();
    }

    @Override
    public List<UserResponse> readUsers() {
        return userRepository.findAll()
                .stream()
                .map(user -> convertToResponse(user))
                .collect(Collectors.toList());
    }

    @Override
    public String updateUser(String id, UserRequest request) {
        UserEntity existingUser = userRepository.findByUserId(id)
                .orElseThrow(() -> new ApiException("User not found", HttpStatus.NOT_FOUND));

        System.out.println(id);


        Optional<UserEntity> user =  userRepository.findByUserId(id);
                  UserEntity  updateU=  user.get();
                  updateU.setName(request.getName());
                  updateU.setEmail(request.getEmail());
                  updateU.setPassword(passwordEncoder.encode(request.getPassword()));
                  updateU.setRole(request.getRole());
                  userRepository.save(updateU);

        return "User updated successfully!.";
    }

    @Override
    public void deleteUser(String id) {
        UserEntity existingUser = userRepository.findByUserId(id)
                .orElseThrow(() -> new ApiException("User not found", HttpStatus.NOT_FOUND));
        userRepository.delete(existingUser);
    }
}
