package com.ourfancyteamname.officespace.services.impl;

import com.ourfancyteamname.officespace.db.converters.dtos.UserConverter;
import com.ourfancyteamname.officespace.db.entities.Role;
import com.ourfancyteamname.officespace.db.entities.User;
import com.ourfancyteamname.officespace.db.entities.User_;
import com.ourfancyteamname.officespace.db.entities.view.UserRoleListView;
import com.ourfancyteamname.officespace.db.repos.RoleRepository;
import com.ourfancyteamname.officespace.db.repos.UserRepository;
import com.ourfancyteamname.officespace.db.repos.UserRoleRepository;
import com.ourfancyteamname.officespace.db.repos.view.UserRoleListViewRepository;
import com.ourfancyteamname.officespace.db.services.PaginationService;
import com.ourfancyteamname.officespace.db.services.SortingService;
import com.ourfancyteamname.officespace.db.services.SpecificationService;
import com.ourfancyteamname.officespace.dtos.ColumnSearchRequest;
import com.ourfancyteamname.officespace.dtos.TablePagingRequest;
import com.ourfancyteamname.officespace.dtos.TableSearchRequest;
import com.ourfancyteamname.officespace.dtos.TableSortingRequest;
import com.ourfancyteamname.officespace.dtos.UserDto;
import com.ourfancyteamname.officespace.dtos.security.RoleDto;
import com.ourfancyteamname.officespace.enums.DataBaseDirection;
import com.ourfancyteamname.officespace.enums.DataBaseOperation;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.persistence.EntityManager;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@SpringBootTest
@RunWith(MockitoJUnitRunner.class)
public class UserServiceImplTest {

  private static final String SEARCH_TERM = "foo";

  @InjectMocks
  private UserServiceImpl service;

  @Mock
  private UserRepository userRepository;

  @Mock
  private RoleRepository roleRepository;

  @Mock
  private PaginationService paginationService;

  @Mock
  private SortingService sortingService;

  @Mock
  private SpecificationService specificationService;

  @Mock
  private UserConverter userConverter;

  @Mock
  private UserRoleRepository userRoleRepository;

  @Mock
  private EntityManager entityManager;

  @Mock
  private UserRoleListViewRepository userRoleListViewRepository;

  @Mock
  private PasswordEncoder passwordEncoder;

  @Test
  public void findAllByPaging() {
    ColumnSearchRequest columnSearchRequest = ColumnSearchRequest.builder()
        .columnName(User_.LAST_NAME)
        .operation(DataBaseOperation.EQUAL)
        .term(SEARCH_TERM)
        .build();
    TableSortingRequest tableSortingRequest = TableSortingRequest.builder()
        .columnName(User_.LAST_NAME)
        .direction(DataBaseDirection.ASC)
        .build();
    TablePagingRequest tablePagingRequest = TablePagingRequest.builder()
        .page(0)
        .pageSize(10)
        .build();
    TableSearchRequest tableSearchRequest = TableSearchRequest.builder()
        .columnSearchRequests(Arrays.asList(columnSearchRequest))
        .pagingRequest(tablePagingRequest)
        .sortingRequest(tableSortingRequest)
        .build();
    User aUser = User.builder()
        .id(1)
        .email("dang")
        .build();
    Specification<User> specs = (root, query, builder) -> builder.equal(root.get(User_.LAST_NAME), SEARCH_TERM);
    Mockito.when(specificationService.specificationBuilder(tableSearchRequest)).thenReturn(specs);
    Mockito.when(sortingService.getSort(tableSortingRequest)).thenReturn(Sort.unsorted());
    Mockito.when(paginationService.getPage(tablePagingRequest, Sort.unsorted())).thenReturn(PageRequest.of(0, 10));
    Mockito.when(userRepository.findAll(specs, PageRequest.of(0, 10)))
        .thenReturn(new PageImpl(Arrays.asList(aUser), PageRequest.of(0, 10), 1));
    Mockito.when(userConverter.toDto(aUser))
        .thenReturn(UserDto.builder().email("dang").build());
    Page<UserDto> actual = service.findAllByPaging(tableSearchRequest);
    Assert.assertEquals(1, actual.getContent().size());
    Assert.assertEquals("dang", actual.getContent().get(0).getEmail());
  }

