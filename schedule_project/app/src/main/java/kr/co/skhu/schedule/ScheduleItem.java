package kr.co.skhu.schedule;

public class ScheduleItem {

    private int id;
    private int checked;
    private String title;
    private String content;
    private String date;

    public ScheduleItem() {

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getChecked() {
        return checked;
    }

    public void setChecked(int checked) {
        this.checked = checked;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getdDay() {
        return date;
    }

    public void setdDay(String date) {
        this.date = date;
    }
}