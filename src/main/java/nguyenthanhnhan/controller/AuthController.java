package nguyenthanhnhan.controller;

import jakarta.validation.Valid;
import nguyenthanhnhan.dto.RegisterRequest;
import nguyenthanhnhan.service.AccountService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthController {

    private final AccountService accountService;

    public AuthController(AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping("/login")
    public String loginPage(
            @RequestParam(required = false) String error,
            @RequestParam(required = false) String logout,
            Model model) {
        model.addAttribute("hasError", error != null);
        model.addAttribute("hasLogout", logout != null);
        return "auth/login";
    }

    @GetMapping("/register")
    public String registerPage(@RequestParam(required = false) String success, Model model) {
        if (!model.containsAttribute("registerRequest")) {
            model.addAttribute("registerRequest", new RegisterRequest());
        }
        model.addAttribute("success", success != null);
        return "auth/register";
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute RegisterRequest registerRequest, BindingResult bindingResult, Model model) {
        if (!registerRequest.getPassword().equals(registerRequest.getConfirmPassword())) {
            bindingResult.rejectValue("confirmPassword", "password.mismatch", "Mật khẩu nhập lại không khớp");
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("success", false);
            return "auth/register";
        }

        try {
            accountService.registerUser(registerRequest.getLoginName(), registerRequest.getPassword());
            return "redirect:/register?success=1";
        } catch (IllegalArgumentException e) {
            bindingResult.rejectValue("loginName", "loginName.duplicate", e.getMessage());
            model.addAttribute("success", false);
            return "auth/register";
        }
    }
}
