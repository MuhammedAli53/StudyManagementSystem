package com.schoolmanagement.utils;


import com.schoolmanagement.entity.concretes.Student;
import com.schoolmanagement.payload.response.StudentResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CreateResponseObjectForService {

    public StudentResponse createStudentResponse(Student student) { // burda bir mapper yaptik. Tum yapiyi mapper package alt
        return StudentResponse.builder().userId(student.getId()).username(student.getUsername())
                .name(student.getName()).surname(student.getSurname()).birthDay(student.getBirthDay()).birthPlace(student.getBirthPlace())
                .phoneNumber(student.getPhoneNumber()).gender(student.getGender()).email(student.getEmail()).motherName(student.getMotherName())
                .fatherName(student.getFatherName()).studentNumber(student.getStudentNumber()).isActive(student.isActive()).build();
    }
}
