package exam.domain.models.binding;

import javax.validation.constraints.NotNull;

public class DocumentCreateBindingModel {
    private String title;
    private String content;

    @NotNull
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @NotNull
    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
