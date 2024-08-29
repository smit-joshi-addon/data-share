package com.track.share.exceptions;

public class UsernameUnavailableException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public UsernameUnavailableException(String message) {
		super(message);
	}
}
