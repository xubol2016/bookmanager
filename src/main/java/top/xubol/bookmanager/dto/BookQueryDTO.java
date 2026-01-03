package top.xubol.bookmanager.dto;

import lombok.Data;

@Data
public class BookQueryDTO {
    private Integer page = 1;
    private Integer size = 10;
    private String title;
    private String author;
    private String publisher;
    private Long categoryId;
    private Integer status;
}
