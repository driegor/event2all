package org.uoc.pfc.eventual.model.post;

import org.uoc.pfc.eventual.utils.enums.PostEnum;

public class Comment extends Post<String> {

    public Comment() {
	super("", PostEnum.COMMENT);
    }

    public Comment(String header) {
	super(header, PostEnum.COMMENT);
    }

    public Comment(String header, String content) {
	super(header, content, PostEnum.COMMENT);
    }
}
