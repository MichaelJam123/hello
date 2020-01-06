package app.com.seehope.vo;

import lombok.Data;

/**
 * 数据分页
 */
@Data
public class PageVo {

    private Integer pageSize;
    private Integer page;
    private Integer limit;
    private Integer offset;
    private boolean tottal;


}
