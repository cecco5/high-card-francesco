package it.sara.demo.web.assembler;

import it.sara.demo.service.user.criteria.CriteriaGetUsers;
import it.sara.demo.service.user.result.GetUsersResult;
import it.sara.demo.web.user.request.GetUsersRequest;
import it.sara.demo.web.user.response.GetUsersResponse;
import org.springframework.stereotype.Component;

/**
 * Assembler for converting between web layer and service layer DTOs for user search operations.
 *
 * <p>Responsibilities:</p>
 * <ul>
 *   <li>Convert {@link GetUsersRequest} to {@link CriteriaGetUsers} (web → service)</li>
 *   <li>Convert {@link GetUsersResult} to {@link GetUsersResponse} (service → web)</li>
 * </ul>
 *
 * <p>This pattern maintains separation between web and service layers,
 * as per the application architecture requirements.</p>
 */
@Component
public class GetUsersAssembler {

  /**
   * Converts web layer request to service layer criteria.
   *
   * @param request Web layer request containing search, pagination, and sorting parameters
   * @return Service layer criteria ready for business logic processing
   */
  public CriteriaGetUsers toCriteria(GetUsersRequest request) {
    CriteriaGetUsers criteria = new CriteriaGetUsers();
    criteria.setQuery(request.getQuery());
    criteria.setOffset(request.getOffset());
    criteria.setLimit(request.getLimit());

    // Default to BY_FIRSTNAME if no order specified
    criteria.setOrder(
        request.getOrder() != null
            ? request.getOrder()
            : CriteriaGetUsers.OrderType.BY_FIRSTNAME
    );

    return criteria;
  }

  /**
   * Converts service layer result to web layer response.
   *
   * @param result Service layer result containing user list and pagination info
   * @return Web layer response ready for HTTP serialization
   */
  public GetUsersResponse toResponse(GetUsersResult result) {
    GetUsersResponse response = new GetUsersResponse();
    response.setStatus(GetUsersResponse.success("Users retrieved successfully.").getStatus());
    response.setUsers(result.getUsers());
    response.setTotal(result.getTotal());

    return response;
  }
}

