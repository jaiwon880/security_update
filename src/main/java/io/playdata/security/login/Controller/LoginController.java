package io.playdata.security.login.Controller;

import io.playdata.security.board.service.BoardService;
import io.playdata.security.login.model.AccountDTO;
import io.playdata.security.login.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.security.auth.login.AccountNotFoundException;
import java.security.Principal;

@Controller
@RequestMapping("/")
public class LoginController {
    @Autowired
    private LoginService loginService;
    @Autowired
    private BoardService boardService;

    @PostMapping("/register")
    public String register(AccountDTO user,
                           RedirectAttributes redirectAttributes) {
        try {
            loginService.register(user);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("msg", e.getMessage());
            return "redirect:/register";
        }

        redirectAttributes.addFlashAttribute("msg", "정상 등록 되었습니다");
        return "redirect:/";
    }

    @GetMapping("/register")
    public String register(Model model) {
        // TODO : 로그인 여부 감지 -> 제한
        model.addAttribute("user", new AccountDTO());
        return "register";
    }

    @GetMapping
    public String index() {
        return "home";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/home")
    public String home(Model model) {

        return "home";
    }

    @GetMapping("/profile")
    public String profile(Model model, Principal principal) {
        String username = principal.getName();
        AccountDTO account = loginService.getAccountByUsername(username);
        model.addAttribute("account", account);
        return "profile/profile";
    }

    @GetMapping("/profile/delete")
    public String deleteProfileConfirmation(Model model) {
        return "profile/profile_delete";
    }

    @PostMapping("/profile/delete")
    public String deleteProfile(RedirectAttributes redirectAttributes, Principal principal) {
        String username = principal.getName();
        try {
            loginService.deleteAccountByUsername(username);
            boardService.deleteAllByUsername(username);
            SecurityContextHolder.clearContext(); // 로그아웃 처리

            redirectAttributes.addFlashAttribute("msg", "회원 탈퇴되었습니다");

            return "redirect:/";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/profile";
        }
    }

    @GetMapping("/profile/edit")
    public String editProfile(Model model, Principal principal) {
        String username = principal.getName();
        AccountDTO account = loginService.getAccountByUsername(username);
        model.addAttribute("account", account);
        return "profile/profile_update";
    }

    @PostMapping("/profile/edit")
    public String updateProfile(AccountDTO updatedAccount, RedirectAttributes redirectAttributes) {
        try {
            loginService.updateAccount(updatedAccount);
            redirectAttributes.addFlashAttribute("msg", "프로필이 수정되었습니다.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/profile";
    }
}