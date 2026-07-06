package com.easyjava.entity.query;
/*这是一个分页计算类 SimplePage，它封装了完整的分页逻辑和计算*/
import com.easyjava.enums.PageSize;
public class SimplePage {
    private Integer pageNo;
    private Integer countTotal;
    private Integer pageSize;
    private Integer pageTotal;
    private Integer start;
    private Integer end;

    private SimplePage() {

    }

    public SimplePage(Integer pageNo, Integer countTotal, Integer pageSize) {
        if (pageNo == null) {
            pageNo = 0;
        }
        this.pageNo = pageNo;
        this.countTotal = countTotal;
        this.pageSize = pageSize;
        action();
    }

   public void action() {
       // 1. 保护性设置pageSize（保留合法值）
       if (this.pageSize == null || this.pageSize <= 0) {
           this.pageSize = PageSize.SIZE20.getSize(); // 仅当非法时才覆盖
       }

       // 2. 计算总页数（更精确的算法）
       this.pageTotal = (this.countTotal + this.pageSize - 1) / this.pageSize;

       // 3. 保护性设置pageNo（仅校正非法值）
       if (this.pageNo == null || this.pageNo < 1) {
           this.pageNo = 1; // 仅处理null或负值
       } else if (this.pageNo > this.pageTotal && this.pageTotal > 0) {
           this.pageNo = this.pageTotal; // 仅当超出最大页时调整
       }

       // 4. 计算分页区间
       this.start = (this.pageNo - 1) * this.pageSize;
       this.end = this.pageSize;
   }
    public Integer getPageNo() {
        return pageNo;
    }

    public void setPageNo(int pageNo) {
        this.pageNo = pageNo;
    }

    public Integer getCountTotal() {
        return countTotal;
    }

    public void setCountTotal(int countTotal) {
        this.countTotal = countTotal;
        this.action();
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public Integer getPageTotal() {
        return pageTotal;
    }

    public void setPageTotal(int pageTotal) {
        this.pageTotal = pageTotal;
    }

    public Integer getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public Integer getEnd() {
        return end;
    }

    public void setEnd(int end) {
        this.end = end;
    }
}
