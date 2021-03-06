package org.molgenis.security.permission;

import static java.util.Objects.requireNonNull;

import org.molgenis.data.security.EntityTypeIdentity;
import org.molgenis.data.security.EntityTypePermission;
import org.molgenis.security.core.UserPermissionEvaluator;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/permission")
public class MolgenisPermissionController {
  private final UserPermissionEvaluator permissionService;

  public MolgenisPermissionController(UserPermissionEvaluator permissionService) {
    this.permissionService = requireNonNull(permissionService);
  }

  @GetMapping("/{entityTypeId}/read")
  @ResponseBody
  public boolean hasReadPermission(@PathVariable("entityTypeId") String entityTypeId) {
    return permissionService.hasPermission(
            new EntityTypeIdentity(entityTypeId), EntityTypePermission.READ_METADATA)
        && permissionService.hasPermission(
            new EntityTypeIdentity(entityTypeId), EntityTypePermission.READ_DATA);
  }

  @GetMapping("/{entityTypeId}/write")
  @ResponseBody
  public boolean hasWritePermission(@PathVariable("entityTypeId") String entityTypeId) {
    return permissionService.hasPermission(
            new EntityTypeIdentity(entityTypeId), EntityTypePermission.READ_METADATA)
        && permissionService.hasPermission(
            new EntityTypeIdentity(entityTypeId), EntityTypePermission.UPDATE_DATA);
  }
}
