package uz.pdp.appjparelationships.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import uz.pdp.appjparelationships.entity.Address;
import uz.pdp.appjparelationships.entity.Group;
import uz.pdp.appjparelationships.entity.Student;
import uz.pdp.appjparelationships.payload.StudentDTO;
import uz.pdp.appjparelationships.repository.AddressRepository;
import uz.pdp.appjparelationships.repository.GroupRepository;
import uz.pdp.appjparelationships.repository.StudentRepository;
import uz.pdp.appjparelationships.repository.SubjectRepository;

import java.util.Optional;

@RestController
@RequestMapping("/student")
public class StudentController {


    final StudentRepository studentRepository;
    final AddressRepository addressRepository;
    final GroupRepository groupRepository;
    final SubjectRepository subjectRepository;

    public StudentController(StudentRepository studentRepository, AddressRepository addressRepository, GroupRepository groupRepository, SubjectRepository subjectRepository) {
        this.studentRepository = studentRepository;
        this.addressRepository = addressRepository;
        this.groupRepository = groupRepository;
        this.subjectRepository = subjectRepository;
    }

    //1. VAZIRLIK
    @GetMapping("/forMinistry")
    public Page<Student> getStudentListForMinistry(@RequestParam int page) {
        //1-1=0     2-1=1    3-1=2    4-1=3
        //select * from student limit 10 offset (0*10)
        //select * from student limit 10 offset (1*10)
        //select * from student limit 10 offset (2*10)
        //select * from student limit 10 offset (3*10)
        Pageable pageable = PageRequest.of(page, 10);
        return studentRepository.findAll(pageable);
    }

    //2. UNIVERSITY
    @GetMapping("/forUniversity/{universityId}")
    public Page<Student> getStudentListForUniversity(@PathVariable Integer universityId,
                                                     @RequestParam int page) {
        //1-1=0     2-1=1    3-1=2    4-1=3
        //select * from student limit 10 offset (0*10)
        //select * from student limit 10 offset (1*10)
        //select * from student limit 10 offset (2*10)
        //select * from student limit 10 offset (3*10)
        Pageable pageable = PageRequest.of(page, 10);
        return studentRepository.findAllByGroup_Faculty_UniversityId(universityId, pageable);
    }

    //3. FACULTY DEKANAT
    @GetMapping("/forDekanat/{facultyId}")
    public Page<Student> getStudentsByFacultyId(@PathVariable Integer facultyId, @RequestParam Integer page) {

        Pageable pageable = PageRequest.of(page, 5);
        return studentRepository.findAllByGroup_Faculty_Id(facultyId, pageable);
    }


    //4. GROUP OWNER

    @GetMapping("/forGroupOwner/{groupId}")
    public Page<Student> getStudentByGroupId(@PathVariable Integer groupId, @RequestParam Integer page) {

        Pageable pageable = PageRequest.of(page, 5);
        return studentRepository.findAllByGroup_Id(groupId, pageable);
    }


    @GetMapping("/{id}")
    public Student getById(@PathVariable Integer id) {

        Optional<Student> optionalStudent = studentRepository.findById(id);
        return optionalStudent.orElse(null);
    }


    @DeleteMapping("/{id}")
    public String deleteById(@PathVariable Integer id) {


        Optional<Student> optionalStudent = studentRepository.findById(id);
        if (optionalStudent.isPresent()) {
            Student student = optionalStudent.get();
            Address address = student.getAddress();
            Integer addressId = address.getId();
            studentRepository.deleteById(id);
            addressRepository.deleteById(addressId);
            return "Student deleted";
        }
        return "Such student not found";

    }


    @PostMapping
    public String addStudent(@RequestBody StudentDTO studentDTO) {

        Student newStudent = new Student();
        newStudent.setFirstName(studentDTO.getFirstName());
        newStudent.setLastName(studentDTO.getLastName());


        Address address = new Address();
        address.setCity(studentDTO.getCity());
        address.setDistrict(studentDTO.getDistrict());
        address.setStreet(studentDTO.getStreet());

        Address savedAddress = addressRepository.save(address);

        newStudent.setAddress(savedAddress);

        Optional<Group> optionalGroup = groupRepository.findById(studentDTO.getGroupId());
        if (optionalGroup.isPresent()) {
            Group group = optionalGroup.get();
            newStudent.setGroup(group);
            newStudent.setSubjects(studentDTO.getSubjects());

            studentRepository.save(newStudent);
            return "Student added";
        }
        return "Such group not found";
    }


    @PutMapping("/{id}")
    public String editStudent(@PathVariable Integer id, @RequestBody StudentDTO studentDTO) {

        Optional<Student> optionalStudent = studentRepository.findById(id);
        if (optionalStudent.isPresent()) {
            Student editingStudent = optionalStudent.get();
            editingStudent.setFirstName(studentDTO.getFirstName());
            editingStudent.setLastName(studentDTO.getLastName());

            Address address = editingStudent.getAddress();
            address.setCity(studentDTO.getCity());
            address.setDistrict(studentDTO.getDistrict());
            address.setStreet(studentDTO.getStreet());
            Address savedAddress = addressRepository.save(address);
            editingStudent.setAddress(savedAddress);


            Optional<Group> optionalGroup = groupRepository.findById(studentDTO.getGroupId());
            if (optionalGroup.isPresent()) {
                Group editingGroup = optionalGroup.get();
                editingStudent.setGroup(editingGroup);

                editingStudent.setSubjects(studentDTO.getSubjects());
                studentRepository.save(editingStudent);
                return "Student edited";
            } else {
                return "Such group not found";
            }
        }
        return "Such student not found";
    }
}
