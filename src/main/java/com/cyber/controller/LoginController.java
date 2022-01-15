package com.cyber.controller;

import com.cyber.annotation.DefaultExceptionMessage;
import com.cyber.dto.MailDTO;
import com.cyber.dto.UserDTO;
import com.cyber.entity.ConfirmationToken;
import com.cyber.entity.ResponseWrapper;
import com.cyber.entity.User;
import com.cyber.entity.common.AuthenticationRequest;
import com.cyber.exception.TicketNGProjectException;
import com.cyber.mapper.MapperUtil;
import com.cyber.service.ConfirmationTokenService;
import com.cyber.service.UserService;
import com.cyber.util.JWTUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;
import java.util.List;

@RestController
@Tag(name = "Authentication Controller",description = "Authenticate API")
public class LoginController {

	@Value("${app.local-url}")
	private String BASE_URL;

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

	@PostMapping("/create-user")
	@Operation(summary = "Create new Account")
	@DefaultExceptionMessage(defaultMessage = "Sth went wrong with email, try again !!!")
	private ResponseEntity<ResponseWrapper> doRegister(@RequestBody UserDTO userDTO) throws TicketNGProjectException {
		//create user, save it in DB, and then get it as dto
		UserDTO createdUser = userService.save(userDTO);
		sendEmail(createEmail(createdUser));

		return ResponseEntity.ok(new ResponseWrapper("User has been created",createdUser));
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

	private MailDTO createEmail(UserDTO userDTO){

		User user = mapperUtil.convert(userDTO,new User());
		ConfirmationToken confirmationToken = new ConfirmationToken(user);
		confirmationToken.setIsDeleted(false);

		ConfirmationToken createdConfirmationToken = confirmationTokenService.save(confirmationToken);

		return MailDTO
				.builder()
				.emailTo(user.getUserName())
				.token(createdConfirmationToken.getToken())
				.subject("Confirm Registration")
				.message("To confirm your account, please click here: ")
				.url(BASE_URL + "/confirmation?token=")
				.build();
	}

	private void sendEmail(MailDTO mailDTO){

		SimpleMailMessage mailMessage = new SimpleMailMessage();

		mailMessage.setTo(mailDTO.getEmailTo());
		mailMessage.setSubject(mailDTO.getSubject());
		mailMessage.setText(mailDTO.getMessage() + mailDTO.getUrl() + mailDTO.getToken());

		confirmationTokenService.sendEmail(mailMessage);
	}

}
