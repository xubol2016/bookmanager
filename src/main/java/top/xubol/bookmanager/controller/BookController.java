package top.xubol.bookmanager.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import top.xubol.bookmanager.common.RequireRole;
import top.xubol.bookmanager.common.Result;
import top.xubol.bookmanager.dto.BookQueryDTO;
import top.xubol.bookmanager.dto.BookSaveDTO;
import top.xubol.bookmanager.dto.BookUpdateDTO;
import top.xubol.bookmanager.entity.Book;
import top.xubol.bookmanager.service.BookService;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api")
public class BookController {

    @Autowired
    private BookService bookService;

    @Value("${file.upload.path}")
    private String uploadPath;

    @GetMapping("/books")
    public Result<Page<Book>> getBookPage(BookQueryDTO queryDTO) {
        Page<Book> page = bookService.getBookPage(queryDTO);
        return Result.success(page);
    }

    @GetMapping("/books/{id}")
    public Result<Book> getBookById(@PathVariable Long id) {
        Book book = bookService.getById(id);
        if (book == null) {
            return Result.failed("图书不存在");
        }
        return Result.success(book);
    }

    @PostMapping("/admin/books")
    @RequireRole("admin")
    public Result<String> addBook(@RequestBody BookSaveDTO saveDTO) {
        bookService.addBook(saveDTO);
        return Result.success("添加成功");
    }

    @PutMapping("/admin/books")
    @RequireRole("admin")
    public Result<String> updateBook(@RequestBody BookUpdateDTO updateDTO) {
        bookService.updateBook(updateDTO);
        return Result.success("更新成功");
    }

    @DeleteMapping("/admin/books/{id}")
    @RequireRole("admin")
    public Result<String> deleteBook(@PathVariable Long id) {
        bookService.deleteBook(Collections.singletonList(id));
        return Result.success("删除成功");
    }
    
    @PostMapping("/admin/books/batch-delete")
    @RequireRole("admin")
    public Result<String> deleteBooks(@RequestBody List<Long> ids) {
        bookService.deleteBook(ids);
        return Result.success("批量删除成功");
    }

    @PostMapping("/admin/books/upload")
    @RequireRole("admin")
    public Result<String> uploadCover(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return Result.validateFailed("文件不能为空");
        }

        // 获取文件名
        String originalFilename = file.getOriginalFilename();
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
        // 生成新文件名
        String fileName = UUID.randomUUID().toString() + suffix;

        // 创建文件对象
        File dest = new File(uploadPath + fileName);
        if (!dest.getParentFile().exists()) {
            dest.getParentFile().mkdirs();
        }

        try {
            file.transferTo(dest);
            // 返回相对路径或URL，这里假设前端配置了静态资源映射
            // 假设映射路径为 /uploads/** -> file:uploads/
            // 返回 "uploads/" + fileName
            // 实际上 config 中 file.upload.path 是 uploads/
            // 如果 Spring Boot 配置了静态资源映射，通常我们可以直接返回访问路径
            return Result.success("uploads/" + fileName);
        } catch (IOException e) {
            e.printStackTrace();
            return Result.failed("文件上传失败");
        }
    }
}
