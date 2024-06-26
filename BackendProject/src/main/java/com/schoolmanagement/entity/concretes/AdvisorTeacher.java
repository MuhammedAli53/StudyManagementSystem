package com.schoolmanagement.entity.concretes;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AdvisorTeacher implements Serializable { // teacherle iliskili olacak. ogretmen olan kisi eger advisorteacher ise bu classa baglicaz.

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private UserRole userRole; //UserRole concrete class olusturmamizin nedeni, ileriye dogru yeni roller eklenebileceginden
    //herhangi bir degisiklikte bircok yerde degisiklik yapilmasina neden olmasidir.

    @OneToOne
    private Teacher teacher;

    @OneToMany(mappedBy = "advisorTeacher", orphanRemoval = true, cascade = CascadeType.PERSIST, fetch = FetchType.EAGER)
    private List<Student> students;

    @OneToMany(mappedBy = "advisorTeacher", cascade = CascadeType.ALL)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private List<Meet> meets;
}
