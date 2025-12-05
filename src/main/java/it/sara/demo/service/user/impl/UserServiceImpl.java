package it.sara.demo.service.user.impl;

import it.sara.demo.exception.GenericException;
import it.sara.demo.service.database.UserRepository;
import it.sara.demo.service.database.model.User;
import it.sara.demo.service.user.UserService;
import it.sara.demo.service.user.criteria.CriteriaAddUser;
import it.sara.demo.service.user.criteria.CriteriaGetUsers;
import it.sara.demo.service.user.result.AddUserResult;
import it.sara.demo.service.user.result.GetUsersResult;
import it.sara.demo.service.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

  @Autowired private StringUtil stringUtil;

  @Autowired private UserRepository userRepository;

  @Override
  public AddUserResult addUser(CriteriaAddUser criteria) throws GenericException {

    AddUserResult returnValue;
    User user;

    try {

      returnValue = new AddUserResult();

      user = new User();
      user.setFirstName(criteria.getFirstName());
      user.setLastName(criteria.getLastName());
      user.setEmail(criteria.getEmail());
      user.setPhoneNumber(criteria.getPhoneNumber());

      if (!userRepository.save(user)) {
        throw new GenericException(500, "Error saving user");
      }

    } catch (Exception e) {
      if (log.isErrorEnabled()) {
        log.error(e.getMessage(), e);
      }
      throw new GenericException(GenericException.GENERIC_ERROR);
    }
    return returnValue;
  }

  @Override
  public GetUsersResult getUsers(CriteriaGetUsers criteriaGetUsers) throws GenericException {
    return null;
  }
}