  @Test(expected = IllegalArgumentException.class)
  public void findById_notFound() {
    service.findById(null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void findById_notFound2() {
    int userId = 1;
    Mockito.when(userRepository.findById(userId)).thenReturn(Optional.empty());
    service.findById(userId);
  }

  @Test
  public void findById_success() {
    int userId = 1;
    User user = User.builder().build();
    Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(user));
    Mockito.when(userConverter.toDto(user)).thenReturn(UserDto.builder().build());
    service.findById(userId);
    Mockito.verify(userConverter, Mockito.times(1)).toDto(user);
  }

  @Test(expected = IllegalArgumentException.class)
  public void editUser_notFound() {
    int userId = 1;
    UserDto userDto = UserDto.builder().id(userId).build();
    Mockito.when(userRepository.findById(userDto.getId())).thenReturn(Optional.empty());
    service.editUser(userDto);
  }

  @Test(expected = IllegalArgumentException.class)
  public void editUser_duplicatedUsername() {
    int userId = 1;
    String username = "username";
    UserDto userDto = UserDto.builder().id(userId).username("username2").build();
    User user = User.builder().id(userId).username(username).build();
    User user1 = User.builder().id(2).username("username2").build();
    Mockito.when(userRepository.findById(userDto.getId())).thenReturn(Optional.of(user));
    Mockito.when(userRepository.findByUsername(userDto.getUsername())).thenReturn(Optional.of(user1));
    service.editUser(userDto);
  }

  @Test
  public void editUser_success() {
    int userId = 1;
    String username = "username";
    UserDto userDto = UserDto.builder().id(userId).username(username).build();
    User user = User.builder().id(userId).username(username).build();
    Mockito.when(userRepository.findById(userDto.getId())).thenReturn(Optional.of(user));
    Mockito.when(userRepository.findByUsername(userDto.getUsername())).thenReturn(Optional.of(user));
    service.editUser(userDto);
    Mockito.verify(userRepository, Mockito.times(1)).save(user);
  }

  @Test(expected = IllegalArgumentException.class)
  public void createUser_duplicateUsername() {
    int userId = 1;
    String username = "username";
    UserDto userDto = UserDto.builder().id(userId).username(username).build();
    Mockito.when(userRepository.existsByUsername(userDto.getUsername())).thenReturn(true);
    service.createUser(userDto);
  }

  @Test
  public void createUser_success() {
    int userId = 1;
    String username = "username";
    UserDto userDto = UserDto.builder().id(userId).username(username).build();
    User user = User.builder().id(userId).username(username).build();
    Mockito.when(userRepository.existsByUsername(userDto.getUsername())).thenReturn(false);
    Mockito.when(userConverter.toEntity(userDto)).thenReturn(user);
    service.createUser(userDto);
    Mockito.verify(userConverter, Mockito.times(1)).toEntity(userDto);
    Mockito.verify(passwordEncoder, Mockito.times(1)).encode(userDto.getPassword());
    Mockito.verify(userRepository, Mockito.times(1)).save(user);
  }

  @Test
  public void updateUserRole() {
    int roleId = 1;
    String username = "username";
    RoleDto roleDto = RoleDto.builder().id(roleId).build();
    List<String> usernames = Arrays.asList(username);
    User user = User.builder().username(username).build();
    Mockito.when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
    service.updateUserRole(roleDto, usernames);
    Mockito.verify(entityManager, Mockito.times(1)).flush();
    Mockito.verify(userRepository, Mockito.times(1)).findByUsername(username);
    Mockito.verify(userRoleRepository, Mockito.times(1)).removeByRoleId(roleId);
    Mockito.verify(userRoleRepository, Mockito.times(1)).saveAll(Mockito.any());
  }

  @Test
  public void updateRoleUser() {
    int userId = 2;
    String code = "role";
    UserDto userDto = UserDto.builder().id(userId).build();
    List<String> roles = Arrays.asList(code);
    Role role = Role.builder().build();
    Mockito.when(roleRepository.findByCode(code)).thenReturn(Optional.of(role));
    service.updateRoleUser(userDto, roles);
    Mockito.verify(entityManager, Mockito.times(1)).flush();
    Mockito.verify(roleRepository, Mockito.times(1)).findByCode(code);
    Mockito.verify(userRoleRepository, Mockito.times(1)).removeByUserId(userId);
    Mockito.verify(userRoleRepository, Mockito.times(1)).saveAll(Mockito.any());
  }

  @Test
  public void createRoleUser() {
    User user = User.builder().build();
    String role = "role";
    List<String> roles = Arrays.asList(role);
    Mockito.when(roleRepository.findByCode(role))
        .thenReturn(Optional.of(Role.builder().code(role).build()));
    service.createRoleUser(user, roles);
    Mockito.verify(roleRepository, Mockito.times(1)).findByCode(role);
    Mockito.verify(userRoleRepository, Mockito.times(1)).saveAll(Mockito.any());
  }

  @Test
  public void createUserRole() {
    Role role = Role.builder().build();
    String user = "dang";
    List<String> users = Arrays.asList(user);
    Mockito.when(userRepository.findByUsername(user))
        .thenReturn(Optional.of(User.builder().username(user).build()));
    service.createUserRole(role, users);
    Mockito.verify(userRepository, Mockito.times(1)).findByUsername(user);
    Mockito.verify(userRoleRepository, Mockito.times(1)).saveAll(Mockito.any());
  }

  @Test
  public void removeUser() {
    int userId = 1;
    service.removeUser(userId);
    Mockito.verify(userRoleRepository, Mockito.times(1)).removeByUserId(userId);
    Mockito.verify(entityManager, Mockito.times(1)).flush();
    Mockito.verify(userRepository, Mockito.times(1)).deleteById(userId);
  }

  @Test
  public void findUserRoleListView() {
    TableSearchRequest tableSearchRequest = TableSearchRequest.builder().build();
    Specification specs = specificationService.specificationBuilder(tableSearchRequest);
    Pageable pageable = paginationService.getPage(tableSearchRequest.getPagingRequest(), null);
    Page result = new PageImpl(Arrays.asList(UserRoleListView.builder().build()));
    Mockito.when(userRoleListViewRepository.findAll(specs, pageable))
        .thenReturn(result);
    Page actual = service.findUserRoleListView(tableSearchRequest);
    Assert.assertEquals(1, actual.getTotalElements());
  }
}
