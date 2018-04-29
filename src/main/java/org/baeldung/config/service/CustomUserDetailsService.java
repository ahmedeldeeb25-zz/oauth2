/*
 * Copyright 2014-2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.baeldung.config.service;

import java.util.Collection;

import org.baeldung.config.model.Customers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

 

@Service
@Primary
public class CustomUserDetailsService implements UserDetailsService {

	private final CustomerRepository userRepository;

	@Autowired
	public CustomUserDetailsService(CustomerRepository userRepository) {
		this.userRepository = userRepository;
	}

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		Customers user = userRepository.findOneByEmail(email);
		if (user == null) {
			throw new UsernameNotFoundException(String.format("User %s does not exist!", email));
		}
 
		return new UserRepositoryUserDetails(user);
	}

	private final static class UserRepositoryUserDetails extends Customers implements UserDetails {

		private static final long serialVersionUID = 1L;

		private UserRepositoryUserDetails(Customers user) {
			super(user);
		}

		@Override
		public Collection<? extends GrantedAuthority> getAuthorities() {
			return getRoles();
		}

		@Override
		public String getUsername() {
			return getId().toString(); 
		}
		
		public int getuserId() {
			return getId();
		}

		@Override
		public boolean isAccountNonExpired() {
			return true;
		}

		@Override
		public boolean isAccountNonLocked() {
			return true;
		}

		@Override
		public boolean isCredentialsNonExpired() {
			return true;
		}

		@Override
		public boolean isEnabled() {
			return true;
		}

	}

}
