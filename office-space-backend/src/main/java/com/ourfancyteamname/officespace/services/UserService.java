package com.ourfancyteamname.officespace.services;

import com.ourfancyteamname.officespace.db.entities.Role;
import com.ourfancyteamname.officespace.db.entities.UserRole;
import com.ourfancyteamname.officespace.dtos.TableSearchRequest;
import com.ourfancyteamname.officespace.dtos.UserDto;
import com.ourfancyteamname.officespace.dtos.security.RoleDto;
import org.springframework.data.domain.Page;

import java.util.List;

public interface UserService {

  Page<UserDto> findAllByPaging(TableSearchRequest tableSearchRequest);

  List<UserRole> updateUserRole(RoleDto roleDto, List<String> usernames);

  List<UserRole> createUserRole(Role roleDto, List<String> usernames);

  void deleteUserRole(Integer roleId);
}
