package com.ourfancyteamname.officespace.db.repos;

import com.ourfancyteamname.officespace.db.entities.Permission;
import com.ourfancyteamname.officespace.enums.PermissionCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, Integer> {

  @Query("select p.code from Permission p " +
      "left join RolePermission rp on rp.permissionId = p.id " +
      "where rp.roleId = :roleId")
  List<PermissionCode> findPermissionCodeByRoleId(@Param("roleId") Integer roleId);

  @Query("select p from Permission p " +
      "left join RolePermission rp on rp.permissionId = p.id " +
      "left join Role r on r.id = rp.roleId " +
      "where r.code = :role")
  List<Permission> findPermissionByRole(@Param("role") String role);

  Optional<Permission> findByCode(PermissionCode code);
}
