package com.kwin.forum.controller;

import com.kwin.forum.annotation.LoginRequired;
import com.kwin.forum.entity.User;
import com.kwin.forum.service.FollowService;
import com.kwin.forum.service.LikeService;
import com.kwin.forum.service.UserService;
import com.kwin.forum.util.HostHolder;
import com.kwin.forum.util.JsonUtils;
import com.kwin.forum.util.UUIDUtils;
import com.qiniu.util.Auth;
import com.qiniu.util.StringMap;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

import static com.kwin.forum.contants.CommentContent.ENTITY_TYPE_USER;

@Controller
@RequestMapping("/user")
public class UserController extends BaseController {
    @Autowired
    private UserService userService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private LikeService likeService;

    @Autowired
    private FollowService followService;

    @Value("${forum.path.upload}")
    private String uploadPath;

    @Value("${forum.path.domain}")
    private String domain;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Value("${qiniu.key.access}")
    private String accessKey;

    @Value("${qiniu.key.secret}")
    private String secretKey;

    @Value("${qiniu.bucket.header.name}")
    private String headerBucketName;

    @Value("${qiniu.bucket.header.url}")
    private String headerBucketUrl;

    @LoginRequired
    @GetMapping(path = "/setting")
    public String  getSettingPage(Model model) {
        //上传文件名称
        String fileName = UUIDUtils.generateUUID();
        //设置响应信息
        StringMap policy = new StringMap();
        policy.put("returnBody", JsonUtils.getJSONString(0));
        //生成上传凭证
        Auth auth = Auth.create(accessKey,secretKey);
        String uploadToken = auth.uploadToken(headerBucketName,fileName,3600,policy);

        model.addAttribute("uploadToken",uploadToken);
        model.addAttribute("fileName",fileName);

        return "/site/setting";
    }

    //更新头像路径
    @PostMapping(path = "/header/url")
    @ResponseBody
    public String updateHeaderUrl(String fileName) {
        if (StringUtils.isBlank(fileName)) {
            return JsonUtils.getJSONString(1,"文件名不能为空!");
        }

        String url = headerBucketUrl + "/" + fileName;

        userService.updateHeader(hostHolder.getUser().getId(),url);

        return JsonUtils.getJSONString(0);
    }

    //废弃
    /**
     * 上传头像，并更新登录用户的头像
     * @param headerImage
     * @param model
     * @return
     */
    @LoginRequired
    @PostMapping(path = "/upload")
    public String uploadHeader(MultipartFile headerImage, Model model) {
        logger.info("准备上传头像");
        if (headerImage == null) {
            model.addAttribute("error","您还没有选择图片!");
            return "/site/setting";
        }

        String fileName = headerImage.getOriginalFilename();
        String suffix = fileName.substring(fileName.lastIndexOf("."));

        if (StringUtils.isBlank(suffix)) {
            model.addAttribute("error","文件的格式不正确!");
            return "/site/setting";
        }

        //生成随机的文件名
        fileName = UUIDUtils.generateUUID() + suffix;

        //确定文件的存放路径
        logger.info("确定文件的存放路径:" + uploadPath + "/" + fileName);
        File dest = new File(uploadPath + "/" + fileName);
        try {
            headerImage.transferTo(dest);
        } catch (IOException e) {
            logger.error("上传文件失败:" + e.getMessage());
            throw new RuntimeException("上传文件失败，服务器发生异常!",e);
        }

        //更新当前用户的头像
        logger.info("头像上传成功，更新当前用户头像");
        User user = hostHolder.getUser();
        String headerUrl = domain + contextPath + "/user/header/" + fileName;
        userService.updateHeader(user.getId(),headerUrl);

        return "redirect:/index";
    }

    //废弃
    /**
     * 获取头像
     * @param fileName
     * @param response
     */
    @GetMapping(path = "/header/{fileName}")
    public void getHeader(@PathVariable("fileName") String fileName, HttpServletResponse response) {
        logger.info("读取头像" + fileName);
        //服务器存放路径
        fileName = uploadPath + "/" + fileName;
        //文件的后缀
        String suffix = fileName.substring(fileName.lastIndexOf("."));

        //响应图片
        response.setContentType("/image/" + suffix);
        try(
            FileInputStream in = new FileInputStream(fileName);
            OutputStream os = response.getOutputStream();
        ){
            byte[] buffer = new byte[1024];
            int b = 0;
            while((b = in.read(buffer)) != -1) {
                os.write(buffer,0,b);
            }
        } catch (Exception e) {
            logger.error("读取文件失败:" + e.getMessage());
        }
    }

    /**
     * 获取个人主页
     * @param userId
     * @param model
     * @return
     */
    @GetMapping(path = "/profile/{userId}")
    public String getProfilePage(@PathVariable("userId") int userId,Model model) {
        User user = userService.findUserById(userId);
        if (user == null) {
            throw new RuntimeException("该用户不存在!");
        }

        //用户
        model.addAttribute("user",user);
        //点赞数量
        int likeCount = likeService.findUserLikeCount(userId);
        model.addAttribute("likeCount",likeCount);

        //关注数量
        long followeeCount = followService.findFolloweeCount(userId,ENTITY_TYPE_USER);
        model.addAttribute("followeeCount",followeeCount);
        //粉丝数量
        long followerCount = followService.findFollowerCount(ENTITY_TYPE_USER,userId);
        model.addAttribute("followerCount",followerCount);
        //是否已关注
        boolean hasFollowed = false;
        if (hostHolder.getUser() != null) {
            hasFollowed = followService.hasFollowed(hostHolder.getUser().getId(),ENTITY_TYPE_USER,userId);
        }
        model.addAttribute("hasFollowed",hasFollowed);

        return "/site/profile";
    }
}
