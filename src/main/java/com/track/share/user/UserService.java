package com.track.share.user;

import java.util.List;

public interface UserService {

	public Users createUser(Users user);

	public List<Users> getUsers();

	public Users getUser(String username);

	public void removeUser(Long userId);

}
