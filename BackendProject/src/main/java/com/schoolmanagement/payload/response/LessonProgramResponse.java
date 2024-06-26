package com.schoolmanagement.payload.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.schoolmanagement.entity.concretes.EducationTerm;
import com.schoolmanagement.entity.concretes.Lesson;
import com.schoolmanagement.entity.concretes.Teacher;
import com.schoolmanagement.entity.enums.Day;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LessonProgramResponse {

    private Long lessonProgramId;

    private Day day;

    private LocalTime startTime;

    private LocalTime stopTime;

    private Set<Lesson> lessonName;

    private EducationTerm educationTerm;

    private Set<TeacherResponse> teachers; // neden teachers? mantigi nedir?
    //okulda 2 matematik vardir, biri a sinifina digeri b sinifina giriyordur.

    private Set<StudentResponse> students;

}
