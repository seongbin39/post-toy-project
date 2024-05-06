package store.sbin.postservice.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import store.sbin.postservice.service.post.PostService;

@Controller
public class HomeController {

    private final PostService postService;

    public HomeController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping({"/", "index.html", "home"})
    public String home(Model model) {
        model.addAttribute("postList", postService.getRecentPostList());
        model.addAttribute("noticeList", postService.getNoticeList());
        return "content/home";
    }

    @GetMapping("/login")
    public String login(Authentication authentication) {
        // 이미 로그인한 경우 메인 페이지로 리다이렉트
        if (authentication != null && authentication.isAuthenticated()) {
            return "redirect:/";
        }

        return "content/users/login";
    }

    @PostMapping("/menu")
    public String menu() {
        return "content/menu";
    }

    @GetMapping("/signup")
    public String signUp() {
        return "content/users/signup";
    }

}
