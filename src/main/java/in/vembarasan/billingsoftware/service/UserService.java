package in.vembarasan.billingsoftware.service;

import in.vembarasan.billingsoftware.io.UserRequest;
import in.vembarasan.billingsoftware.io.UserResponse;

import java.util.List;

public interface UserService {

    UserResponse createUser(UserRequest request);

    String getUserRole(String email);

    List<UserResponse> readUsers();

    void deleteUser(String id);
    String updateUser(String id, UserRequest request) ;

}
