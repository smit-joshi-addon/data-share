package com.track.share.user;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;



@Service
class UserServiceImpl implements UserService {

	@Autowired
	private UserRepository userRepository;

	@Override
	public Users createUser(Users user) {
		Optional<Users> u = userRepository.findByEmail(user.getEmail());
		if(!u.isEmpty())
			return null;
		user.setPassword(new BCryptPasswordEncoder().encode(user.getPassword()));
		return userRepository.save(user);
	}

	@Override
	public List<Users> getUsers() {
		return userRepository.findAll();
	}

	@Override
	public Users getUser(String username) {
		return userRepository.findByEmail(username).orElse(null);
	}

	@Override
	public void removeUser(Long userId) {
		userRepository.deleteById(userId);
	}

}
