package br.com.wagnerpires.springsecurityjwt.security.service.impl;

import br.com.wagnerpires.springsecurityjwt.security.UserAuthenticated;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import br.com.wagnerpires.springsecurityjwt.repository.UserRepository;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
  private final UserRepository userRepository;

  public UserDetailsServiceImpl(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    return userRepository.findByUsername(username)
        .map(UserAuthenticated::new)  // .map(user -> new UserAuthenticated(user))
        .orElseThrow(
            () -> new UsernameNotFoundException("User Not Found with username: " + username));
  }
}
