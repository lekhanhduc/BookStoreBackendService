package vn.khanhduc.bookstorebackend.model;

import jakarta.persistence.*;
import lombok.*;

@Entity(name = "RoleHasPermission")
@Table(name = "role_has_permission")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class RoleHasPermission extends AbstractEntity<Integer>{

    @ManyToOne
    @JoinColumn(name = "role_id")
    private Role role;

    @ManyToOne
    @JoinColumn(name = "permission_id")
    private Permission permission;
}
