package top.xubol.bookmanager.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import top.xubol.bookmanager.dto.BookQueryDTO;
import top.xubol.bookmanager.dto.BookSaveDTO;
import top.xubol.bookmanager.dto.BookUpdateDTO;
import top.xubol.bookmanager.entity.Book;

import java.util.List;

public interface BookService extends IService<Book> {

    /**
     * 分页查询图书
     */
    Page<Book> getBookPage(BookQueryDTO queryDTO);

    /**
     * 添加图书
     */
    void addBook(BookSaveDTO saveDTO);

    /**
     * 更新图书
     */
    void updateBook(BookUpdateDTO updateDTO);

    /**
     * 删除图书 (支持批量)
     */
    void deleteBook(List<Long> ids);

    /**
     * 更新图书状态 (上架/下架)
     */
    void updateStatus(Long id, Integer status);
}
