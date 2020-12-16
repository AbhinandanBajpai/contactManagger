package com.amart.Controllers;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.Optional;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.amart.Dao.ContactRepository;
import com.amart.Dao.UserRepository;
import com.amart.entities.Contact;
import com.amart.entities.User;
import com.amart.helper.Message;

@Controller
@RequestMapping("/user")
public class UserController {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private ContactRepository contactRepository;

	@ModelAttribute
	public void addCommonData(Model model, Principal principal) {
		String userName = principal.getName();
//		System.out.println("UserName"+" "+userName);

		// getting the data of userName(email) from database.

		User userByUserName = userRepository.getUserByUserName(userName);
//		System.out.println(userByUserName);
		model.addAttribute("user", userByUserName);
	}

	@RequestMapping("/index")
	public String dashBoard(Model model, Principal principal) {// Principal is from spring security and use to fetch the
																// username

		model.addAttribute("title", "User Dashboard");
		return "normal/user_dashboard";
	}

	// open add form handler

	@GetMapping("/add-contact")
	public String addContactForm(Model model) {

		model.addAttribute("title", "Add Contact");
		model.addAttribute("contact", new Contact());
		return "/normal/add_contact_form";
	}

	// processing add contact form

	@PostMapping("/process-contact")
	public String processContact(@ModelAttribute Contact contact, @RequestParam("profileImage") MultipartFile file,
			Principal principal) {
		try {
			String name = principal.getName();
			User user = userRepository.getUserByUserName(name);

			// processing the image

			if (file.isEmpty()) {
//			System.out.println("Image File Is Emplty");
				contact.setImage("contact.ico");
			} else {
				contact.setImage(file.getOriginalFilename());

				File saveFile = new ClassPathResource("static/img").getFile();

				Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + file.getOriginalFilename());

				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
//			System.out.println("Image Is Uploaded");
			}

			contact.setUser(user);
			user.getContacts().add(contact);
			userRepository.save(user);
//		System.out.println("Added To Database");
//		System.out.println("Data"+contact);
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("Error" + e.getMessage());
//			e.printStackTrace();
		}
		return "/normal/add_contact_form";
	}

	// show contacts handler

	@GetMapping("/show-contacts/{page}")
	public String showContacts(@PathVariable("page") int page, Model model, Principal principal) {
		model.addAttribute("title", "show user contacts");
//		String userName = principal.getName();
//		User user = userRepository.getUserByUserName(userName);
//		List<Contact> contacts = user.getContacts();
		String userName = principal.getName();
		User user = userRepository.getUserByUserName(userName);

		Pageable pageable = PageRequest.of(page, 5);

		Page<Contact> contacts = contactRepository.findContactsByUser(user.getId(), pageable);

		model.addAttribute("contacts", contacts);
		model.addAttribute("currentPage", page);
		model.addAttribute("totalPages", contacts.getTotalPages());

		return "normal/show_contacts";
	}

	// showing specific contact detail

	@RequestMapping("/contact/{cId}")
	public String showContctDetail(@PathVariable("cId") int cId, Model model, Principal principal) {

		Optional<Contact> contactOptional = contactRepository.findById(cId);
		Contact contact = contactOptional.get();

		String userName = principal.getName();

		User user = userRepository.getUserByUserName(userName);
		if (user.getId() == contact.getUser().getId()) {
			model.addAttribute("contact", contact);
		}

		return "normal/contact_detail";
	}

	// delete Conacte
	@GetMapping("/delete/{cId}")
	public String deleteContact(@PathVariable("cId") int cId, Model model, HttpSession session, Principal principal) {
		Optional<Contact> contactOptional = contactRepository.findById(cId);
		Contact contact = contactOptional.get();

		contact.setUser(null);

		contactRepository.deleteById(contact.getcId());

		session.setAttribute("message", new Message("Contact deleted successfully", "success"));

		return "redirect:/user/show-contacts/0";
	}

	// Update Contact Start
	@PostMapping("/update-contact/{cId}")
	public String openUpdateForm(Model model, @PathVariable("cId") int cId) {

		model.addAttribute("title", "Update Contact");

		Contact contact = contactRepository.findById(cId).get();
		model.addAttribute("contact", contact);
		return "normal/update_form";
	}

	@PostMapping("/process-update")
	private String updateHandler(Model model, @ModelAttribute Contact contact,
			@RequestParam("profileImage") MultipartFile file, Principal principal) {
		// fetching old contact details
		try {

			Contact oldContactDetail = contactRepository.findById(contact.getcId()).get();

			if (!file.isEmpty()) {
				// delete old pic

				File deleteFile = new ClassPathResource("static/img").getFile();
				File file1 = new File(deleteFile, oldContactDetail.getImage());
				file1.delete();

				// update new pic

				File saveFile = new ClassPathResource("static/img").getFile();

				Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + file.getOriginalFilename());

				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
				contact.setImage(file.getOriginalFilename());
			} else {
				contact.setImage(oldContactDetail.getImage());
			}

			User user = userRepository.getUserByUserName(principal.getName());
			contact.setUser(user);
			contactRepository.save(contact);

		} catch (Exception e) {
			// TODO: handle exception
		}

		return "redirect:/user/contact/" + contact.getcId();
	}

	// Update Contact End

	// your profile handler start
	@GetMapping("/profile")
	public String yourProfile(Model model) {
		model.addAttribute("title", "Profile Page");
		return "normal/profile";
	}

	// your profile handler end
}
