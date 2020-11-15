package com.amart.Controllers;

import java.awt.print.Printable;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.hibernate.internal.build.AllowPrintStacktrace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.amart.Dao.UserRepository;
import com.amart.entities.User;
import com.amart.helper.Message;

@Controller
public class HomeController {

	@Autowired
	private UserRepository userRepository;
//	
//	@GetMapping("/test")
//	@ResponseBody
//	public String test() {
//		User user = new User();
//		user.setName("Abhinandan");
//		user.setEmail("abhi.bajpai326@gmail.com");
//		userRepository.save(user);
//		return "working";
//	}
	
	@GetMapping("/")
	public String home(Model model) {
		
		model.addAttribute("title", "Home-Smart Contact");
		return "home";
	}
	
	
	@GetMapping("/about")
	public String about(Model model) {
		
		model.addAttribute("title", "Home-Smart Contact");
		return "about";
	}
	
	@GetMapping("/signup")
	public String signUp(Model model) {
		
		model.addAttribute("user", new User());
		return "signup";
	}
	@PostMapping("/do_register")
	public String registerUser(@Valid @ModelAttribute("user") User user,BindingResult bindingResult, @RequestParam(value="agreement", defaultValue = "false")  
	                           Boolean agreement, Model model , HttpSession session) {
		
	try {
		
		if(!agreement) {

			System.out.println("please agree the term and conditions");
			throw new Exception("please agree the term and conditions");
		}
		if(bindingResult.hasErrors()) {
			System.out.println("Error"+" "+bindingResult.toString());
			model.addAttribute("user", user);
			return "signup";
		}
		
		
		user.setRole("ROLE_USER");
		user.setEnabled(true);
		User result = userRepository.save(user);
		
		model.addAttribute("user", result);
		
		System.out.println("Agreement"+" "+ agreement+" USER "+" "+user);
		 model.addAttribute("user", new User());
		    session.setAttribute("message", new Message("Registered Sucessfully", "alert-success"));
		    return "signup";
		
	} catch (Exception e) {
		// TODO: handle exception
	    e.printStackTrace();
	    model.addAttribute("user", user);
	    session.setAttribute("message", new Message("Something went wrong"+e.getMessage(), "alert-danger"));
		return "signup";
	}
	
	}
	}
