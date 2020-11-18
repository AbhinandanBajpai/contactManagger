package com.amart.Controllers;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.amart.Dao.UserRepository;
import com.amart.entities.User;

@Controller
@RequestMapping("/user")
public class UserController {

	@Autowired
	private UserRepository userRepository;
	@RequestMapping("/index")
	public String dashBoard(Model model, Principal principal) {//Principal is from spring security and use to fetch the username
		String userName = principal.getName();
		System.out.println("UserName"+" "+userName);
		
		
		// getting the data of userName(email) from database.
		
		User userByUserName = userRepository.getUserByUserName(userName);
		System.out.println(userByUserName);
		model.addAttribute("user", userByUserName);
		
		
		
		return "normal/user_dashboard";
	}
}
