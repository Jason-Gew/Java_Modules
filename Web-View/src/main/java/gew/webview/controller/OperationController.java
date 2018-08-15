package gew.webview.controller;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.List;

import gew.webview.config.AppMessageConfig;
import gew.webview.model.Status;
import gew.webview.model.User;
import gew.webview.service.RegistrationService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;


@Log4j2
@Controller
public class OperationController {

    @Autowired
    private AppMessageConfig appMessageConfig;

    @Autowired
    private RegistrationService registrationService;

    private static final String ADD_USER_URL = "addUser";

    @GetMapping(value = {"/", "/index" })
    public String index(Model model) {

        model.addAttribute("welcome", appMessageConfig.getWelcome());
        model.addAttribute("localDateTime", ZonedDateTime.now());
        return "index";
    }

    @GetMapping(value = { "/userList", "/users"})
    public ModelAndView userListDisplay(Model model) {

        ModelAndView mv = new ModelAndView("/userList");
        List<User> users = registrationService.getAllUsers();
        log.info("-> Get Current User List (Total: {})", users.size());
        mv.addObject("users", users);

        return mv;
    }

    @GetMapping(value = {"/addUser" })
    public ModelAndView addNewUserDisplay() {
        ModelAndView mv = new ModelAndView("/addUser");
        User user = new User();
        mv.addObject("newUser", user);

        return mv;
    }

    @PostMapping(value = { "/addUser" })
    public String saveUser(Model model, @ModelAttribute("newUser") User user) {

        String check = user.validateUserInfo();
        if (!check.equalsIgnoreCase(Status.SUCCESS.toString())) {
            model.addAttribute("result", check);
            log.error("Add User [{}] Failed:", user.toString(), check);
            return ADD_USER_URL;
        } if (registrationService.exist(user)) {
            model.addAttribute("result", appMessageConfig.getDuplicateUsername());
            log.error("Add User [{}] Failed:", user.toString(), "User Already Exist");
            return ADD_USER_URL;
        } else {
            check = registrationService.addUser(user);
            model.addAttribute("result", "Registration " + check);
            log.info("Add User [{}] : {}", user.toString(), check);
            return ADD_USER_URL;
        }

    }

}