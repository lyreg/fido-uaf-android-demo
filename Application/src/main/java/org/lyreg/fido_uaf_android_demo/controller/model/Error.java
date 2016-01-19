/*
* Copyright Daon.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

package org.lyreg.fido_uaf_android_demo.controller.model;

public class Error {

	public static Error UNEXPECTED_ERROR = new Error(1,"An unexpected error occurred.  Please see the log files.");
	public static Error METHOD_NOT_IMPLEMENTED = new Error(2,"The method has not been implemented");
	
	public static Error USER_NOT_FOUND = new Error(10,"User not found");
	public static Error INVALID_CREDENTIALS = new Error(11,"Invalid credentials provided - the user could not be authenticated");
	public static Error INSUFFICIENT_CREDENTIALS = new Error(12, "The user cannot be authenticated - please supply a username and password or a FIDO authentication response");
	public static Error AUTHENTICATION_REQUEST_ID_NOT_PROVIDED = new Error(100,"The authentication request ID must be provided");

	public static Error PASSWORD_NOT_PROVIDED = new Error(101,"The password must be provided");
	public static Error EMAIL_NOT_PROVIDED = new Error(102,"The email must be provided");
	public static Error FIRST_NAME_NOT_PROVIDED = new Error(103,"The first name must be provided");
	public static Error LAST_NAME_NOT_PROVIDED = new Error(104,"The last name must be provided");
	
	public static Error FIDO_AUTH_COMPLETE_USER_NOT_FOUND = new Error(200,"The user was authenticated by FIDO but this user is not in the system");
	public static Error UNKNOWN_SESSION_IDENTIFIER = new Error(201,"Unknown session identifier");
	public static Error EXPIRED_SESSION = new Error(202,"The specified session has expired");
	public static Error NON_EXISTENT_SESSION = new Error(203,"The specified session does not exist");

	public static Error TRANSACTION_CONTENT_NOT_PROVIDED = new Error(303,"Transaction data must be provided");
	
	private int code;
	private String message;
	private String fidoMessage;
	private Long fidoResponseCode;
	private String fidoResponseMsg;

	public Error() {
	}

	public Error(int code, String message) {
		this.code = code;
		this.message = message;
	}
	
	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	
	public String toString() {
		return "Code: " + this.getCode() + " Message: " + this.getMessage();
	}

	public String getFidoMessage() {
		return fidoMessage;
	}

	public void setFidoMessage(String fidoMessage) {
		this.fidoMessage = fidoMessage;
	}

	public Long getFidoResponseCode() {
		return fidoResponseCode;
	}

	public void setFidoResponseCode(Long fidoResponseCode) {
		this.fidoResponseCode = fidoResponseCode;
	}

	public String getFidoResponseMsg() {
		return fidoResponseMsg;
	}

	public void setFidoResponseMsg(String fidoResponseMsg) {
		this.fidoResponseMsg = fidoResponseMsg;
	}
}
