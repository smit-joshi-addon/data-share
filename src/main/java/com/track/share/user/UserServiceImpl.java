package com.track.share.user;

import java.util.List;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.track.share.exceptions.NotFoundException;
import com.track.share.exceptions.UsernameUnavailableException;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
class UserServiceImpl implements UserService {

	private UserRepository userRepository;

	@Override
	public Users createUser(Users user) {
		if (userRepository.existsByEmail(user.getEmail())) {
			throw new UsernameUnavailableException("email is Unavailable, please try with another email");
		}
		user.setStatus(true);
		user.setPassword(new BCryptPasswordEncoder().encode(user.getPassword()));
		return userRepository.save(user);
	}

	@Override
	public List<Users> getUsers() {
		return userRepository.findAll();
	}

	@Override
	public Users getUser(String username) {
		return userRepository.findByEmail(username)
				.orElseThrow(() -> new UsernameNotFoundException("user not found with username " + username));
	}

	@Override
	public void removeUser(Long userId) {
		if (!userRepository.existsById(userId)) {
			throw new NotFoundException("user not found with userId " + userId);
		}
		userRepository.deleteById(userId);
	}

}
