/*
 * Copyright (c) 2022-2024 the original author or authors.
 *
 * MIT License
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package com.example.SIDIS_Auth.usermanagement.api;

import com.example.SIDIS_Auth.usermanagement.model.Role;
import com.example.SIDIS_Auth.usermanagement.model.User;
import com.example.SIDIS_Auth.usermanagement.services.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.security.RolesAllowed;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Based on https://github.com/Yoh0xFF/java-spring-security-example
 *
 */
@Tag(name = "UserAdmin")
@RestController
@RequestMapping(path = "api/admin/user")
@RolesAllowed(Role.ADMIN)
@RequiredArgsConstructor
public class UserAdminApi {

	private final UserService userService;
	private final UserViewMapper userViewMapper;

	@PostMapping
	public UserView create(@RequestBody @Valid final CreateUserRequest request) {
		final var user = userService.create(request);
		return userViewMapper.toUserView(user);
	}

	@PutMapping("{id}")
	public UserView update(@PathVariable final Long id, @RequestBody @Valid final EditUserRequest request) {
		final var user = userService.update(id, request);
		return userViewMapper.toUserView(user);
	}

	@DeleteMapping("{id}")
	public UserView delete(@PathVariable final Long id) {
		final var user = userService.delete(id);
		return userViewMapper.toUserView(user);
	}

	@GetMapping("{id}")
	public UserView get(@PathVariable final Long id) {
		final var user = userService.getUser(id);
		return userViewMapper.toUserView(user);
	}

	@PostMapping("search")
	public ListResponse<UserView> search(@RequestBody final SearchRequest<SearchUsersQuery> request) {
		final List<User> searchUsers = userService.searchUsers(request.getPage(), request.getQuery());
		return new ListResponse<>(userViewMapper.toUserView(searchUsers));
	}
}
