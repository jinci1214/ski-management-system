package com.ski.ticketing.controller.user;

import com.ski.ticketing.pojo.entity.Courses;
import com.ski.ticketing.pojo.entity.Orders;
import com.ski.ticketing.result.Result;
import com.ski.ticketing.service.CourseService;
import com.ski.ticketing.service.VisitorOrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import org.springframework.security.core.Authentication;

@RestController("userCourseController")
@RequestMapping("/user/course")
@Slf4j
public class CourseController {

    @Autowired
    private CourseService courseService;

    @Autowired
    private VisitorOrderService visitorOrderService;

    @GetMapping("/list")
    public Result<List<Courses>> list() {
        List<Courses> list = courseService.getAll();
        return Result.success(list);
    }

    @PostMapping("/join/{courseId}")
    public Result<Orders> joinCourse(@PathVariable Long courseId, Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        log.info("用户{}加入课程{}", userId, courseId);
        return Result.success(visitorOrderService.joinCourse(userId, courseId));
    }
}
