package com.cepheid.cloud.skel.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/***
 * This is a ResourceNotFoundException class which extends from Exception, it
 * shall be used when the given item is not found.
 * 
 * @author Wei Wang
 * @version 1.0
 */
@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends Exception {

	private static final long serialVersionUID = 1L;

	/**
	 * Default constructor for ResourceNotFoundException with a given message.
	 * 
	 * @param message Message to be generated for the exception.
	 */
	public ResourceNotFoundException(String message) {
		super(message);
	}
}
