package top.xubol.bookmanager.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import top.xubol.bookmanager.common.Result;
import top.xubol.bookmanager.dto.BookQueryDTO;
import top.xubol.bookmanager.dto.BookSaveDTO;
import top.xubol.bookmanager.dto.BookUpdateDTO;
import top.xubol.bookmanager.entity.Book;
import top.xubol.bookmanager.entity.BorrowRecord;
import top.xubol.bookmanager.mapper.BookMapper;
import top.xubol.bookmanager.mapper.BorrowRecordMapper;
import top.xubol.bookmanager.service.BookService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BookServiceImpl extends ServiceImpl<BookMapper, Book> implements BookService {

    private final BorrowRecordMapper borrowRecordMapper;

    @Override
    public Page<Book> getBookPage(BookQueryDTO queryDTO) {
        Page<Book> page = new Page<>(queryDTO.getPage(), queryDTO.getSize());
        LambdaQueryWrapper<Book> wrapper = new LambdaQueryWrapper<>();

        if (StringUtils.hasText(queryDTO.getTitle())) {
            wrapper.like(Book::getTitle, queryDTO.getTitle());
        }
        if (StringUtils.hasText(queryDTO.getAuthor())) {
            wrapper.like(Book::getAuthor, queryDTO.getAuthor());
        }
        if (StringUtils.hasText(queryDTO.getPublisher())) {
            wrapper.like(Book::getPublisher, queryDTO.getPublisher());
        }
        if (queryDTO.getCategoryId() != null) {
            wrapper.eq(Book::getCategoryId, queryDTO.getCategoryId());
        }
        if (queryDTO.getStatus() != null) {
            wrapper.eq(Book::getStatus, queryDTO.getStatus());
        }
        
        wrapper.orderByDesc(Book::getId);

        return this.page(page, wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addBook(BookSaveDTO saveDTO) {
        // 校验ISBN唯一性
        Long count = this.count(new LambdaQueryWrapper<Book>().eq(Book::getIsbn, saveDTO.getIsbn()));
        if (count > 0) {
            throw new RuntimeException("ISBN已存在: " + saveDTO.getIsbn());
        }

        Book book = new Book();
        BeanUtils.copyProperties(saveDTO, book);
        // 默认上架
        if (book.getStatus() == null) {
            book.setStatus(1);
        }
        this.save(book);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateBook(BookUpdateDTO updateDTO) {
        Book book = this.getById(updateDTO.getId());
        if (book == null) {
            throw new RuntimeException("图书不存在");
        }

        // 如果修改了ISBN，需要校验唯一性
        if (StringUtils.hasText(updateDTO.getIsbn()) && !updateDTO.getIsbn().equals(book.getIsbn())) {
             Long count = this.count(new LambdaQueryWrapper<Book>().eq(Book::getIsbn, updateDTO.getIsbn()));
             if (count > 0) {
                 throw new RuntimeException("ISBN已存在: " + updateDTO.getIsbn());
             }
        }

        BeanUtils.copyProperties(updateDTO, book);
        this.updateById(book);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteBook(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return;
        }

        for (Long id : ids) {
            // 检查是否有借阅记录 (未归还的?)
            // Requirement: "检查图书是否有借阅记录" - generally implies any records might block physical delete,
            // but for soft delete, usually we care about active borrowings.
            // Spec says "check if book has borrowing records".
            // If strictly "has records", we can't delete popular books even if returned.
            // Let's assume we check for "Active" borrowing (Status = 1).
            Long activeBorrowCount = borrowRecordMapper.selectCount(new LambdaQueryWrapper<BorrowRecord>()
                    .eq(BorrowRecord::getBookId, id)
                    .eq(BorrowRecord::getStatus, 1)); // 1: 借阅中

            if (activeBorrowCount > 0) {
                 Book book = this.getById(id);
                 throw new RuntimeException("图书 [" + (book != null ? book.getTitle() : id) + "] 当前有未归还的借阅记录，无法删除");
            }
        }

        this.removeBatchByIds(ids);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateStatus(Long id, Integer status) {
        Book book = this.getById(id);
        if (book == null) {
             throw new RuntimeException("图书不存在");
        }
        book.setStatus(status);
        this.updateById(book);
    }
}
