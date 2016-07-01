package com.chauthai.overscrolldemo;

/**
 * Created by Chau Thai on 6/30/16.
 */
public class InboxItem {
    private final int avatarResId;
    private final String title;
    private final String content;

    public InboxItem(int avatarResId, String title, String content) {
        this.avatarResId = avatarResId;
        this.title = title;
        this.content = content;
    }

    public int getAvatarResId() {
        return avatarResId;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }
}
