package com.cyber.controller;

import com.cyber.annotation.DefaultExceptionMessage;
import com.cyber.dto.UserDTO;
import com.cyber.entity.ConfirmationToken;
import com.cyber.entity.ResponseWrapper;
import com.cyber.entity.User;
import com.cyber.entity.AuthenticationRequest;
import com.cyber.exception.TicketNGProjectException;
import com.cyber.util.MapperUtil;
import com.cyber.service.ConfirmationTokenService;
import com.cyber.service.UserService;
import com.cyber.util.JWTUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;

@RestController
@Tag(name = "Authentication Controller",description = "Authenticate API")
public class LoginController {

	private AuthenticationManager authenticationManager;
	private UserService userService;
	private MapperUtil mapperUtil;
	private JWTUtil jwtUtil;
	private ConfirmationTokenService confirmationTokenService;

	public LoginController(AuthenticationManager authenticationManager, UserService userService, MapperUtil mapperUtil, JWTUtil jwtUtil, ConfirmationTokenService confirmationTokenService) {
		this.authenticationManager = authenticationManager;
		this.userService = userService;
		this.mapperUtil = mapperUtil;
		this.jwtUtil = jwtUtil;
		this.confirmationTokenService = confirmationTokenService;
	}

	@PostMapping("/authenticate")
	@Operation(summary = "Login to application")
	@DefaultExceptionMessage(defaultMessage = "Bad Credentials !!!")
	public ResponseEntity<ResponseWrapper> doLogin(@RequestBody AuthenticationRequest authenticationRequest) throws TicketNGProjectException, AccessDeniedException {

		String password = authenticationRequest.getPassword();
		String username = authenticationRequest.getUsername();

		UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(username,password);
		authenticationManager.authenticate(authentication);

		UserDTO foundUser = userService.findByUserName(username);
		User convertedUser = mapperUtil.convert(foundUser,new User());

		if(!foundUser.isEnabled()){
			throw new TicketNGProjectException("Please verify your user !!");
		}

		String jwtToken = jwtUtil.generateToken(convertedUser);

		return ResponseEntity.ok(new ResponseWrapper("Login Successful",jwtToken));
	}

	@GetMapping("/confirmation")
	@Operation(summary = "Confirm Account")
	@DefaultExceptionMessage(defaultMessage = "Failed to confirm email, try again !!!")
	public ResponseEntity<ResponseWrapper> confirmEmail(@RequestParam("token") String token) throws TicketNGProjectException {
		ConfirmationToken confirmationToken = confirmationTokenService.readByToken(token);
		UserDTO confirmUser = userService.confirm(confirmationToken.getUser());
		confirmationTokenService.delete(confirmationToken);

		return ResponseEntity.ok(new ResponseWrapper("User has been confirmed",confirmUser));
	}
}
