package top.xubol.bookmanager.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import top.xubol.bookmanager.common.RequireRole;
import top.xubol.bookmanager.common.Result;
import top.xubol.bookmanager.dto.BorrowApplyDTO;
import top.xubol.bookmanager.dto.BorrowQueryDTO;
import top.xubol.bookmanager.entity.BorrowRecord;
import top.xubol.bookmanager.entity.User;
import top.xubol.bookmanager.service.BorrowService;
import top.xubol.bookmanager.service.UserService;
import top.xubol.bookmanager.util.JwtUtils;

@RestController
@RequestMapping("/api")
public class BorrowController {

    @Autowired
    private BorrowService borrowService;

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtils jwtUtils;

    @PostMapping("/borrow/apply")
    public Result<String> applyBorrow(@RequestBody BorrowApplyDTO applyDTO, HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token == null || !token.startsWith("Bearer ")) {
            return Result.unauthorized("未登录");
        }
        try {
            String username = jwtUtils.extractUsername(token.substring(7));
            Result<User> userRes = userService.getUserInfo(username);
            if (userRes.getCode() != 200 || userRes.getData() == null) {
                return Result.failed("用户不存在");
            }
            return borrowService.applyBorrow(userRes.getData().getId(), applyDTO);
        } catch (Exception e) {
            return Result.unauthorized("Token无效");
        }
    }

    @PostMapping("/borrow/return")
    public Result<String> returnBook(@RequestParam Long recordId, HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token == null || !token.startsWith("Bearer ")) {
            return Result.unauthorized("未登录");
        }
        try {
            String username = jwtUtils.extractUsername(token.substring(7));
            Result<User> userRes = userService.getUserInfo(username);
            // 这里如果是管理员，可能不需要校验是否是本人？
            // 简单处理：传入UserId给Service，Service校验是否是本人。
            // 如果是User操作，必须是本人。如果是Admin操作呢？
            // 假设Admin操作也通过此接口？或者管理员有单独接口？
            // 需求说 "处理图书归还流程"，没细说谁操作。
            // 假设这是用户自助归还。
            return borrowService.returnBook(userRes.getData().getId(), recordId);
        } catch (Exception e) {
             return Result.unauthorized("Token无效");
        }
    }

    @GetMapping("/borrow/my-records")
    public Result<Page<BorrowRecord>> getMyRecords(BorrowQueryDTO queryDTO, HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token == null || !token.startsWith("Bearer ")) {
            return Result.unauthorized(null);
        }
        try {
            String username = jwtUtils.extractUsername(token.substring(7));
            Result<User> userRes = userService.getUserInfo(username);
            return borrowService.getMyRecords(userRes.getData().getId(), queryDTO);
        } catch (Exception e) {
             return Result.unauthorized(null);
        }
    }

    @GetMapping("/admin/borrow/list")
    @RequireRole("admin")
    public Result<Page<BorrowRecord>> getRecordPage(BorrowQueryDTO queryDTO) {
        return borrowService.getRecordPage(queryDTO);
    }
    
    @GetMapping("/admin/borrow/overdue")
    @RequireRole("admin")
    public Result<Page<BorrowRecord>> getOverduePage(BorrowQueryDTO queryDTO) {
        queryDTO.setStatus(3); // 强制查询超期
        return borrowService.getRecordPage(queryDTO);
    }
}
