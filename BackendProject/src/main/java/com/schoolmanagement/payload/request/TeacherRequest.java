package com.schoolmanagement.payload.request;

import com.schoolmanagement.payload.request.abstracts.BaseUserRequest;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class TeacherRequest extends BaseUserRequest {

    @NotNull(message = "Please select Lesson")
    private Set<Long> lessonsIdList;

    @NotNull(message = "Please select isAdvisor Teacher")
    private boolean isAdvisorTeacher = true; // is keywordle basladi. Boolean lar is keywordle baslar genelde. Lombok bunun get methodunu calistirmiyor
    //bu nedenle primitive type a donduk. Lombok Boolean turundeki classlarda is ...  ile baslayan kelimelerin getter'inde sorun yasatiyor.


    @NotNull(message = "Please enter your email")
    @Email(message = "Please enter valid email")
    @Size(min=5, max=50 , message = "Your email should be between 5 and 50 chars")
    private String email;
}
