package service;

import dto.request.LoginRequest;
import dto.response.LoginResponse;

public interface AuthService {

    LoginResponse iniciarSesion(LoginRequest request);

}
