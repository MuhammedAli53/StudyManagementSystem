package com.schoolmanagement.service;

import com.schoolmanagement.entity.concretes.LessonProgram;
import com.schoolmanagement.entity.concretes.Teacher;
import com.schoolmanagement.entity.enums.RoleType;
import com.schoolmanagement.exception.BadRequestException;
import com.schoolmanagement.exception.ResourceNotFoundException;
import com.schoolmanagement.payload.dto.TeacherRequestDto;
import com.schoolmanagement.payload.request.ChooseLessonTeacherRequest;
import com.schoolmanagement.payload.request.TeacherRequest;
import com.schoolmanagement.payload.response.ResponseMessage;
import com.schoolmanagement.payload.response.TeacherResponse;
import com.schoolmanagement.repository.TeacherRepository;
import com.schoolmanagement.utils.CheckSameLessonProgram;
import com.schoolmanagement.utils.FieldControl;
import com.schoolmanagement.utils.Messages;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TeacherService {

    private final TeacherRepository teacherRepository;
    private final LessonProgramService lessonProgramService;
    private final FieldControl fieldControl;
    private final PasswordEncoder passwordEncoder;
    private final TeacherRequestDto teacherRequestDto;
    private final UserRoleService userRoleService;
    private final AdvisorTeacherService advisorTeacherService;

    // Not: Save() **********************************************************
    public ResponseMessage<TeacherResponse> save(TeacherRequest teacherRequest) {
        //ogretmenin derslerini almamiz lazim lessonProgramServiceden.
        Set<LessonProgram> lessons = lessonProgramService.getLessonProgramById(teacherRequest.getLessonsIdList());

        if(lessons.size()==0){
            throw  new BadRequestException(Messages.LESSON_PROGRAM_NOT_FOUND_MESSAGE);
        } else {

            // !!! Dublicate kontrolu
            fieldControl.checkDuplicate(teacherRequest.getUsername(),
                    teacherRequest.getSsn(),
                    teacherRequest.getPhoneNumber(),
                    teacherRequest.getEmail());

            // !!! dto -> POJO donusumu
            Teacher teacher = teacherRequestToDto(teacherRequest);
            // !!! Rol bilgisi setleniyor
            teacher.setUserRole(userRoleService.getUserRole(RoleType.TEACHER));
            // !!! dersProgrami ekleniyor
            teacher.setLessonsProgramList(lessons);
            // !!! sifre encode ediliyor
            teacher.setPassword(passwordEncoder.encode(teacherRequest.getPassword()));
            // !!! Db ye kayit islemi
            Teacher savedTeacher = teacherRepository.save(teacher);
            //TODO AdvisorTeacher yazilinca ekleme yapilacak
            if (teacherRequest.isAdvisorTeacher()){
                // degisiklşigi advisorteacher tablosunda yapicaz. eger isadvisor fieldi true ise advisorservice gir kayit yap. parametre olarak teacheri gonder.
                advisorTeacherService.saveAdvisorTeacher(savedTeacher);
            }

            return ResponseMessage.<TeacherResponse>builder()
                    .message("Teacher saved successfully")
                    .httpStatus(HttpStatus.CREATED)
                    .object(createTeacherResponse(savedTeacher))
                    .build();
        }
    }

    private Teacher teacherRequestToDto(TeacherRequest teacherRequest){
        return teacherRequestDto.dtoTeacher(teacherRequest);
    }

    private TeacherResponse createTeacherResponse(Teacher teacher){
        return TeacherResponse.builder()
                .userId(teacher.getId())
                .username(teacher.getUsername())
                .name(teacher.getName())
                .surname(teacher.getSurname())
                .birthDay(teacher.getBirthDay())
                .birthPlace(teacher.getBirthPlace())
                .ssn(teacher.getSsn())
                .phoneNumber(teacher.getPhoneNumber())
                .gender(teacher.getGender())
                .email(teacher.getEmail())
                .build();
    }

    public List<TeacherResponse> getAllTeacher() {
        return teacherRepository.findAll().stream().map(this::createTeacherResponse).collect(Collectors.toList());
    }

    public ResponseMessage<TeacherResponse> updateTeacher(TeacherRequest newTeacher, Long userId) {
        Optional<Teacher> teacher = teacherRepository.findById(userId); // idli data var mi

        //dto uzerinden eklenecek lessonlar.
        Set<LessonProgram> lessons = lessonProgramService.getLessonProgramById(newTeacher.getLessonsIdList());
        if (!teacher.isPresent()){
            throw new ResourceNotFoundException(Messages.NOT_FOUND_USER_MESSAGE);
        } else if (lessons.size()==0) {
            throw new BadRequestException(Messages.LESSON_PROGRAM_NOT_FOUND_MESSAGE);
        } else if (!checkParameterForUpdateMethod(teacher.get(),newTeacher)) {
            fieldControl.checkDuplicate(newTeacher.getUsername(),
                    newTeacher.getSsn(),
                    newTeacher.getPhoneNumber(),
                    newTeacher.getEmail());
        }
        Teacher updatedTeacher = createUpdatedTeacher(newTeacher,userId);
        //burda rolu setledik biz. createUpdateTeacher donusumunde setledik
        updatedTeacher.setPassword(passwordEncoder.encode(newTeacher.getPassword()));
        updatedTeacher.setLessonsProgramList(lessons);
        Teacher savedTeacher = teacherRepository.save(updatedTeacher);
        advisorTeacherService.updateAdvisorTeacher(newTeacher.isAdvisorTeacher(),savedTeacher);

        return ResponseMessage.<TeacherResponse>builder().message("Teacher updated successfully")
                .object(createTeacherResponse(savedTeacher)).httpStatus(HttpStatus.OK).build();

    }
    private Teacher createUpdatedTeacher(TeacherRequest teacher, Long id){
        return Teacher.builder().id(id)
                .username(teacher.getUsername())
                .name(teacher.getName())
                .surname(teacher.getSurname())
                .ssn(teacher.getSsn())
                .birthDay(teacher.getBirthDay())
                .birthPlace(teacher.getBirthPlace())
                .phoneNumber(teacher.getPhoneNumber())
                .isAdvisor(teacher.isAdvisorTeacher())
                .userRole(userRoleService.getUserRole(RoleType.TEACHER))  // role setlemesi dtoda.
                .gender(teacher.getGender())
                .email(teacher.getEmail())
                .build();
    }
    private boolean checkParameterForUpdateMethod(Teacher teacher, TeacherRequest newTeacherRequest) {
        return teacher.getSsn().equalsIgnoreCase(newTeacherRequest.getSsn())
                || teacher.getUsername().equalsIgnoreCase(newTeacherRequest.getUsername())
                || teacher.getPhoneNumber().equalsIgnoreCase(newTeacherRequest.getPhoneNumber())
                || teacher.getEmail().equalsIgnoreCase(newTeacherRequest.getEmail());
    }

    public List<TeacherResponse> getTeacherByName(String teacherName) {

        return teacherRepository.getTeacherByNameContaining(teacherName)
                .stream().map(this::createTeacherResponse).collect(Collectors.toList());
        // turetilebilen bir methoddur.
        // containing bir keyword.
    }

    public ResponseMessage<?> deleteTeacher(Long id) {
        teacherRepository.findById(id).orElseThrow(()->{
            throw new ResourceNotFoundException(Messages.NOT_FOUND_USER_MESSAGE);
        });
        teacherRepository.deleteById(id);
        return ResponseMessage.builder().message("Teacher is deleted").httpStatus(HttpStatus.OK).build();
    }

    public ResponseMessage<TeacherResponse> getSavedTeacherById(Long id) {
        Teacher teacher = teacherRepository.findById(id).orElseThrow(()->{
            throw new ResourceNotFoundException(Messages.NOT_FOUND_USER_MESSAGE);
        });
        return ResponseMessage.<TeacherResponse>builder().message("Teacher successfully found").httpStatus(HttpStatus.OK)
                .object(createTeacherResponse(teacher)).build();
    }

    public Page<TeacherResponse> search(int page, int size, String sort, String type) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sort).ascending());
        if (Objects.equals(type,"desc")){
            pageable = PageRequest.of(page, size, Sort.by(sort).descending());
        }
        return teacherRepository.findAll(pageable).map(this::createTeacherResponse);
    }

    public ResponseMessage<TeacherResponse> chooseLesson(ChooseLessonTeacherRequest chooseLessonRequest) {

        //!!! ya teacher yoksa
        Teacher teacher = teacherRepository.findById(chooseLessonRequest.getTeacherId()).orElseThrow(
                ()-> new ResourceNotFoundException(Messages.NOT_FOUND_USER_MESSAGE));
        //!!! LessonProgram getiriliyor
        Set<LessonProgram> lessonPrograms = lessonProgramService.getLessonProgramById(chooseLessonRequest.getLessonProgramId());

        // !!!  LessonProgram ici bos mu kontrolu
        if(lessonPrograms.size()==0) {
            throw new ResourceNotFoundException(Messages.LESSON_PROGRAM_NOT_FOUND_MESSAGE);
        }
        // !!! Teacher in mevcut ders programi getiriliyor
        Set<LessonProgram> existLessonProgram =teacher.getLessonsProgramList();

        // eklenecek olan LessonProgram mevcuttaki LessonProgramda var mi kontrolu. cakisma olmamasi lazim.
        // start ve stop saatleri arasinda cakisma olmayacak. Mesela matematik dersi ekli bir ogretmende. P.tesi sali gunlerine ekli.
        //yeni bir ders daha eklemek istedik, sali gunune mesela. eski var olan dersimizle yeni ders saatlerimizin cakismamasi lazim.
        CheckSameLessonProgram.checkDuplicateLessonPrograms(existLessonProgram,lessonPrograms);
        existLessonProgram.addAll(lessonPrograms);
        teacher.setLessonsProgramList(existLessonProgram);
        Teacher savedTeacher = teacherRepository.save(teacher);

        return ResponseMessage.<TeacherResponse>builder()
                .message("LessonProgram added to Teacher")
                .httpStatus(HttpStatus.CREATED)
                .object(createTeacherResponse(savedTeacher))
                .build();

    }
        //************************** StudentInfoService *************************
    public Teacher getTeacherByUsername(String username) { // donen deger pojo, demek ki farkli bir service de kullaniliyor.
        if (!teacherRepository.existsByUsername(username)){ // findby ile de alabilirdik.
            throw new ResourceNotFoundException(Messages.NOT_FOUND_USER_MESSAGE);
        }
        return teacherRepository.getTeacherByUsername(username);
    }

    public boolean existByUsername(String username) {
        return teacherRepository.existsByUsername(username);
    }

    public Set<Teacher> getTeacherByIds(List<Long> teacherIdList) {
        return teacherRepository.findByIdsEquals(teacherIdList);
    }
}