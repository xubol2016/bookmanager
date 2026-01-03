package top.xubol.bookmanager.dto;

import lombok.Data;

@Data
public class CategoryDTO {
    
    private String name;

    private Long parentId;

    private Integer sortOrder;
}
