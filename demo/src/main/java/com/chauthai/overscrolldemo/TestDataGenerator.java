package com.chauthai.overscrolldemo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Chau Thai on 6/30/16.
 */
public class TestDataGenerator {
    private static final String[] TITLES = {
            "Brunch this weekend?",
            "Summer BBQ",
            "Oui Oui",
            "Birthday gift",
            "Recipe to try",
            "Giants game"
    };

    private static final String[] CONTENTS = {
            "I will be in your neighborhood doing errands this weekend.",
            "Wish I could come, but I'm out of town this weekend.",
            "Do you have Paris recommendations? Have you ever been?",
            "Have any ideas about what we should get Heidi for her birthday?",
            "We should eat this: Grated Squash, Corn and tomatillo Tacos.",
            "Any interest in seeing the Giants play next game?"
    };

    private static final int[] AVATARS = {
            R.drawable.avatar1,
            R.drawable.avatar2,
            R.drawable.avatar3
    };

    public static List<InboxItem> generateTestData(int n) {
        List<InboxItem> list = new ArrayList<>();

        for (int i = 0; i < n; i++) {
            InboxItem item = new InboxItem(
                    AVATARS[i % AVATARS.length],
                    TITLES[i % TITLES.length],
                    CONTENTS[i % CONTENTS.length]
            );

            list.add(item);
        }

        return list;
    }
}
