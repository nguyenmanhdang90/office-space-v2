package com.ourfancyteamname.officespace.db.converters;

import com.ourfancyteamname.officespace.db.converters.dtos.RoleConverter;
import com.ourfancyteamname.officespace.dtos.security.RoleDto;
import com.ourfancyteamname.officespace.db.entities.Role;

import org.junit.Assert;
import org.junit.Test;
import org.mapstruct.factory.Mappers;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class RoleEnumDtoConverterTest {

  private RoleConverter roleConverter = Mappers.getMapper(RoleConverter.class);

  @Test
  public void toDto_success() {
    Role role = new Role();
    role.setCode("SUPER_ADMIN");
    role.setId(1);
    Role activeRole = new Role();
    activeRole.setCode("SUPER_ADMIN");
    activeRole.setId(1);
    Role nonActiveRole = new Role();
    nonActiveRole.setCode("SUPER_ADMIN");
    nonActiveRole.setId(2);
    RoleDto result = roleConverter.toDto(role, activeRole);
    RoleDto result2 = roleConverter.toDto(role, nonActiveRole);
    Assert.assertTrue(result.getIsUsing());
    Assert.assertFalse(result2.getIsUsing());
  }
}
