package com.lhb.nowcoder.controller;

import com.lhb.nowcoder.annotation.LoginRequire;
import com.lhb.nowcoder.entity.User;
import com.lhb.nowcoder.service.FollowService;
import com.lhb.nowcoder.service.LikeService;
import com.lhb.nowcoder.service.UserService;
import com.lhb.nowcoder.util.CookieUtil;
import com.lhb.nowcoder.util.HostHolder;
import com.lhb.nowcoder.util.NowCoderUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

import static com.lhb.nowcoder.util.NowCoderConstant.*;

@Controller
@RequestMapping("/user")
@Slf4j
public class UserController {

    @Value("${community.path.upload}")
    private String uploadPath;

    @Value("${community.path.domain}")
    private String domain;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Resource
    private HostHolder hostHolder;

    @Resource
    private UserService userService;

    @Resource
    private LikeService likeService;

    @Resource
    private FollowService followService;

    @LoginRequire
    @RequestMapping(path = "/setting", method = RequestMethod.GET)
    public String getSettingPage() {
        return "/site/setting";
    }

    @LoginRequire
    @RequestMapping(path = "/upload", method = RequestMethod.POST)
    public String uploadHeader(MultipartFile headerImage, Model model) {
        if (headerImage == null) {
            model.addAttribute("error", "您还没有选择图片!");
            return "/site/setting";
        }

        String filename = headerImage.getOriginalFilename();
        String suffix = filename.substring(filename.lastIndexOf("."));
        if (StringUtils.isBlank(suffix)) {
            model.addAttribute("error", "文件的格式不正确!");
            return "/site/setting";
        }

        // 生成随机的文件名
        filename = NowCoderUtil.generateUUID() + suffix;
        File dest = new File(uploadPath + "\\" + filename);
        try {
            headerImage.transferTo(dest);
        } catch (IOException e) {
            log.error("上传文件失败: " + e.getMessage());
            throw new RuntimeException("上传文件失败,服务器发生异常!", e);
        }
        User user = hostHolder.getUser();
        String headerImgUrl = domain + contextPath + "/user/header/" + filename;
        userService.updateHeader(user.getId(), headerImgUrl);

        return "redirect:/index";
    }

    @RequestMapping(path = "/header/{filename}", method = RequestMethod.GET)
    public void getHeader(@PathVariable String filename, HttpServletResponse response) {
        filename = uploadPath + "\\" + filename;
        String suffix = filename.substring(filename.lastIndexOf("."));
        // 响应图片
        response.setContentType("image/" + suffix);
        try (
                FileInputStream fis = new FileInputStream(filename);
                OutputStream os = response.getOutputStream();
        ) {
            byte[] buffer = new byte[1024];
            int b = 0;
            while ((b = fis.read(buffer)) != -1) {
                os.write(buffer, 0, b);
            }
        } catch (IOException e) {
            log.error("读取头像失败: " + e.getMessage());
        }
    }

    @LoginRequire
    @RequestMapping(path = "/updatePassword", method = RequestMethod.POST)
    public String updatePassword(@CookieValue("ticket") String ticket, String oldPassword, String newPassword, String confirmPassword, Model model,
                                 HttpServletRequest request, HttpServletResponse response) {
        Map<String, Object> map = userService.updatePassword(oldPassword, newPassword, confirmPassword);
        if (map != null) {
            model.addAttribute("oldPasswordError", map.get("oldPasswordError"));
            model.addAttribute("newPasswordError", map.get("newPasswordError"));
            model.addAttribute("confirmPasswordError", map.get("confirmPasswordError"));
            return "/site/setting";
        }
        userService.logout(ticket);
        CookieUtil.removeCookie(request, "ticket", response);
        return "redirect:/login";
    }

    @RequestMapping(path = "/profile/{userId}", method = RequestMethod.GET)
    public String getProfilePage(@PathVariable int userId, Model model) {
        User currentUser = hostHolder.getUser();

        User user = userService.findUserById(userId);
        if (user == null) {
            throw new RuntimeException("该用户不存在");
        }
        // 用户
        model.addAttribute("user", user);
        // 点赞数
        Integer userLikeCount = likeService.findUserLikeCount(userId);
        System.out.println(userLikeCount);
        model.addAttribute("likeCount", userLikeCount);
        // 关注数
        long followeeCount = followService.findFolloweeCount(userId, ENTITY_TYPE_USER);
        model.addAttribute("followeeCount", followeeCount);
        // 粉丝数
        long followerCount = followService.findFollowerCount(userId, ENTITY_TYPE_USER);
        model.addAttribute("followerCount", followerCount);

        // 关注状态
        boolean hasFollowed;
        if (currentUser == null) {
            hasFollowed = false;
        } else {
            hasFollowed = followService.hasFollowed(currentUser.getId(), ENTITY_TYPE_USER, userId);
        }
        model.addAttribute("hasFollowed", hasFollowed);
        return "/site/profile";

    }

}
