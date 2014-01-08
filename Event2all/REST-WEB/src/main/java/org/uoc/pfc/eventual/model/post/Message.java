package org.uoc.pfc.eventual.model.post;

import org.uoc.pfc.eventual.utils.enums.PostEnum;

public class Message extends Post<String> {

    public Message(String header) {
	super(header, PostEnum.MESSAGE);
    }

    public Message(String header, String content) {
	super(header, content, PostEnum.MESSAGE);
    }

}
