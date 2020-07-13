package com.lhb.nowcoder.controller;

import com.lhb.nowcoder.entity.DiscussPost;
import com.lhb.nowcoder.entity.Page;
import com.lhb.nowcoder.service.ElasticsearchService;
import com.lhb.nowcoder.service.LikeService;
import com.lhb.nowcoder.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.lhb.nowcoder.util.NowCoderConstant.ENTITY_TYPE_POST;

@Controller
public class SearchController {
    @Resource
    private ElasticsearchService elasticsearchService;

    @Resource
    private LikeService likeService;

    @Resource
    private UserService userService;

    @RequestMapping(path = "/search", method = RequestMethod.GET)
    public String search(String keyword, Page page, Model model) {
        org.springframework.data.domain.Page<DiscussPost> searchResult =
                elasticsearchService.searchDiscussPost(keyword, page.getCurrent() - 1, page.getLimit());

        //聚合数据
        List<Map<String,Object>> discussPostList = new ArrayList<>();

        if (searchResult != null) {
            for (DiscussPost discussPost : searchResult) {
                Map<String,Object> map =new HashMap<>();
                map.put("post",discussPost);
                map.put("user",userService.queryById(discussPost.getUserId()));
                map.put("likeCount",likeService.findEntityLikeCount(ENTITY_TYPE_POST,discussPost.getId()));
                discussPostList.add(map);
            }
            model.addAttribute("discussPosts",discussPostList);
            model.addAttribute("keyword",keyword);
            // 分页信息
            page.setPath("/search?keyword="+keyword);
            page.setRows(searchResult == null ? 0 : (int) searchResult.getTotalElements());
        }else {
            String errorMsg = "抱歉,找不到关于"+keyword+"的任何数据";
            model.addAttribute("error",errorMsg);
        }

        return "/site/search";
    }

}
