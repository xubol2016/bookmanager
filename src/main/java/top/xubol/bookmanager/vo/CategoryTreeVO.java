package top.xubol.bookmanager.vo;

import lombok.Data;
import java.io.Serializable;
import java.util.List;

@Data
public class CategoryTreeVO implements Serializable {
    
    private Long id;

    private String name;

    private Long parentId;
    
    private Integer level;
    
    private Integer sortOrder;

    private List<CategoryTreeVO> children;
}
