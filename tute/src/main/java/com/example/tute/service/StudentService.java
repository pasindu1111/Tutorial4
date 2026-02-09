package com.example.tute.service;

import com.example.tute.dto.StudentDTO;
import com.example.tute.entity.Student;
import com.example.tute.exception.ResourceNotFoundException;
import com.example.tute.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class StudentService {

    private final StudentRepository studentRepository;

    @Transactional(readOnly = true)
    public Page<Student> getAllStudents(int page, int size, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);
        return studentRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public Student getStudentById(Long id) {
        return studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + id));
    }

    @Transactional
    public Student createStudent(StudentDTO studentDTO) {
        // Check if email already exists
        if (studentRepository.existsByEmail(studentDTO.getEmail())) {
            throw new IllegalArgumentException("Email already exists: " + studentDTO.getEmail());
        }

        Student student = new Student();
        student.setName(studentDTO.getName());
        student.setEmail(studentDTO.getEmail());
        student.setBatch(studentDTO.getBatch());
        student.setGpa(studentDTO.getGpa());

        return studentRepository.save(student);
    }

    @Transactional
    public Student updateStudent(Long id, StudentDTO studentDTO) {
        Student student = getStudentById(id);

        // Check if email is being changed and if new email already exists
        if (!student.getEmail().equals(studentDTO.getEmail())
                && studentRepository.existsByEmail(studentDTO.getEmail())) {
            throw new IllegalArgumentException("Email already exists: " + studentDTO.getEmail());
        }

        student.setName(studentDTO.getName());
        student.setEmail(studentDTO.getEmail());
        student.setBatch(studentDTO.getBatch());
        student.setGpa(studentDTO.getGpa());

        return studentRepository.save(student);
    }

    @Transactional
    public void deleteStudent(Long id) {
        Student student = getStudentById(id);
        studentRepository.delete(student);
    }
}
