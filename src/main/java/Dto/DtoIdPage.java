package Dto;

public class DtoIdPage {
    
    private int id;
    private int page;
    private int size;

    public DtoIdPage(int id, int page, int size) {
        this.id = id;
        this.page = page;
        this.size = size;
    }

    public DtoIdPage() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }
    
}