package com.example.smsw.controller;

import org.springframework.web.multipart.MultipartFile;

import com.itextpdf.text.Image;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.example.smsw.entity.Student;
import com.example.smsw.service.StudentService;

import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class StudentController {
	
	private StudentService studentService;

	public StudentController(StudentService studentService) {
		super();
		this.studentService = studentService;
	}
	
	
	//handler to remove popup success message as blank 
	@PostMapping("/resetSuccessMessage")
	public ResponseEntity<Void> resetSuccessMessage(HttpSession session) {
		session.removeAttribute("successMessage");
		return ResponseEntity.ok().build();
	}
		
	
	
	
	//what should get and what should not will be done here.
	
	//handler method to handle list of students and return mode and view 

	@GetMapping("/students")
	public String listStudents(
			Model model,
			HttpSession session) {

		if (session.getAttribute("user") == null) {
			return "redirect:/";
		}

		model.addAttribute(
				"students",
				studentService.getAllStudents());

		model.addAttribute(
				"totalStudents",
				studentService.getAllStudents().size());

		return "students";
	}
	
	//get the add student page
	@GetMapping("/students/new")
	public String createStudentForm(Model model,
	                                HttpSession session) {

		if (session.getAttribute("user") == null) {
			return "redirect:/";
		}

		Student student = new Student();
		model.addAttribute("student", student);

		return "create_student";
	}
	
	
	//saving student
	@PostMapping("/students")
	public String saveStudent(
			@ModelAttribute("student") Student student,
			@org.springframework.web.bind.annotation.RequestParam("photoFile")
			MultipartFile photo,
			HttpSession session) {

		try {

			if (!photo.isEmpty()) {

				String fileName =
						System.currentTimeMillis() + "_" +
								photo.getOriginalFilename();

				Path uploadPath =
						Paths.get("uploads");

				if (!Files.exists(uploadPath)) {
					Files.createDirectories(uploadPath);
				}

				photo.transferTo(
						uploadPath.resolve(fileName));

				student.setPhoto(fileName);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		studentService.saveStudent(student);

		session.setAttribute(
				"successMessage",
				"Successfully Added");

		return "redirect:/students";
	}
	
	
	//get the update or edit page
	@GetMapping("/students/edit/{id}")
	public String editStudentForm(@PathVariable Long id,
	                              Model model,
	                              HttpSession session) {

		if (session.getAttribute("user") == null) {
			return "redirect:/";
		}

		model.addAttribute(
				"student",
				studentService.getStudentById(id));

		return "edit_student";
	}
	
	
	//update data into existing table
	@PostMapping("/students/{id}")
	public String updateStudent(@PathVariable Long id,
			@ModelAttribute("student") Student student,
			Model model, HttpSession session) {
		
		//get student from database by id
		Student existingStudent = studentService.getStudentById(id);
		existingStudent.setId(id);
		existingStudent.setFirstName(student.getFirstName());
		existingStudent.setLastName(student.getLastName());
		existingStudent.setEmail(student.getEmail());
		existingStudent.setCollegeName(student.getCollegeName());
		
		//save update student object 
		studentService.updateStudent(existingStudent);
		session.setAttribute("successMessage", "Successfully Updated");
		return "redirect:/students";
	
	}

	@GetMapping("/students/search")
	public String searchStudents(
			@RequestParam("keyword") String keyword,
			Model model,
			HttpSession session) {

		if (session.getAttribute("user") == null) {
			return "redirect:/";
		}

		model.addAttribute(
				"students",
				studentService.searchStudents(keyword));

		return "students";
	}


	@GetMapping("/students/view/{id}")
	public String viewStudent(@PathVariable Long id, Model model) {

		model.addAttribute(
				"student",
				studentService.getStudentById(id));

		return "student_details";
	}

	@GetMapping("/students/pdf/{id}")
	public void generatePdf(
			@PathVariable Long id,
			HttpServletResponse response)
			throws Exception {

		Student student =
				studentService.getStudentById(id);

		response.setContentType("application/pdf");

		String pdfName =
				student.getFirstName() + "_"
						+ student.getLastName()
						+ ".pdf";

		response.setHeader(
				"Content-Disposition",
				"attachment; filename=\"" + pdfName + "\"");

		Document document =
				new Document();

		PdfWriter.getInstance(
				document,
				response.getOutputStream());

		document.open();

		document.add(
				new Paragraph(
						"Student Details"));

		document.add(
				new Paragraph(
						" "));

		if (student.getPhoto() != null &&
				!student.getPhoto().isEmpty()) {

			String imagePath =
					Paths.get(
									"uploads",
									student.getPhoto())
							.toAbsolutePath()
							.toString();

			Image photo =
					Image.getInstance(imagePath);

			photo.scaleToFit(120, 120);

			document.add(photo);

			document.add(
					new Paragraph(" "));
		}


		document.add(
				new Paragraph(
						"First Name : "
								+ student.getFirstName()));

		document.add(
				new Paragraph(
						"Last Name : "
								+ student.getLastName()));

		document.add(
				new Paragraph(
						"Email : "
								+ student.getEmail()));

		document.add(
				new Paragraph(
						"College : "
								+ student.getCollegeName()));

		document.close();
	}



	//handler method to delete student
	@GetMapping("/students/{id}")
	public String deleteStudent(@PathVariable Long id, HttpSession session) {
		studentService.deleteStudentById(id);
		session.setAttribute("successMessage", "Successfully Deleted");
		return "redirect:/students";
	}
	
}
