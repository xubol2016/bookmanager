package top.xubol.bookmanager.dto;

import lombok.Data;

@Data
public class BookUpdateDTO {
    private Long id;
    private String title;
    private String author;
    private String publisher;
    private String isbn;
    private String coverUrl;
    private Long categoryId;
    private Integer stock;
    private String location;
    private String description;
    private Integer status;
}
