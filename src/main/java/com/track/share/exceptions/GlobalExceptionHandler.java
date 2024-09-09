package com.track.share.exceptions;

import java.time.ZonedDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.HandlerMethod;

import jakarta.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ResponseStatus(HttpStatus.CONFLICT)
	@ExceptionHandler(value = UsernameUnavailableException.class)
	public ApiErrorResponse handleUsernameUnavailableException(UsernameUnavailableException ex,
			HttpServletRequest request, HandlerMethod method) {
		return new ApiErrorResponse(HttpStatus.CONFLICT, ex.getMessage(), request.getRequestURI(),
				method.getMethod().getName(), ZonedDateTime.now());
	}

	@ResponseStatus(HttpStatus.NOT_FOUND)
	@ExceptionHandler(value = NotFoundException.class)
	public ApiErrorResponse handleNotFoundException(NotFoundException ex, HttpServletRequest request,
			HandlerMethod method) {

		return new ApiErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage(), request.getRequestURI(),
				method.getMethod().getName(), ZonedDateTime.now());
	}

	@ResponseStatus(HttpStatus.UNAUTHORIZED)
	@ExceptionHandler(value = UnauthorizedException.class)
	public ApiErrorResponse unauhorizedUser(UnauthorizedException ex, HttpServletRequest request,
			HandlerMethod method) {
		return new ApiErrorResponse(HttpStatus.UNAUTHORIZED, ex.getMessage(), request.getRequestURI(),
				method.getMethod().getName(), ZonedDateTime.now());
	}
}
