package springstudythird.security;

import org.springframework.security.crypto.password.PasswordEncoder;

import springstudythird.security.NoEncodingPasswordEncoder;

public class NoEncodingPasswordEncoder implements PasswordEncoder {
	
	@Override
	public String encode(CharSequence rawPwd) {
		return rawPwd.toString();
	}
	
	@Override
	public boolean matches(CharSequence rawPwd, String encodedPwd) {
		return rawPwd.toString().equals(encodedPwd);
	}
}