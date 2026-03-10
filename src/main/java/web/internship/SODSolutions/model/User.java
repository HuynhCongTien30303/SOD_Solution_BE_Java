package web.internship.SODSolutions.model;

import jakarta.persistence.*;
import lombok.*;
import web.internship.SODSolutions.model.common.Auditable;

import java.time.Instant;
import java.util.List;

@Entity
@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "users")
public class User extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "full_name")
    private String name;

    private String email;

    private String password;

    private String phone;

    private String address;

    private String avatar;

    @Column(name = "company_name")
    private String companyName;

    private boolean isActive;

    private String codeId;

    private Instant codeExpired;

    @ManyToOne
    @JoinColumn(name = "role_id")
    private Role role;

    @OneToMany(mappedBy = "user")
    private List<Project> projects;
}
